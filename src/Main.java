import Model.Algorithm.AES;
import Model.Key_Controller.Key;
import Model.Key_Controller.KeyGenerator;
import Model.USB_Controller.UsbDriveFinder;
import Model.USB_Controller.UsbReader;

import javax.security.auth.kerberos.KerberosTicket;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String args[]) throws Exception {


        List<File> usbs = UsbDriveFinder.getUsbDrives();
        System.out.println(usbs);
        UsbReader usb = new UsbReader(usbs.get(1).getPath().toString().substring(0,1));
        usb.readFiles();

        Key key  = new Key(KeyGenerator.generateKey("sexyboo123","shaharusd").toCharArray());
        AES algo = new AES(key);
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


        for (String path :
                usb.getFilePaths()) {

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    FileHolder file = new FileHolder(path,algo,Boolean.TRUE);
                    try {
                        System.out.println("File: "+path+" Being encrypted");
                        file.writeBlocksToFile(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        
    }

}
