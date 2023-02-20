package gitlet;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/** An object representing Combinations of log messages,
 * other metadata (commit date, author, etc.),
 * a reference to a tree, and references to parent commits.
 * The repository also maintains a mapping from branch heads
 * (in this course, we've used names like master, proj2, etc.)
 * to references to commits, so that certain important
 * commits have symbolic names.
 * @author Avik Samanta*/

public class Commit implements Serializable {

    /** Initializes the message, the parent commit, and the commit's Sha1 id
     * and takes care of initial commit if no parent found.
     * @param message
     * @param parent
     * */

    public Commit(String message, String parent) {
        _message = message;
        _parent = new ArrayList<>();
        if (parent == null) {
            this._timestamp = "Wed Dec 31 16:00:00 1969 -0800";
        } else {
            _parent.add(parent);
        }
        _blorbs = new TreeMap<String, Blorb>();
        _branchPoint = null;
    }
    /** Checks if tracked.
     * @param b
     * @return Real permutation.*/
    public boolean isTracked(Blorb b) {
        if (_blorbs.containsKey(b.getName())) {
            Blorb temp = _blorbs.get(b.getName());
            return temp.getContents().equals(b.getContents());
        } else {
            return false;
        }
    }
    /** Gets a branch.
     * @return Wraps*/
    public String getBranch() {
        return _branchPoint;
    }
    /** Gets a branch.
     * @param branchName*/
    public void changeBranch(String branchName) {
        _branchPoint = branchName;
    }
    /** Gets a branch.*/
    public void removeBranch() {
        _branchPoint = null;
    }
    /** Gets a branch.
     * @return Wraps*/
    public TreeMap<String, Blorb> getBlorbDir() {
        return _blorbs;
    }
    /** Gets a branch.
     * @param curr*/
    public void addBlorb(Blorb curr) {
        _blorbs.put(curr.getName(), curr);
    }
    /** Gets a branch.
     * @param id*/
    public void removeBlorb(String id) {
        _blorbs.remove(id);
    }
    /** Gets a branch.
     * @param filename
     * @return Wraps*/
    public boolean hasFile(String filename) {
        return _blorbs.containsKey(filename);
    }
    /** Gets a branch.*/
    public void clearBlorbDirectory() {
        _blorbs.clear();
    }
    /** Gets a branch.
     * @param id
     * @return Wraps*/
    public Blorb getBlorb(String id) {
        Blorb curr = (Blorb) _blorbs.get(id);
        if (curr == null) {
            Map<String, Blorb> allBlorbs = _blorbs;
            for (Map.Entry<String, Blorb> entry : allBlorbs.entrySet()) {
                String xd = entry.getKey();
                if (xd.toUpperCase().contains(id.toUpperCase())) {
                    curr = _blorbs.get(xd);
                }
            }
        }
        return curr;
    }
    /** Gets a branch.
     * @param file
     * @return Wraps*/
    public boolean inCommit(String file) {
        return _blorbs.containsKey(file);
    }
    /** Gets a branch.
     * @param newMessage*/
    public void changeMessage(String newMessage) {
        _message = newMessage;
    }
    /** Gets a branch.
     * @param dir*/
    @SuppressWarnings("unchecked")
    public void changeBlorbDirectory(TreeMap<String, Blorb> dir) {
        _blorbs = dir;
    }
    /** Gets a branch.
     * @param newTimestamp*/
    public void changeTimestamp(String newTimestamp) {
        _timestamp = newTimestamp;
    }
    /** Gets a branch.
     * @param newParent*/
    public void addParent(String newParent) {
        _parent.add(newParent);
    }

    /** Gets a branch.
     * @return Wraps*/
    public String getMessage() {
        return this._message;
    }
    /** Gets a branch.
     * @return Wraps*/
    public String getTimestamp() {
        return this._timestamp;
    }
    /** Gets a branch.
     * @return Wraps*/
    public ArrayList<String> getParent() {
        return this._parent;
    }
    /** The message of the current commit.*/
    private String _message;
    /** The time the current commit was created.*/
    private String _timestamp;
    /** The Parent of the current commit.*/
    private ArrayList<String> _parent;
    /** The staticCommit's pointers to blobs.*/
    private TreeMap<String, Blorb> _blorbs;
    /** The staticCommit's pointers to blobs.*/
    private String _branchPoint;
}

