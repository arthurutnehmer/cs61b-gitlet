package gitlet;
import java.io.Serializable;
import java.util.HashMap;

/**
 * A class that defines a commit.
 * @author Arthur Utnehmer
 */
public class Commit implements Serializable {


    /**
     * @param parent - Pointer to parent Node.
     * @param message - Message from author.
     * @param blobs - The blobs to put into the tree.
     * @param date - The date of the commit.
     * @param key - The key.
     */
    protected Commit(String parent, String date,
                     String message, HashMap<String,
                     String> blobs, String key) {
        _parent = parent;
        _date = date;
        _message = message;
        _blobs = blobs;
        _key = key;
    }


    /** Commit that will copy parent table.
     * @param parentTable - parent table to copy.
     */
    protected Commit(HashMap<String, String> parentTable) {
        _blobs = new HashMap<String, String>();
        for (String location : parentTable.keySet()) {
            _blobs.put(location, parentTable.get(location));
        }
        _parent = "not set";
        _date = "not set";
        _message = "not set";
        _key = "not set";
    }

    /** Set commit to blobs connected to commit.
     * @param fuckingBlobs - Used to set blobs tracked by commit.
     */
    public void setBlobs(HashMap<String, String> fuckingBlobs) {
        this._blobs = fuckingBlobs;
    }

    /** Get blobs from this commit.
     * @return blobs.
     */
    public HashMap<String, String> getBlobs() {
        return _blobs;
    }

    /** Sets the date of the commit.
     * @param date - Date of commit.
     */
    public void setDate(String date) {
        this._date = date;
    }

    /** Gets the date of the commit.
     * @return date.
     */
    public String getDate() {
        return _date;
    }

    /** Sets the key for this commit.
     * @param key - The key for the commit.
     */
    public void setKey(String key) {
        this._key = key;
    }

    /** Gets the key of the commit.
     * @return key.
     */
    public String getKey() {
        return _key;
    }

    /** Sets the message for this commit.
     * @param message - The message for the commit.
     */
    public void setMessage(String message) {
        this._message = message;
    }

    /** Gets the message for this commit.
     * @return message.
     */
    public String getMessage() {
        return _message;
    }

    /** Sets the parent for this commit.
     * @param parent - The parent of this commit.
     */
    public void setParent(String parent) {
        this._parent = parent;
    }

    /** Gets the parent of this commit.
     * @return parent.
     */
    public String getParent() {
        return _parent;
    }

    @Override
    public String toString() {
        return ("commit " + _key
                + "\n" + "Date: " + _date + " -0800"
                + "\n" + _message
                + "\n");
    }

    @Override
    public boolean equals(Object obj) {
        Commit other = (Commit) obj;
        HashMap<String, String> blobs = other.getBlobs();
        return other.equals(_blobs);

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /** Key or commit id for the commit.*/
    private String _key;

    /** Parent Commit hash. */
    private String _parent;

    /** Time of commit. */
    private String _date;

    /** Message for commit. */
    private String _message;

    /** Hash for blobs. */
    private HashMap<String, String> _blobs;
}
