package com.Yuan.engine;

import org.json.JSONObject;

public class Value {

        public static enum command{
                SET,
                DELETE
        }
        public command command_type; // can only be DELETE, SET
        public String raw_value;
        public Value(JSONObject json){
                if (json.getString("Command").equals("SET")){
                        raw_value = json.getString("Value");
                        command_type = command.SET;
                }
                else command_type = command.DELETE;
        }
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
}
