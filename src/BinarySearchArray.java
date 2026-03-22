import java.util.ArrayList;
import java.util.List;

/*
 * BinarySearchArray
 * -----------------
 * This class stores unique keys inside one sorted 1D array.
 *
 * Because the array is always sorted:
 * - search can use binary search
 * - insert must keep the order by shifting elements right
 * - delete must keep the order by shifting elements left
 *
 * This is simpler than a tree because there are no nodes and no child links.
 * The whole structure is just:
 * - one array
 * - one size variable
 */
public class BinarySearchArray {

    /*
     * data:
     * the array that stores the keys in sorted order.
     *
     * size:
     * how many positions currently contain valid keys.
     * Valid keys live only in data[0] ... data[size - 1].
     */
    private final int[] data;
    private int size;

    /*
     * Measurements required by the assignment.
     *
     * time:
     * execution time of the last operation.
     *
     * comparisons:
     * comparison-style work of the last operation.
     *
     * levels:
     * for this structure, we count how many array cells we accessed.
     */
    public long time;
    public int comparisons;
    public int levels;

    /*
     * Creates an empty sorted array structure with fixed capacity.
     *
     * This version is intentionally simple:
     * the array does not grow automatically.
     * So the caller should create it with enough space for the experiment.
     */
    public BinarySearchArray(int capacity) {
        if (capacity < 1) {
            capacity = 1;
        }

        this.data = new int[capacity];
        this.size = 0;
    }

    /*
     * binarySearchPosition(key)
     * -------------------------
     * Binary search on the used part of the sorted array.
     *
     * Returns:
     * - index >= 0 if the key exists
     * - otherwise -(insertionPoint + 1)
     *
     * This is useful because the same helper can support:
     * - search
     * - insert
     * - delete
     */
    private int binarySearchPosition(int key) {
        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = (low + high) / 2;

            this.levels++;

            this.comparisons++;
            if (data[mid] == key) {
                return mid;
            }

            this.comparisons++;
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
     * after finding the first possible value in range,
     * we can scan forward until values become too large.
     */
    private int lowerBound(int key) {
        int low = 0;
        int high = size;

        while (low < high) {
            int mid = (low + high) / 2;

            this.levels++;
            this.comparisons++;
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
     * Keeps the array sorted after insertion.
     *
     * Steps:
     * 1. Search for the key with binary search.
     * 2. If it already exists, do nothing.
     * 3. If the array is full, do nothing.
     * 4. Shift larger elements one step right.
     * 5. Put the key in the free position.
     */
    public void insert(int key) {
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        int position = binarySearchPosition(key);
        if (position >= 0) {
            this.time = System.nanoTime() - startTime;
            return;
        }

        if (this.size == this.data.length) {
            this.time = System.nanoTime() - startTime;
            return;
        }

        int insertionPoint = -(position + 1);

        for (int i = this.size; i > insertionPoint; i--) {
            this.data[i] = this.data[i - 1];
            this.levels++;
        }

        this.data[insertionPoint] = key;
        this.levels++;
        this.size++;

        this.time = System.nanoTime() - startTime;
    }

    /*
     * search(key)
     * -----------
     * Binary search because the array is sorted.
     *
     * Returns the key if found, otherwise -1.
     */
    public int search(int key) {
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        int position = binarySearchPosition(key);
        this.time = System.nanoTime() - startTime;

        if (position >= 0) {
            return this.data[position];
        }

        return -1;
    }

    /*
     * delete(key)
     * -----------
     * Deletes the key if it exists.
     *
     * Steps:
     * 1. Find the key with binary search.
     * 2. If it does not exist, return false.
     * 3. Shift later elements one step left.
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

        for (int i = position; i < this.size - 1; i++) {
            this.data[i] = this.data[i + 1];
            this.levels++;
        }

        this.data[this.size - 1] = 0;
        this.size--;

        this.time = System.nanoTime() - startTime;
        return true;
    }

    /*
     * rangeSearch(low, high)
     * ----------------------
     * Returns all keys x such that:
     * low <= x <= high
     *
     * Because the array is sorted:
     * - first find the first possible position with lowerBound(low)
     * - then move right until values become bigger than high
     */
    public List<Integer> rangeSearch(int low, int high) {
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        List<Integer> result = new ArrayList<>();
        int index = lowerBound(low);

        while (index < this.size) {
            this.levels++;
            this.comparisons++;
            if (this.data[index] > high) {
                break;
            }

            result.add(this.data[index]);
            index++;
        }

        this.time = System.nanoTime() - startTime;
        return result;
    }

    /*
     * printName()
     * -----------
     * Helper required by the assignment.
     */
    public void printName() {
        System.out.println("Binary Search");
    }
}
