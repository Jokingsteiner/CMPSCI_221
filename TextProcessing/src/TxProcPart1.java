import java.io.*;
import java.util.*;

/**
 * Created by cjk98 on 1/20/2017.
 * For CMPSCI_221
 */
public class TxProcPart1 {
    private static final Comparator<Map.Entry<String, Integer>> BY_VALUE = new ByValue();
    private List<String> tokenList;

    public TxProcPart1() {
        this.tokenList = new ArrayList<>();
    }

    public List<String> tokenize(String textFilePath) {
        File file = new File(textFilePath);
        FileReader fr = null;
        try {
            fr = new FileReader(file);
            BufferedReader buffReader = new BufferedReader(fr);
            String line;
            String[] tokenOfLine;
            while ((line = buffReader.readLine()) != null){
                tokenOfLine = line.split("[^\\w]+");                                                                // \W is a non-alphanumeric set, + means these delimiter occur one or more times
                for (String s: tokenOfLine) {
                    if (s.length() != 0)
                    tokenList.add(s.toLowerCase());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not Found");
        } catch (IOException e) {
            System.out.println("File read IO exception caught");
        } finally {
            try {
                if (fr != null)
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void print(Map<String, Integer> frequenciesMap) {
        List<Map.Entry<String, Integer>> sortList = new ArrayList<>(frequenciesMap.entrySet());
        sortList.sort(BY_VALUE);
        for (Map.Entry<String, Integer> e: sortList){
//            System.out.println("\"" + e.getKey() + "\", " + e.getValue());
            System.out.println(e.getKey() + ", " + e.getValue());
        }
    }

    public static void main (String arg[]){
        System.out.println("Text Processing Start");
        System.out.println("FilePath: " + arg[0]);
        TxProcPart1 object = new TxProcPart1();
        List<String> tokenList = object.tokenize(arg[0]);
/*        for (String s : tokenList){
            System.out.println(s);
        }*/
        object.print(object.computeWordFrequencies(tokenList));
    }
}
