import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by cjk98 on 1/21/2017.
 *
 * for finding intersection in two files
 */
public class TxProcPart2 {
    private FileReaderWBuffer fr1, fr2;
    // testing
    public HashSet<String> commonList = null;

    public TxProcPart2(String filePath1, String filePath2) throws FileNotFoundException {
        fr1 = new FileReaderWBuffer(filePath1);
        fr2 = new FileReaderWBuffer(filePath2);
    }

    // brutal method
    public int findIntersection1() {
        HashSet<String> cmnTokenSet = new HashSet<>();
        HashSet<String> tokenSet2 = new HashSet<>();
        String line;

        // initiate common token set
        while ((line = fr1.readLine()) != null)
            cmnTokenSet.addAll(getTokenFromString(line));

        // read second file
        while ((line = fr2.readLine()) != null) {
            tokenSet2.addAll(getTokenFromString(line));
        }
        cmnTokenSet.retainAll(tokenSet2);
        commonList = new HashSet<String>(cmnTokenSet);

        fr1.close();
        fr2.close();
        return cmnTokenSet.size();
    }

    private List<String> getTokenFromString (String str) {
        List<String> tokenList = new ArrayList<>();
        String[] tokenOfLine = str.split("[^\\w]+");                                                                // \W is a non-alphanumeric set, + means these delimiter occur one or more times
        for (String s: tokenOfLine) {
            if (s.length() != 0)
                tokenList.add(s.toLowerCase());
        }
        return tokenList;
    }

    public static void main (String arg[]) throws FileNotFoundException {
        System.out.println("Finding Intersections");
        System.out.println("FilePath1: " + arg[0]);
        System.out.println("FilePath2: " + arg[1]);
        TxProcPart2 object = new TxProcPart2(arg[0], arg[1]);
        System.out.println(object.findIntersection1());
        for (String s : object.commonList)
            System.out.println(s);
    }
}
