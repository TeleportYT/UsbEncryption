package Model.USB_Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UsbDriveFinder {

    public static List<String> getUsbDrives() {
        List<String> usbDrives = new ArrayList<>();
        File[] roots = File.listRoots();

        for (File root : roots) {
            if (isUsbDrive(root)) {
                usbDrives.add(root.getAbsolutePath());
            }
        }

        usbDrives.remove(usbDrives.get(0));
        return usbDrives;
    }

    private static boolean isUsbDrive(File file) {
        String path = file.getAbsolutePath();
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("windows")) {
            return file.exists() && file.canRead() && path.matches("^[A-Z]:\\\\$");
        } else if (os.contains("linux") || os.contains("mac os x")) {
            // On Linux and macOS, USB drives are usually mounted under /media/username or /mnt
            return file.exists() && file.canRead() && path.startsWith("/media/") || path.startsWith("/mnt/");
        }

        return false;
    }

}