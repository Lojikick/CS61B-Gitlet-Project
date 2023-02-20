package gitlet;

import java.io.Serializable;

public class Branch implements Serializable {
    /** Gets a branch.
     * @param commitId
     * @param name*/
    public Branch(String commitId, String name) {
        _commit = commitId;
        _name = name;
    }
    /** Gets a branch.
     * @param commitId*/
    public void changeCommit(String commitId) {
        _commit = commitId;
    }
    /** Gets a branch.
     * @return*/
    public String getCommit() {
        return _commit;
    }
    /** Gets a branch.
     * @return*/
    public String getName() {
        return _name;
    }
    /** Gets a branch.*/
    private String _commit;
    /** Gets a branch.*/
    private String _name;
}
