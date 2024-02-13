package Model.Algorithm;

import Model.Key_Controller.Key;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author tarik
 */
public class AES {

    static File fileText;
    static FileWriter fw2;
    static BufferedWriter bw2;
    static boolean initialized = false;

    private Key key;



    public AES(Key key) throws Exception {
        this.key = key;

        /*
        long startTime = System.currentTimeMillis();

        String option = "d";
        String keyFile = "C:\\Users\\Vivien\\Downloads\\testingEncryption\\src\\key";
        String inputFile = "C:\\Users\\Vivien\\Downloads\\testingEncryption\\src\\data.enc";

        BufferedReader reader3 = new BufferedReader(new FileReader(keyFile));


        String line3 = reader3.readLine();

        while(line3.length() < 32)
            line3 += "0";

        if(line3.length() > 32)
            line3 = line3.substring(0, 32);

        reader3.close();

         */
        key.extendKey();
        System.out.println("Extended key:");
        printMatrix(key.getExtendedKey(), 4, 40);
        /*

        BufferedReader reader2 = new BufferedReader(new FileReader(inputFile));

        String line2 = reader2.readLine();
        while (line2 != null)
        {
            System.out.println("Working on line: "+ line2);
            while (line2.length() < 32) {
                line2 += "0";
            }

            if (line2.length() > 32) {
                line2 = line2.substring(0, 32);
            }


            if ( option.equalsIgnoreCase("e"))
            {


            }
            else
            {

            }
            line2 = reader2.readLine();
        }
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
        reader2.close();
        bw2.close();
         */
    }

    public Block Encrypt(Block dataBlock){
        //all rounds are here
        for (int c = 0; c < 15; c++) {

            for (int c2 = 0; c2 < 4; c2++) {
                dataBlock.getData()[0][c2] ^= key.getExtendedKey()[0][c * 4 + c2];
                dataBlock.getData()[1][c2] ^= key.getExtendedKey()[1][c * 4 + c2];
                dataBlock.getData()[2][c2] ^= key.getExtendedKey()[2][c * 4 + c2];
                dataBlock.getData()[3][c2] ^= key.getExtendedKey()[3][c * 4 + c2];
            }

         //  System.out.println("After addRoundKey(" + c + "):");
            //printPlainText(dataBlock.getData(), 4, 4);

            for (int c2 = 0; c2 < 4; c2++) {

                dataBlock.getData()[0][c2] = SBox.getSBoxValue(dataBlock.getData()[0][c2]);
                dataBlock.getData()[1][c2] = SBox.getSBoxValue(dataBlock.getData()[1][c2]);
                dataBlock.getData()[2][c2] = SBox.getSBoxValue(dataBlock.getData()[2][c2]);
                dataBlock.getData()[3][c2] = SBox.getSBoxValue(dataBlock.getData()[3][c2]);
            }

           // System.out.println("After subBytes:");
            //printPlainText(dataBlock.getData(), 4, 4);

            for (int c2 = 0; c2 < 4; c2++) {
                char temp[] = {dataBlock.getData()[c2][0], dataBlock.getData()[c2][1], dataBlock.getData()[c2][2], dataBlock.getData()[c2][3]};
                dataBlock.getData()[c2][(0 + 4 - c2) % 4] = temp[0];
                dataBlock.getData()[c2][(1 + 4 - c2) % 4] = temp[1];
                dataBlock.getData()[c2][(2 + 4 - c2) % 4] = temp[2];
                dataBlock.getData()[c2][(3 + 4 - c2) % 4] = temp[3];
            }

          //  System.out.println("After shiftRows:");
           // printPlainText(dataBlock.getData(), 4, 4);

            if (c == 13) {
                break;
            }

            mixColumn2(0, dataBlock.getData());
            mixColumn2(1, dataBlock.getData());
            mixColumn2(2, dataBlock.getData());
            mixColumn2(3, dataBlock.getData());

          //  System.out.println("After mixColumns:");
           // printPlainText(dataBlock.getData(), 4, 4);
        }
        for (int c2 = 0; c2 < 4; c2++) {
            dataBlock.getData()[0][c2] ^= key.getExtendedKey()[0][14 * 4 + c2];
            dataBlock.getData()[1][c2] ^= key.getExtendedKey()[1][14 * 4 + c2];
            dataBlock.getData()[2][c2] ^= key.getExtendedKey()[2][14 * 4 + c2];
            dataBlock.getData()[3][c2] ^= key.getExtendedKey()[3][14 * 4 + c2];
        }

       // System.out.println("After addRoundKey(14):");
      //  printPlainText(dataBlock.getData(), 4, 4);

        //encryption is complete!
        return dataBlock;
    }

