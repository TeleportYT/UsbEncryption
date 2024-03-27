package Model;

import Model.Algorithm.AES;
import Model.Algorithm.Block;

public class ThreadBlock implements Runnable{
    private Block bl;
    private AES algorithm;
    private Boolean mode;//T-Encrypt , F-Decrypt

    public ThreadBlock(Block bl, AES algorithm, Boolean mode) {
        this.bl = bl;
        this.algorithm = algorithm;
        this.mode = mode;
    }

    @Override
    public void run() {
        if (mode){
            System.out.println(AES.writeMatrix(bl.getData(),4,4));
            algorithm.Encrypt(bl);
            System.out.println(AES.writeMatrix(bl.getData(),4,4));

        }
        else{
            algorithm.Decrypt(bl);
        }
    }
}
