package com.fgroupindonesia.helper;

import org.json.JSONObject;

public class RespondHelper {

    public static boolean isValidRespond(String respond) throws Exception{

        JSONObject jo = new JSONObject(respond);

        if(jo.getString("status").equalsIgnoreCase("valid")){
            return true;
        }

        return false;
    }

}
