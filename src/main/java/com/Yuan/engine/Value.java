package com.Yuan.engine;

public class Value {

        String command_type; // can only be DELETE, SET
        String raw_value;

        public Value(String c, String r){
                command_type = c;
                raw_value = r;
        }

        @Override
        public String toString() {
                return
                        "command_type=" + command_type  +
                        ", raw_value=" + raw_value;
        }
}
