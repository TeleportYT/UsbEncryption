package Model.USB_Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UsbReader {

    private String driveLetter;
    private List<String> filePaths;
    public UsbReader(String driveLetter) {
        this.driveLetter = driveLetter;
        this.filePaths = new ArrayList<>();
    }

    public void readFiles() throws InterruptedException {
        File drive = new File(driveLetter + ":\\");
        File[] files = drive.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    filePaths.add(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    readDirectory(file);
                }
            }

            // Sort the file paths
            filePaths.sort(String::compareTo);
        }
    }

    private void readDirectory(File dir) {
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    filePaths.add(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    readDirectory(file);
                }
            }
        }
    }

    public List<String> getFilePaths() {
        return filePaths;
    }
}