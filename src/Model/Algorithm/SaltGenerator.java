package Model.Algorithm;

import java.io.File;

public class SaltGenerator {
    public static String generate(String filePath){
        File file = new File(filePath);
        String salt = file.getName()+file.getAbsolutePath();
        System.out.println("Salt: "+ salt+" gen: "+salt.getBytes().toString());
        return salt.substring(0,32);
    }
}
