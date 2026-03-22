import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * BinarySearchArray
 * -----------------
 * This class implements the third structure from the assignment:
 * a one-dimensional sorted array of unique integer keys.
 *
 * Main idea:
 * - the array is always kept sorted
 * - search is done with binary search
 * - insert first finds the correct position, then shifts elements right
 * - delete first finds the key, then shifts elements left
 * - range search finds the first valid key and then scans forward
 *
 * This structure is different from the two BSTs:
 * - it does not have left/right children
 * - it does not need inorder(), because the array is already sorted
 *
 * Tradeoff:
 * - search is fast: O(log n)
 * - insert/delete are slower: O(n), because shifting may be needed
 */
public class BinarySearchArray {

    /*
     * A small default capacity so the class can be created even if
     * no initial size is provided.
     */
    private static final int DEFAULT_CAPACITY = 16;

    /*
     * data:
     * the sorted array that holds the keys.
     *
     * size:
     * how many positions are currently used.
     * Only data[0] to data[size - 1] contain valid keys.
     *
     * time, comparisons, levels:
     * measurements required by the assignment.
     *
     * For this structure, "levels" are interpreted as how many array cells
     * we accessed while completing an operation.
     */
    private int[] data;
    private int size;

    public long time;
    public int comparisons;
    public int levels;

    /*
     * Creates an empty structure with a default capacity.
     */
    public BinarySearchArray() {
        this(DEFAULT_CAPACITY);
    }

    /*
     * Creates an empty structure with a chosen initial capacity.
     * This is useful for experiments, because a larger initial capacity
     * reduces the chance of resizing during measurements.
     */
    public BinarySearchArray(int initialCapacity) {
        int safeCapacity = Math.max(1, initialCapacity);
        this.data = new int[safeCapacity];
        this.size = 0;
    }

    /*
     * ensureCapacity(minCapacity)
     * ---------------------------
     * If the backing array is too small, create a larger one.
     *
     * This does not change the logical structure of the sorted array.
     * It only gives us more physical space to store future keys.
     */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= data.length) {
            return;
        }

        int newCapacity = Math.max(data.length * 2, minCapacity);
        data = Arrays.copyOf(data, newCapacity);
    }

    /*
     * binarySearchPosition(key)
     * -------------------------
     * Classic binary search over the used portion of the sorted array.
     *
     * Returns:
     * - index >= 0 if the key is found
     * - otherwise -(insertionPoint + 1)
     *
     * This is the same convention used by Java's Arrays.binarySearch().
     * It is convenient because one helper can serve search, insert, and delete.
     */
    private int binarySearchPosition(int key) {
        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            /*
             * We accessed one array cell: data[mid].
             */
            levels++;

            comparisons++;
            if (data[mid] == key) {
                return mid;
            }

            comparisons++;
            if (data[mid] < key) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -(low + 1);
    }

    /*
     * lowerBound(key)
     * ---------------
     * Returns the first index whose value is >= key.
     *
     * This is useful for range search:
     * once we find the first possible value in range, we can scan forward
     * until values become too large.
     */
    private int lowerBound(int key) {
        int low = 0;
        int high = size;

        while (low < high) {
            int mid = low + (high - low) / 2;

            levels++;
            comparisons++;
            if (data[mid] < key) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return low;
    }

    /*
     * insert(key)
     * -----------
     * Keeps the array sorted at all times.
     *
     * Steps:
     * 1. Use binary search to see if the key already exists.
     * 2. If it exists, do nothing.
     * 3. Otherwise find the insertion point.
     * 4. Shift all larger elements one position to the right.
     * 5. Store the key in the new gap.
     */
    public void insert(int key) {
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        int position = binarySearchPosition(key);
        if (position >= 0) {
            /*
             * The assignment wants unique keys, so duplicates are ignored.
             */
            this.time = System.nanoTime() - startTime;
            return;
        }

        int insertionPoint = -(position + 1);
        ensureCapacity(size + 1);

        /*
         * Shift every larger element one step to the right
         * to open space for the new key.
         */
        for (int i = size; i > insertionPoint; i--) {
            data[i] = data[i - 1];
            levels++;
        }

        data[insertionPoint] = key;
        levels++;
        size++;

        this.time = System.nanoTime() - startTime;
    }

    /*
     * search(key)
     * -----------
     * Uses binary search because the array is sorted.
     *
     * Returns:
     * - the key itself if found
     * - -1 if not found
     */
    public int search(int key) {
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        int position = binarySearchPosition(key);
        this.time = System.nanoTime() - startTime;

        if (position >= 0) {
            return data[position];
        }

        return -1;
    }

    /*
     * delete(key)
     * -----------
     * Deletes the key if it exists.
     *
     * Steps:
     * 1. Use binary search to locate the key.
     * 2. If it is not found, return false.
     * 3. Otherwise shift all later elements one step left.
     * 4. Decrease size.
     */
    public boolean delete(int key) {
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        int position = binarySearchPosition(key);
        if (position < 0) {
            this.time = System.nanoTime() - startTime;
            return false;
        }

        for (int i = position; i < size - 1; i++) {
            data[i] = data[i + 1];
            levels++;
        }

        if (size > 0) {
            data[size - 1] = 0;
        }
        size--;

        this.time = System.nanoTime() - startTime;
        return true;
    }

    /*
     * rangeSearch(low, high)
     * ----------------------
     * Returns all keys x such that:
     * low <= x <= high
     *
     * Since the array is sorted, we do not need to scan from the beginning.
     * We first find the first index whose value is >= low.
     * Then we move forward until values become larger than high.
     */
    public List<Integer> rangeSearch(int low, int high) {
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        List<Integer> result = new ArrayList<>();
        int index = lowerBound(low);

        while (index < size) {
            levels++;
            comparisons++;
            if (data[index] > high) {
                break;
            }

            result.add(data[index]);
            index++;
        }

        this.time = System.nanoTime() - startTime;
        return result;
    }

    /*
     * Small helper requested by the assignment.
     */
    public void printName() {
        System.out.println("Binary Search");
    }
}
