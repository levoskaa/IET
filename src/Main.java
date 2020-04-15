import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IndexUtil indexUtil = new IndexUtil();
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. feladat:");
        System.out.print("Index generálása? (Y/N): ");
        if (scanner.next().toUpperCase().equals("Y")) {
            indexUtil.createIndex("D:/Projektek/IET/corpus");
            System.out.println("Index létrehozva");
        } else {
            indexUtil.loadIndex();
            System.out.println("Index betöltve");
        }
        scanner.nextLine();

        System.out.println("\n2. feladat:");
        System.out.print("Keresőszavak szóközzel elválasztva: ");
        String[] keyWords = scanner.nextLine().split(" ");
        SearchUtil searchUtil = new SearchUtil(indexUtil.getIndex());
        List<String> results = searchUtil.search(Arrays.asList(keyWords));
        if (results == null || results.isEmpty()) {
            System.out.println("Nincs találat");
        } else {
            for (String result : results)
                System.out.println(result);
        }
    }
}
