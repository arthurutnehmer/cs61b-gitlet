package gitlet;


import ucb.junit.textui;
import org.junit.Test;

import java.util.HashMap;



/** The suite of all JUnit tests for the gitlet package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(UnitTest.class));
    }


    /** Testing get commit. */
    @Test
    public void testGetCommit() {
        FileOrganizer testFile = new FileOrganizer();
        testFile.initializeDirectory();
        CommitManager testGetCommit = new CommitManager();
        testGetCommit.initialCommit();
        testFile.writeCommits(testGetCommit);
        testGetCommit = testFile.readCommits();
        HashMap<String, String> blobKeys = new HashMap<String, String>();
        for (int x = 0; x < 10; x++) {
            String message = "commit " + x;
            String blobHash = "hash " + x;
            String blobFileLocation = "file" + x;
            blobKeys.put(blobFileLocation, blobHash);
            testGetCommit.commit(message, blobKeys);
        }
        testFile.writeCommits(testGetCommit);
        testGetCommit.printLog();
        testFile.removaGitlet();
    }

    /** Testing get branches. */
    @Test
    public void testGetBranches() {
        FileOrganizer testFile = new FileOrganizer();
        testFile.initializeDirectory();
        CommitManager testGetCommit = new CommitManager();
        testGetCommit.initialCommit();
        testFile.writeCommits(testGetCommit);
        testGetCommit = testFile.readCommits();
        HashMap<String, String> blobKeys = new HashMap<String, String>();
        for (int x = 0; x < 10; x++) {
            String message = "commit " + x;
            String blobHash = "hash " + x;
            String blobFileLocation = "file" + x;
            blobKeys.put(blobFileLocation, blobHash);
            testGetCommit.commit(message, blobKeys);
        }
        testFile.writeCommits(testGetCommit);
        System.out.println(testGetCommit.getBranches());
        testFile.removaGitlet();
    }

    /** Testing delete gitlet */
    @Test
    public void testDeleteGitlet() {
        FileOrganizer testFile = new FileOrganizer();
        testFile.removaGitlet();
    }

    /** Testing status empty. */
    @Test
    public void testStatusEmpty() {
        FileOrganizer test = new FileOrganizer();
        test.removaGitlet();
        Controller testController = new Controller();
        testController.initilizeRepo();
        testController.status();
        test.removaGitlet();

    }





}


