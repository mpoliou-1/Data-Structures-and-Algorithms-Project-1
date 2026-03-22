public class Main {
    public static void main(String[] args) throws Exception {
        DynamicBST tree = new DynamicBST();

        // 1. Δοκιμή INSERT
        System.out.println("--- Testing Insert ---");
        int[] keysToInsert = {50, 30, 70, 20, 40, 60, 80};
        for (int k : keysToInsert) {
            tree.insert(k);
            System.out.println("Inserted: " + k);
        }

        // 2. Δοκιμή SEARCH
        System.out.println("\n--- Testing Search ---");
        System.out.println("Search 40 (Found?): " + (tree.search(40) != -1)); // Πρέπει true
        System.out.println("Search 99 (Found?): " + (tree.search(99) != -1)); // Πρέπει false

        // 3. Δοκιμή RANGE SEARCH (Morris ή Iterative)
        System.out.println("\n--- Testing Range Search [25, 65] ---");
        java.util.List<Integer> range = tree.rangeSearch(25, 65);
        System.out.println("Keys in range: " + range); 
        // Πρέπει να εκτυπώσει: [30, 40, 50, 60] ταξινομημένα

        // 4. Δοκιμή DELETE
        System.out.println("\n--- Testing Delete ---");
        System.out.println("Delete 20 (Leaf): " + tree.delete(20));
        System.out.println("Delete 30 (Node with child): " + tree.delete(30));
        System.out.println("Delete 50 (Root with 2 children): " + tree.delete(50));
        
        // Έλεγχος αν το δέντρο "ζει" ακόμα μετά τις διαγραφές
        System.out.println("Range Search after deletes: " + tree.rangeSearch(0, 100));
        
        tree.inorder(tree.root);
    }
}
