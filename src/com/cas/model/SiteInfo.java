/**
*  an Android implementation of REST and XML-RPC access to Moodle 2.2 servers or higher
*  Copyright (C) 2012  Justin Stevanz, Andrew Kelson and Matthias Peitsch
*
*	Contact the.omega.online@gmail.com for further information.
*
*   This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.cas.model;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

// TODO: Auto-generated Javadoc
/**
 * The Class SiteInfo.
 */
public class SiteInfo implements Parcelable {
	//private static SiteInfo instance;

	/**
	 * Instantiates a new site info.
	 */
	public SiteInfo() {

	}

//	public static SiteInfo getInstance() {
//		if (instance == null) {
//			synchronized(SiteInfo.class) {
//				if (instance == null) {
//					instance = new SiteInfo();
//				}
//			}
//		}
//		return instance;
//	}

	/** The sitename. */
private String sitename;	
	
	/**
	 * Sets the site name.
	 *
	 * @param sitename the new site name
	 */
	public void setSiteName(String sitename) {
       this.sitename = sitename;
    }

    /**
     * Gets the site name.
     *
     * @return the site name
     */
    public String getSiteName() {
       return sitename;
    }
    
    /** The username. */
    private String username;	
	
	/**
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(String username) {
       this.username = username;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
       return username;
    }
	    
    /** The firstname. */
    private String firstname;	
	
	/**
	 * Sets the firstname.
	 *
	 * @param firstname the new firstname
	 */
	public void setFirstname(String firstname) {
       this.firstname = firstname;
    }

    /**
     * Gets the firstname.
     *
     * @return the firstname
     */
    public String getFirstname() {
       return firstname;
    }
	    
    /** The lastname. */
    private String lastname;	
	
	/**
	 * Sets the lastname.
	 *
	 * @param lastname the new lastname
	 */
	public void setLastname(String lastname) {
       this.lastname = lastname;
    }

    /**
     * Gets the lastname.
     *
     * @return the lastname
     */
    public String getLastname() {
       return lastname;
    }
	    
    /** The fullname. */
    private String fullname;	
	
	/**
	 * Sets the fullname.
	 *
	 * @param fullname the new fullname
	 */
	public void setFullname(String fullname) {
       this.fullname = fullname;
    }

    /**
     * Gets the fullname.
     *
     * @return the fullname
     */
    public String getFullname() {
       return fullname;
    }
	    
    /** The userid. */
    private int userid;	
	
	/**
	 * Sets the userid.
	 *
	 * @param userid the new userid
	 */
	public void setUserid(int userid) {
       this.userid = userid;
    }

    /**
     * Gets the userid.
     *
     * @return the userid
     */
    public int getUserid() {
       return userid;
    }
	    
    /** The siteurl. */
    private String siteurl;	
	
	/**
	 * Sets the site url.
	 *
	 * @param siteurl the new site url
	 */
	public void setSiteUrl(String siteurl) {
       this.siteurl = siteurl;
    }

    /**
     * Gets the site url.
     *
     * @return the site url
     */
    public String getSiteUrl() {
       return siteurl;
    }
	    
    /** The userpictureurl. */
    private String userpictureurl;	
	
	/**
	 * Sets the user picture url.
	 *
	 * @param userpictureurl the new user picture url
	 */
	public void setUserPictureUrl(String userpictureurl) {
       this.userpictureurl = userpictureurl;
    }

    /**
     * Gets the user picture url.
     *
     * @return the user picture url
     */
    public String getUserPictureUrl() {
       return userpictureurl;
    }
    
    /** The downloadfiles. */
    private boolean downloadfiles;	
	
	/**
	 * Sets the download files.
	 *
	 * @param downloadfiles the new download files
	 */
	public void setDownloadFiles(boolean downloadfiles) {
       this.downloadfiles = downloadfiles;
    }

    /**
     * Gets the download files.
     *
     * @return the download files
     */
    public boolean getDownloadFiles() {
       return downloadfiles;
    }
    
    /** The functions. */
    private Map<String,String> functions = Collections.synchronizedMap(new TreeMap<String,String>());

    /**
     * Adds the function.
     *
     * @param name the name
     * @param version the version
     */
    public void addFunction(String name, String version) {
    	functions.put(name, version);
    }	 

