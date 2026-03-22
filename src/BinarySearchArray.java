import java.util.ArrayList;
import java.util.List;

/*
 * BinarySearchArray
 * -----------------
 * This class implements the third structure from the assignment:
 * a one-dimensional sorted array of unique keys.
 */
public class BinarySearchArray implements SearchStructure {

    /*
     * @param data: the actual array that stores the keys.
     * @param size: how many positions currently hold valid keys.
     */
    private final int[] data;
    private int size;

    // Metrics to compare
    public long time;
    public int comparisons;
    public int levels;

    public BinarySearchArray(int capacity) {
        if (capacity < 1) {
            capacity = 1;
        }

        this.data = new int[capacity];
        this.size = 0;
    }

    /*
     * @func binarySearchPosition
     * This helper performs standard binary search on the used part of the array.
     * Returns index >= 0 if the key exists, otherwise -(insertionPoint + 1)
     */
    private int binarySearchPosition(int key) {
        int low = 0;
        int high = this.size - 1;

        while (low <= high) {
            
            // mid is the middle position of the current search interval.
            int mid = (low + high) / 2;

            this.levels++;

            // First we see if this is the exact key we want
            this.comparisons++;
            if (this.data[mid] == key) {
                return mid;
            }

            
            // Then we decide whether to continue left or right.
            this.comparisons++;
            if (this.data[mid] < key) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        
         // If we exit the loop, the key was not found.
         // low is now the correct insertion point.
        return -(low + 1);
    }

    /*
     * @func lowerBound
     * Returns the first index whose value is >= key.
     * It is useful for range search.
     */
    private int lowerBound(int key) {
        int low = 0;
        int high = this.size;

        while (low < high) {
            int mid = (low + high) / 2;

            this.levels++;

            this.comparisons++;
            if (this.data[mid] < key) {
                
                // If mid is still smaller than the target,
                // the first valid position must be to the right.
                
                low = mid + 1;
            } else {
                
                // mid might already be the answer,
                // so we keep it in the search interval.
                high = mid;
            }
        }

        return low;
    }

    /*
     * @func insert
     * Inserts a key while keeping the array sorted.
     */
    public void insert(int key) {
        long startTime = System.nanoTime();

        // reset metrics
        this.comparisons = 0;
        this.levels = 0;

        int position = binarySearchPosition(key);

        // If position >= 0, the key already exists.
        if (position >= 0) {
            this.time = System.nanoTime() - startTime;
            return;
        }

        
        // If size == data.length, there is no free space left.
        // So we just stop.
        if (this.size == this.data.length) {
            this.time = System.nanoTime() - startTime;
            return;
        }

        // Recover the true insertion point from the negative return value.
        int insertionPoint = -(position + 1);

        
        // Shift everything larger than the new key one position to the right.
        // We start from the end and move backward so no value gets overwritten
        // before it is copied.
        for (int i = this.size; i > insertionPoint; i--) {
            this.data[i] = this.data[i - 1];
            this.levels++;
        }

        
        // Now the correct slot is free, so we store the new key there.
        this.data[insertionPoint] = key;
        this.levels++;
        this.size++;

        this.time = System.nanoTime() - startTime;
    }

    /*
     * @func search
     * Searches for a key using binary search.
     * Returns the key if found, otherwise -1
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
     * @func delete
     * Deletes the key if it exists.
     */
    public boolean delete(int key) {
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        int position = binarySearchPosition(key);

        // If the key does not exist, deletion fails.
        if (position < 0) {
            this.time = System.nanoTime() - startTime;
            return false;
        }

        
        // Shift all elements after the deleted key one step to the left.
        for (int i = position; i < this.size - 1; i++) {
            this.data[i] = this.data[i + 1];
            this.levels++;
        }

        // Optional cleanup of the last used cell.
        this.data[this.size - 1] = 0;
        this.size--;

        this.time = System.nanoTime() - startTime;
        return true;
    }

    /* @func rangeSearch
    * Performs a range search.
    * Returns a list of keys within the range [low, high].
    */
    public List<Integer> rangeSearch(int low, int high) {
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        List<Integer> result = new ArrayList<>();

        
        // Jump directly to the first candidate that could belong in the range.
        
        int index = lowerBound(low);

        while (index < this.size) {
            this.levels++;
            this.comparisons++;

            // Once we pass high, we can stop immediately.
            
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
     * @func printName
     * Just prints the name of this structure.
     */
    public void printName() {
        System.out.println("Binary Search");
    }
}
