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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

// TODO: Auto-generated Javadoc
/**
 * The Class Module.
 */
public class Module implements Parcelable {
	//private static Module instance;

	/**
	 * Instantiates a new module.
	 */
	public Module() {

	}

//	public static Module getInstance() {
//		if (instance == null) {
//			synchronized(Module.class) {
//				if (instance == null) {
//					instance = new Module();
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
        
    /** The name. */
    private String name;	
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
       this.name = name;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
       return name;
    }
    
    /** The description. */
    private String description;	
	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
       this.description = description;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
       return description;
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
        
	/** The modicon. */
	private String modicon;	
	
	/**
	 * Sets the mod icon.
	 *
	 * @param modicon the new mod icon
	 */
	public void setModIcon(String modicon) {
	   this.modicon = modicon;
	}
	
	/**
	 * Gets the mod icon.
	 *
	 * @return the mod icon
	 */
	public String getModIcon() {
	   return modicon;
	}
    
    /** The modname. */
    private String modname;	
	
	/**
	 * Sets the mod name.
	 *
	 * @param modname the new mod name
	 */
	public void setModName(String modname) {
       this.modname = modname;
    }

    /**
     * Gets the mod name.
     *
     * @return the mod name
     */
    public String getModName() {
       return modname;
    }
    
    /** The modplural. */
    private String modplural;	
	
	/**
	 * Sets the mod plural.
	 *
	 * @param modplural the new mod plural
	 */
	public void setModPlural(String modplural) {
       this.modplural = modplural;
    }

    /**
     * Gets the mod plural.
     *
     * @return the mod plural
     */
    public String getModPlural() {
       return modplural;
    }
    
    /** The availablefrom. */
    private int availablefrom;	
	
	/**
	 * Sets the available from.
	 *
	 * @param availablefrom the new available from
	 */
	public void setAvailableFrom(int availablefrom) {
       this.availablefrom = availablefrom;
    }

    /**
     * Gets the available from.
     *
     * @return the available from
     */
    public int getAvailableFrom() {
       return availablefrom;
    }
    
    /** The availableuntil. */
    private int availableuntil;	
	
	/**
	 * Sets the available until.
	 *
	 * @param availableuntil the new available until
	 */
	public void setAvailableUntil(int availableuntil) {
       this.availableuntil = availableuntil;
    }

    /**
     * Gets the available until.
     *
     * @return the available until
     */
    public int getAvailableUntil() {
       return availableuntil;
    }
    
    /** The indent. */
    private int indent;	
	
	/**
	 * Sets the indent.
	 *
	 * @param indent the new indent
	 */
	public void setIndent(int indent) {
       this.indent = indent;
    }

    /**
     * Gets the indent.
     *
     * @return the indent
     */
    public int getIndent() {
       return indent;
    }
    
    /** The contents. */
    private ArrayList<Content> contents = new ArrayList<Content>();	
	
	/**
	 * Sets the contents.
	 *
	 * @param contents the new contents
	 */
	public void setContents(ArrayList<Content> contents) {
       this.contents = contents;
    }

    /**
     * Gets the contents.
     *
     * @return the contents
     */
    public ArrayList<Content> getContents() {
       return contents;
    }

    /**
     * Populate module.
     *
     * @param jsonObject the json object
     */
    public void populateModule(JSONObject jsonObject) {
    
    	    	 
    	try {  
    		if (jsonObject != null) {
    			
	    		String id = jsonObject.getString("id"); 
	    		this.setId(Integer.valueOf(id));
		        String url = jsonObject.optString("url"); 
		        if (url != null && url.trim().length() > 0)
		        	this.setUrl(url);
		        String name = jsonObject.optString("name");  
		        if (name != null && name.trim().length() > 0)
		        	this.setName(name);
		        String description = jsonObject.optString("description"); 
		        if (description != null && description.trim().length() > 0) 
		        	this.setDescription(description);
		        String visible = jsonObject.optString("visible");  
		        if (visible != null && visible.trim().length() > 0)
		        	this.setVisible(Integer.valueOf(visible));
		        String modicon = jsonObject.optString("modicon");  
		        if (modicon != null && modicon.trim().length() > 0)
		        	this.setModIcon(modicon);
		        String modname = jsonObject.optString("modname"); 
		        if (modname != null && modname.trim().length() > 0)
		        	this.setModName(modname);
		        String modplural = jsonObject.optString("modplural");  
		        if (modplural != null && modplural.trim().length() > 0)
		        	this.setModPlural(modplural);	
		        String availablefrom = jsonObject.optString("availablefrom");  
		        if (availablefrom != null && availablefrom.trim().length() > 0)
		        	this.setAvailableFrom(Integer.valueOf(availablefrom));
		        String availableuntil = jsonObject.optString("availableuntil");  
		        if (availableuntil != null && availableuntil.trim().length() > 0)
		        	this.setAvailableUntil(Integer.valueOf(availableuntil));
		        String indent = jsonObject.optString("indent");  
		        if (indent != null && indent.trim().length() > 0)
		        	this.setIndent(Integer.valueOf(indent));
		        
		        JSONArray contents = jsonObject.getJSONArray("contents");
		        ArrayList<Content> contentsArray = new ArrayList<Content>();
	    	    // looping through all Contents 
	    	    for(int i = 0; i < contents.length(); i++){ 
	    	        JSONObject c = contents.getJSONObject(i); 
	    	        Content content = new Content();
	    	        content.populateContent(c);
	    	        //Toast.makeText(context.getApplicationContext(), course.getShortName(), Toast.LENGTH_LONG).show();
	    	        // Storing each json item in variable 
	    	        contentsArray.add(content);
	    	    } 	
	    	    
	    	    if (contentsArray.size() > 0) {
	    	    	this.setContents(contentsArray);
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
    public static final Parcelable.Creator<Module> CREATOR = new Parcelable.Creator<Module>() { 
        public Module createFromParcel(Parcel in) { 
            return new Module(in); 
        } 
 
        public Module[] newArray(int size) { 
            return new Module[size]; 
        } 
    }; 
 
    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    // write your object's data to the passed-in Parcel 
    public void writeToParcel(Parcel dest, int flags) { 
    	dest.writeInt(id); 
    	dest.writeString(url);
    	dest.writeString(name);
    	dest.writeString(description);
    	dest.writeInt(visible);
    	dest.writeString(modicon);
    	dest.writeString(modname);
    	dest.writeString(modplural);
    	dest.writeInt(availablefrom);
    	dest.writeInt(availableuntil);
    	dest.writeInt(indent);
    	dest.writeTypedList(contents); 
    }
    
    /**
     * Instantiates a new module.
     *
     * @param in the in
     */
    private Module(Parcel in) { 
        this.id = in.readInt(); 
        this.url = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.visible = in.readInt();
        this.modicon = in.readString();
        this.modname = in.readString();
        this.modplural = in.readString();
        this.availablefrom = in.readInt();
        this.availableuntil = in.readInt();
        this.indent = in.readInt();
        in.readTypedList(this.contents, Content.CREATOR); 
    } 
}