package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Controller {

    /** Exception for file already exist. */
    private static final String GIT_ALREADY_EXIST = "A Gitlet version-control"
            + " system already exists in the current directory.";

    Controller() {
        _fileOperation = new FileOrganizer();
        _hashedFiles = new HashMap<String, String>();
        _commits = new CommitManager();
    }


    /**
     * Gitlet Exist.
     */
    public void gitletExist() throws GitletException {
        if (!_fileOperation.gitletExist()) {
            throw new GitletException();
        }
    }

    /**
     * Initialize repo.
     */
    public void initilizeRepo() throws GitletException {
        if (_fileOperation.gitletExist()) {
            throw new GitletException(GIT_ALREADY_EXIST);
        }
        _fileOperation.initializeDirectory();
        _commits.initialCommit();
        _fileOperation.writeCommits(_commits);
    }

    /**
     * Get status of the git repo.
     */
    public void status() {
        Set<String> listOfBranches = _commits.getBranches();
        List<String> listOfStagedFiles
                = _fileOperation.listFilesInDirectory(
                        FileOrganizer.STAGED_FOR_ADDITION);
        List<String> listOfRemovedFIles
                = _fileOperation.listFilesInDirectory(
                        FileOrganizer.STAGED_FOR_REMOVAL);
        String currentBranch = _commits.getCurrentBranch();
        System.out.println("=== Branches ===");
        for (String branch : listOfBranches) {
            if (currentBranch.equals(branch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println("");
        System.out.println("=== Staged Files ===");
        for (String staged : listOfStagedFiles) {
            System.out.println(staged);
        }
        System.out.println("");
        System.out.println("=== Removed Files ===");
        for (String removed : listOfRemovedFIles) {
            System.out.println(removed);
        }
        System.out.println("");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println("");
        System.out.println("=== Untracked Files ===");
        System.out.println("");
    }


    /**
     * Add file to stage for commit.
     * @param  fileName - name of file to be staged for addition.
     */
    public void addFile(String fileName) throws GitletException {
        File file = new File(fileName);
        if (_fileOperation.folderContains(
                FileOrganizer.STAGED_FOR_REMOVAL, fileName)) {
            _fileOperation.removeFromFolder(
                    FileOrganizer.STAGED_FOR_REMOVAL, fileName);
            return;
        }
        if (!file.exists()) {
            throw new GitletException("File does not exist.");
        }
        HashMap<String, String> blobs = _commits.getBlobsFromHead();
        if (blobs.get(fileName) != null) {
            String oldFileHash = blobs.get(fileName);
            String newFileHash = Utils.sha1(Utils.readContents(file));
            if (oldFileHash.equals(newFileHash)) {
                return;
            }
        }

        File destination
                = new File(_fileOperation.STAGED_FOR_ADDITION.getAbsolutePath()
                + File.separator + file.getName());
        if (destination.exists()) {
            destination.delete();
        }
        _fileOperation.moveTo(file, destination);
    }

    /**
     * Loads commits.
     */
    public void loadCommits() throws GitletException {
        _commits = _fileOperation.readCommits();
    }

    /**
     * Save Commits.
     */
    public void saveCommits() {
        _fileOperation.writeCommits(_commits);
    }

    /**
     * Log.
     */
    public void printLog() {
        _commits.printLog();
    }

    /**
     * Global log.
     */
    public void globalLog() {
        _commits.printLog();
    }


    /**
     * find UUID of commit with message.
     * @param message - Message to search for in commits.
     */
    public void find(String message) throws GitletException {
        ArrayList<String> uuids = _commits.getCommitsWithMessage(message);
        for (String uuid : uuids) {
            System.out.println(uuid);
        }
    }

    /**
     * Checks out version of file in version of commit.
     * @param fileName - Name of file.
     * @param commitId - The commit id.
     */
    public void checkoutGitCommit(String fileName,
                                  String commitId) throws GitletException {
        if (_commits.getCommit(commitId) == null) {
            throw new GitletException("No commit with that id exists.");
        }
        HashMap<String, String> currentCommit
                = _commits.getCommitBlobs(commitId);
        getCommitFromId(fileName, currentCommit);
    }


    /**
     * Checks out all files in branch.
     * @param branch - Branch to reinstate.
     */
    public void checkOutBranch(String branch) {
        if (!_commits.getBranches().contains(branch)) {
            throw new GitletException("No such branch exists.");
        }
        if (_commits.getCurrentBranch().equals(branch)) {
            throw new GitletException("No need to checkout "
                    + "the current branch.");
        }
        _commits.setCurrentBranch(branch);
        this.clearWorkingDirectory();
        HashMap<String, String> currentCommit = _commits.getBlobsFromHead();
        for (String elements : currentCommit.keySet()) {
            getCommitFromId(elements, currentCommit);
        }
    }

    /**
     * Create a new branch.
     * @param branch - Branch to create
     */
    public void branch(String branch) throws GitletException {
        _commits.addBranch(branch);
    }

    /**
     * Will remove a file.
     * @param fileName - File to remove.
     */
    public void remove(String fileName) {
        if (_fileOperation.folderContains(
                FileOrganizer.STAGED_FOR_ADDITION, fileName)) {
            _fileOperation.removeFromFolder(
                    FileOrganizer.STAGED_FOR_ADDITION, fileName);
            return;
        }
        HashMap<String, String> blobs;
        blobs = _commits.getBlobsFromHead();
        if (blobs.keySet().contains(fileName)) {
            File fileToRemove = new File(fileName);
            if (!fileToRemove.exists()) {
                fileToRemove = new File(FileOrganizer.BLOBS_DIRECTORY
                        + File.separator + blobs.get(fileName));
            }
            File destination
                    = new File(
                    _fileOperation.STAGED_FOR_REMOVAL.getAbsolutePath()
                            + File.separator + fileName);
            if (destination.exists()) {
                destination.delete();
            }
            _fileOperation.moveTo(fileToRemove, destination);
            fileToRemove = new File(fileName);
            if (fileToRemove.exists()) {
                fileToRemove.delete();
            }
            return;
        }
        throw new GitletException("No reason to remove the file.");
    }


    /**
     * Checks out version of file in latest commit.
     * @param fileName - Name of file to check out.
     */
    public void checkoutLatestCommit(String fileName) {
        HashMap<String, String> currentCommit = _commits.getBlobsFromHead();
        getCommitFromId(fileName, currentCommit);
    }


    /**
     * Checks out git version of file according to id.
     * @param fileName - Name of file.
     * @param currentCommit - The current commit.
     */
    public void getCommitFromId(String fileName,
                                HashMap<String, String> currentCommit) {
        File fileToReinstate;
        File blobFile;
        if (currentCommit.containsKey(fileName)) {
            fileToReinstate = new File(fileName);
            blobFile = new File(_fileOperation.BLOBS_DIRECTORY
                    + File.separator + currentCommit.get(fileName));
            if (fileToReinstate.exists()) {
                fileToReinstate.delete();
            }
            _fileOperation.moveTo(blobFile, fileToReinstate);
        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    /**
     * Make a commit of what is currently in the repo.
     * @param message - Message for commit.
     */
    @SuppressWarnings("unchecked")
    public void commit(String message) throws GitletException {
        if (message.isBlank()) {
            throw new GitletException("Please enter a commit message.");
        }
        boolean clearedStagedToRemove = clearStagedToRemove();
        hashFilesInStagedForAddition();
        _commits = _fileOperation.readCommits();
        HashMap<String, String> currentCommitBlobs;
        currentCommitBlobs = _commits.getBlobsFromHead();
        HashMap<String, String> newCommitBlobs
                = (HashMap<String, String>) currentCommitBlobs.clone();
        this.saveStagedFiles(currentCommitBlobs, newCommitBlobs);

        if (currentCommitBlobs.equals(newCommitBlobs)
                & !clearedStagedToRemove) {
            throw new GitletException("No changes added to the commit.");
        }
        _commits.commit(message, newCommitBlobs);
        _hashedFiles = new HashMap<String, String>();
        _fileOperation.writeCommits(_commits);
    }


    /**
     * Saves files in staged for commit.
     * @param currentCommitBlobs - Blobs in current commit.
     * @param newCommitBlobs - New blobs for commit.
     */
    public void saveStagedFiles(HashMap<String, String> currentCommitBlobs,
                                HashMap<String, String> newCommitBlobs) {
        File stagedFileToBlob;
        for (String fileLocations : _hashedFiles.keySet()) {
            String blobInCommit = currentCommitBlobs.get(fileLocations);
            stagedFileToBlob = new File(
                    _fileOperation.STAGED_FOR_ADDITION.getAbsolutePath()
                            + File.separator + fileLocations);
            if (!_hashedFiles.get(fileLocations).equals(blobInCommit)) {
                _fileOperation.saveAsBlob(_hashedFiles.get(fileLocations),
                        stagedFileToBlob);
            } else {
                _hashedFiles.remove(fileLocations);
            }
            stagedFileToBlob.delete();
        }
        for (String fileLocation : _hashedFiles.keySet()) {
            newCommitBlobs.put(fileLocation, _hashedFiles.get(fileLocation));
        }
    }

    /**
     * Informs us if the stated to remove was cleared.
     * @return boolean that is true if files were cleared in
     * staged for removal.
     */
    public boolean clearStagedToRemove() {
        boolean cleared = false;
        List<String> filesToDelete = _fileOperation.listFilesInDirectory(
                FileOrganizer.STAGED_FOR_REMOVAL);
        for (String items : filesToDelete) {
            File item = new File(
                    FileOrganizer.STAGED_FOR_REMOVAL + File.separator + items);
            item.delete();
            cleared = true;
        }
        return cleared;
    }

    /**
     * Clears working directory.
     */
    public void clearWorkingDirectory() {
        File file = new File(".");
        List<String> filesToDelete = _fileOperation.listFilesInDirectory(file);
        for (String fileToDelete : filesToDelete) {
            file = new File(fileToDelete);
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    /**
     * Hash map for files in a directory.
     * files will be hashed and mapped.
     */
    public void hashFilesInStagedForAddition() {
        List<String> filesToBeHashed
                = Utils.plainFilenamesIn(_fileOperation.STAGED_FOR_ADDITION);
        File file;
        for (String name : filesToBeHashed) {
            file = new File(_fileOperation.STAGED_FOR_ADDITION.getAbsolutePath()
                    + File.separator + name);
            String key  = Utils.sha1(Utils.readContents(file));
            _hashedFiles.put(name, key);
        }
    }

    public void hashTrackedFiles(HashMap<String, String> trackedFiles) {
        File file;
        for (String name : trackedFiles.keySet()) {
            file = new File(name);
            String key  = Utils.sha1(Utils.readContents(file));
            _hashedFiles.put(name, key);
        }
    }


    /**
     * @return - Hashed files map.
     */
    public HashMap<String, String> getHashedFiles() {
        return _hashedFiles;
    }

    /**
     * @param hashedFiles - Takes in hashed files map.
     */
    public void setHashedFiles(HashMap<String, String> hashedFiles) {
        this._hashedFiles = hashedFiles;
    }

    /**
     * Hashmap that stores <Sting1, String2>.
     * Where string 1 is the file name and
     * string2 is the hash of the blob.
     */
    private HashMap<String, String> _hashedFiles;

    /**
     * File Organizer.
     */
    private FileOrganizer _fileOperation;

    /**
     * Commit Manager.
     */
    private CommitManager _commits;
}
