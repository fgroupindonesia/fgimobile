package com.fgroupindonesia.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;

public class OptionConfiguration {
	
	String filenameInternal, result;
	Context aContext;
	Activity activity;
	boolean fileExistance=false;
	
	// need to be called
	// after context was initialized earlier
	public boolean exists(){
		
		File file = aContext.getFileStreamPath(filenameInternal);
	    if(file == null || !file.exists()) {
	        return false;
	    }
	    
	    return true;
		
	}
	
	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public OptionConfiguration(){
		//default
		this.setFilenameInternal("fgimobile-options.dat");
	}
	
	private String nextLine(){
		return System.getProperty("line.separator");
	}
	
	public String getDataOpsi(){
		  StringBuffer sb = new StringBuffer();
		  // the order is quite simple
		  // 1st
		  sb.append(UserData.Username);
		  sb.append(nextLine());
		  // 2nd
		  sb.append(UserData.Passw);
		  sb.append(nextLine());
		  // 3rd
		  sb.append(UserData.RememberLogin);
		  sb.append(nextLine());
		  // 4th
		  sb.append(UserData.NotifLimitPayment);
		
		return sb.toString();
		
	}
	
	public void writeConfigFile(){
		
		FileOutputStream outputStream;

        try {
           
            outputStream = aContext.openFileOutput(filenameInternal, Context.MODE_PRIVATE);
           
            // save by bytes
            outputStream.write(getDataOpsi().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
	 public String readConfigFile() {
	        try {
	            FileInputStream fileInputStream = aContext.openFileInput(filenameInternal);
	            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

	            StringBuffer sb = new StringBuffer();
	            String line = null;

	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	                sb.append(nextLine());
	            }
	            result = sb.toString();
	            //for future usage
	            //by another activities
	            parseData(result);
	        } catch (Exception e) {
	        	ShowDialog.shortMessage(this.getActivity(), e.getMessage());
	            ShowDialog.shortMessage(this.getActivity(), "Error while reading config file!");
	        }
	        
	        return result;
	    }

	 private void parseData(String in){
		 
		 String wholeData[] = in.split(nextLine());
		 UserData.Username=wholeData[0];
		 UserData.Passw=wholeData[1];
		 UserData.RememberLogin=Boolean.valueOf(wholeData[2]);
		 UserData.NotifLimitPayment=Boolean.valueOf(wholeData[3]);
		 
	 }
	 
	public String getFilenameInternal() {
		return filenameInternal;
	}

	public void setFilenameInternal(String filenameInternal) {
		this.filenameInternal = filenameInternal;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Context getContext() {
		return aContext;
	}

	public void setContext(Context aContext) {
		this.aContext = aContext;
	}
}
