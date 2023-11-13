package ui.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.User;
import filehandling.DirectLoftAccess;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import ui.App;

/**
 * This class contains the JUnit tests for the UserInfoScreenController class.
 * It tests the functionality of viewing and updating user info including error
 * messages for invalid input and returning/log out without saving changes.
 */
public class UserInfoScreenControllerTest extends ControllerTestBase {

    private Collection<String> fields = Arrays.asList("#name", "#username",
            "#email", "#password0", "#password1",
            "#password2");

    private static User user;

    @Override
    public void start(Stage stage) throws IOException {
        user = new User("Test Person", "tester", "test123", "testPerson@gmail.com");
        SceneSwitcher.setUser(user);
        new DirectLoftAccess().registerUser(user);

        root = App.customStart(stage, "UserInfoScreen.fxml", new UserInfoScreenController());
    }

    @AfterAll
    public static void cleanUp() {
        deleteTestfile();
    }

    @BeforeAll
    public static void setUp() {
        deleteTestfile();
    }

    @Test
    public void testFieldsNotFilledOut() {
        Platform.runLater(() -> {
            lookup("#name").queryTextInputControl().setText("");
            lookup("#username").queryTextInputControl().setText("");
            lookup("#email").queryTextInputControl().setText("");
        });
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Save changes");
        Text errorMessage = lookup("#errorMessage").queryText();
        assertEquals(errorMessage.getText(), "Please fill out all fields");
    }

    @Test
    public void testPasswordsHalfUpdated() {
        lookup("#password1").queryTextInputControl().setText("test");
        clickOn("Save changes");
        Text errorMessage = lookup("#errorMessage").queryText();
        assertEquals(errorMessage.getText(), "Please fill out all fields");
        lookup("#password1").queryTextInputControl().setText("");
        lookup("#password2").queryTextInputControl().setText("test");
        clickOn("Save changes");
        Text errorMessage2 = lookup("#errorMessage").queryText();
        assertEquals(errorMessage2.getText(), "Please fill out all fields");
    }

    @Test
    public void testAlreadyUsedUsername() {
        User user2 = new User("test1", "tester2", "testPerson", "testPerson2@gmail.com");
        loftAccess.registerUser(user2);
        lookup("#name").queryTextInputControl().setText("Test Person");
        lookup("#username").queryTextInputControl().setText("tester2");
        lookup("#password0").queryTextInputControl().setText("test123");
        lookup("#password1").queryTextInputControl().setText("test123");
        lookup("#password2").queryTextInputControl().setText("test123");
        lookup("#email").queryTextInputControl().setText("testPerson@gmail.com");
        clickOn("Save changes");
        Text errorMessage = lookup("#errorMessage").queryText();
        assertEquals(errorMessage.getText(), "Username is taken");
    }

    @Test
    public void testPasswordDontMatch() {
        fillFields("test");
        lookup("#password1").queryTextInputControl().setText("test123");
        lookup("#password2").queryTextInputControl().setText("test321");
        clickOn("Save changes");
        Text errorMessage = lookup("#errorMessage").queryText();
        assertEquals(errorMessage.getText(), "Passwords do not match");
    }

    @Test
    public void testChangingInfoWithoutPassword() {
        lookup("#name").queryTextInputControl().setText("Joe Hunter");
        lookup("#username").queryTextInputControl().setText("JoeHunter");
        lookup("#email").queryTextInputControl().setText("hunter@gmail.com");
        lookup("#password0").queryTextInputControl().setText("test123");
        clickOn("Save changes");
        assertEquals(SceneSwitcher.getUser().getPassword(), "test123");
        assertEquals(SceneSwitcher.getUser().getName(), "Joe Hunter");
        assertEquals(SceneSwitcher.getUser().getUsername(), "JoeHunter");
        assertEquals(SceneSwitcher.getUser().getEmail(), "hunter@gmail.com");
    }

    @Test
    public void testWrongOldPassword() {
        fillFields("test321");
        clickOn("Save changes");
        Text errorMessage = lookup("#errorMessage").queryText();
        assertEquals(errorMessage.getText(), "The old password is wrong");
    }

    @Test
    public void testPasswordTooShort() {
        clickOn("#name");
        writeSeparator("\t", "T", "test", "test123", "t", "t", "t@t.no");
        clickOn("Save changes");
        Text errorMessage = lookup("#errorMessage").query();
        assertEquals(errorMessage.getText(), "Password must be at least 4 characters");
    }

