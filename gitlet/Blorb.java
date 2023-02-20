package gitlet;
import java.io.File;
import java.io.Serializable;

public class Blorb implements Serializable {
    /** Gets a branch.
     * @param temp*/
    public Blorb(File temp) {
        File curr = temp;
        _contents = Utils.readContentsAsString(curr);
        _fileName = temp.getName();
    }
    /** Gets a branch.
     * @return*/
    public String getContents() {
        return _contents;
    }
    /** Gets a branch.
     * @return*/
    public String getName() {
        return _fileName;
    }
    /** Gets a branch.*/
    private String _contents;
    /** Gets a branch.*/
    private String _fileName;

}
