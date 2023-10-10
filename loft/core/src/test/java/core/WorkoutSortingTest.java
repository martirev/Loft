package core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class contains JUnit tests for the WorkoutSorting class. It tests the
 * functionality of the methods in the WorkoutSorting class, which sorts and
 * retrieves data from a list of Workout objects.
 */
public class WorkoutSortingTest {

    private Workout workout1;
    private Workout workout2;
    private Exercise exercise1;
    private Exercise exercise2;
    private Exercise exercise3;
    private Exercise exercise4;
    private List<Workout> workouts = new ArrayList<Workout>();

    /**
     * Sets up needed stuff before each test method runs.
     */
    @BeforeEach
    public void setUp() {
        workout1 = new Workout();
        workout2 = new Workout(LocalDate.of(2019, 1, 1));
        exercise1 = new Exercise("Bench Press");
        exercise2 = new Exercise("Squats");
        exercise3 = new Exercise("Bench Press");
        exercise4 = new Exercise("Squats");

        final Set benchSet1 = new Set(10, 150);
        final Set benchSet2 = new Set(8, 130);
        final Set benchSet3 = new Set(6, 110);
        final Set benchSet4 = new Set(4, 90);
        exercise1.addSet(benchSet1);
        exercise1.addSet(benchSet2);
        exercise1.addSet(benchSet3);
        exercise1.addSet(benchSet4);

        final Set squatSet1 = new Set(10, 200);
        final Set squatSet2 = new Set(8, 180);
        final Set squatSet3 = new Set(6, 160);
        final Set squatSet4 = new Set(4, 140);
        exercise2.addSet(squatSet1);
        exercise2.addSet(squatSet2);
        exercise2.addSet(squatSet3);
        exercise2.addSet(squatSet4);

        workout1.addExercise(exercise1);
        workout1.addExercise(exercise2);

        final Set benchSet5 = new Set(4, 130);
        final Set benchSet6 = new Set(4, 120);
        exercise3.addSet(benchSet5);
        exercise3.addSet(benchSet6);

        final Set squatSet5 = new Set(5, 170);
        final Set squatSet6 = new Set(5, 180);
        exercise4.addSet(squatSet5);
        exercise4.addSet(squatSet6);

        workout2.addExercise(exercise3);
        workout2.addExercise(exercise4);

        workouts.add(workout1);
        workouts.add(workout2);
    }

    @Test
    public void testGetMostRecentWorkouts() {
        WorkoutSorting workoutSorting = new WorkoutSorting(workouts);
        List<Workout> mostRecentWorkouts = workoutSorting.getMostRecentWorkouts();

        assertEquals(2, mostRecentWorkouts.size());
        assertEquals(workout1, mostRecentWorkouts.get(0));
        assertEquals(workout2, mostRecentWorkouts.get(1));
    }

    @Test
    public void testGetSameExersices() {
        WorkoutSorting workoutSorting = new WorkoutSorting(workouts);
        assertEquals(2, workoutSorting.getSameExersices().size());
        assertTrue(workoutSorting.getSameExersices().containsKey("Bench Press"));
        assertTrue(workoutSorting.getSameExersices().containsKey("Squats"));

        Map<String, List<Exercise>> sameExercises = workoutSorting.getSameExersices();
        for (String name : sameExercises.keySet()) {
            for (Exercise exercise : sameExercises.get(name)) {
                assertTrue(exercise.getName().equals(name));
            }
        }
    }

    @Test
    public void testGetExercisesPr() {
        WorkoutSorting workoutSorting = new WorkoutSorting(workouts);
        assertEquals(150, workoutSorting.getExercisesPr("Bench Press"));
        assertEquals(200, workoutSorting.getExercisesPr("Squats"));
    }

    @Test
    public void testGetSameExercisesString() {
        WorkoutSorting workoutSorting = new WorkoutSorting(workouts);
        List<Exercise> exercises = workoutSorting.getSameExersices("Bench Press");
        assertEquals(2, exercises.size());
        assertTrue(exercises.contains(exercise1));
        assertTrue(exercises.contains(exercise3));
        assertFalse(exercises.contains(exercise2));
    }

    @Test
    public void testGetPrOnDay() {
        WorkoutSorting workoutSorting = new WorkoutSorting(workouts);
        assertEquals(150, workoutSorting.getPrOnDay(exercise1, workout1.getDate()));
        assertEquals(130, workoutSorting.getPrOnDay(exercise3, workout2.getDate()));
    }

    @Test
    public void testGetTotalWeightOnDay() {
        WorkoutSorting workoutSorting = new WorkoutSorting(workouts);
        assertEquals(2750, workoutSorting.getTotalWeightOnDay(workout2.getDate()));
    }

    @Test
    public void testGetUniqueDates() {
        WorkoutSorting workoutSorting = new WorkoutSorting(workouts);
        List<LocalDate> list = new ArrayList<>();
        list.add(workout2.getDate());
        list.add(workout1.getDate());

        List<LocalDate> unique = workoutSorting.getUniqueDates();
        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), unique.get(i));
        }
    }

    @Test
    public void getWeightPerDay() {
        WorkoutSorting workoutSorting = new WorkoutSorting(workouts);
        HashMap<LocalDate, Integer> hashmap = new HashMap<>();
        hashmap.put(workout1.getDate(), workout1.getTotalWeight());
        hashmap.put(workout2.getDate(), workout2.getTotalWeight());
        assertEquals(hashmap, workoutSorting.getWeightPerDay());
    }
}