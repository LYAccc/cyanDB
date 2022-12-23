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

        private TreeMap<String,DataBlock> dataBlocks;

        private TreeMap<Long,long[]> sparse_index;

        private int store_size = 10;

        private CyanDB(String dir) throws IOException {
            this.working_directory = dir;
            this.cache_table = new TreeMap<>();
            this.dataBlocks = new TreeMap<>();
            //initialize sparse_index
            File database_meta = new File(working_directory + "database_meta.meta");
            if(database_meta.exists()){
                RandomAccessFile file = new RandomAccessFile(database_meta,"rw");
//                JSONArray a = new JSONArray();

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
        public void write_to_disk(int part_size) throws IOException {
            RandomAccessFile file = new RandomAccessFile(working_directory+System.currentTimeMillis()+".table","rw");
            JSONObject jsondata = new JSONObject();
            String first_value_key = null; // record current first value as the index of the data block.
            for (Map.Entry<String,Value> entry : cache_table.entrySet()) {
                    jsondata.put(entry.getKey(),entry.getValue().to_json());
                    if(first_value_key == null) first_value_key = entry.getKey();
                    if(jsondata.length() > part_size){
                            write_helper(file,jsondata,first_value_key);
                            first_value_key = null;
                    }
            }
            if(jsondata.length() > 0){
                write_helper(file,jsondata,first_value_key);
            }
            for (Map.Entry<String,DataBlock> entry: dataBlocks.entrySet()) jsondata.put(entry.getKey(),entry.getValue().to_json());
            byte[] databytes = jsondata.toString().getBytes(StandardCharsets.UTF_8);
            long start = file.getFilePointer();
            file.write(databytes);
            //meta info will improve later
            file.writeLong(start);
            file.writeLong((long)databytes.length);
            // clear;
            cache_table.clear();
            dataBlocks.clear();

        }
        public Value query(String key){
               if(cache_table.containsKey(key)) return cache_table.get(key);

               return null;
        }
        public void write_helper(RandomAccessFile file, JSONObject jsondata, String first_value_key) throws IOException {
            byte[] databytes = jsondata.toString().getBytes(StandardCharsets.UTF_8);
            long start = file.getFilePointer();
            long len = databytes.length;
            file.write(databytes);
            dataBlocks.put(first_value_key,new DataBlock(start,len));
            jsondata.clear();
        }
        public void Set(String key, String value) throws IOException {
                Value v = new Value(Value.command.SET, value);
                cache_table.put(key,v);
                if(cache_table.size() >= store_size) write_to_disk(5);
        }
        public void Delete(String key){
               Value v = new Value(Value.command.DELETE, null);
               cache_table.put(key,v);
        }






}
