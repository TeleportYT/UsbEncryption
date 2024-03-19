package Model;

import Model.Algorithm.AES;
import Model.Algorithm.SaltGenerator;
import Model.Key_Controller.Key;
import Model.Key_Controller.KeyGenerator;
import Model.USB_Controller.UsbDriveFinder;
import Model.USB_Controller.UsbReader;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ModelControl implements Observable {

    private List<AESObserver> observers = new ArrayList<>();
    public void StartProcess(String diskOnkey,String pass,String salt,Boolean Mode) throws Exception {
        UsbReader usb = new UsbReader(diskOnkey);
        usb.readFiles();
        int amount = usb.getFilePaths().size();
        System.out.println("amount: "+amount);

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


        for (String path :
                usb.getFilePaths()) {

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Key key  = new Key(KeyGenerator.generateKey(pass, !salt.isEmpty() ? salt : SaltGenerator.generate(path)).toCharArray());
                    AES algo = null;
                    try {
                        algo = new AES(key);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    FileHolder file = new FileHolder(path,algo,Mode);
                    try {
                       // System.out.println("File: "+path+" Being encrypted");
                        file.writeBlocksToFile(path);
                        double progress = (double) 1 / amount;
                        notifyProgressObservers(progress);
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
        notifyObservers();
    }

    public void addListener(AESObserver observer) {
        observers.add(observer);
    }

    public void removeListener(AESObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (AESObserver b :
                observers) {
            b.update();
        }
    }

    private void notifyProgressObservers(double amount) {
        for (AESObserver b :
                observers) {
            b.updateProgress(amount);
        }
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {

    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}
