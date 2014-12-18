package com.cas.utility;

import com.cas.model.Content;
import com.cas.model.Course;
import com.cas.model.Module;
import com.cas.model.User;
import android.app.Application;

public class Globals extends Application
{
	public User user = null;
	public String username = null;
	public String password = null;
	public int user_id = 0; 
	public String mode = null;
	public Course current_course = null;
	public Module current_module = null;
	public Content current_content = null;
	public boolean isDownloading = false;
	public boolean isUploading = false;
	
	public String sessionStartTime = null;
	
}
