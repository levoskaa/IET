import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class IndexUtil {
    public static void createIndex(String directoryPath) {
        Map<String, List<Map.Entry<String, Integer>>> index = generateIndex("D:/Projektek/IET/corpus");
        saveIndex(index);
    }

    private static Map<String, List<Map.Entry<String, Integer>>> generateIndex(String directoryPath) {
        TermRecognizer tr;
        Map<String, List<Map.Entry<String, Integer>>> index = new HashMap<>();
        try {
            tr = new TermRecognizer();
            List<File> files = new ArrayList<>();
            getFiles(files, Paths.get(directoryPath));
            for (File file : files) {
                String fileName = file.getName().toLowerCase();
                String fileText = Util.readFileAsString(file.getAbsolutePath());
                Map<String, Integer> terms = tr.termFrequency(fileText);

                for (String word : terms.keySet()) {
                    if (!index.containsKey(word.toLowerCase())) {
                        index.put(word.toLowerCase(), new ArrayList<Map.Entry<String, Integer>>());
                    }
                    List<Map.Entry<String, Integer>> indexList = index.get(word.toLowerCase());
                    indexList.add(new AbstractMap.SimpleEntry<String, Integer>(fileName, terms.get(word)));
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return index;
    }

    private static void saveIndex(Map<String, List<Map.Entry<String, Integer>>> index) {
        try (PrintWriter printWriter = new PrintWriter("index.txt", "UTF-8")) {
            for (String word : index.keySet()) {
                printWriter.print(word + " ");
                for (Map.Entry<String, Integer> entry : index.get(word))
                    printWriter.print(entry.getKey() + " " + entry.getValue() + " ");
                printWriter.println();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static List<File> getFiles(List<File> files, Path directoryPath) {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    getFiles(files, path);
                } else {
                    files.add(path.toFile());
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return files;
    }
}
