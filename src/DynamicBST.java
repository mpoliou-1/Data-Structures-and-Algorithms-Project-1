/*
* Source for the dynamic BST implementation :
* https://www.geeksforgeeks.org/dsa/binary-search-tree-set-1-search-and-insertion/
* https://www.geeksforgeeks.org/dsa/deletion-in-binary-search-tree/
* https://www.geeksforgeeks.org/dsa/insertion-in-binary-search-tree/
* 
* We also used the slides provided from the class
* It was then modified to suit our own source code for the assignment requirements
*/

import java.util.ArrayList;
import java.util.List;

public class DynamicBST implements SearchStructure {
    public DynamicBSTNode root;
    
    // Metrics to compare
    public long time;
    public int comparisons;
    public int levels;

    public DynamicBST(){
        this.root = null;
    }


    /*
    * @func insert
    * Inserting a new key, after checking if the tree is empty
    * @param key is the unique number of each node
    * 
    */

    public void insert(int key){

        // These variables are for the measurements asked in this assignment
        long startTime = System.nanoTime(); // Time starting
        this.comparisons = 0;
        this.levels = 0;


        // If the tree is empty, we create a new root and stop
        if (this.root == null){
            this.root = new DynamicBSTNode(key);
            this.levels = 1;
            this.time = System.nanoTime() - startTime; // Time ending
            return;
        }

        DynamicBSTNode current = root;
        DynamicBSTNode parent = null;

        // We then recur down the tree to find the right position
        while (current != null){
            this.levels++;
            parent = current;   // We keep the parent as the last, non-null node
            this.comparisons++; // +1 comparisons

            // If the key exists, we stop(return)
            if (key == current.key){
                this.time = System.nanoTime() - startTime;
                return;
            }

            // Now we check if the key is smaller than the current
            if (key < current.key){
                current = current.left; // Obviously, we go to the left
            } else{
                current = current.right; // Else, we go right
            }
        }

        // Lastly, we create the new node, and we link it to its parent
        DynamicBSTNode newNode = new DynamicBSTNode(key);
        this.comparisons++;

        if (key < parent.key){
            parent.left = newNode;
        } else{
            parent.right = newNode;
        }

        this.time = System.nanoTime() - startTime;
    }



    /*
    * @func search
    * @param key is the unique number of each node
    * Searching for a key starting from the root
    * Returns the key if found, or -1 if the key does not exist
    */

    public int search(int key){

        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;
        // if the tree is empty return error
        if (root == null){
            this.time = System.nanoTime() - startTime;
            return -1;
        }    

        DynamicBSTNode current = root;

        while(current != null){

            // We visited one more node while following the search path.
            this.levels++;
            this.comparisons++;
            // If we find the key, we just return it
            if (key == current.key){
                this.time = System.nanoTime() - startTime;
                return current.key;
            }

            // Then we go down the tree and choose sides 
            this.comparisons++;
            if (key < current.key){
                current = current.left;
            } else{
                current = current.right;
            }
        }
        
        // If we get to here, we fell off the tree, thus return error
        this.time = System.nanoTime() - startTime;
        return -1;
    }


    /*
    * @func  delete
    * @param key is the unique number of each node
    * Deletes a key from the tree
    * Returns true if found, else false.
    */
    public boolean delete(int key){
        
        DynamicBSTNode current = root;
        DynamicBSTNode parent = null;
        DynamicBSTNode nextNode = null;
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;

        // Firstly, search for the node and its parent
        while (current != null && current.key != key){
            
            this.levels++;
            this.comparisons++;
            parent = current;

            this.comparisons++;
            if (key < current.key){
                current = current.left;
            } else {
                current = current.right;
            }
        }

        // If we dont find the key, return false

        if (current == null){
            this.time = System.nanoTime() - startTime;
            return false;
        }

        this.levels++;


        // If the node has a maximum of one child, so 0 or 1
        if (current.left == null || current.right == null){

            if (current.left == null){
                nextNode = current.right;
            }
            else { 
                nextNode = current.left;
            }
        


        // If we delete the root
        if (parent == null){
            root = nextNode;
        } else if (current == parent.left){
            parent.left = nextNode;
        } else {
            parent.right = nextNode;
        }
    }

        // If the node has 2 children
        else {
            DynamicBSTNode pSuccessor = current;
            DynamicBSTNode successor = current.right;


            // We find the in-order successor 
            // (the leftmost in the right subtree)
            while (successor.left != null) {
                this.levels++;
                pSuccessor = successor;
                successor = successor.left;
            }

            // We replace the value of the current node with the successor
            current.key = successor.key;

            // We delete the successor who now has a maximum of 1 child
            if (pSuccessor != current) {
                pSuccessor.left = successor.right;
            } else {
                pSuccessor.right = successor.right;
            }
        }
        this.time = System.nanoTime() - startTime;
        return true;
    }


    /* @func rangeSearch
    * Performs a range search.
    * Returns a list of keys within the range [low, high].
    */
    public List<Integer> rangeSearch(int low, int high){
        long startTime = System.nanoTime();
        this.comparisons = 0;
        this.levels = 0;
        List<Integer> result = new ArrayList<>();
        
        // we will be using another function to help us do everything
        findInRange(result, root, low, high);


        this.time = System.nanoTime() - startTime;
        return result;

    }

    /* @func findInRange
    * This function is actually doing the search for rangeSearch.
    * Starts from an empty node, 
    * and goes to every possible subtree to find all 
    * the keys between the values of low and high
    */
    private void findInRange(List<Integer> result, DynamicBSTNode node, int low, int high){
        
        /* We start on an empty node
        * If true, we return nothing
        * If false, we start the proccess to look for the keys
        */
        if (node == null){
            return;
        }

        this.levels++;
        this.comparisons++;

        /* If the key is higher than the value we set as low,
        * there might be keys to look for 
        * on the left subtree
        */
        if (node.key > low){
            findInRange(result, node.left, low, high);
        }

        /* Checking if the current node is between the values we set
        * If true, we add it to the result list
        */

        this.comparisons += 2;
        if (node.key >= low && node.key <= high) {
            result.add(node.key);
        }


        this.comparisons++;
        /* If the key is lower than the value we set as high,
        * there might be keys to look for 
        * on the right subtree
        */
        if (node.key < high){
            findInRange(result, node.right, low, high);
        }
    }

    /* @func inorder
    * Public version requested by the assignment.
    * It starts the traversal from the root.
    */
    public void inorder(){
        inorder(this.root);
        System.out.println();
    }

    /* @func inorder
    * This function just prints the keys
    * in ascending order
    */
    private void inorder(DynamicBSTNode subroot){
        // If the root is empty, then the tree is empty so do nothing
        if (subroot == null){
            return;
        }
        
        inorder(subroot.left);
        System.out.print(subroot.key + " ");
        inorder(subroot.right);

    }


    /*
     * @func printName()
     * It prints the name of this data structure implementation.
     */
    public void printName() {
        System.out.println("BST with Dynamic Memory Allocation");
    }
}
