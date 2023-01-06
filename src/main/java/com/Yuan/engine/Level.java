package com.Yuan.engine;

import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Level {
            private String level_file_address;
            private String level_log_address;

            HashMap<Integer, HashSet<Pair<String,String[]>>> level = new HashMap<>(); // list[0]... o level files collection,  file_name:[min_key,max_key]

            public Level(String file_address,String log_address) throws IOException {

                RandomAccessFile level_file = new RandomAccessFile(file_address,"rwd");
                RandomAccessFile level_log = new RandomAccessFile(log_address,"rwd");


            }
            public void load_level_file(RandomAccessFile level_file, RandomAccessFile level_log) throws IOException {
                            if(level_file.length() == 0 && level_log.length()==0) return;
                            if(level_file.length() != 0){
                                   byte[] databyte = new byte[(int)level_file.length()]; // could be problematic
                                   level_file.read(databyte);
                                   JSONObject jsondata = new JSONObject(new String(databyte, StandardCharsets.UTF_8));
                                   json_to_level(jsondata);
                            }
                            if(level_log.length() != 0){
                                    byte[] databyte = new byte[(int)level_file.length()]; // could be problematic
                                    level_log.read(databyte);
                                    JSONArray jsonarr = new JSONArray(new String(databyte,StandardCharsets.UTF_8));
                                for (int i = 0; i < jsonarr.length(); i++) {
                                    JSONObject jsondata = jsonarr.getJSONObject(i);
                                    int cur_lev = jsondata.getInt("level");
                                    String file_name = jsondata.getString("file_name");

                                      if(jsondata.getString("action").equals("add")){  // action has add, delete
                                                level.putIfAbsent(cur_lev,new HashSet<>());
                                                String file_address = level_file_address.substring(0,level_file_address.lastIndexOf("\\")) +
                                                        jsondata.getString("file_name");
                                                Table table = new Table(file_address);
                                                table.set_min_max();
                                                level.get(cur_lev).add(new Pair<>(jsondata.getString("file_name"),new String[]{table.min_key, table.max_key}));
                                      }
                                      else{ // action is "delete"
                                               level.get(cur_lev).remove(jsondata.getString("file_name"));
                                      }
                                }
                                // to work delete content in level_log
                                // update level_file


                            }
            }

            public void json_to_level(JSONObject jsondata){
                for (String lev: jsondata.keySet()) {
                    JSONArray arr = jsondata.getJSONArray(lev);
                    int cur_level = Integer.valueOf(lev);
                    for (int i = 0; i < arr.length(); i++) {
                              JSONObject f = arr.getJSONObject(i);
                              level.putIfAbsent(cur_level,new HashSet<>());
                              String file_name = f.keys().next();
                              String min = f.getJSONArray(file_name).getString(0); // 0 min;
                              String max = f.getJSONArray(file_name).getString(1); // 1 max;
                              level.get(cur_level).add(new Pair<>(file_name,new String[]{min,max}));
                    }
                }

            }

}
