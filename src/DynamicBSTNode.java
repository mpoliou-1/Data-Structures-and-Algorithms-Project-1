/*
* Source for the dynamic BST implementation :
* https://www.geeksforgeeks.org/dsa/binary-search-tree-set-1-search-and-insertion/
* 
* It was then modified to suit our own source code for the assignment requirements
*
* This class is used to define the structure of the nodes
*/

public class DynamicBSTNode {
    int key;
    DynamicBSTNode left, right;

    public DynamicBSTNode(int item){
        this.key = item;
        this.left = this.right = null;
    }
}
