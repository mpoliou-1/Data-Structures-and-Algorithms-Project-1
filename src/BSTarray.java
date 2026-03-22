import java.util.ArrayList;
import java.util.List;

/*
 * BSTarray
 * --------
 * This class implements a Binary Search Tree (BST), but instead of using
 * node objects with pointers/references, it stores the whole tree inside
 * one fixed-size 2D array.
 *
 *
 * The source of implementation was the DynamicBST class, where a few 
 * changes were made.
 * 
 *
 * tree[0][i] -> key stored at logical node i
 * tree[1][i] -> index of the left child of node i
 * tree[2][i] -> index of the right child of node i
 *
 * So, conceptually, the assignment talks about "rows", but because we use
 * a 3 x K array, each node is really represented by one column index i.
 *
 * Example:
 * If root = 4, then the root node is stored in column 4.
 * The key is tree[0][4].
 * The left child index is tree[1][4].
 * The right child index is tree[2][4].
 *
 * Special value -1:
 * We use -1 everywhere to mean "nothing".
 * For example:
 * - root = -1 means the tree is empty
 * - left child = -1 means there is no left child
 * - right child = -1 means there is no right child
 * - avail = -1 means there is no free position left in the array
 *
 * Free-list idea:
 * This implementation has fixed capacity. That means we cannot create
 * unlimited new nodes like in the dynamic BST. Instead, we manually manage
 * which array positions are free.
 *
 * The variable avail stores the first free position.
 * For unused positions, tree[2][i] does NOT mean "right child".
 * Instead, it stores the next free position in the free-list.
 *
 * So tree[2] has two roles:
 * - for used nodes: it stores the right child
 * - for free nodes: it stores the next free slot
 */
public class BSTarray {

    // Constants we use instead of raw numbers 0, 1, 2
    private static final int key = 0;
    private static final int left = 1;
    private static final int right = 2;

    /*
     * tree:
     * the 3 x K array that stores the whole BST.
     *
     * capacity:
     * maximum number of nodes that can exist at the same time.
     *
     * In the current version, capacity is mostly descriptive/documentary.
     * The code detects whether the structure is full through avail == -1,
     * but we still keep capacity because it expresses an important truth
     * about this data structure: unlike the dynamic BST, its size limit
     * is fixed when the object is created.
     *
     * root:
     * index of the root node, or -1 if the tree is empty.
     *
     * avail:
     * head of the free-list. This tells us the first available array slot
     * that can be used for a new node.
     */
    private final int[][] tree;
    private final int capacity;
    private int root;
    private int avail;

    // Metrics to compare
    public long time;
    public int comparisons;
    public int levels;

    /*
     * Constructor
     * -----------
     * Creates an empty BST with capacity k.
     *
     * At the beginning:
     * - the tree is empty, so root = -1
     * - every position is free
     * - avail points to the first free position
     *
     * We initialize the free-list like this:
     * 0 -> 1 -> 2 -> 3 -> ... -> k-1 -> -1
     *
     * That means:
     * - avail = 0
     * - tree[right][0] = 1
     * - tree[right][1] = 2
     * ...
     * - tree[right][k-1] = -1
     *
     * If k = 0, the tree has no space at all, so avail = -1 immediately.
     */
    public BSTarray(int k) {
        /*
         * Allocate the 3 x K storage area.
         * Each of the 3 rows has length k.
         */
        this.tree = new int[3][k];
        this.capacity = k;
        this.root = -1;

        /*
         * If there is at least one slot, the first free slot is 0.
         * If k == 0, there are no free slots at all.
         */
        this.avail = (k == 0) ? -1 : 0;

        for (int i = 0; i < k; i++) {
            /*
             * key is initialized to 0 just as a default value.
             * It does not matter yet, because the row is not used.
             */
            tree[key][i] = 0;

            /*
             * left starts as -1 because free rows do not have children.
             */
            tree[left][i] = -1;

            /*
             * right is used here to build the free-list.
             * Each free slot points to the next free slot.
             * The last free slot points to -1.
             */
            tree[right][i] = (i == k - 1) ? -1 : i + 1;
        }
    }

