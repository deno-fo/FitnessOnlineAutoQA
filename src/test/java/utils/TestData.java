package utils;

public class TestData {

    public static final String PASSWORD =
            "qwerty";

    public static final String NAME =
            "Auto";

    public static final String SURNAME =
            "Test";

    public static final String REGISTERED_USER_EMAIL =
            "test16@sign.com";

    public static final String REGISTERED_USER_PASSWORD =
            "qwerty";

    public static final String INVALID_EMAIL =
            "invalid_email@sign.com";

    public static final String INVALID_PASSWORD =
            "wrongPassword";

    public static final String CUSTOM_WORKOUT_NAME =
            "Auto Workout";

    public static final String CUSTOM_WORKOUT_DAY_NAME =
            "Auto chest day";

    public static final String EXERCISE_NAME =
            "30-degree incline dumbbell bench press";

    public static final String EXERCISE_SETS = "2";
    public static final String EXERCISE_REPEATS = "10";
    public static final String EXERCISE_WEIGHT = "20";

    public static final String UPDATED_WORKOUT_DAY_NAME =
            "Updated Chest Day";
    public static final String UPDATED_EXERCISE_SETS =
            "3";
    public static final String UPDATED_EXERCISE_REPEATS =
            "12";
    public static final String UPDATED_EXERCISE_WEIGHT =
            "25";

    public static String uniqueEmail() {
        return "autotest_"
                + System.currentTimeMillis()
                + "@sign.com";
    }

    public static String uniqueFeedPostText() {
        return "Auto feed post "
                + System.currentTimeMillis();
    }

    public static String uniqueFeedCommentText() {
        return "Auto feed comment "
                + System.currentTimeMillis();
    }

    private TestData() {
    }
}
