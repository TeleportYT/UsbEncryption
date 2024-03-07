import Model.Algorithm.AES;
import Model.Algorithm.Block;
import Model.Key_Controller.KeyGenerator;
import Model.Key_Controller.Key;
import Model.ThreadBlock;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileHolder {
    private String filepath;
    private Queue<Block> blocks;

    private AES algo;

    private Boolean mode;//True-Encrypt False-Decrypt
    private static final int CHUNK_SIZE = 1024 * 1024; // 1 MB

    public FileHolder(String filepath,AES algo,Boolean mode) {
        this.filepath = filepath;
        this.blocks = readFileIntoBlocks(filepath);
        this.mode = mode;
        this.algo = algo;
    }

    private Queue<Block> readFileIntoBlocks(String filepath) {
        Queue<Block> blocks = new LinkedList<>();

        try (InputStream inputStream = new FileInputStream(filepath)) {
            if (filepath.endsWith(".zip")) {
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                ZipEntry zipEntry;

                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    byte[] buffer = new byte[16]; // read 16 bytes at a time
                    int bytesRead;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    String hexString = bytesToHexString(byteArrayOutputStream.toByteArray(),16);
                    blocks.add(new Block(hexString));
                }
            } else {
                byte[] buffer = new byte[16]; // read 16 bytes at a time
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    String hexString = bytesToHexString(buffer, bytesRead);
                    blocks.add(new Block(hexString));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return blocks;
    }


    private String bytesToHexString(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }

        return sb.toString();
    }

    public String getFilePath() {
        return filepath;
    }

    public Queue<Block> getBlocks() {
        return blocks;
    }

    public void writeBlocksToFile(String outputFilePath) throws IOException {
        if (outputFilePath.endsWith(".zip")) {
            writeBlocksToZipFileWithEncryptedFiles(outputFilePath);
        } else {
            writeBlocksToNonZipFile(outputFilePath);
        }
    }

    private void writeBlocksToZipFileWithEncryptedFiles(String outputFilePath) throws IOException {
        Path tempDirectory = Files.createTempDirectory("temp");
        Path zipFilePath = Paths.get(outputFilePath);

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filepath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                Path extractedFilePath = tempDirectory.resolve(zipEntry.getName());
                Files.createDirectories(extractedFilePath.getParent());
                Files.copy(zipInputStream, extractedFilePath);


                FileHolder fileHolder = new FileHolder(extractedFilePath.toString(),algo,mode);
                ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                for (Block block : fileHolder.blocks) {
                    ThreadBlock bl = new ThreadBlock(block, algo, mode);
                    executorService.submit(bl);
                }
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

                extractedFilePath.toFile().delete();
                Files.copy(fileHolder.getBlocksAsInputStream(), tempDirectory.resolve(zipEntry.getName()));
                //System.out.println("Next file");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()))) {
            for (File file : tempDirectory.toFile().listFiles()) {
                addFileToZip(zipOutputStream, file, file.getName());
                //System.out.println("Next Add");
            }
        }
        //System.out.println("Next Finish");
        Files.walk(tempDirectory).map(Path::toFile).forEach(File::delete);
        //System.out.println("End");
    }

    private void addFileToZip(ZipOutputStream zipOutputStream, File file, String entryName) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zipOutputStream.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, bytesRead);
            }
            zipOutputStream.closeEntry();
        }
    }

    private InputStream getBlocksAsInputStream() throws IOException, InterruptedException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (Block block : blocks) {
            ThreadBlock bl = new ThreadBlock(block, algo, mode);
            executorService.submit(bl);
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        for (Block block : blocks) {
            byte[] data = hexStringToBytes(AES.writeMatrix(block.getData(), 4, 4));
            outputStream.write(data);
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private void writeBlocksToNonZipFile(String outputFilePath) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {
            int numBlocks = blocks.size();
            byte[] blockData = new byte[numBlocks * 16]; // Each block is 16 bytes long
            int offset = 0;
            for (Block block : blocks) {
                byte[] data = hexStringToBytes(AES.writeMatrix(block.getData(), 4, 4));
                System.arraycopy(data, 0, blockData, offset, data.length);
                offset += data.length;

                fileOutputStream.write(blockData, 0, data.length);
            }
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


    public static void main(String[] args) throws Exception {
        Key k = new Key(KeyGenerator.generateKey("vladi1977", "kjhjkhjlkjlkj").toCharArray());
        FileHolder fileHolder = new FileHolder("C:\\Users\\Vivien\\Downloads\\snowy-mountain-peak-starry-galaxy-majesty-generative-ai.zip",new AES(k),Boolean.FALSE);
        System.out.println("File Path: " + fileHolder.getFilePath());
        System.out.println("Saving file");
        fileHolder.writeBlocksToFile("C:\\Users\\Vivien\\Downloads\\snowy-mountain-peak-starry-galaxy-majesty-generative-ai.zip");
        System.out.println("Finished");
    }
}