    /*
     * @func getNode()
     * 
     * Reserves one free array position so it can be used as a new BST node.
     *
     * This is the array-version of "allocate memory for a new node".
     *
     * How it works:
     * 1. Look at avail. That is the first free position.
     * 2. Save it in freeRow.
     * 3. Move avail to the next free position.
     * 4. Clear the child links of freeRow.
     * 5. Return freeRow so the caller can use it as a real node.
     *
     * If avail == -1, the array is full and there is no free slot left.
     */
    private int getNode() {
        if (avail == -1) {
            return -1;
        }

        /*
         * freeRow becomes the slot we are taking from the free-list.
         */
        int freeRow = avail;

        /*
         * Move avail to the next free slot.
         * At this point, freeRow is no longer part of the free-list.
         */
        avail = tree[right][freeRow];

        /*
         * Now that freeRow is becoming a real BST node,
         * its left and right child references should start empty.
         */
        tree[left][freeRow] = -1;
        tree[right][freeRow] = -1;

        return freeRow;
    }

    /*
     * freeNode(line)
     * --------------
     * Returns a used node position back to the free-list.
     *
     * This is the array-version of "deallocate the deleted node".
     *
     * How it works:
     * 1. Clear the stored key and child fields.
     * 2. Make this slot point to the current head of the free-list.
     * 3. Update avail so that this slot becomes the new head.
     *
     * Because the free-list acts like a stack, the most recently freed
     * position becomes the first one reused later.
     */
    private void freeNode(int line) {
        tree[key][line] = 0;
        tree[left][line] = -1;
        tree[right][line] = avail;
        avail = line;
    }

    /*
     * insert(key)
     * -----------
     * Inserts a new key into the BST if the key is not already present.
     *
     * High-level BST logic:
     * - smaller keys go left
     * - larger keys go right
     * - equal keys are ignored, because the assignment wants unique keys
     *
     * Steps:
     * 1. If the tree is empty, create the root.
     * 2. Otherwise start from the root.
     * 3. Move left or right according to BST comparisons.
     * 4. Stop when we find an empty child position.
     * 5. Reserve a free slot with getNode().
     * 6. Store the key there and link it to its parent.
     */
    public void insert(int key) {
        /*
         * Start measuring this specific call of insert().
         */
        long startTime = System.nanoTime();

        /*
         * Reset the metrics so they describe only this operation,
         * not previous ones.
         */
        this.comparisons = 0;
        this.levels = 0;

        /*
         * Special case: the tree is empty, so the first inserted key
         * becomes the root.
         */
        if (root == -1) {
            int newRow = getNode();
            if (newRow == -1) {
                /*
                 * This would only happen if capacity is zero.
                 * In that case insertion cannot proceed.
                 */
                this.time = System.nanoTime() - startTime;
                return;
            }

            tree[key][newRow] = key;
            root = newRow;

            /*
             * We visited one logical level: the root position.
             */
            levels = 1;
            this.time = System.nanoTime() - startTime;
            return;
        }

        /*
         * Start from the root and walk downward until we find the position
         * where the new key belongs.
         */
        int current = root;
        int parent = -1;

        /*
         * Traverse the tree exactly like a standard BST.
         *
         * current:
         * the node we are currently examining
         *
         * parent:
         * the previous node, which will matter when we eventually
         * attach the new node under it
         */
        while (current != -1) {
            levels++;
            parent = current;

            /*
             * First check whether the key already exists.
             * If it does, we do nothing because duplicates are not allowed.
             */
            comparisons++;
            if (key == tree[key][current]) {
                this.time = System.nanoTime() - startTime;
                return;
            }

            /*
             * If the new key is smaller, we go to the left child.
             * Otherwise we go to the right child.
             */
            comparisons++;
            if (key < tree[key][current]) {
                current = tree[left][current];
            } else {
                current = tree[right][current];
            }
        }

        /*
         * If we exited the loop, current became -1.
         * That means we found the correct empty position where the new node
         * should be attached.
         */
        int newRow = getNode();
        if (newRow == -1) {
            /*
             * The array is full, so we cannot insert any more keys.
             */
            this.time = System.nanoTime() - startTime;
            return;
        }

        tree[key][newRow] = key;

        /*
         * Now link the new node under its parent in the proper side.
         */
        comparisons++;
        if (key < tree[key][parent]) {
            tree[left][parent] = newRow;
        } else {
            tree[right][parent] = newRow;
        }

        /*
         * Store how long the insertion took.
         */
        this.time = System.nanoTime() - startTime;
    }

