import java.util.Locale;

/*
 * @class ResultsPrinter
 * This class was AI generated in order to print the results
 * We used the prompt "I would like a class that prints the results all together",
 * and this was what we got.
 */
public class ResultsPrinter {

    private static final Locale GREEK_LOCALE = Locale.forLanguageTag("el-GR");

    public void printInitialInorders(int n, DynamicBST dynamicTree, BSTarray arrayTree) {
        System.out.println("N = " + n);

        System.out.print("Δέντρο δυναμική παραχώρηση μνήμης, inorder διάσχιση: ");
        dynamicTree.inorder();

        System.out.print("Δέντρο array, inorder διάσχιση: ");
        arrayTree.inorder();

        System.out.println();
    }

    public void printOperationTable(ExperimentRunner.OperationTable table) {
        System.out.println(table.title);

        System.out.printf(
                GREEK_LOCALE,
                "%-8s %-12s %-12s %-12s %-12s %-12s %-12s %-12s %-12s %-12s%n",
                "N",
                "Δυν Πράξ", "Δυν Χρ", "Δυν Επ",
                "Πιν Πράξ", "Πιν Χρ", "Πιν Επ",
                "Δυαδ Πράξ", "Δυαδ Χρ", "Δυαδ Επ"
        );

        for (ExperimentRunner.TableRow row : table.rows) {
            System.out.printf(
                    GREEK_LOCALE,
                    "%-8d %-12.2f %-12.2f %-12.2f %-12.2f %-12.2f %-12.2f %-12.2f %-12.2f %-12.2f%n",
                    row.n,
                    row.dynamicTree.comparisons, row.dynamicTree.time, row.dynamicTree.levels,
                    row.arrayTree.comparisons, row.arrayTree.time, row.arrayTree.levels,
                    row.binarySearch.comparisons, row.binarySearch.time, row.binarySearch.levels
            );
        }

        System.out.println();
    }
}
