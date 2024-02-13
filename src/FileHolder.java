import Model.Algorithm.AES;
import Model.Algorithm.Block;
import Model.Key_Controller.Key;
import Model.Key_Controller.KeyGenerator;
import Model.ThreadBlock;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileHolder {
    private String filepath;
    private Queue<Block> blocks;

    public FileHolder(String filepath) {
        this.filepath = filepath;
        this.blocks = new ArrayDeque<>();
        readAndSeparateIntoBlocks();
    }

    private void readAndSeparateIntoBlocks() {
        try (FileInputStream fileInputStream = new FileInputStream(filepath)) {
            byte[] buffer = new byte[16];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byte[] blockBytes = new byte[bytesRead];
                System.arraycopy(buffer, 0, blockBytes, 0, bytesRead);
                blocks.add(new Block(bytesToHexString(blockBytes)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder hexNumbers = new StringBuilder();
        for (byte b : bytes) {
            hexNumbers.append(String.format("%02x", b));
        }
        return hexNumbers.toString();
    }

    public String getFilePath() {
        return filepath;
    }

    public Queue<Block> getBlocks() {
        return blocks;
    }

    public void writeBlocksToBinaryFile(String outputFilePath) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {
            for (Block block : blocks) {
                String hexString = AES.writeMatrix(block.getData(),4,4);
                byte[] binaryData = hexStringToBytes(hexString);
                fileOutputStream.write(binaryData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private byte[] hexStringToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len-1; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return data;
    }

    public void EnctyptFile(AES aes){
        for (Block block : blocks){
            aes.Encrypt(block);
        }
    }

    public void DecryptFile(AES aes){
        for (Block block : blocks){
            aes.Decrypt(block);
        }
    }

    public static void main(String[] args) throws Exception {
        FileHolder fileHolder = new FileHolder("C:\\Users\\Vivien\\Downloads\\FIFA 19-Xbox 360-Multi[Jtag-RGH]-eNJoY-iT.rar");
        System.out.println("File Path: " + fileHolder.getFilePath());

        Key k = new Key(KeyGenerator.generateKey("vladi1977", "kjhjkhjlkjlkj").toCharArray());
        AES algo = new AES(k);

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (Block block : fileHolder.blocks) {
            ThreadBlock bl = new ThreadBlock(block, algo, Boolean.TRUE);
            executorService.submit(bl);
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        fileHolder.writeBlocksToBinaryFile(fileHolder.getFilePath());
    }
}