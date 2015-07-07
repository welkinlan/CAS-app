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

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

// TODO: Auto-generated Javadoc
/**
 * The Class Content.
 */
public class Content implements Parcelable  {
		
	//private static Content instance;

	/**
	 * Instantiates a new content.
	 */
	public Content() {

	}

//	public static Content getInstance() {
//		if (instance == null) {
//			synchronized(Content.class) {
//				if (instance == null) {
//					instance = new Content();
//				}
//			}
//		}
//		return instance;
//	}
	
	/** The type. */
private String type;	
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
       this.type = type;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
       return type;
    }
    
    /** The filename. */
    private String filename;	
	
	/**
	 * Sets the file name.
	 *
	 * @param filename the new file name
	 */
	public void setFileName(String filename) {
       this.filename = filename;
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
       return filename;
    }
    
    /** The filepath. */
    private String filepath;	
	
	/**
	 * Sets the file path.
	 *
	 * @param filepath the new file path
	 */
	public void setFilePath(String filepath) {
       this.filepath = filepath;
    }

    /**
     * Gets the file path.
     *
     * @return the file path
     */
    public String getFilePath() {
       return filepath;
    }
	
	/** The filesize. */
	private int filesize;	
	
	/**
	 * Sets the file size.
	 *
	 * @param filesize the new file size
	 */
	public void setFileSize(int filesize) {
       this.filesize = filesize;
    }

    /**
     * Gets the file size.
     *
     * @return the file size
     */
    public int getFileSize() {
       return filesize;
    }

	/** The fileurl. */
	private String fileurl;	
	
	/**
	 * Sets the file url.
	 *
	 * @param fileurl the new file url
	 */
	public void setFileUrl(String fileurl) {
       this.fileurl = fileurl;
    }

    /**
     * Gets the file url.
     *
     * @return the file url
     */
    public String getFileUrl() {
       return fileurl;
    }
    
    /** The content. */
    private String content;	
	
	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	public void setContent(String content) {
       this.content = content;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public String getContent() {
       return content;
    }
    
    /** The timecreated. */
    private long timecreated;	
	
	/**
	 * Sets the time created.
	 *
	 * @param timecreated the new time created
	 */
	public void setTimeCreated(long timecreated) {
       this.timecreated = timecreated;
    }

    /**
     * Gets the time created.
     *
     * @return the time created
     */
    public long getTimeCreated() {
       return timecreated;
    }
    
    /** The timemodified. */
    private long timemodified;	
	
	/**
	 * Sets the time modified.
	 *
	 * @param timemodified the new time modified
	 */
	public void setTimeModified(long timemodified) {
       this.timemodified = timemodified;
    }

    /**
     * Gets the time modified.
     *
     * @return the time modified
     */
    public long getTimeModified() {
       return timemodified;
    }
    
    /** The sortorder. */
    private int sortorder;	
	
	/**
	 * Sets the sort order.
	 *
	 * @param sortorder the new sort order
	 */
	public void setSortOrder(int sortorder) {
       this.sortorder = sortorder;
    }

    /**
     * Gets the sort order.
     *
     * @return the sort order
     */
    public int getSortOrder() {
       return sortorder;
    }
    
    /** The userid. */
    private int userid;	
	
	/**
	 * Sets the user id.
	 *
	 * @param userid the new user id
	 */
	public void setUserId(int userid) {
       this.userid = userid;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public int getUserId() {
       return userid;
    }
	    
    /** The author. */
    private String author;	
	
	/**
	 * Sets the author.
	 *
	 * @param author the new author
	 */
	public void setAuthor(String author) {
       this.author = author;
    }

    /**
     * Gets the author.
     *
     * @return the author
     */
    public String getAuthor() {
       return author;
    }
    
    /** The license. */
    private String license;	
	
	/**
	 * Sets the license.
	 *
	 * @param license the new license
	 */
	public void setLicense(String license) {
       this.license = license;
    }

    /**
     * Gets the license.
     *
     * @return the license
     */
    public String getLicense() {
       return license;
    }

    /** The transition. */
    private String transition;	
	
	/**
	 * Gets the transition.
	 *
	 * @return the transition
	 */
	public String getTransition() {
		return transition;
	}

	/**
	 * Sets the transition.
	 *
	 * @param transition the new transition
	 */
	public void setTransition(String transition) {
		this.transition = transition;
	}

    
    /** The mode. */
    private String mode;	
	
	/**
	 * Gets the mode.
	 *
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * Sets the mode.
	 *
	 * @param mode the new mode
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
    
    /**
     * Populate content.
     *
     * @param jsonObject the json object
     */
    public void populateContent(JSONObject jsonObject) {
     
		if (jsonObject != null) {    			
	        String type = jsonObject.optString("type"); 
	        if (type != null && type.trim().length() > 0)
	        	this.setType(type);
	        String filename = jsonObject.optString("filename"); 
	        if (filename != null && filename.trim().length() > 0)
	        	this.setFileName(filename);
	        String filepath = jsonObject.optString("filepath"); 
	        if (filepath != null && filepath.trim().length() > 0)
	        	this.setFilePath(filepath);
	        String filesize = jsonObject.optString("filesize"); 
	        if (filesize != null && filesize.trim().length() > 0)
	        	this.setFileSize(Integer.valueOf(filesize));
	        String fileurl = jsonObject.optString("fileurl"); 
	        if (fileurl != null && fileurl.trim().length() > 0)
	        	this.setFileUrl(fileurl);
	        String content = jsonObject.optString("content"); 
	        if (content != null && content.trim().length() > 0) 
	        	this.setContent(content);		        
	        String timecreated = jsonObject.optString("timecreated");  
	        if (timecreated != null && timecreated.trim().length() > 0)
	        	this.setTimeCreated(Long.valueOf(timecreated));
	        String timemodified = jsonObject.optString("timemodified");  
	        if (timemodified != null && timemodified.trim().length() > 0)
	        	this.setTimeModified(Long.valueOf(timemodified));		        
	        String sortorder = jsonObject.optString("sortorder");  
	        if (sortorder != null && sortorder.trim().length() > 0)
	        	this.setSortOrder(Integer.valueOf(sortorder));
	        String userid = jsonObject.optString("userid");  
	        if (userid != null && userid.trim().length() > 0)
	        	this.setUserId(Integer.valueOf(userid));
	        String author = jsonObject.optString("author");  
	        if (author != null && author.trim().length() > 0)
	        	this.setAuthor(author);
	        String license = jsonObject.optString("license");  
	        if (license != null && license.trim().length() > 0)
	        	this.setLicense(license);	
	        String transition = jsonObject.optString("Transition");  
	        if (transition != null && transition.trim().length() > 0)
	        	this.setTransition(transition);
	        String mode = jsonObject.optString("Game");  
	        if (mode != null && mode.trim().length() > 0)
	        	this.setMode(mode);
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
    public static final Parcelable.Creator<Content> CREATOR = new Parcelable.Creator<Content>() { 
        public Content createFromParcel(Parcel in) { 
            return new Content(in); 
        } 
 
        public Content[] newArray(int size) { 
            return new Content[size]; 
        } 
    }; 
 
    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    // write your object's data to the passed-in Parcel 
    public void writeToParcel(Parcel dest, int flags) { 
    	dest.writeString(type); 
    	dest.writeString(filename);
    	dest.writeString(filepath);
    	dest.writeInt(filesize);
    	dest.writeString(fileurl);
    	dest.writeString(content);
    	dest.writeLong(timecreated);
    	dest.writeLong(timemodified);
    	dest.writeInt(sortorder);
    	dest.writeInt(userid);
    	dest.writeString(author);
    	dest.writeString(license);
    	dest.writeString(transition);
    	dest.writeString(mode);
    }
    
    /**
     * Instantiates a new content.
     *
     * @param in the in
     */
    private Content(Parcel in) { 
        this.type = in.readString(); 
        this.filename = in.readString();
        this.filepath = in.readString();
        this.filesize = in.readInt();
        this.fileurl = in.readString();
        this.content = in.readString();
        this.timecreated = in.readLong();
        this.timemodified = in.readLong();
        this.sortorder = in.readInt();
        this.userid = in.readInt();
        this.author = in.readString();
        this.license = in.readString();
        this.transition = in.readString();
        this.mode = in.readString();
    } 
}