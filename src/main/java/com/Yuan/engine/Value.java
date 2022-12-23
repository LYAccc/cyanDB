package com.Yuan.engine;

import org.json.JSONObject;

public class Value {

        public static enum command{
                SET,
                DELETE
        }
        command command_type; // can only be DELETE, SET
        String raw_value;

        public Value(command c, String r){
                command_type = c;
                raw_value = r;
        }
        public Value(command c){
                command_type = c;
        }

        public JSONObject to_json(){
                JSONObject j = new JSONObject();
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