    /*
     * search(key)
     * -----------
     * Searches for a key in the BST.
     *
     * Returns:
     * - the key itself if found
     * - -1 if not found
     *
     * The search is efficient because at each node we only continue into
     * one subtree, not both. The BST ordering tells us which direction is
     * still possible.
     */
    public int search(int key) {
        /*
         * Start measuring this specific call of search().
         */
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        /*
         * current always points to the node we are currently checking.
         */
        int current = root;

        while (current != -1) {
            /*
             * We visited one more node while following the search path.
             */
            levels++;

            comparisons++;
            if (key == tree[key][current]) {
                this.time = System.nanoTime() - startTime;
                return tree[key][current];
            }

            comparisons++;
            if (key < tree[key][current]) {
                current = tree[left][current];
            } else {
                current = tree[right][current];
            }
        }

        /*
         * We fell off the tree, so the key does not exist.
         */
        this.time = System.nanoTime() - startTime;
        return -1;
    }

    /*
     * delete(key)
     * -----------
     * Deletes a key from the BST.
     *
     * Returns:
     * true  -> if the key existed and was deleted
     * false -> if the key was not found
     *
     * Deletion is the most complex BST operation because after removing
     * a node, the BST property must still remain true.
     *
     * There are 3 classic cases:
     *
     * Case 1: the node is a leaf
     * - just disconnect it from its parent
     *
     * Case 2: the node has exactly one child
     * - connect the parent directly to that child
     *
     * Case 3: the node has two children
     * - find the inorder successor
     * - copy its key into the node we want to delete
     * - delete the successor from its original place
     *
     * Cases 1 and 2 can be handled together.
     */
    public boolean delete(int key) {
        /*
         * Start measuring this specific call of delete().
         */
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        /*
         * current will move through the tree searching for the key.
         * parent trails one step behind current.
         */
        int current = root;
        int parent = -1;

        /*
         * Step 1:
         * find the node to delete and its parent.
         *
         * parent is needed because after removing current, we must reconnect
         * the parent to whatever subtree remains.
         */
        while (current != -1) {
            levels++;

            comparisons++;
            if (key == tree[key][current]) {
                break;
            }

            parent = current;

            comparisons++;
            if (key < tree[key][current]) {
                current = tree[left][current];
            } else {
                current = tree[right][current];
            }
        }

        /*
         * If we reached -1, the key does not exist in the tree.
         */
        if (current == -1) {
            this.time = System.nanoTime() - startTime;
            return false;
        }

        /*
         * Case 1 or Case 2:
         * the node has at most one child.
         *
         * If one of the child links is -1, then either:
         * - both are -1 -> leaf
         * - exactly one is valid -> one-child case
         *
         * In both situations, the solution is the same:
         * connect the parent directly to the existing child
         * (or to -1 if this node was a leaf).
         */
        if (tree[left][current] == -1 || tree[right][current] == -1) {
            int child = (tree[left][current] != -1) ? tree[left][current] : tree[right][current];

            if (parent == -1) {
                /*
                 * Special case:
                 * we are deleting the root itself.
                 * Then the new root becomes the child (or -1 if tree becomes empty).
                 */
                root = child;
            } else if (tree[left][parent] == current) {
                /*
                 * current was the left child of its parent.
                 */
                tree[left][parent] = child;
            } else {
                /*
                 * current was the right child of its parent.
                 */
                tree[right][parent] = child;
            }

            /*
             * The old slot is no longer used by the tree,
             * so we recycle it through the free-list.
             */
            freeNode(current);
            this.time = System.nanoTime() - startTime;
            return true;
        }

        /*
         * Case 3:
         * the node has two children.
         *
         * We use the inorder successor:
         * the smallest node in the right subtree.
         *
         * Why is that a good choice?
         * Because it is guaranteed to be the next larger key,
         * so replacing current's key with it preserves BST order.
         */
        int successorParent = current;
        int successor = tree[right][current];

        /*
         * We count stepping into the right subtree as another level visit.
         */
        levels++;

        /*
         * Move left as much as possible in the right subtree.
         * The leftmost node there is the inorder successor.
         */
        while (tree[left][successor] != -1) {
            successorParent = successor;
            successor = tree[left][successor];
            levels++;
        }

        /*
         * Copy the successor's key into the node we wanted to delete.
         * This way, from the outside, it is as if the original node was deleted.
         */
        tree[key][current] = tree[key][successor];

        /*
         * The successor cannot have a left child, because it was the leftmost
         * node in that subtree.
         * Therefore it has at most one child: its right child.
         *
         * So deleting the successor is now a simple 0/1 child deletion.
         */
        int successorChild = tree[right][successor];
        if (successorParent == current) {
            /*
             * The successor was the direct right child of current.
             */
            tree[right][successorParent] = successorChild;
        } else {
            /*
             * The successor was deeper and was a left child of its parent.
             */
            tree[left][successorParent] = successorChild;
        }

        /*
         * Recycle the successor's old slot.
         */
        freeNode(successor);
        this.time = System.nanoTime() - startTime;
        return true;
    }

