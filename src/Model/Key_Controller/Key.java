package Model.Key_Controller;

import Model.Algorithm.Rcon;
import Model.Algorithm.SBox;

public class Key {
    private char[][] key;

    public char[][] getExtendedKey() {
        return extendedKey;
    }

    private char extendedKey[][] = new char[4][60];

    public Key(char[] key) {
        this.key = new char[4][8];
        int c4 = 0;
        for (int c2 = 0; c2 < 32; c2 += 2) {
            this.key[c4 % 4][c4++ / 4] = (char)Integer.parseInt("" + key[c2] + key[c2+1], 16);
        }
    }


    public void extendKey() {

        for (int c = 0; c < 4; c++) {
            for (int c2 = 4; c2 < 8; c2++) {
                extendedKey[c][c2] = this.key[c][c2-4];
            }
        }

        char c = 1;
        for (int i = 7; i < 55; i++) {

            // STEP 1
            // rotate
            //      System.out.println("c: " + (int) c + " i: " + i + " rot 4 byte: ");
            //     System.out.println("" + String.format("%02X", (int) extended[0][i]) + " " + String.format("%02X", (int) extended[1][i]) + " " + String.format("%02X", (int) extended[2][i]) + " " + String.format("%02X", (int) extended[3][i]));
            char tempArr[] = new char[4];
            tempArr[0] = extendedKey[1][i];
            tempArr[1] = extendedKey[2][i];
            tempArr[2] = extendedKey[3][i];
            tempArr[3] = extendedKey[0][i];

            //   System.out.println("After rotation" + String.format("%02X", (int) tempArr[0]) + " " + String.format("%02X", (int) tempArr[1]) + " " + String.format("%02X", (int) tempArr[2]) + " " + String.format("%02X", (int) tempArr[3]));

            //substitute
            tempArr[0] = SBox.getSBoxValue(tempArr[0]);
            tempArr[1] = SBox.getSBoxValue(tempArr[1]);
            tempArr[2] = SBox.getSBoxValue(tempArr[2]);
            tempArr[3] = SBox.getSBoxValue(tempArr[3]);

            //   System.out.println("After sub" + String.format("%02X", (int) tempArr[0]) + " " + String.format("%02X", (int) tempArr[1]) + " " + String.format("%02X", (int) tempArr[2]) + " " + String.format("%02X", (int) tempArr[3]));

            char rCon = Rcon.getRconValue(c);
            c = (char) (c + 1);
            tempArr[0] ^= rCon;

            extendedKey[0][i + 1] = (char) (extendedKey[0][i - 3] ^ tempArr[0]);
            extendedKey[1][i + 1] = (char) (extendedKey[1][i - 3] ^ tempArr[1]);
            extendedKey[2][i + 1] = (char) (extendedKey[2][i - 3] ^ tempArr[2]);
            extendedKey[3][i + 1] = (char) (extendedKey[3][i - 3] ^ tempArr[3]);

            //debugging point
            // System.out.println("c: " + (int)c );
            //       System.out.println("new 4 byte: ");
            //     System.out.println("" + String.format("%02X", (int) extended[0][i + 1]) + " " + String.format("%02X", (int) extended[1][i + 1]) + " " + String.format("%02X", (int) extended[2][i + 1]) + " " + String.format("%02X", (int) extended[3][i + 1]));

            // STEP 2
            for (int c2 = 0; c2 < 3; c2++) {
                i++;
                extendedKey[0][i + 1] = (char) (extendedKey[0][i - 3] ^ extendedKey[0][i]);
                extendedKey[1][i + 1] = (char) (extendedKey[1][i - 3] ^ extendedKey[1][i]);
                extendedKey[2][i + 1] = (char) (extendedKey[2][i - 3] ^ extendedKey[2][i]);
                extendedKey[3][i + 1] = (char) (extendedKey[3][i - 3] ^ extendedKey[3][i]);
            }

            //STEP 3
            i++;
            tempArr[0] = extendedKey[0][i];
            tempArr[1] = extendedKey[1][i];
            tempArr[2] = extendedKey[2][i];
            tempArr[3] = extendedKey[3][i];

//            System.out.println(" step3 begins" + String.format("%02X", (int) tempArr[0]) + " " + String.format("%02X", (int) tempArr[1]) + " " + String.format("%02X", (int) tempArr[2]) + " " + String.format("%02X", (int) tempArr[3]));

            //substitute
            tempArr[0] = SBox.getSBoxValue(tempArr[0]);
            tempArr[1] = SBox.getSBoxValue(tempArr[1]);
            tempArr[2] = SBox.getSBoxValue(tempArr[2]);
            tempArr[3] = SBox.getSBoxValue(tempArr[3]);

            //    System.out.println("After step3 sub- i: " + i + " " + String.format("%02X", (int) tempArr[0]) + " " + String.format("%02X", (int) tempArr[1]) + " " + String.format("%02X", (int) tempArr[2]) + " " + String.format("%02X", (int) tempArr[3]));
//
//                extended[0][i + 1] = (char) (extended[0][i - 3] ^ tempArr[0]);
//                extended[1][i + 1] = (char) (extended[1][i - 3] ^ tempArr[1]);
//                extended[2][i + 1] = (char) (extended[2][i - 3] ^ tempArr[2]);
//                extended[3][i + 1] = (char) (extended[3][i - 3] ^ tempArr[3]);

            extendedKey[0][i + 1] = (char) tempArr[0];
            extendedKey[1][i + 1] = (char) tempArr[1];
            extendedKey[2][i + 1] = (char) tempArr[2];
            extendedKey[3][i + 1] = (char) tempArr[3];

            // STEP 4
            for (int c2 = 0; c2 < 3; c2++) {
                i++;
                extendedKey[0][i + 1] = (char) (extendedKey[0][i - 3] ^ extendedKey[0][i]);
                extendedKey[1][i + 1] = (char) (extendedKey[1][i - 3] ^ extendedKey[1][i]);
                extendedKey[2][i + 1] = (char) (extendedKey[2][i - 3] ^ extendedKey[2][i]);
                extendedKey[3][i + 1] = (char) (extendedKey[3][i - 3] ^ extendedKey[3][i]);
            }

        }
    }
}
