import java.util.ArrayList;
import java.util.List;

/*
 * @class BSTarray
 * This class implements a Binary Search Tree (BST), but instead of using
 * node objects with pointers/references, it stores the whole tree inside
 * one fixed-size 2D array.
 *
 *
 * The source of implementation was the DynamicBST class, where 
 * appropriate changes were made.
 * 
 *
 * tree[0][i] -> key stored at logical node i
 * tree[1][i] -> index of the left child of node i
 * tree[2][i] -> index of the right child of node i
 *
 */
public class BSTarray implements SearchStructure {

    /* Constants we use instead of raw numbers 0, 1, 2
    * for key, left, and right
    */
    private static final int KEY = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;


    private final int[][] tree;
    private int root;
    private int avail;

    // Metrics to compare
    public long time;
    public int comparisons;
    public int levels;

    
    public BSTarray(int k) {
        
        this.tree = new int[3][k];
        this.root = -1; // We start with an empty tree

        /* avail points to the first free position in the array.
         * if k = 0, there is no space at all so avail = -1.
         * if k > 0, the first free slot is 0.
         */
        this.avail = (k == 0) ? -1 : 0;

        for (int i = 0; i < k; i++) {
            
            // key is initialized to 0 just as a default value.
            tree[KEY][i] = 0;

            
            // left starts as -1 because free rows do not have children.
            tree[LEFT][i] = -1;

            //right is used here to build the free-list.
            if (i == k - 1) {
                tree[RIGHT][i] = -1;
}           else {
                tree[RIGHT][i] = i + 1;
            }

        }
    }

    /*
     * @func getNode
     * Reserves one free array position so it can be used as a new BST node.
     * This is the array-version of "allocate memory for a new node".
     */
    private int getNode() {
        if (avail == -1) {
            return -1;
        }

        
        // freeRow becomes the slot we are taking from the free-list.
        int freeRow = avail;

        // We move avail to the next free slot.
        avail = tree[RIGHT][freeRow];

        // left and right children start as empty thus -1.
        tree[LEFT][freeRow] = -1;
        tree[RIGHT][freeRow] = -1;

        return freeRow;
    }

    /*
     * @func freeNode
     * Returns a used node position back to the free-list.
     * This is the array-version of "deallocate the deleted node".
     */
    private void freeNode(int line) {
        tree[KEY][line] = 0;
        tree[LEFT][line] = -1;
        tree[RIGHT][line] = avail;
        avail = line;
    }

    /*
     * @func insert
     * Inserts a new key into the BST if the key is not already present.
     */
    public void insert(int key) {

        // These variables are for the measurements asked in this assignment
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        // If the tree is empty, we create a new root and stop
        if (root == -1) {
            int newRow = getNode();
            if (newRow == -1) {
                // capacity is zero so we cannot proceed
                this.time = System.nanoTime() - startTime;
                return;
            }

            tree[KEY][newRow] = key;
            root = newRow;

            this.levels++;
            this.time = System.nanoTime() - startTime;
            return;
        }

        
        // Start from the root and walk down until we find the position
        // where the new key belongs.
        int current = root;
        int parent = -1;

        // Traverse the tree like a standard BST.
        while (current != -1) {
            this.levels++;
            parent = current;

            // Check whether the key already exists.
            // If it does, we do nothing(return).
            this.comparisons++;
            if (key == tree[KEY][current]) {
                this.time = System.nanoTime() - startTime;
                return;
            }

            
            // If the new key is smaller, we go to the left child.
            // Otherwise we go to the right child.
            this.comparisons++;
            if (key < tree[KEY][current]) {
                current = tree[LEFT][current];
            } else {
                current = tree[RIGHT][current];
            }
        }

        
        // If we exited the loop, current became -1.
        // That means we found the correct empty position
        int newRow = getNode();
        if (newRow == -1) {
            
            // The array is full, so we cannot insert any more keys.
            this.time = System.nanoTime() - startTime;
            return;
        }

        tree[KEY][newRow] = key;

        // Now link the new node to its parent.
        this.comparisons++;
        if (key < tree[KEY][parent]) {
            tree[LEFT][parent] = newRow;
        } else {
            tree[RIGHT][parent] = newRow;
        }

        this.time = System.nanoTime() - startTime;
    }

    /*
     * @func search
     * @param key is the unique number of each node
     * Searches for a key in the BST.
     * Returns the key itself if found, or -1 if not found
     */
    public int search(int key) {

        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        // current points to the node we are currently checking.
        int current = root;

        while (current != -1) {
            
            // We visited one more node while following the search path.
            this.levels++;
            this.comparisons++;
            // If we find the key, we just return it
            if (key == tree[KEY][current]) {
                this.time = System.nanoTime() - startTime;
                return tree[KEY][current];
            }

            // Then we go down the tree and choose sides
            this.comparisons++;
            if (key < tree[KEY][current]) {
                current = tree[LEFT][current];
            } else {
                current = tree[RIGHT][current];
            }
        }

        
        // We fell off the tree, so the key does not exist.
        this.time = System.nanoTime() - startTime;
        return -1;
    }

