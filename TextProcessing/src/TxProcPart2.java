import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjk98 on 1/21/2017.
 *
 * for finding intersection in two files
 */
public class TxProcPart2 {
    private String filePath1, filePath2;

    public TxProcPart2(String filePath1, String filePath2) {
        this.filePath1 = filePath1;
        this.filePath2 = filePath2;
    }

    public int findIntersection1() {
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);
        return 0;
    }

    private List<String> getTokenFromString (String str) {
        List<String> tokenList = new ArrayList<>();
        String[] tokenOfLine = str.split("[^\\w]+");                                                                // \W is a non-alphanum set, + means these delimiter occur one or more times
        for (String s: tokenOfLine) {
            if (s.length() != 0)
                tokenList.add(s.toLowerCase());
        }
        return tokenList;
    }

    public static void main (String arg[]){
        System.out.println("Finding Intersections");
    }
}
