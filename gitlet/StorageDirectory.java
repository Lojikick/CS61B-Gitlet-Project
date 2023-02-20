package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

public class StorageDirectory implements Serializable {

    public StorageDirectory(File cwd, String name) {
        _dirName = name;
        _thisDir = Utils.join(cwd, name);
        _thisDir.mkdir();
    }

    public void newDr(String dName) {
        File newbie = Utils.join(_thisDir, dName);
        newbie.mkdir();
    }

    public void newDr(String dir, String dName) {
        File newbie = Utils.join(dir, dName);
        newbie.mkdir();
    }

    public void addCommit(String dName, Commit newObject, String fName) {
        File newbie = Utils.join(dName, fName);
        try {
            newbie.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
        Utils.writeObject(newbie, newObject);
    }

    public void addCommit(Commit newObject, String fName) {
        File newbie = Utils.join(_thisDir, fName);
        try {
            newbie.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
        Utils.writeObject(newbie, newObject);
    }
    public void addFile(String dName, File newObject, String fName) {
        File newbie = Utils.join(dName, fName);
        try {
            newbie.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
        Utils.writeObject(newbie, newObject);
    }
    public void addFile(File newObject, String fName) {
        File newbie = Utils.join(_thisDir, fName);
        try {
            newbie.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
        Utils.writeObject(newbie, newObject);
    }


    public void addBlorb(Blorb newObject, String fName) {
        File newbie = Utils.join(_thisDir, fName);
        try {
            newbie.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
        Utils.writeObject(newbie, newObject);
    }

    public void addBlorb(File dName, Blorb newObject, String fName) {
        File newbie = Utils.join(dName, fName);
        try {
            newbie.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
        Utils.writeObject(newbie, newObject);
    }

    public Commit getCommit(String fileName, String dName) {
        File file = Utils.join(dName, fileName);
        Commit val = Utils.readObject(file, Commit.class);
        return val;
    }
    public Commit getCommit(String fileName) {
        File file = Utils.join(_thisDir, fileName);
        Commit val = Utils.readObject(file, Commit.class);
        return val;
    }

    public Blorb getBlorb(String fileName, String dName) {
        File file = Utils.join(dName, fileName);
        Blorb val = Utils.readObject(file, Blorb.class);
        return val;
    }

    public File getfile(String fileName, String dName) {
        File file = Utils.join(dName, fileName);
        File val = Utils.readObject(file, File.class);
        return val;
    }

    public File getPath(String fileName) {
        File path = Utils.join(_thisDir, fileName);
        return path;
    }
    public File getPath() {
        return _thisDir;
    }

    public File getFileDr(String fileName, String code) {
        File file = Utils.join(_thisDir, fileName);
        TreeMap temp = Utils.readObject(file, TreeMap.class);
        File val = (File) temp.get(code);
        return val;
    }

    public boolean hasFile(String fileName) {
        File file = Utils.join(_thisDir, fileName);
        return file.exists();
    }
    public boolean hasContents(String fileName, String contents) {
        File curr = Utils.join(_thisDir, fileName);
        List<String> commits = Utils.plainFilenamesIn(curr.getPath());
        for (int j = 0; j < commits.size(); j++) {
            File temp = Utils.join(curr, commits.get(j));
            Blorb check = Utils.readObject(temp, Blorb.class);
            if (contents.equals(check.getContents())) {
                return true;
            }
        }
        return false;
    }

    public Blorb getBlorbDr(String fileName, String code) {
        File file = Utils.join(_thisDir, fileName);
        TreeMap temp = Utils.readObject(file, TreeMap.class);
        Blorb val = (Blorb) temp.get(code);
        return val;
    }
    /**Adds item to the addition stage.*/
    private File _thisDir;
    /**Adds item to the addition stage.*/
    private String _dirName;

}
