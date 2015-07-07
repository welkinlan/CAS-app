/*
 * The helper class for string format
 */
package com.cas.utility;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.text.format.Time;

// TODO: Auto-generated Javadoc
/**
 * The Class StringHelper.
 */
public class StringHelper{
	
	/**
	 * To lower case.
	 *
	 * @param s the s
	 */
	public void toLowerCase(String s){
		
	}
	
	/**
	 * File name helper.
	 *
	 * @param username the username
	 * @param prompt the prompt
	 * @return the string representing the recording file name
	 */
	public String fileNameHelper(String username, String prompt){
		/*
		Time t = new Time();
		t.setToNow();
		String date="";
		date += (t.month<10)?("0"+t.month+"_"):(t.month+"_");
		date += (t.monthDay<10)?("0"+t.monthDay+"_"):(t.monthDay+"_");
		date += (t.hour<10)?("0"+t.hour+"_"):(t.hour+"_");
		date += (t.minute<10)?("0"+t.minute+"_"):(t.minute+"_");
		date += (t.second<10)?("0"+t.second+"_"):(t.second+"_");
		date += t.timezone.replace('/', '_');
		*/
		//DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String date = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
		return username+"_"+prompt+"_"+date+".3gp";
	}
	
	/**
	 * Upload url helper.
	 *
	 * @param file_url the URL on the server to be uploaded
	 * @return the string[] contents
	 */
	public String[] uploadUrlHelper(String file_url){
		String[] result = file_url.split("/");
		String[] contents = new String[4];
		contents[0] = result[6];
		contents[1] = result[7];
		contents[2] = result[8];
		contents[3] = result[10].split("\\?")[0];
		return contents;
		//return ServerUrl.audio_upload_url+"?contextid="+ result[6] +"&component="+
		//		result[7]+"&filearea="+result[8]+"&filename="+file_name;
	}
}
