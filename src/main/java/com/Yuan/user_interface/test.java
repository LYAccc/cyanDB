package com.Yuan.user_interface;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class test {


    public static void main(String[] args) throws IOException {
        Path path = Paths.get(System.getProperty("user.dir") + "/src/main/java/com/Yuan/Database/qing/" + "data_file");
        ReversedLinesFileReader fr = new ReversedLinesFileReader(new File(path.toString()));
        String[] a = fr.readLine().split(" ");
        System.out.println(a[0].equals("SET"));
        fr.close();
    }
}
