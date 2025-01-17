package ui.controllers;

import core.Exercise;
import core.Set;
import core.User;
import core.Workout;
import core.WorkoutSorting;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

/**
 * The JournalScreenController class is responsible for controlling the Journal
 * Screen of the application. It extends the SceneSwitcher class and initializes
 * the user's workout history. It loads the most recent workouts and displays
 * them in the exercises list view. It also loads the selected workout and
 * displays its exercises and sets in the workout list view.
 */
public class JournalScreenController extends SceneSwitcher {

    @FXML
    private ListView<Workout> workoutHistoryListView;

    @FXML
    private ListView<TextArea> workoutListView;

    private WorkoutSorting workoutSorting;

    /**
     * Initializes the controller class. The user of the apliication is loaded in
     * and the users workouts are loaded in.
     *
     * @param location  Location for resolving relative paths for the root object,
     *                  or null if the location is not known.
     * @param resources Used to localuze the root of the object, or null if the root
     *                  object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User prev = getUser();
        User user = loftAccess.getUser(prev.getUsername(), prev.getPassword());
        workoutSorting = new WorkoutSorting(user.getWorkouts());
        workoutHistoryListView.setOnMouseClicked(event -> {
            workoutListView.getItems().clear();
            Workout selected = workoutHistoryListView.getSelectionModel().getSelectedItem();
            loadWorkout(selected);
        });
        loadWorkoutHistory();
    }

    /**
     * Loads the workout history by getting the most recent workouts and adding
     * them to the exercises list view.
     * Uses the workoutSorting object to get the most recent workouts.
     */
    private void loadWorkoutHistory() {
        List<Workout> workouts = workoutSorting.getMostRecentWorkouts();
        for (Workout workout : workouts) {
            workoutHistoryListView.getItems().add(workout);
        }
    }

    /**
     * Loads the given workout by iterating over its exercises and calling the
     * loadExercise method for each exercise.
     *
     * @param workout the workout to be loaded
     */
    private void loadWorkout(Workout workout) {
        if (workout == null) {
            return;
        }
        workout.getExercises().stream().forEach(this::loadExercise);
    }

    /**
     * Loads the given exercise into the workout list view, displaying the exercise
     * name and its sets.
     * Each set is displayed with its weight and number of reps.
     *
     * @param exercise the exercise to load into the workout list view
     */
    private void loadExercise(Exercise exercise) {
        List<Set> sets = exercise.getSets();
        TextArea elementArea = new TextArea();
        StringBuilder sb = new StringBuilder();
        sb.append(exercise);
        for (int i = 0; i < sets.size(); i++) {
            final Set set = sets.get(i);
            sb.append("\n\tSet " + (i + 1) + ": ");
            sb.append("Weight: " + set.getWeight() + " Reps: " + set.getReps());
        }

        elementArea.setPrefWidth(workoutListView.getWidth() - 40);
        elementArea.setPrefRowCount(1 + sets.size());
        elementArea.setEditable(false);
        elementArea.setText(sb.toString());

        workoutListView.getItems().add(elementArea);
    }

    /**
     * Switches to the home screen.
     */
    @FXML
    private void handleReturnPress() {
        insertPane("HomeScreen.fxml");
    }

    /**
     * Switches to the progress screen.
     */
    @FXML
    private void switchToProgress() {
        insertPane("ProgressScreen.fxml");
    }
}
