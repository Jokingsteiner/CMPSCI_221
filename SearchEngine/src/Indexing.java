import java.io.File;
import java.nio.charset.StandardCharsets;
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


    public Indexing() {
        this.regexBetweenMarkers = Pattern.quote("<title>") + "(.*?)" + Pattern.quote("</title>");
        this.patternBetweenMarkers = Pattern.compile(regexBetweenMarkers);
    }

    private class PostingNode {
        int termFrequency;
        int documentFrequency;
        double tf_idf;
        String docID;

        public PostingNode(int termFrequency, int documentFrequency, String docID) {
            this.termFrequency = termFrequency;
            this.documentFrequency = documentFrequency;
            this.docID = docID;
        }
    }

    public void buildIndex (){
        String addr, fileString;
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
                }
                fr.close();
            }
        }


    }


    public static void main (String arg[]){
        long start = System.currentTimeMillis();

        Indexing testIndexer = new Indexing();
        testIndexer.buildIndex();

        System.out.println(String.format("Time cost1 : %s ms", System.currentTimeMillis() - start));
    }
}
