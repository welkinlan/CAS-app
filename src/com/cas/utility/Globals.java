/*
 * The global class for accessing application-level variables 
 */
package com.cas.utility;

import com.cas.model.Content;
import com.cas.model.Course;
import com.cas.model.Module;
import com.cas.model.User;
import android.app.Application;

// TODO: Auto-generated Javadoc
/**
 * The Class Globals.
 */
public class Globals extends Application
{
	
	/** The user. */
	public User user = null;
	
	/** The username. */
	public String username = null;
	
	/** The password. */
	public String password = null;
	
	/** The user_id. */
	public int user_id = 0; 
	
	/** The mode. */
	public String mode = null;
	
	/** The current_course. */
	public Course current_course = null;
	
	/** The current_module. */
	public Module current_module = null;
	
	/** The current_content. */
	public Content current_content = null;
	
	/** The is downloading. */
	public boolean isDownloading = false;
	
	/** The is uploading. */
	public boolean isUploading = false;
	
	/** The session start time. */
	public String sessionStartTime = null;
	
}
