import java.net.URL;
import java.util.ArrayList;

/**
 * Created by cjk98 on 3/17/2017.
 */
public class CalNDCG {
    private final static ArrayList<String> queryTerms = new ArrayList<>();
    private static QueryMatching qmObj;
    private static NDCG ndcgObj;
    private static double maxAvgNDCG = Double.MIN_VALUE;

    public CalNDCG() {
        System.out.printf("Initializing...");
        long start = System.currentTimeMillis();
        queryTerms.add("mondego");
        queryTerms.add("machine learning");
        queryTerms.add("software engineering");
        queryTerms.add("security");
        queryTerms.add("student affairs");
        queryTerms.add("graduate courses");
        queryTerms.add("Crista Lopes");
        queryTerms.add("REST");
        queryTerms.add("computer games");
        queryTerms.add("information retrieval");
        qmObj = new QueryMatching();
        ndcgObj = new NDCG();
        System.out.printf(String.format("%s ms passed", System.currentTimeMillis() - start) + '\n');
    }

    public static void testBestNDCG() {
        ArrayList<String> allQueryResults = new ArrayList<>();
        double sumOfNDCG;
        double anchorWeight, titleWeight, contextWeight, headerWeight, URLWeight;
        double maxanchorWeight = 0, maxtitleWeight = 0, maxcontextWeight = 0, maxheaderWeight = 0, maxURLWeight = 0;
        for (anchorWeight = 0.05; anchorWeight <= 0.33; anchorWeight += 0.05) {
            qmObj.setAnchorWeight(anchorWeight);
            for (titleWeight = 0.4; titleWeight <= 0.83; titleWeight += 0.05) {
                qmObj.setTitleWeight(titleWeight);
                for (headerWeight = 0; headerWeight <= 0.18; headerWeight += 0.05) {
                    qmObj.setHeaderWeight(headerWeight);
                    for (contextWeight = 0.3; contextWeight <= 1.03; contextWeight += 0.05) {
                        qmObj.setContextWeight(contextWeight);
                        for (URLWeight = 0.3; URLWeight <= 0.73; URLWeight += 0.05) {
                            qmObj.setUrlWeight(URLWeight);
                            // query
                            sumOfNDCG = 0;
                            allQueryResults.clear();
                            for (String s : queryTerms)
                                allQueryResults.addAll(qmObj.search(s, 5));
                            for (int i = 0; i < 10; i++)
                                sumOfNDCG += ndcgObj.calNDCG(allQueryResults, i);
//                            System.out.println(sumOfNDCG / 10);
                            if (maxAvgNDCG < sumOfNDCG / 10) {
                                maxAvgNDCG = sumOfNDCG / 10;
                                maxanchorWeight = anchorWeight; maxtitleWeight = titleWeight; maxheaderWeight = headerWeight; maxcontextWeight = contextWeight; maxURLWeight = URLWeight;
//                                System.out.println("Max Avg NDCG is " + maxAvgNDCG + String.format(" @%.2f %.2f %.2f %.2f %.2f, working on %.2f %.2f %.2f %.2f %.2f", maxanchorWeight, maxtitleWeight, maxheaderWeight, maxcontextWeight, maxURLWeight, anchorWeight, titleWeight, headerWeight, contextWeight, URLWeight));
                            }
                            System.out.println("Max Avg NDCG is " + maxAvgNDCG + String.format(" @%.2f %.2f %.2f %.2f %.2f, working on %.2f %.2f %.2f %.2f %.2f", maxanchorWeight, maxtitleWeight, maxheaderWeight, maxcontextWeight, maxURLWeight, anchorWeight, titleWeight, headerWeight, contextWeight, URLWeight));

                        }
                    }
                }
            }
        }
    }



    public static void main (String arg[]){
        CalNDCG testObj = new CalNDCG();
        testObj.testBestNDCG();
    }
}
