package gitlet;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * A class that organizes the commits.
 * @author Arthur Utnehmer
 */
public class FileOrganizer {

    /** Main metadata folder. */
    static final File GITLET_DIRECTORY = new File(".gitlet");

    /** The blobs (directory for files tied to commit). */
    static final File BLOBS_DIRECTORY = new File(".gitlet"
            + File.separator + "blobs");

    /** Staging folder. */
    static final File STAGING_DIRECTORY = new File(".gitlet"
            + File.separator + "staging");

    /** Staged for addition. */
    static final File STAGED_FOR_ADDITION = new File(".gitlet"
            + File.separator + "staging" + File.separator + "addition");

    /** Staged for removal. */
    static final File STAGED_FOR_REMOVAL = new File(".gitlet"
            + File.separator + "staging"  + File.separator + "removal");

    /** Staging folder. */
    static final File COMMITS_DIRECTORY = new File(".gitlet"
            + File.separator + "commits");

    /** File Where the commits go. */
    static final File COMMITS_FILE = new File(COMMITS_DIRECTORY.getPath()
            + File.separator +  "commits");

    /**
     * Default constructor for the file organizer.
     */
    FileOrganizer() {

    }


    /**
     * Deletes file in specified folder.
     * @param folder - Folder to search.
     * @param name - Name of file to delete.
     */
    public void removeFromFolder(File folder, String name) {
        File file = new File(folder.getAbsolutePath()
                + File.separator + name);
        if (file.exists()) {
            file.delete();
        }

    }

    /**
     * Returns String List of files staged for
     * removal.
     * @return List of files staged for removal.
     * @param directory - The files in this directory.
     */
    public List<String> listFilesInDirectory(File directory) {
        List<String> stagedFiles = Utils.plainFilenamesIn(directory);
        return stagedFiles;
    }

    /**
     * Returns true if folder contains.
     * @return List of files staged for removal.
     * @param folder - The folder.
     * @param file - The file.
     */
    public boolean folderContains(File folder, String file) {
        List<String> stagedFiles = Utils.plainFilenamesIn(folder);
        return stagedFiles.contains(file);
    }

    /**
     * Removes the gitlet file directory.
     */
    public void removaGitlet() {
        if (GITLET_DIRECTORY.exists()) {
            deleteDirectory(GITLET_DIRECTORY);
            GITLET_DIRECTORY.delete();
        }
    }

    /**
     * Will delete a directory.
     * @param file where directory starts.
     */
    public void deleteDirectory(File file) {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            subfile.delete();
        }
    }

    /**
     *  Writes commits to file.
     * @param commitClass - The commit list to write to file.
     */
    public void writeCommits(CommitManager commitClass) {
        Utils.writeObject(COMMITS_FILE, commitClass);

    }

    /**
     *  Read commits from file.
     * @return Commits class.
     */
    public CommitManager readCommits() throws GitletException {
        if (!COMMITS_FILE.exists()) {
            throw new GitletException("Not in an "
                    + "initialized Gitlet directory.");
        }
        return Utils.readObject(COMMITS_FILE, CommitManager.class);
    }

    /**
     * Moves a file into a location.
     * @param source - Name of file.
     * @param destination - Destination directory.
     * @return true if file was moved.
     */
    public boolean moveTo(File source, File destination) {
        if (!source.exists()) {
            return false;
        }
        return this.copyFile(source, destination);
    }

    /**
     * Checks to see if gitlet exist.
     * @return true if gitlet exist.
     */
    public boolean gitletExist() {
        return GITLET_DIRECTORY.exists();
    }

    /**
     * Moves a file.
     * @param source - Source file.
     * @param destination - where to move file to.
     * @return True if source exist, false if it does not
     * or if the copy failed.
     */
    private boolean copyFile(File source, File destination) {
        try {
            Path src = Paths.get(source.getAbsolutePath());
            Path dest = Paths.get(destination.getAbsolutePath());
            Files.copy(src, dest);
        } catch (Exception e) {
            System.out.println("Error copying file.");
            System.out.println(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    /**
     * Saves a file as a blob.
     * @return boolean that says if file was saved.
     * @param hash - Hash.
     * @param source - Source.
     */
    public boolean saveAsBlob(String hash, File source) {
        File blob = new File(BLOBS_DIRECTORY + File.separator + hash);
        if (blob.exists()) {
            return false;
        }
        return moveTo(source, blob);
    }

    /**
     * Initializes the directory.
     */
    public void initializeDirectory() {
        if (!GITLET_DIRECTORY.exists()) {
            GITLET_DIRECTORY.mkdir();
        }
        if (!BLOBS_DIRECTORY.exists()) {
            BLOBS_DIRECTORY.mkdir();
        }
        if (!STAGING_DIRECTORY.exists()) {
            STAGING_DIRECTORY.mkdir();
        }
        if (!STAGED_FOR_ADDITION.exists()) {
            STAGED_FOR_ADDITION.mkdir();
        }
        if (!STAGED_FOR_REMOVAL.exists()) {
            STAGED_FOR_REMOVAL.mkdir();
        }
        if (!COMMITS_DIRECTORY.exists()) {
            COMMITS_DIRECTORY.mkdir();
        }
        if (!COMMITS_FILE.exists()) {
            try {
                COMMITS_FILE.createNewFile();

            } catch (Exception e) {
                System.out.println("Error creating commit file.");
            }
        }
    }


}
