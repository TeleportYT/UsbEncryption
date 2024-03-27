package Model;

import Model.Algorithm.AES;
import Model.Algorithm.SaltGenerator;
import Model.Key_Controller.Key;
import Model.Key_Controller.KeyGenerator;
import Model.USB_Controller.UsbReader;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ModelControl implements Observable {

    private List<AESObserver> observers = new ArrayList<>();


    public void startProcess(FunctionMode mode,String path, String pass, String salt, Boolean Mode) throws Exception {
        switch (mode){
            case Usb -> startUsbProcess(path,pass,salt,Mode);
            case File -> startFileProcess(path,pass,salt,Mode);
            case Folder -> startFolderProcess(path,pass,salt,Mode);
        }
    }

    public void startUsbProcess(String diskOnkey, String pass, String salt, Boolean Mode) throws Exception {
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

    public void startFileProcess(String path, String pass, String salt, Boolean Mode) throws Exception{
        Key key  = new Key(KeyGenerator.generateKey(pass, !salt.isEmpty() ? salt : SaltGenerator.generate(path)).toCharArray());
        AES algo = new AES(key);
        FileHolder file = new FileHolder(path,algo,Mode);
        ((Runnable) () -> {
            notifyProgressObservers(new Random().nextDouble(0.05,0.2));
        }).run();
        file.writeBlocksToFile(path);
        notifyObservers();
    }

    public void startFolderProcess(String path, String pass, String salt, Boolean Mode) throws Exception{
        File[] allFiles = new File(path).listFiles();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (File inFile :
                allFiles) {
            System.out.println(inFile.getName());
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Key key  = new Key(KeyGenerator.generateKey(pass, !salt.isEmpty() ? salt : SaltGenerator.generate(inFile.getAbsolutePath())).toCharArray());
                    AES algo = null;
                    try {
                        algo = new AES(key);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    FileHolder file = new FileHolder(inFile.getAbsolutePath(),algo,Mode);
                    try {
                        System.out.println("File: "+path+" Being encrypted");
                        file.writeBlocksToFile(inFile.getAbsolutePath());
                        double progress = (double) 1 / allFiles.length;
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
