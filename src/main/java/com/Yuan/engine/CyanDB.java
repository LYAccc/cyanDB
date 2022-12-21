package com.Yuan.engine;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.json.*;


public class CyanDB {

        String working_directory;


        private TreeMap<String,Value> cache_table;



        private CyanDB(String dir) throws IOException {
            this.working_directory = dir;
    }
        public static CyanDB build_instance_db(String db_name) throws IOException {
             String dir = (System.getProperty("user.dir") + "/src/main/java/com/Yuan/Database/" + db_name + "/");
             CyanDB db_instance = null;
            if(Files.isDirectory(Paths.get(dir))) {
                 db_instance = new CyanDB(dir);
             }
             return db_instance;
        }
        public void write_to_disk(int part_size) throws IOException {
            RandomAccessFile file = new RandomAccessFile(working_directory+System.currentTimeMillis()+".table","rw");
            JSONObject jsondata = new JSONObject();

            for (Value v: cache_table.values()) {
                    jsondata.put(v.key,v);

                    if(jsondata.length() > part_size){
                            byte[] databytes = jsondata.toString().getBytes(StandardCharsets.UTF_8);
                            long start = file.getFilePointer();
                            file.write(databytes);

                    }
            }

        }
        public void Set(String key, String value){
                Value v = new Value(Value.command.SET, value,key);
                cache_table.put(key,v);
        }
        public void Delete(String key){
               Value v = new Value(Value.command.DELETE, null,key);
               cache_table.put(key,v);
        }






}
