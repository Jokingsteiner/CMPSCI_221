import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by cjk98 on 1/21/2017.
 * to write file
 */
public class FileWriterWBuffer {
    private FileWriter fw;
    private BufferedWriter bw;

    public FileWriterWBuffer(String filePath, boolean append) {
        try {
            this.fw = new FileWriter(filePath, append);
            this.bw = new BufferedWriter(fw);
        } catch (IOException e) {
            System.out.println("Error: Open file to write");
            System.out.println(filePath);
            e.printStackTrace();
        }
    }

    public void write(String content) {
        try {
            bw.write(content);
        } catch (IOException e) {
            System.out.println("Write file failed");
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeLine(String content) {
        try {
            bw.write(content);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Write file failed");
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            bw.close();
        } catch (IOException e) {
            System.out.println("Close file failed");
            e.printStackTrace();
        }
    }
}