    public Block Decrypt(Block dataBlock){
        //System.out.println("Starting Decryption: ");

        for (int c2 = 0; c2 < 4; c2++) {
            dataBlock.getData()[0][c2] ^= key.getExtendedKey()[0][14 * 4 + c2];
            dataBlock.getData()[1][c2] ^= key.getExtendedKey()[1][14 * 4 + c2];
            dataBlock.getData()[2][c2] ^= key.getExtendedKey()[2][14 * 4 + c2];
            dataBlock.getData()[3][c2] ^= key.getExtendedKey()[3][14 * 4 + c2];
        }

        //System.out.println("After addRoundKey(14):");
       // printPlainText(dataBlock.getData(), 4, 4);

        for (int c = 13; c >= 0; c--) {

            for (int c2 = 0; c2 < 4; c2++) {
                char temp[] = {dataBlock.getData()[c2][0], dataBlock.getData()[c2][1], dataBlock.getData()[c2][2], dataBlock.getData()[c2][3]};
                dataBlock.getData()[c2][(0 + c2) % 4] = temp[0];
                dataBlock.getData()[c2][(1 + c2) % 4] = temp[1];
                dataBlock.getData()[c2][(2 + c2) % 4] = temp[2];
                dataBlock.getData()[c2][(3 + c2) % 4] = temp[3];
            }

          //  System.out.println("After invShiftRows:");
           // printPlainText(dataBlock.getData(), 4, 4);

            for (int c2 = 0; c2 < 4; c2++) {
                dataBlock.getData()[0][c2] = SBox.getSBoxInvert(dataBlock.getData()[0][c2]);
                dataBlock.getData()[1][c2] = SBox.getSBoxInvert(dataBlock.getData()[1][c2]);
                dataBlock.getData()[2][c2] = SBox.getSBoxInvert(dataBlock.getData()[2][c2]);
                dataBlock.getData()[3][c2] = SBox.getSBoxInvert(dataBlock.getData()[3][c2]);
            }

          //  System.out.println("After invSubBytes:");
           // printPlainText(dataBlock.getData(), 4, 4);

            for (int c2 = 0; c2 < 4; c2++) {
                dataBlock.getData()[0][c2] ^= key.getExtendedKey()[0][c * 4 + c2];
                dataBlock.getData()[1][c2] ^= key.getExtendedKey()[1][c * 4 + c2];
                dataBlock.getData()[2][c2] ^= key.getExtendedKey()[2][c * 4 + c2];
                dataBlock.getData()[3][c2] ^= key.getExtendedKey()[3][c * 4 + c2];
            }

          //  System.out.println("After addRoundKey(" + c + "):");
            //printPlainText(dataBlock.getData(), 4, 4);

            if (c == 0) {
                return dataBlock;
            }

            invMixColumn2(0, dataBlock.getData());
            invMixColumn2(1, dataBlock.getData());
            invMixColumn2(2, dataBlock.getData());
            invMixColumn2(3, dataBlock.getData());

          //  System.out.println("After invMixColumns:");
            //printPlainText(dataBlock.getData(), 4, 4);

        }
        return dataBlock;
    }

    public static String writeMatrix(char [][] matrix, int row, int column)
    {
        String out = "";

        for (int c = 0; c < row; c++)
            for (int c2 = 0; c2 < column; c2++) {
                out += String.format("%02X", (int) matrix[c2][c]);
            }
        return out;
    }

    public static void printPlainText(char text[][], int row, int column) {
        for (int c = 0; c < row; c++) {

            for (int c2 = 0; c2 < column; c2++) {
                System.out.print("" + String.format("%02X", (int) text[c2][c]) + "");
            }

        }
        System.out.println("");
    }



    public static void printMatrix(char[][] matrix, int row, int column) {
        for (int c = 0; c < row; c++) {
            for (int c2 = 0; c2 < column; c2++) {
                System.out.print("" + String.format("%02X", (int) matrix[c][c2]) + " ");
            }
            System.out.println();
        }
    }





    ////////////////////////  the mixColumns Tranformation ////////////////////////


    public static char mul(int a, char b) {
        int inda = (a < 0) ? (a + 256) : a;
        int indb = (b < 0) ? (b + 256) : b;

        if ((a != 0) && (b != 0)) {
            int index = (LogTable.getLog(inda) + LogTable.getLog(indb));
            char val = (char) (LogTable.getALog(index % 255));
            return val;
        } else {
            return 0;
        }
    } // mul

    // In the following two methods, the input c is the column number in
    // your evolving state matrix st (which originally contained 
    // the plaintext input but is being modified).  Notice that the state here is defined as an
    // array of bytes.  If your state is an array of integers, you'll have
    // to make adjustments. 
    public static void mixColumn2(int c, char st[][]) {
        // This is another alternate version of mixColumn, using the 
        // logtables to do the computation.

        char a[] = new char[4];

        // note that a is just a copy of st[.][c]
        for (int i = 0; i < 4; i++) {
            a[i] = st[i][c];
        }

        // This is exactly the same as mixColumns1, if 
        // the mul columns somehow match the b columns there.
        st[0][c] = (char) (mul(2, a[0]) ^ a[2] ^ a[3] ^ mul(3, a[1]));
        st[1][c] = (char) (mul(2, a[1]) ^ a[3] ^ a[0] ^ mul(3, a[2]));
        st[2][c] = (char) (mul(2, a[2]) ^ a[0] ^ a[1] ^ mul(3, a[3]));
        st[3][c] = (char) (mul(2, a[3]) ^ a[1] ^ a[2] ^ mul(3, a[0]));
    } // mixColumn2

    public static void invMixColumn2(int c, char[][] st) {
        char a[] = new char[4];

        // note that a is just a copy of st[.][c]
        for (int i = 0; i < 4; i++) {
            a[i] = st[i][c];
        }

        st[0][c] = (char) (mul(0xE, a[0]) ^ mul(0xB, a[1]) ^ mul(0xD, a[2]) ^ mul(0x9, a[3]));
        st[1][c] = (char) (mul(0xE, a[1]) ^ mul(0xB, a[2]) ^ mul(0xD, a[3]) ^ mul(0x9, a[0]));
        st[2][c] = (char) (mul(0xE, a[2]) ^ mul(0xB, a[3]) ^ mul(0xD, a[0]) ^ mul(0x9, a[1]));
        st[3][c] = (char) (mul(0xE, a[3]) ^ mul(0xB, a[0]) ^ mul(0xD, a[1]) ^ mul(0x9, a[2]));
    } // invMixColumn2

}