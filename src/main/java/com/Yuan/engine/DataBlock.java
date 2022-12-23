package com.Yuan.engine;

import org.json.JSONObject;

public class DataBlock {
    public long start;
    public long len;
    public DataBlock(long s, long size){
           this.start = s;
           this.len = size;
    }
    public JSONObject to_json(){
           JSONObject json = new JSONObject();
           json.put("start",String.valueOf(start));
           json.put("len",String.valueOf(len));
           return json;
    }
}
