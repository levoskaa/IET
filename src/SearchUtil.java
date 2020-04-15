import java.util.*;
import java.util.stream.Collectors;

public class SearchUtil {
    private Map<String, List<Map.Entry<String, Integer>>> index;

    public SearchUtil(Map<String, List<Map.Entry<String, Integer>>> index) {
        this.index = index;
    }

    public List<String> search(List<String> keyWords) {
        try {
            Set<Map.Entry<String, Integer>> results = new HashSet<>(index.get(keyWords.get(0)));
            for (String keyWord : keyWords) {
                results.retainAll(new HashSet<>(index.get(keyWord)));
            }
            return results.stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) { }
        return null;
    }
}
