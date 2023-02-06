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

            List<TreeSet<file_min_max>> level = new ArrayList<>(); // list[0]... o level files collection,  file_name:[min_key,max_key]

            public Level(String file_address,String log_address) throws IOException {
                RandomAccessFile level_file = new RandomAccessFile(file_address,"rwd");
                RandomAccessFile level_log = new RandomAccessFile(log_address,"rwd");
            }
            public class file_min_max{
                  String file_name;
                  String min;
                  String max;
                  file_min_max (String file_name, String min, String max){
                            this.file_name = file_name;
                            this.min = min;
                            this.max = max;
                }
                String get_min(){
                      return min;
                }
                String get_max(){
                        return max;
                }
                  int compare_to(file_min_max another){  // matain all the tables in the order of their min and max
                        if(this.min.compareTo(another.min) != 0) return this.min.compareTo(another.min);
                        if(this.max.compareTo(another.max) != 0) return this.max.compareTo(another.max);
                        return this.file_name.compareTo(another.file_name); // in level greater than 0 we don't need to worry about min and max
                                                                            // overlap, however in level 0 we need to take care of the situation
                  }
            }
            public void load_level_file(RandomAccessFile level_file, RandomAccessFile level_log) throws IOException {
                            if(level_file.length() == 0 && level_log.length()==0) return;
                            if(level_file.length() != 0){
                                   byte[] databyte = new byte[(int)level_file.length()]; // could be problematic
                                   level_file.read(databyte);
                                   JSONObject jsondata = new JSONObject(new String(databyte, StandardCharsets.UTF_8));
//                                   json_to_level(jsondata);
                            }
                            if(level_log.length() != 0){
                                    byte[] databyte = new byte[(int)level_file.length()]; // could be problematic
                                    level_log.read(databyte);
                                    JSONArray jsonarr = new JSONArray(new String(databyte,StandardCharsets.UTF_8));
                                for (int i = 0; i < jsonarr.length(); i++) {
                                    JSONObject jsondata = jsonarr.getJSONObject(i);
                                    int cur_lev = jsondata.getInt("Level");
                                    String file_name = jsondata.getString("File_name");
                                      String min = jsondata.getString("Min");
                                      String max = jsondata.getString("Max");
                                      if(jsondata.getString("Action").equals("add")){  // action has add, delete
                                          if(cur_lev > level.size() - 1) level.add(new TreeSet<>((a,b) -> a.compare_to(b)));
                                                level.get(cur_lev).add(new file_min_max(jsondata.getString("File_name"),min,max));
                                      }
                                      else{ // action is "delete"
                                               level.get(cur_lev).remove(new file_min_max(jsondata.getString("File_name"),min,max));
                                      }
                                }

                                // to work
                                // delete content in level_log and level_file
                                level_log.seek(0);
                                level_log.setLength(0);
                                level_file.seek(0);
                                level_file.setLength(0);
                                // update level_file
                                write_level(level_file);

                            }
                            return;
            }

            public void write_level(RandomAccessFile level_file) throws IOException {
                JSONObject json = new JSONObject();   // level:file collections
                for (int i = 0; i < level.size(); i++) {
                    JSONArray jarr = new JSONArray(); // [{file:[min,max]},...]
                    for (file_min_max f: level.get(i)) {
                            JSONObject f_json = new JSONObject();
                            JSONArray arr_json = new JSONArray();
                            arr_json.putAll(new String[]{f.min,f.max});
                            f_json.put(f.file_name,arr_json.toString());
                            jarr.put(f_json);
                    }
                    json.put(String.valueOf(i),jarr);
                }
                level_file.seek(0);
                level_file.write(json.toString().getBytes(StandardCharsets.UTF_8));
                return;
            }

            public void json_to_level(JSONObject jsondata){
                for (String lev: jsondata.keySet()) {
                    JSONArray arr = jsondata.getJSONArray(lev);
                    int cur_level = Integer.valueOf(lev);
                    for (int i = 0; i < arr.length(); i++) {
                              JSONObject f = arr.getJSONObject(i);
                              if(cur_level > level.size() - 1) level.add(new TreeSet<>((a,b) -> a.compare_to(b)));
                              String file_name = f.keys().next();
                              String min = f.getJSONArray(file_name).getString(0); // 0 min;
                              String max = f.getJSONArray(file_name).getString(1); // 1 max;
                              level.get(cur_level).add(new file_min_max(file_name,min,max));
                    }
                }
            }

}
