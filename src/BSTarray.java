/* This class is almost identical to the DynamicBST one, but a little more special
* In order to make the array BST we have to keep in mind some differences between 
* dynamic memory allocation and arrays
*/




public class BSTarray {

    private int[][] tree;   
    private int root;
    private int avail;
    private int capacity;

    // Metrics for the measurements we are asked to do
    public long time;
    public int comparisons;
    
    
    public BSTarray(int k) {
        this.tree = new int[3][k];
        this.root = -1; // Empty tree
        this.avail = 0; // The first available position is the first one 
        this.capacity = k;

        /* The free list, which was said in the assignment.
        * Every position shows to the next available.
        * Basically, if the position is taken, go to the next one.
        */
       /* Χρησιμοποιούμε τη γραμμή 1 (που κανονικά είναι για τα αριστερά παιδιά) απλώς ως προσωρινή αποθήκη 
       * για να κρατάμε τη λίστα των ελευθέρων θέσεων. Μόλις μια θέση πιαστεί από την insert, το tree[1][i] θα καθαριστεί και θα πάρει την πραγματική του τιμή 
       * (το index του αριστερού παιδιού ή -1). */
        for (int i = 0; i < k - 1; i++) {
            tree[1][i] = i + 1; 
        }
        tree[1][k - 1] = -1;    // This is basically the end of free positions in the array.
    }
    

    /* @func getNode
    * This function has a sole purpose to grab an empty row from the free list
    */
    private int getNode(){
        // No free positions found. Return
        if (avail == -1){
            return -1;
        }

        int freePos = avail;
        avail = tree[1][avail]; // avail goes to the next avail (if it even makes sense)

        tree[1][freePos] = -1;   // Clearing out the left child
        tree[2][freePos] = -1;  // Same for the right child as well

        return freePos;

    }
    

    /* @func freeNode
    * Function to use in order to deallocate the memory
    * used for a node that was deleted.
    * @param line: The index of the row in the 2D array to be freed.
    */
    private void freeNode(int line){
        // We link the free row to the top of the free list
        tree[1][line] = avail;

        // Then we update the top of the free list and point it to this row
        avail = line;
    }
}
