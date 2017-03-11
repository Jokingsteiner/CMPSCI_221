/**
 * Created by cjk98 on 3/9/2017.
 */
public class GetFileLineOffset {
    private String filePath;
    private String outputPath;

    public GetFileLineOffset(String filePath, String outputPath) {
        this.filePath = filePath;
        this.outputPath = outputPath;
        this.generateOffset();
    }

    public void generateOffset(){
        FileReaderWBuffer fr = new FileReaderWBuffer(this.filePath);
        FileWriterWBuffer fw = new FileWriterWBuffer(this.outputPath, false);
        long offset = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(Long.toString(offset)+ " ");

        String line;
        while ( (line = fr.readLine()) != null) {
            offset = offset + line.length() + "\n".length() + 1;
            // TODO: change the 6 bytes "\n" + " "?? to 4 bytes or less in different system
            sb.append(Long.toString(offset)+ " ");
        }
        fw.writeLine(sb.toString());
        fr.close();
        fw.close();

    }

}
