import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IndexUtil indexUtil = new IndexUtil();
        OntologyUtil ontologyUtil = new OntologyUtil(OntologyUtil.PCSHOP_ONTOLOGY_FNAME);
        SearchUtil searchUtil;
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. feladat:");
        System.out.print("Index generálása? (Y/N): ");
        if (scanner.next().toUpperCase().equals("Y")) {
            indexUtil.createIndex(args[0]);
            System.out.println("Index létrehozva");
        } else {
            indexUtil.loadIndex();
            System.out.println("Index betöltve");
        }
        scanner.nextLine();

        System.out.println("\n2. feladat:");
        do {
            System.out.print("Keresőszavak szóközzel elválasztva: ");
            String[] keyWords = scanner.nextLine().split(" ");
            searchUtil = new SearchUtil(indexUtil, ontologyUtil);
            // To search without query expansion use searchUtil.search(...)
            List<String> results = searchUtil.searchWithQueryExpansion(Arrays.asList(keyWords));
            if (results == null || results.isEmpty()) {
                System.out.println("Nincs találat");
            } else {
                for (String result : results)
                    System.out.println(result);
            }
            System.out.println("\nÚjra? (Y/N)");
        } while (scanner.nextLine().toUpperCase().equals("Y"));
    }
}
