package gitlet;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Formatter;
import java.util.Calendar;
import java.io.Serializable;
import java.util.LinkedList;


public class GitletRepo implements Serializable {
    /**Initializes the Current Working Directory.*/
    public GitletRepo() {
        _CWD = new File(System.getProperty("user.dir"));
        _gitletRepo = Utils.join(_CWD, ".gitlet");
        if (_StageFile != null) {
            loadStage();
        }

    }
    /** Gets a branch.
     * @return Wraps*/
    public File returnSavePath() {
        return _gitletRepo;
    }

    /**Initializes the gitlet repository, the staging area,
     * and all of the Storage Repositories responsible for
     * tracking files, blobs, and commit snapshots.*/
    private void setup() {
        _gitletRepo.mkdir();
        initFDir();
        initBDir();
        initCDir();
        initSnapDir();
        initBranchDir();
        _stagingArea = new Stage();
        _StageFile = new File(_gitletRepo, "stageFile");
        try {
            _StageFile.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
        saveStage();
    }
    /** Gets a branch.*/
    private void saveStage() {
        Utils.writeObject(_StageFile, _stagingArea);
    }
    /** Gets a branch.*/
    private void loadStage() {
        _stagingArea = Utils.readObject(_StageFile, Stage.class);
    }
    /**Creates a new Gitlet version-control.*/
    public void init() {
        setup();
        Commit initial = new Commit("initial commit", null);
        String id = Utils.sha1(Utils.serialize(initial));
        _CommitDir.addCommit(initial, id);
        _currentBranch = new Branch(id, "master");
        newBDr(_currentBranch.getName(), _currentBranch);
        _headFile = getCommitPath(_currentBranch.getCommit());
        updateHead(_headFile);
        initial.changeBranch("master");
        _initialized = true;
    }
    /** Gets a branch.
     * @return*/
    public boolean getInitialized() {
        return _gitletRepo.exists();
    }
    /**Initializes the File Storage repository
     * to Track Files, Uses a Treemap for storage.*/
    public void initFDir() {
        _FileDir = new StorageDirectory(_gitletRepo, "fileDir");

    }
    /**Initializes the Blob Storage.*/
    public void initBDir() {
        _BlorbDir = new StorageDirectory(_gitletRepo, "blorbDir");
    }
    /**Initializes the Commit Storage.*/
    public void initCDir() {
        _CommitDir = new StorageDirectory(_gitletRepo, "commitDir");
    }
    /**Initializes the Snapshot Storage repository to T
     * rack Full Commit Snapshots, Uses a Treemap for storage.*/
    public void initSnapDir() {
        _SnapshotDir = new StorageDirectory(_gitletRepo, "snapshotDir");
    }
    /**Initializes the Branch Storage
     * repository to Track branches and Save the headCommit.*/
    public void initBranchDir() {
        _BranchDir = Utils.join(_gitletRepo, "branchDir");
        _BranchDir.mkdir();
        _headFile = Utils.join(_BranchDir, "headFile");
        try {
            _headFile.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    /** Checks if tracked.
     * @param name
     * @param branch*/
    public void newBDr(String name, Branch branch) {
        File newbie = Utils.join(_BranchDir, name);
        try {
            newbie.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
        Utils.writeObject(newbie, branch);
    }
    /** Checks if tracked.
     * @param path*/
    public void updateHead(File path) {
        File branch = Utils.join(_BranchDir, "headfile");
        branch = path;
    }
    /** Checks if tracked.
     * @param name
     * @param id*/
    public void updateBranch(String name, String id) {
        File curr = Utils.join(_BranchDir, name);
        Branch branch = Utils.readObject(curr, Branch.class);
        branch.changeCommit(id);
        Utils.writeObject(curr, branch);
    }
    /** Checks if tracked.
     * @param name
     * @return*/
    public Branch getBranch(String name) {
        File curr = Utils.join(_BranchDir, name);
        Branch branch = Utils.readObject(curr, Branch.class);
        return branch;
    }

    /** Checks if tracked.
     * @param commitId
     * @return*/
    public File getCommitPath(String commitId) {
        String id = commitId;
        File val = _CommitDir.getPath(id);
        return val;
    }
    /** Checks if tracked.
     * @param branchName
     * @return*/
    public File getBranchPath(String branchName) {
        String id = branchName;
        File val = Utils.join(_BranchDir, id);
        return val;
    }
    /** Checks if tracked.
     * @param branchName
     * @return*/
    public boolean isBranch(String branchName) {
        List<String> branch = Utils.plainFilenamesIn(_BranchDir.getPath());
        return branch.contains(branchName);
    }

    /** Checks if tracked.
     * @param name*/
    public void add(String name) {
        File current = Utils.join(_CWD, name);
        _FileDir.newDr(current.getName());
        Blorb copy = new Blorb(current);
        File currFile = Utils.join(_FileDir.getPath(), current.getName());
        String id = Utils.sha1(Utils.serialize(copy));
        File head = _headFile;
        String parent = head.getName();
        Commit curr = _CommitDir.getCommit(parent);
        if (_stagingArea.inRemoveStage(copy.getName())) {
            _stagingArea.outRemoveStage(copy);
        } else if (curr.isTracked(copy)) {
            curr = curr;
        } else {
            _BlorbDir.addBlorb(copy, id);
            _FileDir.addBlorb(currFile, copy, id);
            loadStage();
            _stagingArea.toAddStage(copy);
            saveStage();
        }
    }

    /**Obtains the addStage arraylist.
     * @param message*/
    @SuppressWarnings("unchecked")
    public void commit(String message) {
        loadStage();
        File head = _headFile;
        String parent = head.getName();
        Commit oldie = _CommitDir.getCommit(parent);
        Commit newbie = new Commit(message, parent);
        newbie.changeMessage(message);
        String t = "%ta %tb %te %tT %tY" + " -0800";
        _mat = _mat.format(t, _cal, _cal, _cal, _cal, _cal);
        String s = _mat.toString();
        newbie.changeTimestamp(s);
        TreeMap prev = oldie.getBlorbDir();
        TreeMap next = (TreeMap) prev.clone();
        newbie.changeBlorbDirectory(next);
        TreeMap<String, Blorb> removed = _stagingArea.getRemoveStage();
        Map<String, Blorb> noBlorbs = removed;
        for (Map.Entry<String, Blorb> entry : noBlorbs.entrySet()) {
            String id = entry.getKey();
            Blorb current = removed.get(id);
            if (newbie.hasFile(current.getName())) {
                newbie.removeBlorb(current.getName());
            }
        }
        TreeMap<String, Blorb> added = _stagingArea.getAddStage();
        Map<String, Blorb> allBlorbs = added;
        for (Map.Entry<String, Blorb> entry : allBlorbs.entrySet()) {
            String id = entry.getKey();
            Blorb current = added.get(id);
            if (newbie.hasFile(current.getName())) {
                newbie.removeBlorb(current.getName());
                newbie.addBlorb(current);
            } else {
                newbie.addBlorb(current);
            }
        }
        _stagingArea.clearAddStage();
        _stagingArea.clearRemStage();
        String id = Utils.sha1(Utils.serialize(newbie));
        _CommitDir.addCommit(newbie, id);
        updateBranch(_currentBranch.getName(), id);
        _currentBranch = getBranch(_currentBranch.getName());
        _headFile = getCommitPath(_currentBranch.getCommit());
        updateHead(_headFile);
        oldie.removeBranch();
        newbie.changeBranch(_currentBranch.getName());
        saveStage();
    }
    /** Checks if tracked.
     * @param message
     * @param parentMerge
     * */
    @SuppressWarnings("unchecked")
    public void commitM(String message, String parentMerge) {
        loadStage();
        File head = _headFile;
        String parent = head.getName();
        Commit oldie = _CommitDir.getCommit(parent);
        Commit newbie = new Commit(message, parent);
        newbie.addParent(parentMerge);
        newbie.changeMessage(message);
        String t = "%ta %tb %te %tT %tY" + " -0800";
        _mat = _mat.format(t, _cal, _cal, _cal, _cal, _cal);
        String s = _mat.toString();
        newbie.changeTimestamp(s);
        TreeMap prev = oldie.getBlorbDir();
        TreeMap next = (TreeMap) prev.clone();
        newbie.changeBlorbDirectory(next);
        TreeMap<String, Blorb> removed = _stagingArea.getRemoveStage();
        Map<String, Blorb> noBlorbs = removed;
        for (Map.Entry<String, Blorb> entry : noBlorbs.entrySet()) {
            String id = entry.getKey();
            Blorb current = removed.get(id);
            if (newbie.hasFile(current.getName())) {
                newbie.removeBlorb(current.getName());
            }
        }

        TreeMap<String, Blorb> added = _stagingArea.getAddStage();
        Map<String, Blorb> allBlorbs = added;
        for (Map.Entry<String, Blorb> entry : allBlorbs.entrySet()) {
            String id = entry.getKey();
            Blorb current = added.get(id);
            if (newbie.hasFile(current.getName())) {
                newbie.removeBlorb(current.getName());
                newbie.addBlorb(current);
            } else {
                newbie.addBlorb(current);
            }
        }
        _stagingArea.clearAddStage();
        _stagingArea.clearRemStage();
        String id = Utils.sha1(Utils.serialize(newbie));
        _CommitDir.addCommit(newbie, id);
        updateBranch(_currentBranch.getName(), id);
        _currentBranch = getBranch(_currentBranch.getName());
        _headFile = getCommitPath(_currentBranch.getCommit());
        updateHead(_headFile);
        oldie.removeBranch();
        newbie.changeBranch(_currentBranch.getName());
        saveStage();
    }
    /**tarting at the current head commit.*/
    public void log() {
        String fullFullMessage = "";
        File headPointer = _headFile;
        String head = headPointer.getName();
        Commit ogCommit = _CommitDir.getCommit(head);
        ArrayList<String> parentIds = ogCommit.getParent();
        Commit parent = null;
        String message = "";
        String date = "";
        String fullMessage = "";
        Commit headCommit = ogCommit;
        headCommit = ogCommit;
        parent = _CommitDir.getCommit(parentIds.get(0));
        while (parent != null) {
            message = headCommit.getMessage();
            date = headCommit.getTimestamp();
            if (parentIds.size() > 1) {
                String id = parentIds.get(0).substring(0, 7);
                String idd = parentIds.get(1).substring(0, 7);
                fullMessage = "===\ncommit ";
                fullMessage += Utils.sha1(Utils.serialize(headCommit));
                fullMessage += "\nMerge: " + id + " " + idd;
                fullMessage += "\nDate: " + date + "\n" + message + "\n\n";
            } else {
                fullMessage = "===\ncommit ";
                fullMessage += Utils.sha1(Utils.serialize(headCommit));
                fullMessage += "\nDate: " + date;
                fullMessage += "\n" + message + "\n\n";

            }
            fullFullMessage = fullFullMessage + fullMessage;
            headCommit = parent;
            if (parent.getParent().isEmpty()) {
                parent = null;
            } else {
                ArrayList<String> tempIds = headCommit.getParent();
                parent = _CommitDir.getCommit(tempIds.get(0));
                parentIds = tempIds;
            }
        }
        message = headCommit.getMessage();
        date = headCommit.getTimestamp();
        fullMessage = "===\ncommit ";
        fullMessage += Utils.sha1(Utils.serialize(headCommit));
        fullMessage += "\nDate: " + date;
        fullMessage += "\n" + message + "\n\n";
        fullFullMessage = fullFullMessage + fullMessage;
        System.out.print(fullFullMessage);
    }
    /**Takes the version of the file as it exists.
     * @param fileName*/
    public void checkout(String fileName) {
        File headPointer = _headFile;
        String head = _headFile.getName();
        Commit headCommit =  _CommitDir.getCommit(head);
        Commit parent = _CommitDir.getCommit(headCommit.getParent().get(0));

        Blorb contents = headCommit.getBlorb(fileName);
        if (contents == null) {
            System.out.println("File does not exist in that commit.");
        } else {
            File original = Utils.join(_CWD, fileName);
            Utils.writeContents(original, contents.getContents());
        }
    }
    /**Takes the version of the file.
     * @param commitId
     * @param fileName*/
    public void checkout(String commitId, String fileName) {
        Commit currentCommit = _CommitDir.getCommit(commitId);
        Blorb contents = currentCommit.getBlorb(fileName);
        Commit parent = _CommitDir.getCommit(currentCommit.getParent().get(0));
        if (contents == null) {
            System.out.println("File does not exist in that commit.");
        } else {
            File original = Utils.join(_CWD, fileName);
            Utils.writeContents(original, contents.getContents());
        }
    }
    /**Takes all files in the commit at the head.
     * @param branch*/
    public void checkoutBranch(String branch) {
        Branch curr = getBranch(branch);
        String branchid = curr.getCommit();
        Commit brCommit = _CommitDir.getCommit(branchid);
        Commit currentCommit = _CommitDir.getCommit(_currentBranch.getCommit());
        Map<String, Blorb> oldBlorbs = brCommit.getBlorbDir();
        for (Map.Entry<String, Blorb> entry : oldBlorbs.entrySet()) {
            String id = entry.getKey();
            Blorb brContents = brCommit.getBlorb(id);
            String fileName = brContents.getName();
            File original = Utils.join(_CWD, fileName);
            if (!currentCommit.hasFile(fileName)) {
                Utils.writeContents(original, brContents.getContents());
            }
        }
        Map<String, Blorb> allBlorbs = currentCommit.getBlorbDir();
        for (Map.Entry<String, Blorb> entry : allBlorbs.entrySet()) {
            String id = entry.getKey();
            Blorb currContents = currentCommit.getBlorb(id);
            Blorb brContents = brCommit.getBlorb(id);
            String fileName = currContents.getName();
            File original = Utils.join(_CWD, fileName);
            if (brContents == null) {
                original.delete();
            } else {
                Utils.writeContents(original, brContents.getContents());
            }
        }
        if (getBranch(branch).getCommit().equals(_headFile)) {
            _stagingArea.clearAddStage();
            _stagingArea.clearRemStage();
        }
        _headFile = getCommitPath(branchid);
        _currentBranch = getBranch(branch);
        updateHead(_headFile);
    }
    /**Unstage the file if it is currently.
     * @param fileName*/
    public void rm(String fileName) {
        loadStage();
        String name = fileName;
        File headPointer = _headFile;
        String head = _headFile.getName();
        Commit headCommit = _CommitDir.getCommit(head);
        if (!_stagingArea.inAddStage(name) && !headCommit.inCommit(name)) {
            System.out.println("No reason to remove the file.");
        } else if (_stagingArea.inAddStage(name)) {
            TreeMap<String, Blorb> add = _stagingArea.getAddStage();
            Blorb curr = add.get(name);
            _stagingArea.outAddStage(curr);
        } else if (headCommit.inCommit(name)) {
            TreeMap<String, Blorb> add = headCommit.getBlorbDir();
            Blorb curr = add.get(name);
            _stagingArea.toRemoveStage(curr);
            File original = Utils.join(_CWD, fileName);
            original.delete();
        }
        saveStage();
    }
    /**Unstage the file if it is currently.
     * @param name*/
    public void rmBranch(String name) {
        try {
            File br = getBranchPath(name);
            Branch branch = Utils.readObject(br, Branch.class);
            if (branch.getName().equals(_currentBranch.getName())) {
                System.out.println("Cannot remove the current branch.");
            } else {
                br.delete();
            }
        } catch (Exception e) {
            System.out.println("A branch with that name does not exist.");
        }
    }

    /**Unstage the file if it is currently.
     * @param message*/
    public void find(String message) {
        List<String> commits = Utils.plainFilenamesIn(_CommitDir.getPath());
        String fullFullMessage = "";
        Commit headCommit = null;
        for (int i = 0; i < commits.size(); i++) {
            String id = commits.get(i);
            headCommit = _CommitDir.getCommit(id);
            if (message.equals(headCommit.getMessage())) {
                fullFullMessage = fullFullMessage + id + " \r\n";
            }
        }
        if (fullFullMessage.equals("")) {
            System.out.println("Found no commit with that message.");
        } else {
            System.out.println(fullFullMessage);
        }
    }

    /**Like log, except displays information.*/
    public void globalLog() {
        List<String> commits = Utils.plainFilenamesIn(_CommitDir.getPath());
        String fullFullMessage = "";
        Commit headCommit =  null;
        String message = "";
        String date = "";
        String fullMessage = "";
        for (int i = 0; i < commits.size(); i++) {
            headCommit = _CommitDir.getCommit(commits.get(i));
            message = headCommit.getMessage();
            date = headCommit.getTimestamp();
            fullMessage = "===\ncommit ";
            fullMessage += Utils.sha1(Utils.serialize(headCommit));
            fullMessage += "\nDate: " + date + "\n";
            fullMessage += message + "\n\n";
            fullFullMessage = fullFullMessage + fullMessage;
        }
        System.out.print(fullFullMessage);
    }

    /**Like log, except displays information.*/
    public void status() {
        List<String> commits = Utils.plainFilenamesIn(_BranchDir.getPath());
        String fullFullMessage = "=== Branches ===" + "\n";
        for (int i = 0; i < commits.size(); i++) {
            if (commits.get(i).equals("headFile")) {
                fullFullMessage = fullFullMessage;
            } else if (_currentBranch.getName().equals(commits.get(i))) {
                String id = commits.get(i);
                fullFullMessage = fullFullMessage + "*" + id + "\n";
            } else {
                String id = commits.get(i);
                fullFullMessage = fullFullMessage + id + "\n";
            }
        }
        fullFullMessage += " \r\n" + "=== Staged Files ===" + "\n";
        TreeMap<String, Blorb> added = _stagingArea.getAddStage();
        Map<String, Blorb> allBlorbs = added;
        for (Map.Entry<String, Blorb> entry : allBlorbs.entrySet()) {
            String id = entry.getKey();
            fullFullMessage = fullFullMessage + id + "\n";
        }
        fullFullMessage += " \r\n" + "=== Removed Files ===" + "\n";
        TreeMap<String, Blorb> removed = _stagingArea.getRemoveStage();
        Map<String, Blorb> remBlorbs = removed;
        for (Map.Entry<String, Blorb> entry : remBlorbs.entrySet()) {
            String id = entry.getKey();
            fullFullMessage = fullFullMessage + id + "\n";
        }
        fullFullMessage += " \r\n" + "=== Modifications ";
        fullFullMessage += "Not Staged For Commit ===" + "\n";
        fullFullMessage += " \r\n" + "=== Untracked Files ===" + "\n";
        System.out.println(fullFullMessage);
    }
    /** Creates a new branch with the given name.
     * @param name*/
    public void branch(String name) {
        File headPointer = _headFile;
        String head = headPointer.getName();
        Branch newbie = new Branch(head, name);
        newBDr(name, newbie);
    }
    /** Creates a new branch with the given name.
     * @param commitId*/
    public void reset(String commitId) {
        if (!_CommitDir.hasFile(commitId)) {
            System.out.println("No commit with that id exists.");
        } else {
            Commit curr = _CommitDir.getCommit(commitId);
            Map<String, Blorb> allBlorbs = curr.getBlorbDir();
            for (Map.Entry<String, Blorb> entry : allBlorbs.entrySet()) {
                String id = entry.getKey();
                checkout(commitId, id);
            }
            curr.clearBlorbDirectory();
            updateBranch(_currentBranch.getName(), commitId);
            _currentBranch = getBranch(_currentBranch.getName());
            _headFile = getCommitPath(_currentBranch.getCommit());
            loadStage();
            _stagingArea.clearAddStage();
            _stagingArea.clearRemStage();
            saveStage();
        }
    }
    /** Creates a new branch with the given name.
     * @param s
     * @param c
     * @param g
     * @return
     * */
    public ArrayList<String> getAllNames(Commit s, Commit c, Commit g) {
        TreeMap<String, Blorb> splitFiles = s.getBlorbDir();
        TreeMap<String, Blorb> currentFiles = c.getBlorbDir();
        TreeMap<String, Blorb> givenFiles = g.getBlorbDir();
        TreeMap<String, Blorb> allFiles = new TreeMap<String, Blorb>();
        Map<String, Blorb> full = splitFiles;
        for (Map.Entry<String, Blorb> cntry : full.entrySet()) {
            String name = cntry.getKey();
            Blorb temp1 = splitFiles
                    .get(name);
            if (!allFiles.containsKey(name)) {
                allFiles.put(name, temp1);
            }
        }
        Map<String, Blorb> cStrag = currentFiles;
        for (Map.Entry<String, Blorb> cntry : cStrag.entrySet()) {
            String name = cntry.getKey();
            Blorb temp1 = currentFiles.get(name);
            if (!allFiles.containsKey(name)) {
                allFiles.put(name, temp1);
            }
        }
        Map<String, Blorb> gStrag = givenFiles;
        for (Map.Entry<String, Blorb> gntry : gStrag.entrySet()) {
            String name = gntry.getKey();
            Blorb temp2 = givenFiles.get(name);
            if (!allFiles.containsKey(name)) {
                allFiles.put(name, temp2);
            }
        }
        Map<String, Blorb> allBlorbs = allFiles;
        ArrayList<String> allNames = new ArrayList<>();
        for (Map.Entry<String, Blorb> entry : allBlorbs.entrySet()) {
            String name = entry.getKey();
            allNames.add(name);
        }
        return allNames;
    }

    /** Creates a new branch with the given name.
     * @param split
     * @param given
     * @param current
     * @return
     * */
    public boolean bigMeatyClaw(Commit split, Commit given, Commit current) {
        boolean temp = false;
        TreeMap<String, Blorb> splitFiles = split.getBlorbDir();
        TreeMap<String, Blorb> currentFiles = current.getBlorbDir();
        TreeMap<String, Blorb> givenFiles = given.getBlorbDir();
        ArrayList<String> allNames = getAllNames(split, given, current);
        for (int i = 0; i < allNames.size(); i++) {
            String name = allNames.get(i);
            Blorb splitb = null;
            Blorb currb = null;
            Blorb givenb = null;
            String s = null;
            String g = null;
            String c = null;
            if (split.hasFile(name)) {
                splitb = splitFiles.get(name);
                s = splitb.getContents();
            }
            if (current.hasFile(name)) {
                currb = currentFiles.get(name);
                c = currb.getContents();
            }
            if (given.hasFile(name)) {
                givenb = givenFiles.get(name);
                g = givenb.getContents();
            }
            if (s == null && c != null && g != null) {
                if (c.equals(g)) {
                    c = c;
                } else {
                    temp = true;
                    cCase(name, givenb, currb);
                }
            } else if (s == null && c == null && g != null) {
                aCase(name, givenb, given);
            } else if (s == null && g == null && c != null) {
                c = c;
            } else if (g == null && (s.equals(c))) {
                rm(name);
            } else if (c == null && (s.equals(g))) {
                c = c;
            } else if (g == null && c == null) {
                c = c;
            } else if (!s.equals(g) && s.equals(c)) {
                aCase(name, givenb, given);
            } else if (s.equals(g) && !s.equals(c)) {
                c = c;
            } else if ((!s.equals(g) && !s.equals(c))) {
                if (c.equals(g)) {
                    c = c;
                } else {
                    temp = true;
                    cCase(name, givenb, currb);
                }
            }
        }
        return temp;
    }
    /** Creates a new branch with the given name.
     * @param name
     * @param split
     * @return*/
    public Blorb condGetBl(String name, Commit split) {
        TreeMap<String, Blorb> splitFiles = split.getBlorbDir();
        if (split.hasFile(name)) {
            return splitFiles.get(name);
        }
        return null;
    }
    /** Creates a new branch with the given name.
     * @param name
     * @param split
     * @return*/
    public String condGetStr(String name, Commit split) {
        TreeMap<String, Blorb> splitFiles = split.getBlorbDir();
        if (split.hasFile(name)) {
            Blorb splitb = splitFiles.get(name);
            return splitb.getContents();
        }
        return null;
    }
    /** Creates a new branch with the given name.
     * @param branchName*/
    public void merge(String branchName) {
        Branch givenBranch = getBranch(branchName);
        Boolean temp = false;
        Branch currentBranch = _currentBranch;
        String spt = getSplitPoint(givenBranch, currentBranch);
        Branch splitBranch = new Branch(spt, "split");
        Commit split = _CommitDir.getCommit(splitBranch.getCommit());
        if (splitBranch.getCommit().equals(givenBranch.getCommit())) {
            String m = "Given branch is an ";
            m += "ancestor of the current branch.";
            System.out.println(m);
            return;
        }
        if (splitBranch.getCommit().equals(currentBranch.getCommit())) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        Commit given = _CommitDir.getCommit(givenBranch.getCommit());
        Commit current = _CommitDir.getCommit(currentBranch.getCommit());
        temp = bigMeatyClaw(split, given, current);
        mergeEndGame(temp, givenBranch, currentBranch);
    }

    public void mergeEndGame(boolean temp, Branch gb, Branch cb) {
        String m = "Merged " + gb.getName();
        m += " into " + cb.getName() + ".";
        commitM(m, gb.getCommit());
        if (temp) {
            System.out.println("Encountered merge conflict");
        }
    }
    public void cCase(String n, Blorb g, Blorb c) {
        String conflict = getMergeString(g, c);
        File conflicted = Utils.join(_CWD, n);
        Utils.writeContents(conflicted, conflict);
        add(n);
    }

    public void aCase(String n, Blorb c, Commit cc) {
        checkout(Utils.sha1(Utils.serialize(cc)), n);
        loadStage();
        _stagingArea.toAddStage(c);
        saveStage();
    }
    public String getMergeString(Blorb g, Blorb c) {
        String conflict = "<<<<<<< HEAD\n";
        if (g == null) {
            conflict = conflict + c.getContents();
            conflict = conflict + "=======\n";
            conflict = conflict + ">>>>>>>\n";
        } else if (c == null) {
            conflict = conflict + "=======\n";
            conflict = conflict + g.getContents();
            conflict = conflict + ">>>>>>>\n";
        } else {
            conflict = conflict + c.getContents();
            conflict = conflict + "=======\n";
            conflict = conflict + g.getContents();
            conflict = conflict + ">>>>>>>\n";
        }
        return conflict;
    }
    public String getSplitPoint(Branch given, Branch current) {
        String result = "";
        Commit g = _CommitDir.getCommit(given.getCommit());
        Commit c = _CommitDir.getCommit(current.getCommit());
        ArrayList<String> gAncestors = getAncestors(given.getCommit());
        ArrayList<String> cAncestors = getAncestors(current.getCommit());
        int cSize = cAncestors.size();
        Queue<String> r = new LinkedList<>();
        r.add(current.getCommit());
        boolean[] visited = new boolean[cAncestors.size()];
        visited[cAncestors.indexOf(current.getCommit())] = true;
        while (!r.isEmpty()) {
            String curr = r.remove();
            if (gAncestors.contains(curr)) {
                result = curr;
                break;
            }
            Commit temp = _CommitDir.getCommit(curr);
            ArrayList<String> neighbors = temp.getParent();
            for (int i = 0; i < neighbors.size(); i++) {
                String currParent = neighbors.get(i);
                int dex = cAncestors.indexOf(currParent);
                if (!visited[dex]) {
                    r.add(currParent);
                    visited[dex] = true;
                }
            }
        }
        return result;
    }

    public ArrayList<String> getAncestors(String curr) {
        Commit headCommit =  _CommitDir.getCommit(curr);
        ArrayList<String> parents = headCommit.getParent();
        ArrayList<String> anc = new ArrayList<>();
        anc.add(curr);
        for (int i = 0; i < parents.size(); i++) {
            Commit parent = _CommitDir.getCommit(parents.get(i));
            if (parent.getParent() == null) {
                parent = null;
            } else {
                ArrayList<String> prevAnc = getAncestors(parents.get(i));
                for (int j = 0; j < prevAnc.size(); j++) {
                    String temp = prevAnc.get(j);
                    if (!anc.contains(temp)) {
                        anc.add(temp);
                    }
                }
            }
        }
        return anc;
    }

    public ArrayList<Commit> getNeighbors(Commit curr) {
        ArrayList<Commit> vals = new ArrayList<>();
        ArrayList<String> ids = curr.getParent();
        for (int i = 0; i < ids.size(); i++) {
            Commit temp = _CommitDir.getCommit(ids.get(i));
            vals.add(temp);
        }
        return vals;
    }

    public boolean stagingAreaEmpty() {
        return _stagingArea.emptyStages();
    }
    public boolean hasCommit(String id) {
        try {
            Commit test = _CommitDir.getCommit(id);
            return true;
        } catch (Exception e) {
            List<String> commits = Utils.plainFilenamesIn(_CommitDir.getPath());
            for (int i = 0; i < commits.size(); i++) {
                String xd = commits.get(i);
                if (xd.toUpperCase().contains(id.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**The Head Branch, a File.
     * @param input
     * @return*/
    public String abbrev(String input) {
        List<String> commits = Utils.plainFilenamesIn(_CommitDir.getPath());
        for (int i = 0; i < commits.size(); i++) {
            String xd = commits.get(i);
            if (xd.toUpperCase().contains(input.toUpperCase())) {
                return xd;
            }
        }
        return input;
    }
    /**The Head Branch, a File.
     * @param id
     * @return*/
    public boolean hasBranch(String id) {
        try {
            Branch test = getBranch(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**The Head Branch, a File.
     * @param id
     * @return*/
    public boolean hasFile(String id) {
        return (_FileDir.hasFile(id));
    }
    /**The Head Branch, a File.
     * @param id
     * @param contents
     * @return*/
    public boolean hasContents(String id, String contents) {
        return (_FileDir.hasContents(id, contents));
    }
    /**The Head Branch, a File.
     * @param id
     * @return*/
    public boolean sameBranch(String id) {
        return (_currentBranch.getName().equals(id));
    }
    /**The Head Branch, a File.
     * @param id
     * @return*/
    public boolean isCurrB(String id) {
        Branch test = getBranch(id);
        return (_currentBranch.getName().equals(id));
    }
    /**The Main Gitlet Repository.*/
    private File _CWD;
    /**The Main Gitlet Repository.*/
    private File _gitletRepo;
    /**The Staging area, represented by a Stage object.*/
    private Stage _stagingArea;
    /**The Master Branch.*/
    private Branch _currentBranch;
    /**The File Directory.*/
    private StorageDirectory _FileDir;
    /**The Commit Directory,
     * system that keeps track of.*/
    private StorageDirectory _CommitDir;
    /**The Blorb Directory.*/
    private StorageDirectory _BlorbDir;
    /**The Snapshot Directory.*/
    private  StorageDirectory _SnapshotDir;
    /**The Branch Directory.*/
    private File _BranchDir;
    /**The Head Branch, a File.*/
    private File _headFile;
    /**The Head Branch, a File.*/
    private File _StageFile;
    /**The Head Branch, a File representing.*/
    private static Formatter _mat = new Formatter();
    /**The Head Branch, a File.*/
    private static String format = "yyyy-MM-dd";
    /**The Head Branch, a File.*/
    private static Calendar _cal = Calendar.getInstance();
    /**The Head Branch, a File.*/
    private static boolean _initialized;


}
