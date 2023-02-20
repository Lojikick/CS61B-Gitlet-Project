package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Avik Samanta
 */
public class Main {

    /** Initializes the Gitlet Repository
     * and handles cases or commands.
     * @param args*/
    public static void main(String... args) {
        _cWD = new File(System.getProperty("user.dir"));
        _gitletRepo = Utils.join(_cWD, ".gitlet");
        GitletRepo master = new GitletRepo();
        _save = Utils.join(_gitletRepo, "saveFile");
        String s = "Gitlet version-control system ";
        s += "already exists in the current directory.";
        if (_save.exists()) {
            master = loadFiles();
        }
        boolean test = master.getInitialized();
        if (args.length == 0) {
            System.out.println("Please enter a command.");
        } else if (args[0].equals("init")) {
            initChecker(master, args, s);
        } else if (!test) {
            System.out.println("Not in an initialized Gitlet directory.");
        } else if (args[0].equals("add")) {
            addChecker(master, args);
        } else if (args[0].equals("commit")) {
            commitChecker(master, args);
        } else if (args[0].equals("checkout")) {
            checkOutChecker(args, master);
        } else if (args[0].equals("log")) {
            master.log();
            saveFiles(master);
        } else if (args[0].equals("rm")) {
            master.rm(args[1]);
            saveFiles(master);
        } else if (args[0].equals("status")) {
            master.status();
            saveFiles(master);
        } else if (args[0].equals("global-log")) {
            master.globalLog();
            saveFiles(master);
        } else if (args[0].equals("find")) {
            master.find(args[1]);
            saveFiles(master);
        } else if (args[0].equals("reset")) {
            resetChecker(master, args);
        } else if (args[0].equals("branch")) {
            branchChecker(master, args);
        } else if (args[0].equals("rm-branch")) {
            String err = "A branch with that name does not exist.";
            if (master.hasBranch(args[1])) {
                master.rmBranch(args[1]);
                saveFiles(master);
            } else {
                System.out.println(err);
            }
        } else if (args[0].equals("merge")) {
            mergeChecker(master, args);
        } else {
            String err = "No command with that name exists.";
            System.out.println(err);
        }
    }
    /** Initializes the Gitlet.
     * @param master
     * @return*/
    public static boolean isTreeClean(GitletRepo master) {
        List<String> commits = Utils.plainFilenamesIn(_cWD.getPath());
        for (int j = 0; j < commits.size(); j++) {
            String name = commits.get(j);
            if (!name.equals(".gitlet")) {
                File curr = Utils.join(_cWD, name);
                String contents = Utils.readContentsAsString(curr);
                if (!master.hasContents(name, contents)) {
                    return false;
                }
            }
        }
        return true;

    }
    /** Initializes the Gitlet.
     * @param args
     * @param master*/
    public static void checkOutChecker(String[] args, GitletRepo master) {
        String err1 = "Incorrect operands.";
        String err2 = "No commit with that id exists.";
        String err3 = "There is an untracked file in ";
        err3 += "the way; delete it, or add and commit it first.";
        String err4 = "No need to checkout the current branch.";
        String err5 = "No such branch exists";
        if (args[0].equals("checkout")) {
            if (args.length == 4) {
                if (!args[2].equals("--")) {
                    System.out.println(err1);
                    System.exit(0);
                }
                String arg = master.abbrev(args[1]);
                if (master.hasCommit(arg)) {
                    master.checkout(arg, args[3]);
                    saveFiles(master);
                } else {
                    System.out.println(err2);
                }
            } else if (args.length == 3) {
                master.checkout(args[2]);
                saveFiles(master);
            } else {
                if (!isTreeClean(master)) {
                    System.out.println(err3);
                } else if (master.isBranch(args[1])) {
                    if (!master.isCurrB(args[1])) {
                        master.checkoutBranch(args[1]);
                        saveFiles(master);
                    } else {
                        System.out.println(err4);
                    }
                } else {
                    System.out.println(err5);
                }
            }
        }
    }
    /** Initializes the Gitlet.
     * @param args
     * @param master*/
    public static void mergeChecker(GitletRepo master, String[] args) {
        String err1 = "You have uncommitted changes.";
        String err2 = "A branch with that name does not exist.";
        String err4 = "Cannot merge a branch with itself.";
        String err3 = "There is an untracked file in ";
        err3 += "the way; delete it, or add and commit it first.";
        if (!master.stagingAreaEmpty()) {
            System.out.println(err1);
            System.exit(0);
        }
        if (!master.hasBranch(args[1])) {
            System.out.println(err2);
            System.exit(0);
        }
        if (master.sameBranch(args[1])) {
            System.out.println(err4);
            System.exit(0);
        }
        if (!isTreeClean(master)) {
            System.out.println(err3);
            System.exit(0);
        } else {
            master.merge(args[1]);
            saveFiles(master);
        }
    }
    /** Initializes the Gitlet.
     * @param args
     * @param master*/
    public static void addChecker(GitletRepo master, String[] args) {
        File current = Utils.join(_cWD, args[1]);
        if (current.exists()) {
            master.add(args[1]);
        } else {
            System.out.println("File does not exist.");
        }
        saveFiles(master);
    }
    /** Initializes the Gitlet.
     * @param args
     * @param master*/
    public static void resetChecker(GitletRepo master, String[] args) {
        String err3 = "There is an untracked file in ";
        err3 += "the way; delete it, or add and commit it first.";
        if (!isTreeClean(master)) {
            System.out.println(err3);
        } else {
            master.reset(args[1]);
            saveFiles(master);
        }
    }
    /** Initializes the Gitlet.
     * @param args
     * @param master*/
    public static void commitChecker(GitletRepo master, String[] args) {
        if (master.stagingAreaEmpty()) {
            System.out.println("No changes added to the commit");
        } else if (args[1].equals("")) {
            System.out.println("Please enter a commit message.");
        } else {
            master.commit(args[1]);
            saveFiles(master);
        }
    }
    /** Initializes the Gitlet.
     * @param args
     * @param master*/
    public static void branchChecker(GitletRepo master, String[] args) {
        String err1 = "A branch with that name already exists.";
        if (!master.hasBranch(args[1])) {
            master.branch(args[1]);
            saveFiles(master);
        } else {
            System.out.println(err1);
        }
    }
    /** Initializes the Gitlet.
     * @param args
     * @param s
     * @param master*/
    public static void initChecker(GitletRepo master, String[] args, String s) {
        if (!master.getInitialized()) {
            master.init();
            try {
                _save.createNewFile();
            } catch (IOException e) {
                System.out.println(e);
            }
        } else {
            System.out.println(s);
        }
        saveFiles(master);
    }
    /** Initializes the Gitlet.
     * @param repo*/
    public static void saveFiles(GitletRepo repo) {
        Utils.writeObject(_save, repo);
    }
    /** Initializes the Gitlet.
     * @return*/
    public static GitletRepo loadFiles() {
        return Utils.readObject(_save, GitletRepo.class);
    }
    /** Initializes the Gitlet.*/
    private static File _cWD;
    /** Initializes the Gitlet.*/
    private static File _gitletRepo;
    /** Initializes the Gitlet.*/
    private static File _save;
}
