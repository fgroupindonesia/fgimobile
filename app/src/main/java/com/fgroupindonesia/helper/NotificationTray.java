package com.fgroupindonesia.helper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.fgroupindonesia.fgimobile.R;

public class NotificationTray {

	public static Context AppContext;
	
	public static int NotifID=28;
	
	public static void showMessage(String title, String pesan){
		
		
		NotificationCompat.Builder builder =
   	         new NotificationCompat.Builder(AppContext)
   	         .setSmallIcon(R.drawable.fg_logo)
   	         .setAutoCancel(true)
   	         .setContentText(pesan)
   	         .setLargeIcon(BitmapFactory.decodeResource(AppContext.getResources(), R.drawable.fg_logo))
   	         .setContentTitle(title);
   	          
   	      Intent notificationIntent = new Intent(AppContext, DataAbsensiActivity.class);
   	      PendingIntent contentIntent = PendingIntent.getActivity(AppContext, 0, notificationIntent,
   	         PendingIntent.FLAG_UPDATE_CURRENT);
   	      builder.setContentIntent(contentIntent);

   	      // Add as notification
   	      NotificationManager manager = (NotificationManager) AppContext.getSystemService(Context.NOTIFICATION_SERVICE);
   	      manager.notify(NotifID, builder.build());
		
	}
	
	public static void clearAllMessages(){
		if(AppContext!=null){
			NotificationManager mNotificationManager = (NotificationManager) AppContext.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(NotifID);
		}

	}
	
}