    /*
     * rangeSearch(low, high)
     * ----------------------
     * Returns a list of all keys x such that:
     * low <= x <= high
     *
     * This is not a full traversal of the whole tree unless necessary.
     * Thanks to the BST property, we can skip subtrees that cannot possibly
     * contain values inside the range.
     *
     * Example:
     * if current key is already smaller than low, then every key in its
     * left subtree is even smaller, so there is no reason to search left.
     */
    public List<Integer> rangeSearch(int low, int high) {
        /*
         * Start measuring this specific call of rangeSearch().
         */
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        /*
         * Because the helper follows an inorder-style logic,
         * the resulting list comes out sorted automatically.
         */
        List<Integer> result = new ArrayList<>();
        findInRange(root, low, high, result);

        this.time = System.nanoTime() - startTime;
        return result;
    }

    /*
     * findInRange(...)
     * ----------------
     * Helper for rangeSearch.
     *
     * Logic:
     * 1. If the current node does not exist, stop.
     * 2. Search left subtree only if it might contain keys >= low.
     * 3. Add current key if it lies inside the interval.
     * 4. Search right subtree only if it might contain keys <= high.
     *
     * This is basically a pruned inorder traversal.
     */
    private void findInRange(int nodeIndex, int low, int high, List<Integer> result) {
        /*
         * Base case of recursion:
         * if the subtree is empty, there is nothing to add.
         */
        if (nodeIndex == -1) {
            return;
        }

        /*
         * We have visited one actual node of the tree.
         */
        levels++;

        comparisons++;
        if (tree[key][nodeIndex] > low) {
            /*
             * Only then can the left subtree still contain values inside the range.
             */
            findInRange(tree[left][nodeIndex], low, high, result);
        }

        comparisons += 2;
        if (tree[key][nodeIndex] >= low && tree[key][nodeIndex] <= high) {
            result.add(tree[key][nodeIndex]);
        }

        comparisons++;
        if (tree[key][nodeIndex] < high) {
            /*
             * Only then can the right subtree still contain values inside the range.
             */
            findInRange(tree[right][nodeIndex], low, high, result);
        }
    }

    /*
     * inorder()
     * ---------
     * Prints all keys of the BST in ascending order.
     *
     * Why does inorder produce sorted output in a BST?
     * Because:
     * - all keys in the left subtree are smaller
     * - all keys in the right subtree are larger
     *
     * Therefore visiting:
     * left -> current -> right
     * naturally prints keys from smallest to largest.
     */
    public void inorder() {
        /*
         * We build the output in a StringBuilder first and print once at the end.
         * That keeps the printing logic clean and avoids repeated formatting code.
         */
        StringBuilder builder = new StringBuilder();
        buildInorder(root, builder);
        System.out.println(builder.toString().trim());
    }

    /*
     * buildInorder(...)
     * -----------------
     * Recursive helper for inorder().
     *
     * The order is always:
     * 1. left subtree
     * 2. current key
     * 3. right subtree
     */
    private void buildInorder(int nodeIndex, StringBuilder builder) {
        /*
         * Base case:
         * an empty subtree contributes nothing to the output.
         */
        if (nodeIndex == -1) {
            return;
        }

        /*
         * First print all smaller keys,
         * then the current key,
         * then all larger keys.
         */
        buildInorder(tree[left][nodeIndex], builder);
        builder.append(tree[key][nodeIndex]).append(' ');
        buildInorder(tree[right][nodeIndex], builder);
    }

    /*
     * printName()
     * -----------
     * Small helper required by the assignment.
     * It prints the name of this data structure implementation.
     */
    public void printName() {
        System.out.println("BST Array");
    }
}
