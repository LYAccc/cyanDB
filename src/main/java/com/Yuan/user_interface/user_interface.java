package com.Yuan.user_interface;




import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.apache.commons.io.input.ReversedLinesFileReader;

public class user_interface {

        public static void main(String[]args) throws IOException {
            Scanner sc= new Scanner(System.in);
            System.out.println("Welcome to CyanDB:)");
            Path working_directory = null;
            while(true){
                System.out.println("please enter your the name of your database");
                String db_name = sc.next();
                working_directory = Paths.get(System.getProperty("user.dir") + "/src/main/java/com/Yuan/Database/" + db_name);
                if(Files.exists(working_directory)){
                    System.out.println("Find your database!");
                    break;
                }
                else{
                    System.out.println("Your database has not been created.");
                    System.out.println("Do you want to create it now?(y/n)");
                    String cm1 = sc.next();

                    if(cm1.equals("y")){
                        Files.createDirectories(working_directory);
                        System.out.println("Your database has been successfully created! ");
                        break;
                    }
                    else continue;
                }
            }

            System.out.println(working_directory.toAbsolutePath().toString());
            System.out.println("you have 4 options to interact with the database");

            System.out.println("1. delete old(k,v) data ");
            System.out.println("2. set new value to a exist key");
            System.out.println("3. select key to see the value of it");
            System.out.println("you can also type 'end' to exit this program");

            Path data_file = Paths.get(working_directory.toAbsolutePath().toString() + "/data_file");
            if(!Files.exists(data_file)) Files.createFile(data_file);

            while(true){
                System.out.println("Your next move!");
                String type = sc.next();
                if(type.equals("end")) break;
                String key = "";
                String value = "";
                if(sc.hasNext()) key = sc.next();
                if(type .equals("set") && sc.hasNext()) value = sc.next();
                switch(type){
                    case("delete"):
                        Files.write(
                                data_file,
                                ("DELETE "+ key + " " + value + System.lineSeparator()).getBytes(),
                                StandardOpenOption.APPEND);
                        break;

                    case("set"):
                        Files.write(
                                data_file,
                                ("SET "+ key + " " + value + System.lineSeparator()).getBytes(),
                                StandardOpenOption.APPEND);
                        break;
                    case("select"):

                        ReversedLinesFileReader fr = new ReversedLinesFileReader(new File(data_file.toString()));

                        String ug;
                        String[] ch;

                        do {
                            ch = null;
                            ug = fr.readLine();
                            if(ug != null) ch = ug.split(" ");
                            else break;

                            if(ch[1].equals(key)) {
                                if (ch[0].equals("SET")) System.out.println("Your query result: " + ch[2]);
                                else {
                                    System.out.println("Do not find your result");
                                }
                                break;
                            }

                        } while(ch != null);
                        fr.close();
                        if(ch == null)System.out.println("Do not find your result");
                        break;
                    default:
                        break;
                }

            }






        }


}
