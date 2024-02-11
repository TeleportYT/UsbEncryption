package Model.Algorithm;

public class Block {
    public char[][] getData() {
        return data;
    }

    private char[][] data;
    public Block(String text){
        this.data = new char[4][4];
        int c3 = 0;
        while (text.length()<32){
            text+='0';
        }
        char[] temp = text.toCharArray();
        for (int c2 = 0; c2 < 32; c2 += 2) {
            data[c3 % 4][c3++ / 4] = (char)Integer.parseInt("" + temp[c2] + temp[c2+1], 16);
        }
    }

/*
Hex to Noraml

 */







}
