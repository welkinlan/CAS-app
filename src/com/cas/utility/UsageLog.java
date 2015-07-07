/*
 * The utility class for saving course/exercise logs into SD card.
 */
package com.cas.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class UsageLog.
 */
public class UsageLog {
	
	/** The file name. */
	final String fileName;

	/**
	 * Instantiates a new usage log.
	 *
	 * @param fileName the file name
	 */
	public UsageLog(String fileName) {
		createSDCardDir();
		this.fileName = sanitizePath(fileName);
	}

	/**
	 * Sanitize path.
	 *
	 * @param fileName the file name
	 * @return the string
	 */
	private String sanitizePath(String fileName) {
		File sdcardDir = Environment.getExternalStorageDirectory();
		String path1 = sdcardDir.getPath() + "/CAS_Log/";
		String p = path1+fileName;
		return p;
	}

	/**
	 * Creates the sd card directory.
	 */
	public void createSDCardDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/CAS_Log";
			File path1 = new File(path);
			if (!path1.exists()) {
				path1.mkdirs();
			}
			Log.v("create sub", "create !@@@@!!");
		} else {
			return;
		}
	}
	
	/**
	 * Save session data.
	 *
	 * @param user_id the user_id
	 * @param start_time the start_time
	 * @param end_time the end_time
	 */
	public void writeSession(int user_id, String start_time, String end_time)
	{       
	   String text = user_id+" "+start_time+" "+end_time;
	   File sessionFile = new File(fileName);
	   try
	   {
		  sessionFile.createNewFile();
	      //BufferedWriter for performance, true to set append to file flag
	      BufferedWriter buf = new BufferedWriter(new FileWriter(sessionFile, true)); 
	      buf.append(text);
	      buf.newLine();
	      buf.close();
	   }
	   catch (IOException e)
	   {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	   }
	}
	
	/**
	 * Save exercise data.
	 *
	 * @param user_id the user_id
	 * @param exercise_id the exercise_id
	 * @param start_time the start_time
	 * @param end_time the end_time
	 */
	public void writeExercise(int user_id, int exercise_id, String start_time, String end_time) {
		String text = user_id+" "+exercise_id+" "+start_time+" "+end_time;
		File exerciseFile = new File(fileName);
		try
		{
			exerciseFile.createNewFile();
			//BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(exerciseFile, true)); 
			buf.append(text);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}


}
