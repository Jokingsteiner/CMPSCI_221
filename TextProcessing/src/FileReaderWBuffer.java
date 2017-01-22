import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static javafx.scene.input.KeyCode.L;

/**
 * Created by cjk98 on 1/21/2017.
 * for convenience
 */
public class FileReaderWBuffer {
    private FileReader fr = null;
    private BufferedReader br = null;
    private String filePath;

    public FileReaderWBuffer(String filePath) throws FileNotFoundException {
        this.filePath = filePath;
        fr = new FileReader(filePath);
        br = new BufferedReader(fr);
    }

    // read a line
    public String readLine() {
        try {
            return br.readLine();
        } catch (IOException e) {
            System.out.println("readline() fail for BufferedReader!");
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            if (br != null && fr != null)
            br.close();
        } catch (IOException e) {
            System.out.println("BufferedReader close failed!");
            e.printStackTrace();
        }
    }

}
