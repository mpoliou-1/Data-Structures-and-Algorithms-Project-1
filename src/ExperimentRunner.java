import java.util.ArrayList;
import java.util.List;

/*
 * ExperimentRunner
 * ----------------
 * Runs the full experimental part of the assignment.
 *
 * For every required N value:
 * - create fresh structures
 * - generate N unique random keys
 * - insert them into all 3 structures
 * - print inorder for N = 30
 * - measure operations A, B, C, D
 * - store average comparisons, time, and levels
 */
public class ExperimentRunner {

    private static final int[] N_VALUES = {30, 50, 100, 200, 500, 800, 1000, 5000, 10000, 100000};

    private final RandomDataGenerator generator;
    private final ResultsPrinter printer;

    public ExperimentRunner() {
        this.generator = new RandomDataGenerator();
        this.printer = new ResultsPrinter();
    }

    public void runAllExperiments() {
        // Create all the tables for all the operations
        OperationTable insertTable = new OperationTable("Operation A - Insert");
        OperationTable deleteTable = new OperationTable("Operation B - Delete");
        OperationTable searchTable = new OperationTable("Operation C - Search");
        OperationTable rangeTable = new OperationTable("Operation D - Range Search");

        for (int n : N_VALUES) {
            // 
            int repetitions = repetitionsFor(n);
            int maxValue = 2 * n;

            DynamicBST dynamicTree = new DynamicBST();
            BSTarray arrayTree = new BSTarray(maxValue);
            BinarySearchArray binaryArray = new BinarySearchArray(maxValue);

            int[] initialKeys = this.generator.generateUniqueRandoms(n, 1, maxValue);
            populateStructures(initialKeys, dynamicTree, arrayTree, binaryArray);

            if (n == 30) {
                this.printer.printInitialInorders(n, dynamicTree, arrayTree);
            }

            insertTable.addRow(measureSingleKeyOperation(n, repetitions, maxValue, dynamicTree, arrayTree, binaryArray, 'I'));
            deleteTable.addRow(measureSingleKeyOperation(n, repetitions, maxValue, dynamicTree, arrayTree, binaryArray, 'D'));
            searchTable.addRow(measureSingleKeyOperation(n, repetitions, maxValue, dynamicTree, arrayTree, binaryArray, 'S'));
            rangeTable.addRow(measureRangeSearch(n, repetitions, maxValue, dynamicTree, arrayTree, binaryArray));
        }

        this.printer.printOperationTable(insertTable);
        this.printer.printOperationTable(deleteTable);
        this.printer.printOperationTable(searchTable);
        this.printer.printOperationTable(rangeTable);
    }

    private void populateStructures(int[] keys, DynamicBST dynamicTree, BSTarray arrayTree, BinarySearchArray binaryArray) {
        for (int key : keys) {
            dynamicTree.insert(key);
            arrayTree.insert(key);
            binaryArray.insert(key);
        }
    }

    /* function: repetitionsFor
     * Function that we use to see how many runs we are going to have
     * returns k = 20 when n < 201
     * k = 50 when 200 < n < 1000
     * k = 100 when n > 1001 
     */
    private int repetitionsFor(int n) {
        if (n < 201) {
            return 20;
        }

        if (n < 1001) {
            return 50;
        }

        return 100;
    }

