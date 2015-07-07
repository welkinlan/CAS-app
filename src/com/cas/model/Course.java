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

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

// TODO: Auto-generated Javadoc
/**
 * The Class Course.
 */
public class Course implements Parcelable {
	//private static Course instance;

	/**
	 * Instantiates a new course.
	 */
	public Course() {

	}

//	public static Course getInstance() {
//		if (instance == null) {
//			synchronized(Course.class) {
//				if (instance == null) {
//					instance = new Course();
//				}
//			}
//		}
//		return instance;
//	}
	
	/** The id. */
private int id;	
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
       this.id = id;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
       return id;
    }

	/** The shortname. */
	private String shortname;	
	
	/**
	 * Sets the short name.
	 *
	 * @param shortname the new short name
	 */
	public void setShortName(String shortname) {
       this.shortname = shortname;
    }

    /**
     * Gets the short name.
     *
     * @return the short name
     */
    public String getShortName() {
       return shortname;
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
    
    /** The enrolledusercount. */
    private int enrolledusercount;	
	
	/**
	 * Sets the enrolled user count.
	 *
	 * @param enrolledusercount the new enrolled user count
	 */
	public void setEnrolledUserCount(int enrolledusercount) {
       this.enrolledusercount = enrolledusercount;
    }

    /**
     * Gets the enrolled user count.
     *
     * @return the enrolled user count
     */
    public int getEnrolledUserCount() {
       return enrolledusercount;
    }
    
    /** The idnumber. */
    private String idnumber;	
	
	/**
	 * Sets the id number.
	 *
	 * @param idnumber the new id number
	 */
	public void setIdNumber(String idnumber) {
       this.idnumber = idnumber;
    }

    /**
     * Gets the id number.
     *
     * @return the id number
     */
    public String getIdNumber() {
       return idnumber;
    }
    
    /** The visible. */
    private int visible;	
	
	/**
	 * Sets the visible.
	 *
	 * @param visible the new visible
	 */
	public void setVisible(int visible) {
       this.visible = visible;
    }

    /**
     * Gets the visible.
     *
     * @return the visible
     */
    public int getVisible() {
       return visible;
    }
    
    /** The coursecontents. */
    private ArrayList<CourseContent> coursecontents = new ArrayList<CourseContent>();	
	
	/**
	 * Sets the course content.
	 *
	 * @param coursecontents the new course content
	 */
	public void setCourseContent(ArrayList<CourseContent> coursecontents) {
       this.coursecontents = coursecontents;
    }

    /**
     * Gets the course content.
     *
     * @return the course content
     */
    public ArrayList<CourseContent> getCourseContent() {
       return coursecontents;
    }

    /**
     * Populate course.
     *
     * @param jsonObject the json object
     */
    public void populateCourse(JSONObject jsonObject) {    
    	    	 
    	try {  
    		if (jsonObject != null) {
    			
	    		String id = jsonObject.getString("id"); 
	    		this.setId(Integer.valueOf(id));
		        String shortname = jsonObject.optString("shortname"); 
		        if (shortname != null && shortname.trim().length() > 0)
		        	this.setShortName(shortname);
		        String fullname = jsonObject.optString("fullname");  
		        if (fullname != null && fullname.trim().length() > 0)
		        	this.setFullname(fullname);
		        String enrolledusercount = jsonObject.optString("enrolledusercount");  
		        if (enrolledusercount != null && enrolledusercount.trim().length() > 0)
		        	this.setEnrolledUserCount(Integer.valueOf(enrolledusercount));
		        String idnumber = jsonObject.optString("idnumber"); 
		        if (idnumber != null && idnumber.trim().length() > 0) 
		        	this.setIdNumber(idnumber);
		        String visible = jsonObject.optString("visible");  
		        if (visible != null && visible.trim().length() > 0)
		        	this.setVisible(Integer.valueOf(visible));
		        
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
    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() { 
        public Course createFromParcel(Parcel in) { 
            return new Course(in); 
        } 
 
        public Course[] newArray(int size) { 
            return new Course[size]; 
        } 
    }; 
 
    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    // write your object's data to the passed-in Parcel 
    public void writeToParcel(Parcel dest, int flags) { 
    	dest.writeInt(id);
    	dest.writeString(shortname);
    	dest.writeString(fullname);
    	dest.writeInt(enrolledusercount);
    	dest.writeString(idnumber);
    	dest.writeInt(visible);
    	dest.writeTypedList(coursecontents); 
    }
    
    /**
     * Instantiates a new course.
     *
     * @param in the in
     */
    private Course(Parcel in) { 
        this.id = in.readInt();
        this.shortname = in.readString();
        this.fullname = in.readString();
        this.enrolledusercount = in.readInt();
        this.idnumber = in.readString();
        this.visible = in.readInt();
        in.readTypedList(this.coursecontents, CourseContent.CREATOR); 
    }
    
}