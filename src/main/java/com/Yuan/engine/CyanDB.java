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

            File database_meta = new File(working_directory + "database_meta.meta");
            if(database_meta.exists()){
                RandomAccessFile file = new RandomAccessFile(database_meta,"rw");
                file.seek(0);
                int len = file.readInt();
                file.seek(4);
                byte[] b = new byte[len];
                file.read(b);
                JSONArray arr = new JSONObject(new String(b,StandardCharsets.UTF_8)).getJSONArray("files");
                for (Object str: arr) {
                    Long l = Long.valueOf((String)str);
                    tables.put(l,new Table(working_directory + l + ".table"));
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

        public void modified_json(JSONObject json){ // change underling datastructure of json from map to linkedmap to preserve order
            try{
                Field changeMap = json.getClass().getDeclaredField("map");
                changeMap.setAccessible(true);
                changeMap.set(json, new LinkedHashMap<>());
                changeMap.setAccessible(false);
            }catch (IllegalAccessException | NoSuchFieldException e) {
                System.out.println("damn,wrong!");
            }

        }
        public void write_to_disk(int part_size) throws IOException {
            long cur_time = System.currentTimeMillis();
            RandomAccessFile file = new RandomAccessFile(working_directory+cur_time+".table","rw");
            JSONObject jsondata = new JSONObject();
            modified_json(jsondata);
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
            // write sparse index info to file
            for (Map.Entry<String,DataBlock> entry: dataBlocks.entrySet()) jsondata.put(entry.getKey(),entry.getValue().to_json());
            byte[] databytes = jsondata.toString().getBytes(StandardCharsets.UTF_8);
            long start = file.getFilePointer();
            file.write(databytes);
            //meta info will improve later
            file.writeLong(start);
            file.writeInt(databytes.length);
            // clear;
            cache_table.clear();
            dataBlocks.clear();
            //record table info
            tables.put(cur_time,new Table(working_directory+cur_time+".table"));

        }
        public Value get(String key) throws IOException {
               if(cache_table.containsKey(key)) return cache_table.get(key);
            for (Table t: tables.values()) {
                Value v = t.query(key);
                if(v != null) return v;
            }
               return null;
        }
        public void write_helper(RandomAccessFile file, JSONObject jsondata, String first_value_key) throws IOException {
            byte[] databytes = jsondata.toString().getBytes(StandardCharsets.UTF_8);
            long start = file.getFilePointer();
            int len = databytes.length;
            file.write(databytes);
            dataBlocks.put(first_value_key,new DataBlock(start,len));
            jsondata.clear();
        }
        public void Set(String key, String value) throws IOException {
                Value v;
                 v = new Value(Value.command.SET, value);
                cache_table.put(key,v);
                if(cache_table.size() >= store_size) write_to_disk(5);
        }
        public void Delete(String key){
               Value v = new Value(Value.command.DELETE);
               cache_table.put(key,v);
        }






}