    /*
     * delete(key)
     * @param key is the unique number of each node
     * Deletes a key from the tree
     * Returns true if found, else false.
     */
    public boolean delete(int key) {
        
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        /*
         * current will move through the tree searching for the key.
         * parent trails one step behind current.
         */
        int current = root;
        int parent = -1;

        // Firstly, search for the node and its parent
        while (current != -1) {
            this.levels++;

            this.comparisons++;
            if (key == tree[KEY][current]) {
                break;
            }

            parent = current;

            this.comparisons++;
            if (key < tree[KEY][current]) {
                current = tree[LEFT][current];
            } else {
                current = tree[RIGHT][current];
            }
        }

        
        // If we reached -1, the key does not exist in the tree.
        if (current == -1) {
            this.time = System.nanoTime() - startTime;
            return false;
        }

        // If the node has a maximum of one child, so 0 or 1
        if (tree[LEFT][current] == -1 || tree[RIGHT][current] == -1) {
            int child = (tree[LEFT][current] != -1) ? tree[LEFT][current] : tree[RIGHT][current];

            // If we delete the root
            if (parent == -1) {
                root = child;
            } else if (tree[LEFT][parent] == current) {
                tree[LEFT][parent] = child;
            } else {
                tree[RIGHT][parent] = child;
            }

            // The old slot is not used, we recycle it through the free-list
            freeNode(current);
            this.time = System.nanoTime() - startTime;
            return true;
        }

        // If the node has 2 children
        int successorParent = current;
        int successor = tree[RIGHT][current];

        this.levels++;

        // We find the in-order successor 
        // (the leftmost one in the right subtree)
        while (tree[LEFT][successor] != -1) {
            successorParent = successor;
            successor = tree[LEFT][successor];
            this.levels++;
        }

        // We replace the value of the current node with the successor
        tree[KEY][current] = tree[KEY][successor];

        // We delete the successor who now has a maximum of 1 child
        int successorChild = tree[RIGHT][successor];
        if (successorParent == current) {
            tree[RIGHT][successorParent] = successorChild;
        } else {
            tree[LEFT][successorParent] = successorChild;
        }

        
        // Recycle the successor's old slot.
        freeNode(successor);
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

        // we will be using another function to help us do everything
        List<Integer> result = new ArrayList<>();
        findInRange(root, low, high, result);

        this.time = System.nanoTime() - startTime;
        return result;
    }

    /* @func findInRange
    * This function is actually doing the search for rangeSearch.
    * Starts from an empty node, 
    * and goes to every possible subtree to find all 
    * the keys between the values of low and high
    */
    private void findInRange(int nodeIndex, int low, int high, List<Integer> result) {
        
        /* We start on an empty node (this time with a nodeIndex)
        * If true, we return nothing
        * If false, we start the proccess to look for the keys
        */
        if (nodeIndex == -1) {
            return;
        }

        this.levels++;
        this.comparisons++;
        
        /* If the key is higher than the value we set as low,
        * there might be keys to look for 
        * on the left subtree
        */
        if (tree[KEY][nodeIndex] > low) {
            findInRange(tree[LEFT][nodeIndex], low, high, result);
        }

        /* Checking if the current node is between the values we set
        * If true, we add it to the result list
        */
        this.comparisons += 2;
        if (tree[KEY][nodeIndex] >= low && tree[KEY][nodeIndex] <= high) {
            result.add(tree[KEY][nodeIndex]);
        }

        this.comparisons++;
        /* If the key is lower than the value we set as high,
        * there might be keys to look for 
        * on the right subtree
        */
        if (tree[KEY][nodeIndex] < high) {
            /*
             * Only then can the right subtree still contain values inside the range.
             */
            findInRange(tree[RIGHT][nodeIndex], low, high, result);
        }
    }

    /* @func inorder
    * This function just prints the keys
    * in ascending order
    */
    public void inorder() {
        // We use a StringBuilder in order to help us
        // print the keys
        StringBuilder builder = new StringBuilder();
        buildInorder(root, builder);
        System.out.println(builder.toString().trim());
    }

    /*
     * @func buildInorder
     * Basic helper for the inorder() function.
     * Implementation is basically the same as the inorder function
     * on the DynamicBST class.
     */
    private void buildInorder(int nodeIndex, StringBuilder builder) {
        
        // An empty subtree contributes nothing to the output 
        if (nodeIndex == -1) {
            return;
        }

        // First print all smaller keys,
        buildInorder(tree[LEFT][nodeIndex], builder);

        builder.append(tree[KEY][nodeIndex]).append(' ');

        // then all larger keys.
        buildInorder(tree[RIGHT][nodeIndex], builder);
    }

    /*
     * @func printName
     * It prints the name of this data structure implementation.
     */
    public void printName() {
        System.out.println("BST Array");
    }
}