    @Test
    public void testUsernameTooShort() {
        clickOn("#name");
        writeSeparator("\t", "T", "t", "test123", "test", "test", "t@t.no");
        clickOn("Save changes");
        Text errorMessage = lookup("#errorMessage").query();
        assertEquals(errorMessage.getText(), "Username must be at least 4 characters");
    }

    @Test
    public void testPasswordIllegal() {
        clickOn("#name");
        writeSeparator("\t", "T", "test", "test123", "?æøå", "?æøå", "t@t.no");
        clickOn("Save changes");
        Text errorMessage = lookup("#errorMessage").query();
        assertEquals(errorMessage.getText(), "Pasword can only contain letters,"
                + " numbers, and the symbols _, @, # and !");
    }

    @Test
    public void testIllegalEmail1() {
        clickOn("#name");
        writeSeparator("\t", "test", "test", "test123", "test", "test", "test");
        clickOn("Save changes");
        Text errorMessage = lookup("#errorMessage").query();
        assertEquals(errorMessage.getText(), "Please enter a valid email");
    }

    @Test
    public void testIllegalEmail2() {
        clickOn("#name");
        writeSeparator("\t", "test", "test", "test123", "test", "test", "te@st");
        clickOn("Save changes");
        Text errorMessage = lookup("#errorMessage").query();
        assertEquals(errorMessage.getText(), "Please enter a valid email");
    }

    @Test
    public void testReturn() {
        clickOn("Return");
        checkOnScene("LØ", "FT");
    }

    @Test
    public void testReturnWithoutSavingChanges() {
        lookup("#password1").queryTextInputControl().setText("test123");
        clickOn("Return");
        Text errorMessage = lookup("#errorMessage").queryText();
        assertEquals(errorMessage.getText(), "Save changes before returning");
    }

    @Test
    public void testLogOut() {
        clickOn("Log out");
        checkOnScene("Log in", "Or register new profile");
    }

    @Test
    public void testLogOutWithoutSavingChanges() {
        fillFields("info");
        clickOn("Log out");
        Text errorMessage = lookup("#errorMessage").queryText();
        assertEquals(errorMessage.getText(), "Save changes before logging out");
    }

    @Test
    public void testSaveChanges() {
        lookup("#name").queryTextInputControl().setText("John Doe");
        lookup("#username").queryTextInputControl().setText("JohnDoe");
        lookup("#password0").queryTextInputControl().setText("test123");
        lookup("#password1").queryTextInputControl().setText("JohnDoe1");
        lookup("#password2").queryTextInputControl().setText("JohnDoe1");
        lookup("#email").queryTextInputControl().setText("johnDoe@gmail.com");
        clickOn("Save changes");

        assertEquals("John Doe", SceneSwitcher.getUser().getName());
        assertEquals("JohnDoe", SceneSwitcher.getUser().getUsername());
        assertEquals("JohnDoe1", SceneSwitcher.getUser().getPassword());
        assertEquals("johnDoe@gmail.com", SceneSwitcher.getUser().getEmail());
    }

    private static void deleteTestfile() {
        try {
            if ((new File(testFileLocation)).exists()) {
                Files.delete(Path.of(testFileLocation));
            }
        } catch (IOException e) {
            System.err.println("Error deleting file");
        }
    }

    /**
     * Checks if the given text nodes are present in the scene.
     *
     * @param textNodesToBePresent The text nodes to be checked.
     */
    private void checkOnScene(String... textNodesToBePresent) {
        ObservableList<Node> rootChildren = root.getChildrenUnmodifiable();
        assertTrue(rootChildren.size() == 1, "There should only be one child of the root");
        assertTrue(rootChildren.get(0).getId().equals("baseAnchor"),
                "The child should be the baseAnchor");

        for (String s : textNodesToBePresent) {
            assertTrue(((Parent) rootChildren.get(0))
                    .getChildrenUnmodifiable()
                    .stream()
                    .anyMatch(x -> x instanceof Text
                            && ((Text) x).getText().equals(s)),
                    "The text \"" + s + "\" should be present");
        }
    }

    private void fillFields(String text) {
        for (String field : fields) {
            lookup(field).queryTextInputControl().setText(text);
        }
    }

    private void writeSeparator(String separator, String... strings) {
        for (int i = 0; i < strings.length; i++) {
            write(strings[i]);
            if (i < strings.length - 1) {
                write(separator);
            }
        }
    }
}
