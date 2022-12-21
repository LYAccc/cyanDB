package com.Yuan.engine;

import org.json.JSONObject;

public class Value {

        public static enum command{
                SET,
                DELETE
        }
        command command_type; // can only be DELETE, SET
        String raw_value;
        String key; // key associated to this value object

        public Value(command c, String r, String key){
                command_type = c;
                raw_value = r;
                this.key = key;
        }

        public JSONObject tojason(){
                JSONObject j = new JSONObject();
                j.put("Key",key);
                if(command_type.equals(command.SET)){
                        j.put("Command","SET");
                        j.put("Value",raw_value);
                }
                else{
                        j.put("Command","DELETE");
                }
                return j;
        }



//        public static Value json_to_value(JSONObject json){
//
//        }
}
