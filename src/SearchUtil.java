import org.semanticweb.owlapi.model.OWLClass;

import java.util.*;
import java.util.stream.Collectors;

public class SearchUtil {
    private IndexUtil indexUtil;
    private OntologyUtil ontologyUtil;

    public SearchUtil(IndexUtil indexUtil, OntologyUtil ontologyUtil) {
        this.indexUtil = indexUtil;
        this.ontologyUtil = ontologyUtil;
    }

    public List<String> search(List<String> keyWords) {
        Map<String, List<Map.Entry<String, Integer>>> index = indexUtil.getIndex();
        keyWords = keyWords.stream().map(String::toLowerCase).collect(Collectors.toList());
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

    public List<String> searchWithQueryExpansion(List<String> keyWords) {
        Map<String, List<Map.Entry<String, Integer>>> index = indexUtil.getIndex();
        List<String> searchResults = search(keyWords);
        if (searchResults == null)
            return Collections.emptyList();
        int[][] expansionMatrix = new int[keyWords.size()][searchResults.size()];
        // Fill expansionMatrix
        for (int keyWordIndex = 0; keyWordIndex < expansionMatrix.length; ++keyWordIndex) {
            String keyWord = keyWords.get(keyWordIndex);
            List<String> descendantNames = ontologyUtil.getSubClasses(keyWord, false)
                    .stream()
                    .map(cls -> cls.getIRI().getFragment())
                    .collect(Collectors.toList());
            OWLClass cls = ontologyUtil.getClass(keyWord);
            Set<String> labels = (cls != null) ? ontologyUtil.getClassAnnotations(cls) : Collections.emptySet();
            labels = labels.stream().map(String::toLowerCase).collect(Collectors.toSet());
            for (int documentIndex = 0; documentIndex < expansionMatrix[0].length; ++documentIndex) {
                // Expansion based on subclasses
                for (String descendant : descendantNames) {
                    if (indexUtil.isContainedInDocument(descendant, searchResults.get(documentIndex)))
                        expansionMatrix[keyWordIndex][documentIndex] += 1;
                }
                // Expansion based on labels
                for (String label : labels) {
                    if (indexUtil.isContainedInDocument(label, searchResults.get(documentIndex)))
                        expansionMatrix[keyWordIndex][documentIndex] += 1;
                }
            }
        }
        // Decide on results based on the expansionMatrix
        // Good results have a number > 0 in every row
        List<String> expandedResults = new ArrayList<>();
        for (int documentIndex = 0; documentIndex < expansionMatrix[0].length; ++documentIndex) {
            boolean goodColumn = true;
            for (int keyWordIndex = 0; keyWordIndex < expansionMatrix.length; ++keyWordIndex) {
                if (expansionMatrix[keyWordIndex][documentIndex] <= 0) {
                    goodColumn = false;
                    break;
                }
            }
            if (goodColumn)
                expandedResults.add(searchResults.get(documentIndex));
        }
        if (expandedResults.isEmpty())
            return searchResults;
        return expandedResults;
    }
}