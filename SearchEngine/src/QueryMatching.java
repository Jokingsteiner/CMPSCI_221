import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by cjk98 on 3/4/2017.
 */
public class QueryMatching {
    private final static String QUERY_STRING = "modego professor";
    private final static double TITLE_WEIGHT = 0.7;
    private final static double CONTEXT_WEIGHT = 0.3;
    private final static int TITLE_LINES = 12389;
    private final static int CONTEXT_LINES = 489669;
    private static final Comparator<Map.Entry<String, Double>> BY_VALUE = new QueryMatching.ByValue();
    private final static Tokenization tokenizer = new Tokenization();
    private final static HashMap<String, String> urlMap = new HashMap<String, String>();

    public QueryMatching() {
        String mapAddr = "H:\\WebRAW\\WEBPAGES_RAW\\bookkeeping.tsv";
        FileReaderWBuffer fr = new FileReaderWBuffer(mapAddr);
        String line;
        while ((line = fr.readLine()) != null){
            List<String> tokenList = new ArrayList<>();
            String[] tokenOfLine = line.split("\\s+");
            for (String s: tokenOfLine) {
                if (s.length() != 0)
                    tokenList.add(s);
            }
            urlMap.put(tokenList.get(0), tokenList.get(1));
        }
//        for (Map.Entry<String,String> e : urlMap.entrySet())
//            System.out.println(e.getKey() + " " + e.getValue());
        fr.close();
    }

    public void search(String queryString) {
        ArrayList<Map.Entry<String, Double>> titleScoreList = this.getTitleScoreList(queryString);
        ArrayList<Map.Entry<String, Double>> cxtScoreList = this.getContextScore(queryString);


    }

    private ArrayList<Map.Entry<String, Double>> getTitleScoreList(String queryString) {
        String filepath = ".\\SearchEngine\\resources\\titleIndex\\title_tfidfWeight.txt";
        ArrayList<Map.Entry<String, Double>> matchResult = calScoreForDoc(queryString.toLowerCase(), filepath, TITLE_LINES, TITLE_WEIGHT);
//        for (int i = 0; i < 20; i ++)
//            System.out.println(matchResult.get(i).getKey() + ", " + matchResult.get(i).getValue());
        return matchResult;
    }

    private ArrayList<Map.Entry<String, Double>> getContextScore(String queryString) {
        String filepath = ".\\SearchEngine\\resources\\contextIndex\\context_tfidfWeight.txt";
        ArrayList<Map.Entry<String, Double>> matchResult = calScoreForDoc(queryString.toLowerCase(), filepath, CONTEXT_LINES, CONTEXT_WEIGHT);
//        FileWriterWBuffer fw = new FileWriterWBuffer(".\\SearchEngine\\testResult.txt", false);
//        for (Map.Entry<String, Double> e: matchResult) {
//            String writeLine = urlMap.get(e.getKey()) + ", " + e.getValue();
//            System.out.println(writeLine);
//            fw.writeLine(writeLine);
//        }
////        for (int i = 0; i < 20; i ++)
////            System.out.println(urlMap.get(matchResult.get(i).getKey()) + ", " + matchResult.get(i).getValue());
//        fw.close();
        return matchResult;
    }

    private ArrayList<Map.Entry<String, Double>> calScoreForDoc(String queryString, String filepath, int numOfLine, double weight) {
        List<String> queryTokens = tokenizer.getTokensFromString(queryString, "[^a-zA-Z0-9/]+");
        TreeMap<String, Double> resultDocMap = new TreeMap<String, Double>();

        for (String queryTerm: queryTokens) {
            String foundResult = binarySearchLine(filepath, queryTerm, 1, numOfLine);
            if (foundResult == null)
                System.out.println("Didn't find line");
            else {                                              // found the queryTerm in our index
                //System.out.println("Found: " + foundResult);
                List<String> tokenList = tokenizer.getTokensFromString(foundResult, "[^a-zA-Z0-9/.]+");

                for (int i = 1; i < tokenList.size(); i+= 2) {
                double score = Double.valueOf(tokenList.get(i));
                // find all docID that this term is in
                String docID = tokenList.get(i + 1);
                resultDocMap.put(docID, resultDocMap.getOrDefault(docID, 0.0) + score * weight);
                }
            }
        }

        ArrayList<Map.Entry<String, Double>> sortedResult = sortByValueOrder(resultDocMap);
        return sortedResult;
    }

    private String binarySearchLine(String filepath, String queryTerm, int firstLineNum, int lastLineNum) {
        int cmpLineNum = (firstLineNum + lastLineNum) / 2;
        Stream<String> lines = getLinesStream(filepath);
        String cmpLine = lines.skip(cmpLineNum - 1).findFirst().get();
        List<String> tokenList = tokenizer.getTokensFromString(cmpLine, "[^a-zA-Z0-9/.]+");
        String cmpTerm = tokenList.get(0);

        if (cmpLineNum == lastLineNum) {
            if (queryTerm.compareTo(cmpTerm) == 0) // queryTerm is found
                return cmpLine;
            else
                return null;
        }
        else {
            if (queryTerm.compareTo(cmpTerm) < 0) {          // queryTerm is before cmpTerm
                return binarySearchLine(filepath, queryTerm, firstLineNum, cmpLineNum);
            } else if (queryTerm.compareTo(cmpTerm) > 0) {   // queryTerm is after cmpTerm
                return binarySearchLine(filepath, queryTerm, cmpLineNum + 1, lastLineNum);
            } else {                                         // queryTerm is found
                return cmpLine;
            }
        }
    }

    public Stream<String> getLinesStream(String filepath) {
        String line;
        Stream<String> lines = null;
        try {
            lines = Files.lines(Paths.get(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }


    private static class ByValue implements Comparator<Map.Entry<String, Double>> {
        @Override
        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
            return (o2.getValue()).compareTo( o1.getValue() );
        }
    }

    private ArrayList<Map.Entry<String, Double>>  sortByValueOrder(Map<String, Double> resultMap) {
        ArrayList<Map.Entry<String, Double>> sortList = new ArrayList<>(resultMap.entrySet());
        sortList.sort(BY_VALUE);
        return sortList;
    }

    public static void main (String arg[]){
        long start = System.currentTimeMillis();
        QueryMatching testObj = new QueryMatching();
        testObj.search("software engineering")
        //testObj.getContextScore("software engineering");
        //testObj.getTitleScore("software engineering");
        System.out.println(String.format("Time cost1 : %s ms", System.currentTimeMillis() - start));
    }

}
