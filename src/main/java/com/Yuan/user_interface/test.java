package com.Yuan.user_interface;


import com.Yuan.engine.CyanDB;
import com.Yuan.engine.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class test {

    public static void main(String[] args) throws IOException {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        CyanDB db_instance = CyanDB.build_instance_db("qing");
        int count = 0;
        String add = System.getProperty("user.dir") + "/src/main/java/com/Yuan/dataset/yelp_business_latitude.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(add))) {
            String line;
            while ((line = br.readLine()) != null && count < 1000) {

                String[] values = line.split(",");
                if(values.length>1)map.put(values[0],values[1]);
                else map.put(values[0],"null");

                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        for (String s :map.keySet()){
//            db_instance.Set(s,map.get(s));
//        }
//        db_instance.flush_memo_table_to_disk();

        int correct = 0;
        Set<String> set = map.keySet();
        for (String s :map.keySet()){
            if(db_instance.get(s) == null){
                  continue;
            }
            String val = db_instance.get(s).raw_value;
            if(!val.equals(map.get(s))){
                System.out.println(val);
            }
            else correct++;
        }
        System.out.println((double) correct);
        System.out.println(set.size());



    }

    }

