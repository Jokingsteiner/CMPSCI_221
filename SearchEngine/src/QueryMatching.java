import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by cjk98 on 3/4/2017.
 */
public class QueryMatching {
    private static String QUERY_STRING = "information retrieval";
    private static double ANCHOR_WEIGHT = 0.5;
    private static double TITLE_WEIGHT = 0.8;
    private static double HEADER_WEIGHT = 0.1;
    private static double CONTEXT_WEIGHT = 0.4;
    private static double URL_WEIGHT = 0.6;
    private final static int ANCHOR_LINES = 83564;
    private final static int TITLE_LINES = 12488;
    private final static int HEADER_LINES = 21770;
    private final static int CONTEXT_LINES = 488274;
    private final static int URL_LINES = 18030;
    private final static Comparator<Map.Entry<String, Double>> BY_VALUE = new QueryMatching.ByValue();
    private final static Tokenization tokenizer = new Tokenization();
    private final static HashMap<String, String> urlMap = new HashMap<>();

    private static HashMap<String, String> filepathMap = new HashMap<>();
    private static HashMap<String, ArrayList<DocNode>> anchorIndex;
    private static HashMap<String, ArrayList<DocNode>> titleIndex;
    private static HashMap<String, ArrayList<DocNode>> headerIndex;
    private static HashMap<String, ArrayList<DocNode>> contextIndex;
    private static HashMap<String, ArrayList<DocNode>> urlIndex;


    private HashMap<String, Double> finalResult = new HashMap<>();
    private RandomAccessFile raf;
    /** line number starts from 1 !!! */
    private HashMap<Integer, Integer> lineMap;

    private class ResultNode {
        public HashMap<String, Double> resultMap;
        private double maxScore;
        private int matchNumber;
        public double factor;

        public ResultNode(HashMap<String, Double> resultMap, double maxScore, int matchNumber) {
            this.resultMap = resultMap;
            this.maxScore = maxScore;
            this.matchNumber = matchNumber;
            this.factor = this.matchNumber / this.maxScore;
        }
    }

