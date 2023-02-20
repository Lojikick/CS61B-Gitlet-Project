package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

public class Stage implements Serializable {
    /**The Staging area of the Gitlet Repository,
     * ques Files to be committed
     * (added) or removed.
     * Uses File Arraylists to que
     * Files for addition or removal,
     * and houses commands to add or remove such files*/
    public Stage() {
        _addStage = new TreeMap<String, Blorb>();
        _removeStage = new TreeMap<String, Blorb>();
    }
    /**Adds item to the addition stage.
     * @return*/
    public boolean emptyStages() {
        return _addStage.isEmpty() && _removeStage.isEmpty();
    }
    /**Adds item to the addition stage.
     * @return*/
    public TreeMap<String, Blorb> getAddStage() {
        return _addStage;
    }
    /**Adds item to the addition stage.
     * @return*/
    public TreeMap<String, Blorb> getRemoveStage() {
        return _removeStage;
    }
    /**Adds item to the addition stage.
     * @param file*/
    public void toAddStage(Blorb file) {
        if (inRemoveStage(file.getName())) {
            outRemoveStage(file);
        }
        _addStage.put(file.getName(), file);
    }
    /**Adds item to the addition stage.
     * @param fileName
     * @return*/
    public boolean inAddStage(String fileName) {
        return _addStage.containsKey(fileName);
    }
    /**Adds item to the addition stage.
     * @param fileName
     * @return*/
    public boolean inRemoveStage(String fileName) {
        return _removeStage.containsKey(fileName);
    }
    /**Adds item to the addition stage.
     * @param file*/
    public void changeAddStage(Blorb file) {
        _addStage.remove(file.getName());
        _addStage.put(file.getName(), file);
    }
    /**Adds item to the addition stage.
     * @param file*/
    public void toRemoveStage(Blorb file) {
        _removeStage.put(file.getName(), file);
    }
    /**Adds item to the addition stage.
     * @param file*/
    public void outAddStage(Blorb file) {
        _addStage.remove(file.getName());
    }

    /** Clears addition stage.*/
    public void clearAddStage() {
        _addStage.clear();
    }
    /** Clears removal stage.*/
    public void clearRemStage() {
        _removeStage.clear();
    }
    /**Adds item to the addition stage.
     * @param file*/
    public void outRemoveStage(Blorb file) {
        _removeStage.remove(file.getName());
    }
    /** The addition stage, represented by an Array list.*/
    private TreeMap<String, Blorb> _addStage;
    /** The removal stage, represented by an Array list.*/
    private TreeMap<String, Blorb> _removeStage;
}
