import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cjk98 on 1/21/2017.
 * to reduce word map (merge 2 word maps into 1)
 */
public class TxProcPart3 {
    private FileReaderWBuffer fr1, fr2;
    private FileWriterWBuffer fw;
    private HashMap<String, Integer> finalMap;

    public TxProcPart3(String filePath1, String filePath2, String destPath) throws FileNotFoundException {
        fr1 = new FileReaderWBuffer(filePath1);
        fr2 = new FileReaderWBuffer(filePath2);
        File file = new File(destPath);
        if (file.exists())
            file.delete();
        fw = new FileWriterWBuffer(destPath, true);
        this.finalMap = new HashMap<>();
    }

    public void merge(){
        String line;
        while ((line = fr1.readLine()) != null){
            List<String> tokens = getTokenFromString(line);
            finalMap.put(tokens.get(0), Integer.valueOf(finalMap.getOrDefault(tokens.get(0), 0) + tokens.get(1)));
        }
        while ((line = fr2.readLine()) != null){
            List<String> tokens = getTokenFromString(line);
            finalMap.put(tokens.get(0), Integer.valueOf(finalMap.getOrDefault(tokens.get(0), 0) + tokens.get(1)));
        }
        for (Map.Entry<String, Integer> s : finalMap.entrySet()){
            fw.writeLine(s.getKey() + ", " + s.getValue());
        }
        fr1.close();
        fr2.close();
        fw.close();
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
        System.out.println("Reducing Map");
        System.out.println("FilePath1: " + arg[0]);
        System.out.println("FilePath2: " + arg[1]);
        TxProcPart3 object = new TxProcPart3(arg[0], arg[1], arg[2]);
        object.merge();
    }
}