    private class DocNode {
        double score;
        String docID;
        public DocNode(double score, String docID) {
            this.score = score;
            this.docID = docID;
        }
    }

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
        fr.close();
        filepathMap.put("anchor", ".\\SearchEngine\\resources\\anchorIndex\\anchorScore.txt");
        filepathMap.put("title", ".\\SearchEngine\\resources\\titleIndex\\title_tfidfWeight.txt");
        filepathMap.put("header", ".\\SearchEngine\\resources\\headerIndex\\header_tfidfWeight.txt");
        filepathMap.put("context", ".\\SearchEngine\\resources\\contextIndex\\context_tfidfWeight.txt");
        filepathMap.put("url", ".\\SearchEngine\\resources\\urlIndex\\url_tfidfWeight.txt");
        anchorIndex = readIndex(filepathMap.get("anchor"));
        titleIndex = readIndex(filepathMap.get("title"));
        headerIndex = readIndex(filepathMap.get("header"));
        contextIndex = readIndex(filepathMap.get("context"));
        urlIndex = readIndex(filepathMap.get("url"));
    }

    public ArrayList<String> search(String queryString, int desiredNum) {
        long startTime;
        int count;

        finalResult.clear();
        startTime = System.currentTimeMillis();
        ResultNode anchorResult = calScoreLocal(queryString, titleIndex, ANCHOR_WEIGHT);
        for(Map.Entry<String, Double> e : anchorResult.resultMap.entrySet())
            finalResult.put(e.getKey(), finalResult.getOrDefault(e.getKey(), 0.0) + e.getValue() * anchorResult.factor);
//        System.out.println(String.format("Search by AnchorText Done: %s ms Elapsed", System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        ResultNode titleResult = calScoreLocal(queryString, titleIndex, TITLE_WEIGHT);
        for(Map.Entry<String, Double> e : titleResult.resultMap.entrySet())
            finalResult.put(e.getKey(), finalResult.getOrDefault(e.getKey(), 0.0) + e.getValue() * titleResult.factor);
//        System.out.println(String.format("Search by Title Done: %s ms Elapsed", System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        ResultNode headerResult = calScoreLocal(queryString, headerIndex, HEADER_WEIGHT);
        for(Map.Entry<String, Double> e : headerResult.resultMap.entrySet())
            finalResult.put(e.getKey(), finalResult.getOrDefault(e.getKey(), 0.0) + e.getValue() * headerResult.factor);
//        System.out.println(String.format("Search by Header Done: %s ms Elapsed", System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        ResultNode contextResult = calScoreLocal(queryString, contextIndex, CONTEXT_WEIGHT);
        for(Map.Entry<String, Double> e : contextResult.resultMap.entrySet())
            finalResult.put(e.getKey(), finalResult.getOrDefault(e.getKey(), 0.0) + e.getValue() * contextResult.factor);
//        System.out.println(String.format("Search by Context Done: %s ms Elapsed", System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        ResultNode urlResult = calScoreLocal(queryString, urlIndex, URL_WEIGHT);
        for(Map.Entry<String, Double> e : urlResult.resultMap.entrySet())
            finalResult.put(e.getKey(), finalResult.getOrDefault(e.getKey(), 0.0) + e.getValue() * urlResult.factor);
//        System.out.println(String.format("Search by URL Done: %s ms Elapsed", System.currentTimeMillis() - startTime));

//        //TODO: uncomment this to write result into a file
//        ArrayList<Map.Entry<String, Double>> resultList = sortByValueOrder(finalResult);
//        FileWriterWBuffer fw = new FileWriterWBuffer(".\\SearchEngine\\"+ queryString + "Results.txt", false);
//        for (int i = 0; i < Math.min(desiredNum, resultList.size()); i++) {
//            String writeLine = resultList.get(i).getKey() + " " + urlMap.get(resultList.get(i).getKey());
//            fw.writeLine(writeLine);
//        }
//        fw.close();
        return getTopResults(finalResult, desiredNum);
    }

    public ArrayList<String> search2(String queryString, int desiredNum) {
        long startTime;
        int count;

        startTime = System.currentTimeMillis();
        ResultNode anchorResult = calScoreForDoc(queryString, filepathMap.get("anchor"), ANCHOR_LINES, ANCHOR_WEIGHT);
        for(Map.Entry<String, Double> e : anchorResult.resultMap.entrySet())
            finalResult.put(e.getKey(), finalResult.getOrDefault(e.getKey(), 0.0) + e.getValue() * anchorResult.factor);
        for(Map.Entry<String, Double> e : anchorResult.resultMap.entrySet())
            System.out.println(e.getKey() + "     " + e.getValue());
        System.out.println(String.format("Search by AnchorText Done: %s ms Elapsed", System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        ResultNode titleResult = calScoreForDoc(queryString, filepathMap.get("title"), TITLE_LINES, TITLE_WEIGHT);
        for(Map.Entry<String, Double> e : titleResult.resultMap.entrySet())
            finalResult.put(e.getKey(), finalResult.getOrDefault(e.getKey(), 0.0) + e.getValue() * titleResult.factor);
        count = 0;
        for(Map.Entry<String, Double> e : titleResult.resultMap.entrySet()) {
//            if (count++ < 30 && e != null)
            System.out.println(e.getKey() + "     " + e.getValue());
        }
        System.out.println(String.format("Search by Title Done: %s ms Elapsed", System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        ResultNode headerResult = calScoreForDoc(queryString, filepathMap.get("header"), HEADER_LINES, HEADER_WEIGHT);
        for(Map.Entry<String, Double> e : headerResult.resultMap.entrySet())
            finalResult.put(e.getKey(), finalResult.getOrDefault(e.getKey(), 0.0) + e.getValue() * headerResult.factor);
        count = 0;
        for(Map.Entry<String, Double> e : headerResult.resultMap.entrySet()) {
//            if (count++ < 30 && e != null)
            System.out.println(e.getKey() + "     " + e.getValue());
        }
        System.out.println(String.format("Search by Header Done: %s ms Elapsed", System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        ResultNode contextResult = calScoreForDoc(queryString, filepathMap.get("context"), CONTEXT_LINES, CONTEXT_WEIGHT);
        for(Map.Entry<String, Double> e : contextResult.resultMap.entrySet())
            finalResult.put(e.getKey(), finalResult.getOrDefault(e.getKey(), 0.0) + e.getValue() * contextResult.factor);
        count = 0;
        for(Map.Entry<String, Double> e : contextResult.resultMap.entrySet()) {
//            if (count++ < 30 && e != null)
            System.out.println(e.getKey() + "     " + e.getValue());
        }
        System.out.println(String.format("Search by Context Done: %s ms Elapsed", System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        ResultNode urlResult = calScoreForDoc(queryString, filepathMap.get("url"), URL_LINES, URL_WEIGHT);
        for(Map.Entry<String, Double> e : urlResult.resultMap.entrySet())
            finalResult.put(e.getKey(), finalResult.getOrDefault(e.getKey(), 0.0) + e.getValue() * urlResult.factor);
        count = 0;
        for(Map.Entry<String, Double> e : urlResult.resultMap.entrySet()) {
//            if (count++ < 30 && e != null)
            System.out.println(e.getKey() + "     " + e.getValue());
        }
        System.out.println(String.format("Search by URL Done: %s ms Elapsed", System.currentTimeMillis() - startTime));

        ArrayList<Map.Entry<String, Double>> resultList = sortByValueOrder(finalResult);

//        //TODO: uncomment this to write result into a file
//        FileWriterWBuffer fw = new FileWriterWBuffer(".\\SearchEngine\\top100Results.txt", false);
//        for (int i = 0; i < Math.min(100, resultList.size()); i++) {
//            String writeLine = resultList.get(i).getKey() + " " + urlMap.get(resultList.get(i).getKey());
//            fw.writeLine(writeLine);
//        }
//        fw.close();
        return getTopResults(finalResult, desiredNum);
    }

    private HashMap<String, ArrayList<DocNode>> readIndex(String filepath) {
        HashMap<String, ArrayList<DocNode>> rtnMap = new HashMap<>();
        FileReaderWBuffer fr = new FileReaderWBuffer(filepath);
        ArrayList<String> lines = fr.readAll();
        for (String s : lines) {
            List<String> tokenList = tokenizer.getTokensFromString(s, "[^a-zA-Z0-9/.]+");
            ArrayList<DocNode> docList = new ArrayList<>();
            String term = tokenList.get(0);
            for (int i = 1; i < tokenList.size(); i+= 2) {
                double score = Double.valueOf(tokenList.get(i));
                String docID = tokenList.get(i + 1);
                DocNode newNode = new DocNode(score, docID);
                docList.add(newNode);
            }
            rtnMap.put(term, docList);
        }
        return rtnMap;
    }

    private ResultNode calScoreLocal(String queryString, HashMap<String, ArrayList<DocNode>> indexMap, double weight) {
        List<String> queryTokens = tokenizer.getTokensFromString(queryString.toLowerCase(), "[^a-zA-Z0-9/]+");
        HashMap<String, Double> resultDocMap = new HashMap<String, Double>();
        double maxScore = Double.MIN_VALUE;
        int matchNum = 0;
        for (String queryTerm: queryTokens) {
            ArrayList<DocNode> foundResult = indexMap.get(queryTerm);
            if (foundResult == null) {
//                System.out.println("Didn't find line with \"" + queryTerm + "\"");
            }
            else {
                // found the queryTerm in our index
                //System.out.println("Found: " + foundResult);
                matchNum++;
                for (DocNode dn : foundResult) {
                    if (dn.score > maxScore)
                        maxScore = dn.score;
                    // find all docID that this term is in
                    resultDocMap.put(dn.docID, resultDocMap.getOrDefault(dn.docID, 0.0) + dn.score * weight);
                }
            }
        }

//        System.out.println("Max Score is " + maxScore);
        return new ResultNode(resultDocMap, maxScore, matchNum);
    }

    private ResultNode calScoreForDoc(String queryString, String filepath, int numOfLine, double weight) {
        List<String> queryTokens = tokenizer.getTokensFromString(queryString.toLowerCase(), "[^a-zA-Z0-9/]+");
        HashMap<String, Double> resultDocMap = new HashMap<String, Double>();
        double maxScore = Double.MIN_VALUE;
        int matchNum = 0;
        for (String queryTerm: queryTokens) {
            try {
                raf = new RandomAccessFile(filepath, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            lineMap = readOffsetFile(filepath.replace(".txt", "") + "Offset.txt");
            String foundResult = binarySearchLine(filepath, queryTerm, 1, numOfLine);
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            String foundResult = simpleSearchLine(filepath, queryTerm);
            if (foundResult == null)
                System.out.println("Didn't find line with \"" + queryTerm + "\"");
            else {
                // found the queryTerm in our index
                //System.out.println("Found: " + foundResult);
                matchNum++;
                List<String> tokenList = tokenizer.getTokensFromString(foundResult, "[^a-zA-Z0-9/.]+");

                for (int i = 1; i < tokenList.size(); i+= 2) {
                double score = Double.valueOf(tokenList.get(i));
                if (score > maxScore)
                    maxScore = score;
                // find all docID that this term is in
                String docID = tokenList.get(i + 1);
                resultDocMap.put(docID, resultDocMap.getOrDefault(docID, 0.0) + score * weight);
                }
            }
        }

        System.out.println("Max Score is " + maxScore);
        return new ResultNode(resultDocMap, maxScore, matchNum);
    }

    // line number starts from 1!!!!!
    private String binarySearchLine(String filepath, String queryTerm, int firstLineNum, int lastLineNum) {
        int cmpLineNum = (firstLineNum + lastLineNum) / 2;
//        Stream<String> lines = getLinesStream(filepath);
//        String cmpLine = lines.skip(cmpLineNum - 1).findFirst().get();
        String cmpLine = randomAccessLine(lineMap.get(cmpLineNum));
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

    private String randomAccessLine(int startBytes) {
        try {
            raf.seek(startBytes);
            return raf.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO: remove me
    public String testRandomAccess(String filepath, int startBytes) {
        try {
            raf = new RandomAccessFile(filepath, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = randomAccessLine(startBytes);
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    /**
     * line number starts from 1!
     * @param filePath
     * @return
     */
    private HashMap<Integer, Integer> readOffsetFile (String filePath) {
        HashMap<Integer, Integer> lineMap = new HashMap<>();
        FileReaderWBuffer fr = new FileReaderWBuffer(filePath);
        List<String> tokens = tokenizer.getTokensFromFile(filePath);
        for (int i = 0; i < tokens.size(); i++)
            lineMap.put(i + 1, Integer.valueOf(tokens.get(i)));
        fr.close();
        return lineMap;
    }

    private static class ByValue implements Comparator<Map.Entry<String, Double>> {
        @Override
        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
            return (o2.getValue()).compareTo( o1.getValue() );
        }
    }

    private ArrayList<Map.Entry<String, Double>> sortByValueOrder(Map<String, Double> resultMap) {
        ArrayList<Map.Entry<String, Double>> sortList = new ArrayList<>(resultMap.entrySet());
        sortList.sort(BY_VALUE);
        return sortList;
    }

    private ArrayList<String> getTopResults(Map<String, Double> resultMap, int topNum) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Map.Entry<String, Double>> sortList = new ArrayList<>(resultMap.entrySet());
        sortList.sort(BY_VALUE);
        for (int i = 0; i < Math.min(topNum, sortList.size()); i++)
            result.add(urlMap.get(sortList.get(i).getKey()));
        return result;
    }

    public void offsetHelper() {
//        ArrayList<String> fileList = new ArrayList<String>();
//        fileList.add(".\\SearchEngine\\resources\\anchorIndex\\anchorScore.txt");
//        fileList.add(".\\SearchEngine\\resources\\titleIndex\\title_tfidfWeight.txt");
//        fileList.add(".\\SearchEngine\\resources\\headerIndex\\header_tfidfWeight.txt");
//        fileList.add(".\\SearchEngine\\resources\\contextIndex\\context_tfidfWeight.txt");
        for (Map.Entry<String, String> e: filepathMap.entrySet()) {
            String filepath = e.getValue();
            new GetFileLineOffset(filepath, filepath.replace(".txt", "") + "Offset.txt");
        }
    }

    public void setAnchorWeight(double weight) {
        this.ANCHOR_WEIGHT = weight;
    }

    public void setTitleWeight(double weight) {
        this.TITLE_WEIGHT = weight;
    }

    public void setHeaderWeight(double weight) {
        this.HEADER_WEIGHT = weight;
    }

    public void setContextWeight(double weight) {
        this.CONTEXT_WEIGHT = weight;
    }

    public void setUrlWeight(double weight) {
        this.URL_WEIGHT = weight;
    }

    public static void main (String arg[]){
        long start = System.currentTimeMillis();
        QueryMatching testObj = new QueryMatching();
        testObj.search(QUERY_STRING, 10);
//        testObj.offsetHelper();

//        HashMap<Integer, Integer> lineMap = testObj.readOffsetFile(filepathMap.get("title").replace(".txt", "") + "Offset.txt");
//        System.out.println(testObj.testRandomAccess(filepathMap.get("title"), lineMap.get(10)));
        System.out.println(String.format("Total Time cost : %s ms", System.currentTimeMillis() - start));
    }

}
