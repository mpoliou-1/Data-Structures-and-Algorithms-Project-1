import java.util.Random;

/*
 * @class RandomDataGenerator
 * Small helper class for the experiment phase of the assignment.
 */
public class RandomDataGenerator {

    // We create a single random object, since it will be enought for the whole class
    private final Random random;

    public RandomDataGenerator() {
        this.random = new Random();
    }

    /*
     * @func generateUniqueRandoms
     * Generates 'count' unique integers in the inclusive interval [min, max].
     * Each possible value in [min, max] gets one position in the used array
     * If it reappears, we skip it.
     */
    public int[] generateUniqueRandoms(int count, int min, int max) {
        validateArguments(count, min, max);

        int rangeSize = max - min + 1;
        if (count > rangeSize) {
            throw new IllegalArgumentException("Cannot generate more unique numbers than the interval contains.");
        }

        int[] result = new int[count];
        boolean[] used = new boolean[rangeSize];
        int index = 0;

        
        // Keep generating values until we have enough different ones.
        while (index < count) {
            int candidate = random.nextInt(max - min + 1) + min;
            int usedIndex = candidate - min;

            
            // used[x] corresponds to the value (min + x).
            // If it is false, this value has not appeared before.
            if (!used[usedIndex]) {
                used[usedIndex] = true;
                result[index] = candidate;
                index++;
            }
        }

        return result;
    }

    /*
     * @func generateRandoms
     * Generates 'count' random integers in the inclusive interval [min, max].
     */
    public int[] generateRandoms(int count, int min, int max) {
        validateArguments(count, min, max);

        // We declare the array to be the same size as the number count.
        int[] result = new int[count];

        // Then we add the random generated numbers to the array.
        for (int i = 0; i < count; i++) {
            result[i] = random.nextInt(max - min + 1) + min;
        }

        return result;
    }

    /*
     * @func generateSortedRange
     * Generates two random numbers in [min, max] and returns them sorted.
     */
    public int[] generateSortedRange(int min, int max) {
        validateArguments(1, min, max);

        // We firstly generate two random numbers in [min, max]
        int first = random.nextInt(max - min + 1) + min;
        int second = random.nextInt(max - min + 1) + min;

        // Then we just compare the two numbers and return them
        if (first <= second) {
            return new int[] {first, second};
        }

        return new int[] {second, first};
    }

    /*
     * @func validateArguments
     * Function to check that everything is appropriate .
     * Throws exception if not.
     */
    private void validateArguments(int count, int min, int max) {

        // First check: count is a negative
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative.");
        }

        // Second check: min is greater than max
        if (min > max) {
            throw new IllegalArgumentException("min cannot be greater than max.");
        }
    }
}
