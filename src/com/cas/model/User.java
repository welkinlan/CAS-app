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

import java.util.ArrayList;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;


// TODO: Auto-generated Javadoc
/**
 * The Class User.
 */
public class User implements Parcelable {
	
	/**
	 * Instantiates a new user.
	 */
	public User() {

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
    
    /** The password. */
    private String password;	
	
	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(String password) {
       this.password = password;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
       return password;
    }
    
    /** The url. */
    private String url;	
	
	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
       this.url = url;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
       return url;
    }
    
    /** The token. */
    private String token;	
	
	/**
	 * Sets the token.
	 *
	 * @param token the new token
	 */
	public void setToken(String token) {
       this.token = token;
    }

    /**
     * Gets the token.
     *
     * @return the token
     */
    public String getToken() {
       return token;
    }
    
    /** The tokencreatedate. */
    private Date tokencreatedate;	
	
	/**
	 * Sets the token create date.
	 */
	public void setTokenCreateDate() {
       this.tokencreatedate = new Date(); 
    }

    /**
     * Gets the token create date.
     *
     * @return the token create date
     */
    public Date getTokenCreateDate() {
       return tokencreatedate;
    }
    
    /** The selectedcourseid. */
    private int selectedcourseid = 99999;	
   	
	   /**
	    * Sets the selected course id.
	    *
	    * @param selectedcourseid the new selected course id
	    */
	   public void setSelectedCourseId(int selectedcourseid) {
   	  this.selectedcourseid = selectedcourseid;
   	}

   	/**
	    * Gets the selected course id.
	    *
	    * @return the selected course id
	    */
	   public int getSelectedCourseId() {
      return selectedcourseid;
   	}
    
    /** The siteinfo. */
    private SiteInfo siteinfo;	
    
    /**
     * Sets the site info.
     *
     * @param siteinfo the new site info
     */
    public void setSiteInfo(SiteInfo siteinfo) {
	   this.siteinfo = siteinfo; 
	}
    
    /**
     * Gets the site info.
     *
     * @return the site info
     */
    public SiteInfo getSiteInfo() {
       return siteinfo;
    }
    
    /** The courses. */
    private ArrayList<Course> courses = new ArrayList<Course>();	
    
    /**
     * Sets the courses.
     *
     * @param courses the new courses
     */
    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses; 
     }
    
    /**
     * Gets the courses.
     *
     * @return the courses
     */
    public ArrayList<Course> getCourses() {
       return courses;
    }
    
    /**
     * Gets the course.
     *
     * @param id the id
     * @return the course
     */
    public Course getCourse(int id) {
    	for (Course course : courses) { 
		  if (course.getId() == id) { 
		    return course;  
		  } 
		} 
		return null; // course not found. 
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
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() { 
        public User createFromParcel(Parcel in) { 
            return new User(in); 
        } 
 
        public User[] newArray(int size) { 
            return new User[size]; 
        } 
    }; 
 
    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    // write your object's data to the passed-in Parcel 
    public void writeToParcel(Parcel dest, int flags) { 
    	dest.writeString(username); 
    	dest.writeString(password);
    	dest.writeString(url);
    	dest.writeString(token);
    	dest.writeLong(tokencreatedate.getTime());
    	dest.writeInt(selectedcourseid);
    	dest.writeParcelable(siteinfo, flags);
    	dest.writeTypedList(courses); 
    }
    
    /**
     * Instantiates a new user.
     *
     * @param in the in
     */
    private User(Parcel in) { 
        this.username = in.readString(); 
        this.password = in.readString();
        this.url = in.readString();
        this.token = in.readString();
        this.tokencreatedate = new Date(in.readLong());
        this.selectedcourseid = in.readInt();
        this.siteinfo = in.readParcelable(SiteInfo.class.getClassLoader());
        in.readTypedList(this.courses, Course.CREATOR); 
    }
}