import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class IndexUtil {
    private Map<String, List<Map.Entry<String, Integer>>> index;

    public IndexUtil() {
        index = new HashMap<>();
    }

    public void createIndex(String directoryPath) {
        generateIndex("D:/Projektek/IET/corpus");
        saveIndex();
    }

    private void generateIndex(String directoryPath) {
        TermRecognizer tr;
        Map<String, List<Map.Entry<String, Integer>>> generatedIndex = new HashMap<>();
        try {
            tr = new TermRecognizer();
            List<File> files = new ArrayList<>();
            getFiles(files, Paths.get(directoryPath));
            for (File file : files) {
                String fileName = file.getName().toLowerCase();
                String fileText = Util.readFileAsString(file.getAbsolutePath());
                Map<String, Integer> terms = tr.termFrequency(fileText);

                for (String word : terms.keySet()) {
                    if (!generatedIndex.containsKey(word.toLowerCase())) {
                        generatedIndex.put(word.toLowerCase(), new ArrayList<Map.Entry<String, Integer>>());
                    }
                    List<Map.Entry<String, Integer>> indexList = generatedIndex.get(word.toLowerCase());
                    indexList.add(new AbstractMap.SimpleEntry<String, Integer>(fileName, terms.get(word)));
                }
            }
            index = generatedIndex;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveIndex() {
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

    public void loadIndex() {
        Map<String, List<Map.Entry<String, Integer>>> loadedIndex = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File("index.txt"));
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");
                List<Map.Entry<String, Integer>> frequencies = new ArrayList<>();
                for (int i = 1; i < line.length; i += 2)
                    frequencies.add(new AbstractMap.SimpleEntry<String, Integer>(line[i], Integer.parseInt(line[i + 1])));
                loadedIndex.put(line[0], frequencies);
            }
            index= loadedIndex;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private List<File> getFiles(List<File> files, Path directoryPath) {
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

    public Map<String, List<Map.Entry<String, Integer>>> getIndex() {
        return index;
    }
}
