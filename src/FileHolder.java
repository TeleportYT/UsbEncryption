import Model.Algorithm.AES;
import Model.Algorithm.Block;
import Model.Key_Controller.KeyGenerator;
import Model.Key_Controller.Key;
import Model.ThreadBlock;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileHolder {
    private String filepath;
    private Queue<Block> blocks;
    private static final int CHUNK_SIZE = 1024 * 1024; // 1 MB

    public FileHolder(String filepath) {
        this.filepath = filepath;
        this.blocks = new ArrayDeque<>();
        readAndSeparateIntoBlocks();
    }

    private void readAndSeparateIntoBlocks() {
        try (FileInputStream fileInputStream = new FileInputStream(filepath);
             FileChannel fileChannel = fileInputStream.getChannel()) {
            long fileSize = fileChannel.size();
            long position = 0;

            while (position < fileSize) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(CHUNK_SIZE);
                int bytesRead = fileChannel.read(byteBuffer, position);
                position += bytesRead;

                if (bytesRead == CHUNK_SIZE || position == fileSize) {
                    byteBuffer.flip();
                    byte[] buffer = new byte[bytesRead];
                    byteBuffer.get(buffer);
                    blocks.add(new Block(bytesToHexString(buffer)));
                } else {
                    byteBuffer.flip();
                    byte[] buffer = new byte[bytesRead];
                    byteBuffer.get(buffer);
                    blocks.add(new Block(bytesToHexString(buffer)));
                    position -= (CHUNK_SIZE - bytesRead);
                }
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
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(outputFilePath, "rw");
             FileChannel fileChannel = randomAccessFile.getChannel()) {
            long position = 0;

            for (Block block : blocks) {
                String hexString = AES.writeMatrix(block.getData(),4,4);
                byte[] binaryData = hexStringToBytes(hexString);

                ByteBuffer byteBuffer = ByteBuffer.wrap(binaryData);
                fileChannel.write(byteBuffer, position);
                position += binaryData.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private byte[] hexStringToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len - 1; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return data;
    }

    public void EnctyptFile(AES aes) {
        for (Block block : blocks) {
            aes.Encrypt(block);
        }
    }

    public void DecryptFile(AES aes) {
        for (Block block : blocks) {
            aes.Decrypt(block);
        }
    }
    public static void main(String[] args) throws Exception {
        FileHolder fileHolder = new FileHolder("C:\\Users\\Vivien\\Downloads\\17763.737.190906-2324.rs5_release_svc_refresh_SERVER_EVAL_x64FRE_en-us_1.iso");
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

        fileHolder.writeBlocksToBinaryFile("C:\\Users\\Vivien\\Downloads\\webrootE.zip");
    }
}
