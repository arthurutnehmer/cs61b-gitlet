package gitlet;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

/**
 * A CommitList that has a head and data inside the commit.
 * @author Arthur Utnehmer
 */
public class CommitManager implements Serializable {

    /** Default time. */
    private static final String INIT_DATE = "Wed Dec 31 16:00:00 1969";

    /** Default commit message. */
    private static final String INIT_MESSAGE = "initial commit";

    /**
     * Start commit list.
     */
    public CommitManager() {
        _hashMapOfCommits = new TreeMap<String, Commit>();
        _heads = new TreeMap<String, String>();
        _branch = "master";
    }

    /**
     * Make an initial commit.
     */
    public void initialCommit() {
        String key = Utils.sha1("Initial Commit");
        _branch = "master";
        this.setHead(key);
        HashMap<String, String> blobs
                = new HashMap<String, String>();
        _hashMapOfCommits.put(key,
                new Commit(null, INIT_DATE, INIT_MESSAGE, blobs, key));
    }

    /**
     * Add to the commit list at current head.
     * @param message The message that is to be saved in the commit.
     * @param blobs The blobs to save in the commit.
     */
    public void commit(String message, HashMap<String, String> blobs) {
        Commit newCommit
                = new Commit(_hashMapOfCommits.get(this.getHead()).getBlobs());
        String fileHash = addBlobsAndHashCommit(blobs, newCommit);
        LocalDateTime myDateObj = LocalDateTime.now();

        DateTimeFormatter myFormatObj
                = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy");
        String formattedDate = myDateObj.format(myFormatObj);

        ArrayList<String> hashValues = new ArrayList<>();
        newCommit.setDate(formattedDate);
        hashValues.add(formattedDate);
        newCommit.setMessage(message);
        hashValues.add(message);
        newCommit.setParent(getHead());
        hashValues.add(fileHash);
        String key = Utils.sha1(hashValues.toArray());
        newCommit.setKey(key);

        this.setHead(newCommit.getKey());
        _hashMapOfCommits.put(newCommit.getKey(), newCommit);
    }

    /**
     * Finds a commit with the UUID.
     * @return null if not in the commit database.
     * @param uuid The uuid of the commit to find.
     */
    public Commit getCommit(String uuid) {
        if (uuid.length() == Utils.UID_LENGTH) {
            if (_hashMapOfCommits.containsKey(uuid)) {
                return _hashMapOfCommits.get(uuid);
            } else {
                return null;
            }
        } else {
            for (String uuidHashed : _hashMapOfCommits.keySet()) {
                String firsNcharacters
                        = uuidHashed.substring(0, uuid.length());
                if (firsNcharacters.equals(uuid)) {
                    return _hashMapOfCommits.get(uuidHashed);
                }
            }
        }
        return null;
    }

    /**
     * Finds a commit with the UUID.
     * @return An arraylist of ids for commits that contain a message.
     * @param message - The message to search for in commits.
     */
    public ArrayList<String> getCommitsWithMessage(String message)
            throws GitletException {
        ArrayList<String> listOfUuids = new ArrayList<String>();
        for (String uuid : _hashMapOfCommits.keySet()) {
            Commit commitMessage = _hashMapOfCommits.get(uuid);
            if (commitMessage.getMessage().equals(message)) {
                listOfUuids.add(uuid);
            }
        }
        if (listOfUuids.isEmpty()) {
            throw new GitletException("Found no commit with that message.");
        }
        return listOfUuids;
    }


    /**
     * Adds a branch using current commit as commit.
     * @param branch - Branch to add.
     */
    public void addBranch(String branch) throws GitletException {
        for (String branches : _heads.keySet()) {
            if (branch.equals(branches)) {
                throw new GitletException("A branch "
                        + "with that name already exists.");
            }
        }
        _heads.put(branch, this.getHead());
    }

    /**
     *  This function examines the blobs that are passed in and
     *  saves the blobs that have a differing hash given there file name.
     *  File names with the same hash are left alone.
     * @param blobs - to be hashed into a blob identifier.
     * @param commit - The commit.
     * @return A string representing the blobs.
     */
    public String addBlobsAndHashCommit(HashMap<String,
            String> blobs, Commit commit) {
        HashMap<String, String> blobKeys = commit.getBlobs();
        for (String item : blobs.keySet()) {
            blobKeys.put(item, blobs.get(item));
        }
        ArrayList<String> listOfElements = new ArrayList<String>();
        for (String item : blobKeys.keySet()) {
            listOfElements.add(blobKeys.get(item));
        }
        return Utils.sha1(listOfElements.toArray());
    }


    public String getCurrentBranch() {
        return _branch;
    }

    /**
     * Returns current Branch commit manager is on.
     * @return current branch.
     */
    public String currentBranch() {
        return _branch;
    }

    /**
     * Set current commit to branch.
     * @param branch - Branch to set commit to.
     */
    public void setCurrentBranch(String branch) {
        _branch = branch;
    }

    /**
     * Returns a list of branches that are in this commit.
     * @return set of branches in commit.
     */
    public Set<String> getBranches() {
        return _heads.keySet();
    }

    /**
     * Return head of current branch.
     * @return String head.
     */
    public String getHead() {
        return _heads.get(_branch);
    }

    /**
     * Set head of current branch.
     * @param head - Sets current branches head to head.
     */
    private void setHead(String head) {
        _heads.put(_branch, head);
    }


    /**
     * Get blobs from head.
     * @return blobs for head.
     */
    public HashMap<String, String> getBlobsFromHead() {
        return _hashMapOfCommits.get(this.getHead()).getBlobs();
    }

    /**
     * Get blobs from commit with specific id.
     * @param commitId - Id of commit to return blobs.
     * @return blobs for head.
     */
    public HashMap<String, String> getCommitBlobs(String commitId) {
        return this.getCommit(commitId).getBlobs();
    }

    /**
     * Print commits.
     */
    public void printLog() {
        String key = this.getHead();
        while (key != null) {
            System.out.println("===");
            Commit commit = _hashMapOfCommits.get(key);
            System.out.println(commit);
            key = commit.getParent();
        }
    }


    /** HashMap holding the commits where <String, Commit>
     * where String is the UUID for the commit.
     * */
    private TreeMap<String, Commit> _hashMapOfCommits;

    /** HashMap holding the branches that exist in this commit database.
     * For  <String1, String2>
     *     where String1 is the branch, and String2 is the UUID of head.
     */
    private TreeMap<String, String> _heads;

    /** The current branch we are on. */
    private String _branch;
}

