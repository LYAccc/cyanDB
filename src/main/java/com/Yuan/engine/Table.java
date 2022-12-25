package com.Yuan.engine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarException;

public class Table {
    private TreeMap<String,DataBlock> sparse_index;

    private String file_address;
    public Table(String file_address) throws IOException {
        this.file_address = file_address;
        this.sparse_index = new TreeMap<>();
        RandomAccessFile file = new RandomAccessFile(file_address,"rw");
        long file_len = file.length();
        file.seek(file_len - 12);
        long sparse_index_start = file.readLong();
        file.seek(file_len-4);
        int len = file.readInt();
        byte[] stream = new byte[len];
        file.seek(sparse_index_start);
        file.read(stream);
        JSONObject json = new JSONObject(new String(stream, StandardCharsets.UTF_8));
        for (String k: json.keySet()) {
            DataBlock d = new DataBlock((JSONObject) json.get(k));
               sparse_index.put(k,d);
        }
    }

    public Value query(String key) throws IOException {
        Map.Entry<String,DataBlock> entry = sparse_index.floorEntry(key);
        RandomAccessFile file = new RandomAccessFile(file_address,"rw");
        if(entry == null) return null;
        byte[] stream = new byte[entry.getValue().len];
        file.seek(Long.valueOf(entry.getValue().start));
        file.read(stream);
        JSONObject json = new JSONObject(new String(stream,StandardCharsets.UTF_8));
        Value res = null;
        try {
            res = new Value(json.getJSONObject(key));
        } catch (JSONException e){
            return null;
        }
        return res;

    }


}
