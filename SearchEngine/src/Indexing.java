import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cjk98 on 2/25/2017.
 */
public class Indexing {
    private String baseAddr = "H:\\WebRAW\\WEBPAGES_RAW";
    private int maxFolderID = 0;
    private int maxDocID = 511;
    String regexBetweenMarkers;
    Pattern patternBetweenMarkers;
    private final static int NUM_OF_DOCUMENTS = 37497;


    public Indexing() {
        this.regexBetweenMarkers = Pattern.quote("<title>") + "(.*?)" + Pattern.quote("</title>");
        this.patternBetweenMarkers = Pattern.compile(regexBetweenMarkers);
    }

    private class PostingNode {
        double tfIdf;
        String word;
        LinkedList<String> docIDList = new LinkedList<String>();

        public PostingNode(String word) {
            this.word = word;
        }

        public void setTFIDF (double tfIdf) {
            this.tfIdf = tfIdf;
        }

        public void addDocID (String docID) {
            this.docIDList.add(docID);
        }
    }

    public void buildIndex (){
        String addr, fileString;
        Tokenization tokenizer = new Tokenization();
        List<String> tokenList = new LinkedList<String>();
        FileWriterWBuffer fw = new FileWriterWBuffer("unsorted.txt", false);

        for (int folderID = 0; folderID <= maxFolderID; folderID++) {
            for (int docID = 0; docID <= maxDocID; docID++) {
                addr = baseAddr + "\\" + Integer.toString(folderID) + "\\" + Integer.toString(docID);
                // make sure that file exists
                File file = new File(addr);
                if (!file.exists())
                    continue;
                FileReaderWBuffer fr = new FileReaderWBuffer(addr);
                fileString = fr.readFileToString(StandardCharsets.UTF_8);
                Matcher matcher = patternBetweenMarkers.matcher(fileString);
                while (matcher.find()) {
                    System.out.println(folderID + "/" + docID + ": " + matcher.group(1));
                    tokenList = tokenizer.getTokensFromString(matcher.group(1), "[^a-zA-Z0-9]+");
//                  NOT FINISHED, tf and idf is done by another python program
                    for (String s : tokenList) {
//                        fw.writeLine(token);
                    }
                }
                fr.close();
            }
    }
        }

    private double calculateTFIDF (int tf, int df, int docNum) {
        double result = Math.log10(1 + tf) * Math.log10(docNum/df);
        return result;
    }

    public void buildTFIDF(String tfResult, String dfResult, String writePath){
        FileReaderWBuffer tfReader = new FileReaderWBuffer(tfResult);
        FileReaderWBuffer dfReader = new FileReaderWBuffer(dfResult);
        FileWriterWBuffer tfidfWriter = new FileWriterWBuffer(writePath, false);
        Tokenization tokenizer = new Tokenization();

        String tfLine, dfLine, writeString;
        List<String> tfTokens = null, dfTokens = null;
        LinkedList<PostingNode> nodeQueue = new LinkedList<PostingNode>();

        tfLine = tfReader.readLine();
        dfLine = dfReader.readLine();
        while ( tfLine != null && dfLine != null) {
            dfTokens = tokenizer.getTokensFromString(dfLine, "[^a-zA-Z0-9/]+");                               // \W is a non-alphanumeric set, + means these delimiter occur one or more times
            int docFreq = Integer.parseInt(dfTokens.get(1));
            PostingNode newNode = new PostingNode(dfTokens.get(0));
            for (int i = 0; i < docFreq; i++) {
                tfTokens = tokenizer.getTokensFromString(tfLine, "[^a-zA-Z0-9/]+");                           // \W is a non-alphanumeric set, + means these delimiter occur one or more times
                if ( tfTokens.get(0).compareTo(dfTokens.get(0)) == 0 ) {                                                // make sure we are processing same term
                    double tfIdf = calculateTFIDF(Integer.parseInt(tfTokens.get(1)), Integer.parseInt(dfTokens.get(1)), NUM_OF_DOCUMENTS);
                    if (i == 0)
                        newNode.setTFIDF (tfIdf);
                    newNode.addDocID(tfTokens.get(2));
                // move to next tf, still same word
                tfLine = tfReader.readLine();
                }
                else {
                    throw new IllegalArgumentException("Mismatching words: " + tfTokens.get(0) + "   " + dfTokens.get(0));
                }
            }
            nodeQueue.add(newNode);
            // move df to next one, continue
            dfLine = dfReader.readLine();
        }

        for (PostingNode node : nodeQueue) {
//            if (node.word.length() < 1)
//                writeString = String.format("%-50s", node.word) + String.format("%10f", node.tfIdf) + "   ";
//            else
                writeString = node.word + " " + String.format("%f", node.tfIdf) + " ";

            for (String s : node.docIDList) {
                writeString = writeString + s + " ";
            }
            tfidfWriter.writeLine(writeString);
        }

        tfReader.close();
        dfReader.close();
        tfidfWriter.close();
    }

    public static void main (String arg[]){
        long start = System.currentTimeMillis();

        Indexing testIndexer = new Indexing();
//        testIndexer.buildIndex();
        testIndexer.buildTFIDF(arg[0], arg[1], arg[2]);
        System.out.println(String.format("Time cost1 : %s ms", System.currentTimeMillis() - start));
    }
}
