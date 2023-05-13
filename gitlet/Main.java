package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Arthur Utnehmer
 */
public class Main {

    /**
     * Driver variable test.
     */
    private static Controller _control;

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        _control = new Controller();
        if (args.length == 0) {
            System.out.println("Please enter a command.");
        } else {
            switch (args[0]) {
            case "init":
                init(args);
                break;
            case "add":
                add(args);
                break;
            case "commit":
                commit(args);
                break;
            case "checkout":
                try {
                    checkout(args);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;

            case "log":
                _control.loadCommits();
                _control.printLog();
                break;
            case "branch":
                try {
                    _control.loadCommits();
                    _control.branch(args[1]);
                    _control.saveCommits();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "find":
                find(args);
                break;
            case "global-log":
                _control.loadCommits();
                _control.globalLog();
                break;
            case "status":
                status(args);
                break;
            case "rm":
                _control.loadCommits();
                try {
                    _control.remove(args[1]);
                } catch (GitletException e) {
                    System.out.println(e.getMessage());
                }
                _control.saveCommits();
                break;
            default:
                System.out.println("No command with that name exists.");
            }
        }
    }



    /**
     * Add.
     * @param args - args.
     */
    public static void add(String[] args) {
        _control.loadCommits();
        try {
            _control.addFile(args[1]);
            _control.saveCommits();
        } catch (GitletException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get controller.
     * @return - A controller.
     */
    public static Controller getcontrol() {
        return _control;
    }

    /**
     * set a controller.
     * @param control - Controller to set.
     */
    public static void setcontrol(Controller control) {
        _control = control;
    }

    /**
     * Init.
     * @param args - args.
     */
    public static void init(String[] args) {
        try {
            _control.initilizeRepo();
            _control.saveCommits();
        } catch (GitletException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * status.
     * @param args - args.
     */
    public static void status(String[] args) {
        try {
            _control.loadCommits();
            _control.status();
        } catch (GitletException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Finds shit.
     * @param args - args.
     */
    public static void find(String[] args) {
        _control.loadCommits();
        String message = "";
        for (int x = 1; x < args.length; x++) {
            message = message + args[x];
        }
        try {
            _control.find(message);
        } catch (GitletException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Makes a fking commit.
     * @param args - args.
     */
    public static void commit(String[] args) {
        _control.loadCommits();
        String message = "";
        try {
            for (int x = 1; x < args.length; x++) {
                message = message + args[x];
            }
            _control.commit(message);
        } catch (GitletException e) {
            System.out.println(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please enter a commit message.");
        }
    }

    /**
     * Makes a fking checkout.
     * @param args - fking args.
     */
    public static void checkout(String[] args) {
        try {
            _control.loadCommits();
            int length = args.length;
            switch (length) {
            case 4:
                if (!args[2].equals("--")) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                _control.checkoutGitCommit(args[3], args[1]);
                _control.saveCommits();
                break;
            case 3:
                _control.checkoutLatestCommit(args[2]);
                _control.saveCommits();
                break;
            case 2:
                _control.checkOutBranch(args[1]);
                _control.saveCommits();
                break;
            default:
                System.out.println("No command with that name exists.");
            }
        } catch (GitletException exc) {
            System.out.println(exc.getMessage());
        }
    }
}
