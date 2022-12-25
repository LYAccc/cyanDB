package com.Yuan.engine;

import org.json.JSONObject;

public class DataBlock {
    public long start;
    public int len;
    public DataBlock(long s, int size){
           this.start = s;
           this.len = size;
    }
    public DataBlock(JSONObject json){
        this.start = Long.valueOf(json.getString("start"));
        this.len = Integer.valueOf(json.getString("len"));
    }
    public JSONObject to_json(){
           JSONObject json = new JSONObject();
           json.put("start",String.valueOf(start));
           json.put("len",String.valueOf(len));
           return json;
    }
}
