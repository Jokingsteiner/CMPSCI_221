import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by cjk98 on 1/21/2017.
 * to reduce word map (merge 2 word maps into 1)
 */
public class TxProcPart3 {
//    private static final Comparator<Map.Entry<String, Integer>> BY_KEY_ALPHABETICALLY = new ByKeyAlphabetically();
    private FileReaderWBuffer fr1, fr2;
    private FileWriterWBuffer fw;
    // ordered with natural order of keys
    private TreeMap<String, Integer> finalMap;

    public TxProcPart3(String filePath1, String filePath2, String destPath) throws FileNotFoundException {
        fr1 = new FileReaderWBuffer(filePath1);
        fr2 = new FileReaderWBuffer(filePath2);
        File file = new File(destPath);
        if (file.exists())
            file.delete();
        fw = new FileWriterWBuffer(destPath, true);
        this.finalMap = new TreeMap<>();
    }

    public void merge2(){
        String line;
        while ((line = fr1.readLine()) != null){
            List<String> tokens = getTokenFromString(line);
            finalMap.put(tokens.get(0), finalMap.getOrDefault(tokens.get(0), 0) + Integer.valueOf(tokens.get(1)));
        }
        while ((line = fr2.readLine()) != null){
            List<String> tokens = getTokenFromString(line);
            finalMap.put(tokens.get(0), finalMap.getOrDefault(tokens.get(0), 0) + Integer.valueOf(tokens.get(1)));
        }

        for (Map.Entry<String, Integer> s : finalMap.entrySet()){
            fw.writeLine(s.getKey() + ", " + s.getValue());
        }
        fr1.close();
        fr2.close();
        fw.close();
    }

    public void merge(){
        String line1 = null, line2 = null;
        List<String> tokens1 = null, tokens2 = null;
        line1 = fr1.readLine();
        line2 = fr2.readLine();
        while ( line1 != null && line2 != null) {
            tokens1 = getTokenFromString(line1);
            tokens2 = getTokenFromString(line2);
            if ( tokens1.get(0).compareTo(tokens2.get(0)) < 0 ) {                                                       //tokens1[0] is smaller
                fw.writeLine( tokens1.get(0) + ", " +  tokens1.get(1));
                line1 = fr1.readLine();
            }
            else if ( tokens1.get(0).compareTo(tokens2.get(0)) > 0 ) {                                                  //tokens1[0] is bigger
                fw.writeLine( tokens2.get(0) + ", " +  tokens2.get(1));
                line2 = fr2.readLine();
            }
            else {                                                                                                       //equal
                int sum =  Integer.parseInt(tokens1.get(1)) + Integer.parseInt(tokens2.get(1));
                fw.writeLine( tokens1.get(0) + ", " +  sum);
                line1 = fr1.readLine();
                line2 = fr2.readLine();
            }
        }

        if ( line1 == null) {
            do {
                tokens2 = getTokenFromString(line2);
                fw.writeLine(tokens2.get(0) + ", " + tokens2.get(1));
            } while ((line2 = fr2.readLine()) != null);
        }
        else if (line2 == null) {
            do {
                tokens1 = getTokenFromString(line1);
                fw.writeLine(tokens1.get(0) + ", " + tokens1.get(1));
            } while ((line1 = fr1.readLine()) != null);
        }

        fr1.close();
        fr2.close();
        fw.close();
    }

/*
    private static class ByKeyAlphabetically implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return o1.getKey().compareTo(o2.getKey());
        }
    }*/

    private List<String> getTokenFromString (String str) {
        List<String> tokenList = new ArrayList<>();
        String[] tokenOfLine = str.split("[^a-zA-Z0-9]+");                                                                // \W is a non-alphanumeric set, + means these delimiter occur one or more times
        for (String s: tokenOfLine) {
            if (s.length() != 0)
                tokenList.add(s.toLowerCase());
        }
        return tokenList;
    }

    public static void main (String arg[]) throws FileNotFoundException {
        System.out.println("Reducing Map");
        System.out.println("FilePath1: " + arg[0]);
        System.out.println("FilePath2: " + arg[1]);
        long start = System.currentTimeMillis();
        TxProcPart3 object = new TxProcPart3(arg[0], arg[1], arg[2]);
        object.merge();
        System.out.println(String.format("Time cost1 : %s ms", System.currentTimeMillis() - start));
    }
}
