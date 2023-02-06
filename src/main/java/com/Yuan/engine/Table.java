package com.Yuan.engine;

import javafx.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarException;

public class Table {
    private TreeMap<String,DataBlock> sparse_index;


    private  String file_address;
    private long file_len;

    private final long sparse_index_start_offset = 16;

    private final long sparse_index_len_offset = 8;

    private final long max_min_key_len_offset = 24;

    private final long max_min_key_start_offset = 32;

    public String min_key;

    public String max_key;

    RandomAccessFile file;

    public Table(String file_address) throws IOException {
        this.file_address = file_address;
        file = new RandomAccessFile(file_address,"rw");
        sparse_index = new TreeMap<>();

    }
    // set this table meta info
    // need to handle fail case
    public void set_sparse_index() throws IOException {

        JSONObject json = read_json(sparse_index_start_offset,sparse_index_len_offset);
        for (String k: json.keySet()) {
            DataBlock d = new DataBlock((JSONObject) json.get(k));
            sparse_index.put(k,d);
        }

    }
    public void initialize() throws IOException {
        set_min_max();
        set_sparse_index();
    }
    public void set_min_max() throws IOException {

           JSONObject json = read_json(max_min_key_start_offset,max_min_key_len_offset);
           min_key = json.getString("min_key");
           max_key = json.getString("max_key");
    }

    public JSONObject read_json(long start_offset, long len_offset) throws IOException {
        long file_len = file.length();
        file.seek(file_len-start_offset);
        long start = file.readLong();
        file.seek(file_len-len_offset);
        long len = file.readLong();
        byte[] stream = new byte[(int)len];  // this could be problematic
        file.seek(start);
        file.read(stream);
        JSONObject json = new JSONObject(new String(stream, StandardCharsets.UTF_8));
        return json;
    }

    public void write_to_disk(int part_size, TreeMap<String,Value> cache_table, TreeMap<String,DataBlock> dataBlocks) throws IOException {
        String min_key = cache_table.firstKey();
        String max_key = cache_table.lastKey();
        JSONObject jsondata = new JSONObject();
        modified_json(jsondata);
        String first_value_key = null; // record current first value as the index of the data block.
        for (Map.Entry<String,Value> entry : cache_table.entrySet()) {
            jsondata.put(entry.getKey(),entry.getValue().to_json());
            if(first_value_key == null) first_value_key = entry.getKey();
            if(jsondata.length() > part_size){
                write_data_helper(file,jsondata,first_value_key,dataBlocks);
                first_value_key = null;
            }
        }
        if(jsondata.length() > 0){
            write_data_helper(file,jsondata,first_value_key,dataBlocks);
        }
        // write sparse index info to file
        long sparse_index_start = file.getFilePointer();
        for (Map.Entry<String,DataBlock> entry: dataBlocks.entrySet()) jsondata.put(entry.getKey(),entry.getValue().to_json());
        byte[] databytes = jsondata.toString().getBytes(StandardCharsets.UTF_8);
        file.write(databytes);
        long sparse_index_len = databytes.length;
        // write max_key and min_key info
        jsondata.clear();
        jsondata.put("min_key",min_key);
        jsondata.put("max_key",max_key);
        long max_min_start = file.getFilePointer();
        databytes = jsondata.toString().getBytes(StandardCharsets.UTF_8);
        file.write(databytes);

        long max_min_len = databytes.length;
        // write offset
         file.writeLong(max_min_start);
         file.writeLong(max_min_len);
         file.writeLong(sparse_index_start);
         file.writeLong(sparse_index_len);
        // clear;
        cache_table.clear();
        dataBlocks.clear();


    }

    public void write_data_helper(RandomAccessFile file, JSONObject jsondata, String first_value_key,TreeMap<String,DataBlock> dataBlocks) throws IOException {
        byte[] databytes = jsondata.toString().getBytes(StandardCharsets.UTF_8);
        long start = file.getFilePointer();
        int len = databytes.length;
        file.write(databytes);
        dataBlocks.put(first_value_key,new DataBlock(start,len));
        jsondata.clear();
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
    // need to handel "DELETE"
    // if doesn't exist return null
    public Value query(String key) throws IOException {
        Map.Entry<String,DataBlock> entry = sparse_index.floorEntry(key);
        RandomAccessFile file = new RandomAccessFile(file_address,"rw");
        if(entry == null) {
            return null;
        }
        byte[] stream = new byte[entry.getValue().len];
        file.seek(Long.valueOf(entry.getValue().start));
        file.read(stream);
        JSONObject json = new JSONObject(new String(stream,StandardCharsets.UTF_8));
        Value res = null;
        try {
            res = new Value(json.getJSONObject(key));
            if(res.command_type == Value.command.DELETE) return null;
        } catch (JSONException e){
            return null;
        }
        return res;

    }


}