    /**
     * Edits the function.
     *
     * @param oldVersion the old version
     * @param newVersion the new version
     * @param name the name
     * @return true, if successful
     */
    public boolean editFunction(String oldVersion, String newVersion, String name) {

        if(functions.containsKey(oldVersion)) {
        	functions.remove(oldVersion);
        	functions.put(newVersion, name);
            return true;
        }

        return false;
    }	
    
    /**
     * Gets the functions.
     *
     * @return the functions
     */
    public Map<String,String> getFunctions() {
    	return functions;
    }
    
    /**
     * Gets the function by name.
     *
     * @param name the name
     * @return the function by name
     */
    public String getFunctionByName(String name) {
    	return functions.get(name);
    }

//    public void viewFunctions() {
//
//        Set<Map.Entry<String, String>> set = functions.entrySet();
//        Iterator<Map.Entry<String, String>> it = set.iterator();
//        while(it.hasNext()) {
//            Map.Entry<String, String> entry = it.next();
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }
//    } 
    

    /**
 * Populate site info.
 *
 * @param jsonObject the json object
 */
public void populateSiteInfo(JSONObject jsonObject) {
    
    	    	 
    	try {  
    		if (jsonObject != null) {
    			
	    		String sitename = jsonObject.getString("sitename"); 
	    		this.setSiteName(sitename);
		        String username = jsonObject.getString("username"); 
		        this.setUsername(username);
		        String firstname = jsonObject.getString("firstname"); 
		        this.setFirstname(firstname);
		        String lastname = jsonObject.getString("lastname"); 
		        this.setLastname(lastname);
		        String fullname = jsonObject.getString("fullname"); 
		        this.setFullname(fullname);
		        String userid = jsonObject.getString("userid"); 
		        this.setUserid(Integer.valueOf(userid));
		        String siteurl = jsonObject.getString("siteurl"); 
		        this.setSiteUrl(siteurl);
		        String userpictureurl = jsonObject.getString("userpictureurl"); 
		        this.setUserPictureUrl(userpictureurl);
		        String downloadfiles = jsonObject.getString("downloadfiles"); 
		        this.setDownloadFiles((downloadfiles.equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		 	    
		        JSONArray functions = jsonObject.getJSONArray("functions");
	    	    // looping through All Contacts 
	    	    for(int i = 0; i < functions.length(); i++){ 
	    	        JSONObject c = functions.getJSONObject(i); 
	    	 
	    	        // Storing each json item in variable 
	    	        String name = c.getString("name"); 
	    	        String version = c.getString("version");   
	    	        this.addFunction(name, version);
	    	    } 

    		}
    	} catch (JSONException e) { 
    	    e.printStackTrace(); 
    	}
    }
    
    /* everything below here is for implementing Parcelable */ 
	 
    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    // 99.9% of the time you can just ignore this 
    public int describeContents() { 
        return 0; 
    } 
    
 /** The Constant CREATOR. */
 // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods 
    public static final Parcelable.Creator<SiteInfo> CREATOR = new Parcelable.Creator<SiteInfo>() { 
        public SiteInfo createFromParcel(Parcel in) { 
            return new SiteInfo(in); 
        } 
 
        public SiteInfo[] newArray(int size) { 
            return new SiteInfo[size]; 
        } 
    }; 
 
    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    // write your object's data to the passed-in Parcel 
    public void writeToParcel(Parcel dest, int flags) { 
    	dest.writeString(sitename);
    	dest.writeString(username);
    	dest.writeString(firstname);
    	dest.writeString(lastname);
    	dest.writeString(fullname);
    	dest.writeInt(userid);
    	dest.writeString(siteurl);
    	dest.writeString(userpictureurl);
    	dest.writeByte((byte) (downloadfiles ? 1 : 0));  
    	
    	dest.writeInt(functions.size());
        for (String s: functions.keySet()) {
            dest.writeString(s);
            dest.writeString(functions.get(s));
        }

    }
    
    /**
     * Instantiates a new site info.
     *
     * @param in the in
     */
    private SiteInfo(Parcel in) { 
        this.sitename = in.readString();
        this.username = in.readString();
        this.firstname = in.readString();
        this.lastname = in.readString();
        this.fullname = in.readString();
        this.userid = in.readInt();
        this.siteurl = in.readString();
        this.userpictureurl = in.readString();
        this.downloadfiles = in.readByte() == 1;

        int count = in.readInt();
        for (int i = 0; i < count; i++) {
        	this.addFunction(in.readString(), in.readString());
        }

    }  
}