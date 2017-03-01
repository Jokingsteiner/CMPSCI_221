import java.io.*;
import java.util.*;

/**
 * Created by cjk98 on 1/20/2017.
 * For CMPSCI_221
 */
public class Tokenization {
    private static final Comparator<Map.Entry<String, Integer>> BY_VALUE = new ByValue();
//    private String PATTERN = "[a-z0-9A-Z]+";
//    private Pattern compile;
    public Tokenization() {
    }

    public List<String> getTokensFromFile(String textFilePath) {
        String line;
        String[] tokenOfLine;
        List<String> tokenList = new ArrayList<>();
        FileReaderWBuffer fr = new FileReaderWBuffer(textFilePath);
        while ((line = fr.readLine()) != null){
            tokenOfLine = line.split("[^a-zA-Z0-9]+");                                                                // \W is a non-alphanumeric set, + means these delimiter occur one or more times
            for (String s: tokenOfLine) {
                if (s.length() != 0)
                tokenList.add(s.toLowerCase());
            }
        }
        fr.close();
        return tokenList;
    }

    public List<String> getTokensFromString (String str, String splitRegEx) {
        List<String> tokenList = new ArrayList<>();
        String[] tokenOfLine = str.split(splitRegEx);
        for (String s: tokenOfLine) {
            if (s.length() != 0)
                tokenList.add(s.toLowerCase());
        }
        return tokenList;
    }

    public Map<String, Integer> computeWordFrequencies(List<String> tokenList) {
        Map<String, Integer> wordFrequenciesMap = new TreeMap<>();
        for (String s: tokenList)
            wordFrequenciesMap.put(s, wordFrequenciesMap.getOrDefault(s, 0) + 1);
        return wordFrequenciesMap;
    }

    private static class ByValue implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return (o2.getValue()).compareTo( o1.getValue() );
        }
    }

    private void printByValueOrder(Map<String, Integer> frequenciesMap) {
        List<Map.Entry<String, Integer>> sortList = new ArrayList<>(frequenciesMap.entrySet());
        sortList.sort(BY_VALUE);
        for (Map.Entry<String, Integer> e: sortList){
            System.out.println(e.getKey() + ", " + e.getValue());
        }
    }

    private void printByTermOrder(Map<String, Integer> frequenciesMap) {
        for (Map.Entry<String, Integer> e: frequenciesMap.entrySet()){
            System.out.println(e.getKey() + ", " + e.getValue());
        }
    }


    public static void main (String arg[]){
        System.out.println("Text Processing Start");
        System.out.println("FilePath: " + arg[0]);
        Tokenization object = new Tokenization();
        long start = System.currentTimeMillis();
        List<String> tokenList = object.getTokensFromFile(arg[0]);
        System.out.println(String.format("Time cost1 : %s ms", System.currentTimeMillis() - start));
        object.printByTermOrder(object.computeWordFrequencies(tokenList));
        System.out.println(String.format("Time cost2 : %s ms", System.currentTimeMillis() - start));
    }
}
