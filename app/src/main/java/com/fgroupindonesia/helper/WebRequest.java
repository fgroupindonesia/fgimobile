package com.fgroupindonesia.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class WebRequest {

	 URL url;
	 String response = "";
	 final String charset ="UTF-8";
	 String targetURL = null, endResult=null;
	 boolean verifiedCodeStat=false;
	 public WebCall webcall;
	 // private static final String URL_ROOT_API = "http://api.fgroupindonesia.com/fgimobile";

	 // for production purposes call below URL
	 private static final String URL_ROOT_API = "http://192.168.0.10/fgimobile";
	 public static final String URL_LOGIN = "/login", URL_NEW_REQUEST = "/save/new/request",
			URL_BUY_VOUCHER="/buy", URL_CHANGE_PASS="/options/changepass",
			URL_STATUS_VOUCHER = "/read/status/voucher",
			URL_STATUS_PAYMENT = "/read/status/payment",
			URL_STATUS_REQUEST="/read/status/request",
			 URL_STATUS_LESSON="/read/status/lesson",
			URL_VERIFICATION_VOUCHER = "/verify/voucher",
			URL_USER_LIMIT="/read/user/limit",
			URL_USER_BASIC_ATTENDANT="/read/basic/attendant",
			 URL_USER_NEXT_ATTENDANT="/read/next/attendant",
			URL_USER_SINGLE_ATTENDANT="/read/single/attendant",
			URL_USER_SINGLE_LESSON="/read/single/lesson",
			URL_USER_ALL_ATTENDANTS="/read/detail/attendant",
			URL_UPDATE_VOUCHER= "/update/time/voucher",
			URL_CHANGE_ATTENDANT= "/update/status/attendant",
			URL_NEW_ATTENDANT= "/save/new/attendant",
			URL_NEW_CONFIRMATION_PAYMENT = "/save/new/confirmation/username";
	 
	 public static final int POST_METHOD = 1, GET_METHOD=2;
	 public static final int SERVER_ERROR = -1, SERVER_NO_RESULT = 0, SERVER_SUCCESS=1, SERVER_VERIFIED=2;
	
	 private String boundary;
	 private static final String LINE_FEED = "\r\n";
	 
	 private int pilihanMethod=0, statusCode=0;
	 private ArrayList<String> keys = new ArrayList();
	 private ArrayList<String> values = new ArrayList(); 

	 private ArrayList<String> keysFiles  = new ArrayList();
	 private ArrayList<FileInputStream> fileStreams = new ArrayList(); 
	 private ArrayList<String> fileNames = new ArrayList(); 

	 private Context myContext;
	 private boolean multipartform = false;
	 private static boolean waitState = false;
	 
	public boolean isMultipartform() {
		return multipartform;
	}

	public void setMultipartform(boolean multipartform) {
		this.multipartform = multipartform;
		
		if(multipartform==true)
		boundary = "===" + System.currentTimeMillis() + "===";
		
	}

	public WebRequest(Context contIn, WebCall webCallIn){
		myContext = contIn;
		pilihanMethod= GET_METHOD;
		webcall = webCallIn;
	}

	public WebRequest(){
		pilihanMethod= GET_METHOD;
	}
	
	public WebRequest(int pilihanNyaMethod){
		pilihanMethod = pilihanNyaMethod;
	}
	
	public void clearData(){
		keys = new ArrayList();
		values = new ArrayList();
	}
	
	public void clearDataMultipartForm(){
		keysFiles = new ArrayList();
		fileStreams = new ArrayList();
		fileNames = new ArrayList();
	}

	public static boolean isWaitState(){
		return waitState;
	}

	public void setWaitState(boolean b){
		waitState = b;
	}

	public int getStatusCode(){
		return statusCode;
	}
	
	public void setStatusCode(int stat){
		statusCode = stat;
	}
	
	public String getEndResult(){
		return endResult;
	}
	
	private void setStatusVerifiedCode(boolean b){
			verifiedCodeStat=b;
	}
	
	public boolean getStatusVerifiedCode(){
		return verifiedCodeStat;
	}
	
	public void setTargetURL(String tujuanURL){
		targetURL = URL_ROOT_API + tujuanURL;
	}
	
	public String getAllDataPassed(){
		
		StringBuilder result = new StringBuilder();
		String overall=null;
		
		if(this.isMultipartform()!=true){
			
			// if this is normal instead of multipartform request
			// we do usual keyvalue pairings...
			// this method is to build the 
			// key=value& ' format along with the passing HTTP Request
			
			for (int index=0; index<keys.size(); index++) {
				result.append(keys.get(index));
				result.append("=");
				result.append(values.get(index));
				result.append("&");
			}
			
			// last character remove the &' from the end of bufferstring
			overall= result.toString();
			overall = overall.substring(0, overall.length()-1);
			
			
		}
		
		return overall;
	}
	
	public void addData(String keyIn, String valIn){
		try{
			keys.add(URLEncoder.encode(keyIn, charset));
			values.add(URLEncoder.encode(valIn, charset));
		}catch(Exception ex){
			endResult = "Error while adding data!";
		}
		
	}
	
	public void addData(String keyIn, boolean valIn){
		
		int nilai = valIn? 1:0;
		
		try{
			keys.add(URLEncoder.encode(keyIn, charset));
			values.add(""+nilai);
		}catch(Exception ex){
			endResult = "Error while adding data!";
		}
		
	}
	
	public void addFile(String keyIn, File fileIn){
		
		try{
			keysFiles.add(URLEncoder.encode(keyIn, charset));
			FileInputStream inputStream = new FileInputStream(fileIn);
			fileStreams.add(inputStream);
			fileNames.add(fileIn.getName());
		}catch(Exception ex){
			endResult = "Error while adding file!";
		}
		
	}
	
	public void execute(){
		
		 try {
			 
			 if(targetURL.contains(URL_NEW_CONFIRMATION_PAYMENT)){
				 // we modified the end point
				 // become username
				 targetURL = targetURL.replace("username", values.get(0));
				 // then clear all key-values 
				 // except for multipartform purposes (file will still remains)
				 this.clearData();
			 }
			 
		        url = new URL(targetURL);

		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(5000);
		        conn.setConnectTimeout(5000);
		        
		        if(pilihanMethod==POST_METHOD){
		        	conn.setRequestMethod("POST");	
		        }else{
		        	conn.setRequestMethod("GET");
		        }
		        
		        // we want to receive the input & output stream
		        conn.setUseCaches(false);
		        conn.setDoInput(true);
		        conn.setDoOutput(true);

		        if(isMultipartform()){
		        	
		        	conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		            //conn.setRequestProperty("User-Agent", "FGIMobile");
		        	
		        }
		        
		        OutputStream outputStream = conn.getOutputStream();
		        BufferedWriter writer = new BufferedWriter(
		                new OutputStreamWriter(outputStream, "UTF-8"));
		        
		        if(isMultipartform()==false){
			        writer.write(this.getAllDataPassed());

		        }else {
		        	// we do writing for each data stored
		        	// for multipartform request
		        	
		        	// write each key-values
		        	for(int index=0; index<keys.size(); index++){
		        		writer.append("--" + boundary).append(LINE_FEED);
			            writer.append("Content-Disposition: form-data; name=\"" + keys.get(index) + "\"")
			                    .append(LINE_FEED);
			            writer.append("Content-Type: text/plain; charset=" + charset).append(
			                    LINE_FEED);
			            writer.append(LINE_FEED);
			            writer.append(values.get(index)).append(LINE_FEED);
			            writer.flush();
		        		
		        	}
		        	
		        	// write each file contents
		        	for (int index=0; index<keysFiles.size(); index++){
		        		String fileName = fileNames.get(index);
		                writer.append("--" + boundary).append(LINE_FEED);
		                writer.append(
		                        "Content-Disposition: form-data; name=\"" + keysFiles.get(index)
		                                + "\"; filename=\"" + fileName + "\"")
		                        .append(LINE_FEED);
		                writer.append(
		                        "Content-Type: "
		                                + conn.guessContentTypeFromName(fileName))
		                        .append(LINE_FEED);
		                writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
		                writer.append(LINE_FEED);
		                writer.flush();

		                FileInputStream inputStream = fileStreams.get(index);
		                byte[] buffer = new byte[4096];
		                int bytesRead = -1;
		                while ((bytesRead = inputStream.read(buffer)) != -1) {
		                    outputStream.write(buffer, 0, bytesRead);
		                }
		                outputStream.flush();
		                inputStream.close();

		                writer.append(LINE_FEED);
		                writer.flush();
		        	}
		        	
		        	// enclosing
		        	writer.append(LINE_FEED).flush();
		            writer.append("--" + boundary + "--").append(LINE_FEED);
		           
		        	
		        }


		        writer.flush();
		        writer.close();
		        outputStream.close();
		        
		        int responseCode=conn.getResponseCode();

		        if (responseCode == HttpURLConnection.HTTP_OK) {
		            String line;
		            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
		            while ((line=br.readLine()) != null) {
		                response+=line;
		            }
		        }
		        else {
		            response="error";    

		        }
		        
		        endResult = response;
		        
		        // this is for debugging purposes
		        tryToGetStatusCode(endResult);

		        // if this is not waitable respond
			 	// then we proceed to the next activity
		        if(!isWaitState()){
		        	webcall.nextActivity();
				}else{
		        	// if this is waitable
					// then we manually handle the success respond
					webcall.onSuccess(endResult);
				}

		        
		    } catch (Exception e) {
		    	cetakErrorFile(e);
		    }
		
	}


	
	private void tryToGetStatusCode(String serverReply){
	
		// if we can convert into numbers there are several possibilities
		// 0 - no result
		// 1 - success
		// -1 - error
		// other number

		try{
			statusCode = Integer.parseInt(serverReply);
			
			if(statusCode>=1){
				// if we obtain an ID from here
				// so we said
				if(statusCode==SERVER_VERIFIED){
					setStatusVerifiedCode(true);
				}
				statusCode=SERVER_SUCCESS;
			}
			
		}catch(Exception ex){

			// we ensure this is a json
			if(serverReply!=null){
				if(serverReply.contains("{") && serverReply.contains("}")){
					statusCode = SERVER_SUCCESS;
				}else if(serverReply.length()==0){
					statusCode = SERVER_ERROR;
				}else if(serverReply.contains("error")!=true) {
					statusCode = SERVER_SUCCESS;
				}	
			}else{
				statusCode = SERVER_ERROR;
			}
			
			
		}
	
	
		
	}
	
	private void cetakErrorFile(Exception ex){
		
		File file = null;
		try{
			File sdCard = Environment.getExternalStorageDirectory();
			file = new File(sdCard.getAbsolutePath() + "/data");
			file.mkdirs();
			file = new File(file.getAbsolutePath() + "/test.log");
			file.createNewFile();
	    	PrintStream ps = new PrintStream(file);
	    	ex.printStackTrace(ps);
	    	ps.close();
	    	
		} catch(Exception e){
			endResult = "Permission on file writing!";
		}
		
	}

	public static boolean checkConnection(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

		if (activeNetworkInfo != null) { // connected to the internet
			// Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

			if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				// connected to wifi
				return true;
			} else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				// connected to the mobile provider's data plan
				return true;
			}
		}
		return false;
	}
	
	
}
