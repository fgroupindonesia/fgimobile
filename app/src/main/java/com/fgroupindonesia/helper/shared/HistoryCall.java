package com.fgroupindonesia.helper.shared;

import android.app.Activity;
import android.content.Context;

import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.WebRequest;

public class HistoryCall {

    private static Activity myAct;
    private static Navigator myNavigator;
    private static String tokenAPI;

    public static void setReference(Activity act, Navigator nav, String aToken){
        myAct = act;
        myNavigator = nav;
        tokenAPI = aToken;
    }

    public static void addHistory(String usName, String desc){

        // only if the activity and navigator are predefined earlier
        if(myAct != null && myNavigator != null) {
            WebRequest httpCall = new WebRequest(myAct, myNavigator);

            httpCall.addData("description", desc);
            httpCall.addData("username", usName);
            httpCall.addData("token", tokenAPI);
            httpCall.setWaitState(true);
            httpCall.setRequestMethod(WebRequest.POST_METHOD);
            httpCall.setTargetURL(URLReference.HistoryAdd);
            httpCall.execute();
        }

    }

}
