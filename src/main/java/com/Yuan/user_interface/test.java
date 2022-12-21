package com.Yuan.user_interface;

import com.Yuan.engine.CyanDB;
import netscape.javascript.JSObject;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.*;
import com.Yuan.engine.Value;


public class test {


    public static void main(String[] args) throws IOException {
       JSONObject j = new JSONObject();
       Value v = new Value(Value.command.SET,"qing","liu");
       j.put("liu",v.tojason());
       System.out.println(j);
       System.out.println(j.getJSONObject("liu").get("Command"));


    }
}
