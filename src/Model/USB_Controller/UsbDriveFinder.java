package Model.USB_Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UsbDriveFinder {

    public static List<File> getUsbDrives() {
        List<File> usbDrives = new ArrayList<>();
        File[] roots = File.listRoots();

        for (File root : roots) {
            if (isUsbDrive(root)) {
                usbDrives.add(root);
            }
        }

        return usbDrives;
    }

    private static boolean isUsbDrive(File file) {
        String path = file.getAbsolutePath();
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("windows")) {
            return path.matches("^[A-Z]:\\\\$");
        } else if (os.contains("linux") || os.contains("mac os x")) {
            return path.matches("/media/[a-zA-Z0-9]+$");
        }

        return false;
    }
}