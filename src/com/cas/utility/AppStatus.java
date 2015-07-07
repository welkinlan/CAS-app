/*
 * The utility class for checking wifi status
 */
package com.cas.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class AppStatus.
 */
public class AppStatus { 
	 
    /** The instance. */
    private static AppStatus instance = new AppStatus(); 
    
    /** The context. */
    static Context context; 
    
    /** The connectivity manager. */
    ConnectivityManager connectivityManager; 
    
    /** The mobile info. */
    NetworkInfo wifiInfo, mobileInfo; 
    
    /** The connected. */
    boolean connected = false; 
 
    /**
     * Gets the single instance of AppStatus.
     *
     * @param ctx the ctx
     * @return single instance of AppStatus
     */
    public static AppStatus getInstance(Context ctx) { 
        context = ctx; 
        return instance; 
    }
    
    /**
     * Gets the connection type.
     *
     * @param con the con
     * @return the connection type
     */
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
 
    /**
     * Checks if is online.
     *
     * @param con the con
     * @return true, if is online
     */
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

