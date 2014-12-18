package com.cas.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppStatus { 
	 
    private static AppStatus instance = new AppStatus(); 
    static Context context; 
    ConnectivityManager connectivityManager; 
    NetworkInfo wifiInfo, mobileInfo; 
    boolean connected = false; 
 
    public static AppStatus getInstance(Context ctx) { 
        context = ctx; 
        return instance; 
    }
    
    public String getConnectionType(Context con) { 
    	String haveConnectedWifi = null; 
    	String haveConnectedMobile = null; 
	 
	    ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo(); 
	    for (NetworkInfo networkInfo : netInfo) { 
	        if (networkInfo.getTypeName().equalsIgnoreCase("WIFI")) 
	            if (networkInfo.isAvailable() && networkInfo.isConnected()) 
	                haveConnectedWifi = "WIFI"; 
	        if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) 
	            if (networkInfo.isAvailable() && networkInfo.isConnected()) 
	                haveConnectedMobile = "MOBILE"; 
	    } 
	    return (haveConnectedWifi != null) ? haveConnectedWifi : haveConnectedMobile; 
	}
 
    public boolean isOnline(Context con) { 
    	boolean haveConnectedWifi = false; 
	    //boolean haveConnectedMobile = false;
	    
		connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo(); 
	    for (NetworkInfo networkInfo : netInfo) { 
	        if (networkInfo.getTypeName().equalsIgnoreCase("WIFI")) 
	            if (networkInfo.isAvailable() && networkInfo.isConnected()) 
	                haveConnectedWifi = true; 
	        /*
	        if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) 
	            if (networkInfo.isAvailable() && networkInfo.isConnected()) 
	                haveConnectedMobile = true; 
	        */
	    } 
	    return haveConnectedWifi;
    } 
} 

