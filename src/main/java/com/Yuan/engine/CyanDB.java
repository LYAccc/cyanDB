package com.Yuan.engine;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.json.*;


public class CyanDB {

        String working_directory;

        private TreeMap<String,Value> immutable_table;

        private TreeMap<String,Value> cache_table;

        private TreeMap<String,DataBlock> dataBlocks;

        private TreeMap<Long,Table> tables;




        private int store_size = 10;

        private CyanDB(String dir) throws IOException {
            this.working_directory = dir;
            this.cache_table = new TreeMap<>();
            this.dataBlocks = new TreeMap<>();
            this.tables = new TreeMap<>();
//            initialize sparse_index
            File directory = new File(this.working_directory);
            File[] files = directory.listFiles();
            if (files == null && files.length == 1){
                return;
            }

            File database_meta = new File(working_directory + "database_meta.meta");
            {
                for (File file: files) {
                    String tn = file.getAbsolutePath();
                    Long table_name = Long.valueOf(tn.substring(tn.lastIndexOf("\\" ) + 1,tn.lastIndexOf(".table")));
                    tables.put(table_name,new Table(file.getAbsolutePath()));
                }

            }

    }
        public static CyanDB build_instance_db(String db_name) throws IOException {
             String dir = (System.getProperty("user.dir") + "/src/main/java/com/Yuan/Database/" + db_name + "/");
             CyanDB db_instance = null;
            if(Files.isDirectory(Paths.get(dir))) {
                 db_instance = new CyanDB(dir);
             }
             return db_instance;
        }



        public Value get(String key) throws IOException {
               if(cache_table.containsKey(key)) return cache_table.get(key);
            for (Table t: tables.values()) {
                Value v = t.query(key);
                if(v != null) return v;
            }
               return null;
        }

        public void Set(String key, String value) throws IOException {
                Value v;
                v = new Value(Value.command.SET, value);
                cache_table.put(key,v);
                if(cache_table.size() >= store_size) {
                    long cur_time = System.currentTimeMillis();
                    Table new_table = new Table(working_directory+ cur_time +".level_0");
                    new_table.write_to_disk(5,cache_table,dataBlocks);
                    new_table.set_sparse_index();
                    //record table info
                    tables.put(cur_time,new_table);
                }
        }
        public void Delete(String key){
               Value v = new Value(Value.command.DELETE);
               cache_table.put(key,v);
        }






}
