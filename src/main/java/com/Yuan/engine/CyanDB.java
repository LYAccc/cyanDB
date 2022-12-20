package com.Yuan.engine;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.json.*;


public class CyanDB {

        Path working_directory;
        Path data_file;

        private TreeMap<String,Value> cache_table;


        private CyanDB(Path dir) throws IOException {
            this.working_directory = dir;
            data_file = Paths.get(working_directory.toAbsolutePath().toString() + "/data_file");
            if(!Files.exists(data_file)) Files.createFile(data_file);
    }
        public static CyanDB build_instance_db(String db_name) throws IOException {
             Path dir = Paths.get(System.getProperty("user.dir") + "/src/main/java/com/Yuan/Database/" + db_name);
             CyanDB db_instance = null;
            if(Files.exists(dir)) {
                 db_instance = new CyanDB(dir);
             }
             return db_instance;
        }
        public void write_to_disk(){

        }
        public void Set(String key, String value){
                Value v = new Value("SET", value);
                cache_table.put(key,v);
        }
        public void Delete(String key){
               Value v = new Value("DELETE", null);
               cache_table.put(key,v);
        }






}