    /*
     * Measures one of the single-key operations:
     * I = insert, D = delete, S = search
     */
    private TableRow measureSingleKeyOperation(int n, int repetitions, int maxValue,
            DynamicBST dynamicTree, BSTarray arrayTree, BinarySearchArray binaryArray, char operation) {
        int[] keys = this.generator.generateRandoms(repetitions, 1, maxValue);

        long dynamicTime = 0;
        long dynamicComparisons = 0;
        long dynamicLevels = 0;

        long arrayTime = 0;
        long arrayComparisons = 0;
        long arrayLevels = 0;

        long binaryTime = 0;
        long binaryComparisons = 0;
        long binaryLevels = 0;

        for (int key : keys) {
            switch (operation) {
                case 'I':
                    dynamicTree.insert(key);
                    arrayTree.insert(key);
                    binaryArray.insert(key);
                    break;
                case 'D':
                    dynamicTree.delete(key);
                    arrayTree.delete(key);
                    binaryArray.delete(key);
                    break;
                case 'S':
                    dynamicTree.search(key);
                    arrayTree.search(key);
                    binaryArray.search(key);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown operation type.");
            }

            dynamicTime += dynamicTree.time;
            dynamicComparisons += dynamicTree.comparisons;
            dynamicLevels += dynamicTree.levels;

            arrayTime += arrayTree.time;
            arrayComparisons += arrayTree.comparisons;
            arrayLevels += arrayTree.levels;

            binaryTime += binaryArray.time;
            binaryComparisons += binaryArray.comparisons;
            binaryLevels += binaryArray.levels;
        }

        return new TableRow(
                n,
                average(dynamicComparisons, dynamicTime, dynamicLevels, repetitions),
                average(arrayComparisons, arrayTime, arrayLevels, repetitions),
                average(binaryComparisons, binaryTime, binaryLevels, repetitions)
        );
    }

    private TableRow measureRangeSearch(int n, int repetitions, int maxValue,
            DynamicBST dynamicTree, BSTarray arrayTree, BinarySearchArray binaryArray) {
        long dynamicTime = 0;
        long dynamicComparisons = 0;
        long dynamicLevels = 0;

        long arrayTime = 0;
        long arrayComparisons = 0;
        long arrayLevels = 0;

        long binaryTime = 0;
        long binaryComparisons = 0;
        long binaryLevels = 0;

        for (int i = 0; i < repetitions; i++) {
            int[] range = this.generator.generateSortedRange(1, maxValue);
            int low = range[0];
            int high = range[1];

            dynamicTree.rangeSearch(low, high);
            arrayTree.rangeSearch(low, high);
            binaryArray.rangeSearch(low, high);

            dynamicTime += dynamicTree.time;
            dynamicComparisons += dynamicTree.comparisons;
            dynamicLevels += dynamicTree.levels;

            arrayTime += arrayTree.time;
            arrayComparisons += arrayTree.comparisons;
            arrayLevels += arrayTree.levels;

            binaryTime += binaryArray.time;
            binaryComparisons += binaryArray.comparisons;
            binaryLevels += binaryArray.levels;
        }

        return new TableRow(
                n,
                average(dynamicComparisons, dynamicTime, dynamicLevels, repetitions),
                average(arrayComparisons, arrayTime, arrayLevels, repetitions),
                average(binaryComparisons, binaryTime, binaryLevels, repetitions)
        );
    }

    private Measurements average(long totalComparisons, long totalTime, long totalLevels, int repetitions) {
        return new Measurements(
                (double) totalComparisons / repetitions,
                (double) totalTime / repetitions,
                (double) totalLevels / repetitions
        );
    }

    public static class Measurements {
        public final double comparisons;
        public final double time;
        public final double levels;

        public Measurements(double comparisons, double time, double levels) {
            this.comparisons = comparisons;
            this.time = time;
            this.levels = levels;
        }
    }

    public static class TableRow {
        public final int n;
        public final Measurements dynamicTree;
        public final Measurements arrayTree;
        public final Measurements binarySearch;

        public TableRow(int n, Measurements dynamicTree, Measurements arrayTree, Measurements binarySearch) {
            this.n = n;
            this.dynamicTree = dynamicTree;
            this.arrayTree = arrayTree;
            this.binarySearch = binarySearch;
        }
    }

    public static class OperationTable {
        public final String title;
        public final List<TableRow> rows;

        public OperationTable(String title) {
            this.title = title;
            this.rows = new ArrayList<>();
        }

        public void addRow(TableRow row) {
            this.rows.add(row);
        }
    }
}
