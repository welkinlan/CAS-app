/*
 * The moodle web service
 */
package com.cas.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cas.model.Course;
import com.cas.model.CourseContent;
import com.cas.model.SiteInfo;
import com.cas.utility.Globals;
import com.example.cas.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class MoodleWebService.
 */
public class MoodleWebService {
	
	/** The context. */
	private Context context;
	
	/** The User info. */
	String UserInfo;
	
	/** The saved. */
	SharedPreferences saved;
	
	/** The mode private. */
	static int MODE_PRIVATE = 0;
	
	/**
	 * Instantiates a new moodle web service.
	 *
	 * @param context the context
	 */
	public MoodleWebService (Context context) {
		this.context = context;
		Globals app = ((Globals)context.getApplicationContext());
		UserInfo = app.username;
	}
	
	/**
	 * Gets the web service response.
	 *
	 * @param serverurl the serverurl
	 * @param functionName the function name
	 * @param urlParameters the url parameters
	 * @param xslRawId the xsl raw id
	 * @return the web service response
	 */
	private JSONObject getWebServiceResponse(String serverurl, String functionName, String urlParameters, int xslRawId) {
		JSONObject jsonobj = null;
		
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(serverurl + functionName).openConnection();		
			//HttpURLConnection con = (HttpURLConnection) new URL(serverurl + functionName + "&moodlewsrestformat=json").openConnection();
		
			con.setConnectTimeout(30000); 
			con.setReadTimeout(30000); 

			con.setRequestMethod("POST");
	        con.setRequestProperty("Content-Type", 
	           "application/x-www-form-urlencoded");
	        con.setRequestProperty("Content-Language", "en-US");
	        con.setDoOutput(true);
	        con.setUseCaches (false);
	        con.setDoInput(true);
	        DataOutputStream wr = new DataOutputStream (con.getOutputStream ());
	        
	        Log.d("URLParameters: ", urlParameters.toString());
			wr.writeBytes (urlParameters);
	        wr.flush ();
	        wr.close ();
	        
	        //Get Response
	        InputStream is =con.getInputStream();  
	        
            Source xmlSource = new StreamSource(is); 
            Source xsltSource = new StreamSource(context.getResources().openRawResource(xslRawId)); 
 
            TransformerFactory transFact = TransformerFactory.newInstance(); 
            Transformer trans = transFact.newTransformer(xsltSource); 
            StringWriter writer = new StringWriter(); 	            
            trans.transform(xmlSource, new StreamResult(writer));             
            	
        	String jsonstr = writer.toString();
        	jsonstr = jsonstr.replace("<div class=\"no-overflow\"><p>", "");
        	jsonstr = jsonstr.replace("</p></div>", "");
        	jsonstr = jsonstr.replace("<p>", "");
        	jsonstr = jsonstr.replace("</p>", "");
        	Log.d("TransformObject: ", jsonstr);
        	jsonobj = new JSONObject(jsonstr);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();				
        } catch (TransformerConfigurationException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } catch (TransformerFactoryConfigurationError e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } catch (TransformerException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonobj;
	}

	/**
	 * Gets the siteinfo.
	 *
	 * @param serverurl the serverurl
	 * @param siteInfo the site info
	 * @return the siteinfo
	 */
	public void getSiteinfo(String serverurl, SiteInfo siteInfo) {
		String urlParameters = ""; // moodle_webservice_get_siteinfo parameters	//core_webservice_get_site_info		
		JSONObject jsonobj = getWebServiceResponse(serverurl, "moodle_webservice_get_siteinfo", urlParameters, R.raw.siteinfoxsl);
		siteInfo.populateSiteInfo(jsonobj);
	}
	
	
	/**
	 * Gets the user courses.
	 *
	 * @param serverurl the serverurl
	 * @param userId the user id
	 * @param coursesArray the courses array
	 * @return the user courses
	 */
	public void getUserCourses(String serverurl, int userId, ArrayList<Course> coursesArray) {
		
		String user = String.valueOf(userId);
		String urlParameters = "";
		
		try {
			urlParameters = "userid=" + URLEncoder.encode(user, "UTF-8");
			
			//online
			JSONObject jsonobj = getWebServiceResponse(serverurl, "moodle_enrol_get_users_courses", urlParameters, R.raw.coursesxsl);
			
			//offline
			saved = context.getSharedPreferences(UserInfo, MODE_PRIVATE);		
			SharedPreferences.Editor e = saved.edit();
			e.putString("user_courses", jsonobj.toString());
			e.commit();
			//Log.i("user_courses_saved", saved.getString("user_courses", null));
			
			
			JSONArray courses = jsonobj.getJSONArray("courses");
    	    // looping through All Contacts 
    	    for(int i = 0; i < courses.length(); i++){ 
    	        JSONObject c = courses.getJSONObject(i); 
    	        Course course = new Course();
    	        course.populateCourse(c);
    	        //Toast.makeText(context.getApplicationContext(), course.getShortName(), Toast.LENGTH_LONG).show();
    	        // Storing each json item in variable 
    	        coursesArray.add(course);
    	    } 			

		} catch (JSONException e) {
			// TODO Auto-generated catch block
    	    e.printStackTrace(); 
		} catch (UnsupportedEncodingException e1) { // moodle_enrol_get_users_courses parameters
			// TODO Auto-generated catch block
			e1.printStackTrace();		 
		} 
	}
	
	/**
	 * Gets the course contents.
	 *
	 * @param serverurl the serverurl
	 * @param courseid the courseid
	 * @param courseContentsArray the course contents array
	 * @return the course contents
	 */
	public void getCourseContents(String serverurl, int courseid, ArrayList<CourseContent> courseContentsArray) {
		
		String course = String.valueOf(courseid);
		String urlParameters = "";
		
		try {
			urlParameters = "courseid=" + URLEncoder.encode(course, "UTF-8");
						
			//core_course_get_contents
			JSONObject jsonobj = getWebServiceResponse(serverurl, "core_course_get_contents", urlParameters, R.raw.contentxsl);

			if(jsonobj == null){
				return;
			}
			//offline
			saved = context.getSharedPreferences(UserInfo, MODE_PRIVATE);		
			SharedPreferences.Editor e = saved.edit();
			e.putString("course_contents"+courseid, jsonobj.toString());
			e.commit();
			//Log.i("course_contents"+courseid, saved.getString("course_contents"+courseid, null));
			
			
			JSONArray coursecontents = jsonobj.getJSONArray("coursecontents");
    	    // looping through All Course Content 
    	    for(int i = 0; i < coursecontents.length(); i++){ 
    	        JSONObject c = coursecontents.getJSONObject(i); 
    	        CourseContent coursecontent = new CourseContent();
    	        coursecontent.populateCourseContent(c);
    	        //Toast.makeText(context.getApplicationContext(), coursecontent.getName(), Toast.LENGTH_LONG).show();
    	        // Storing each json item in variable 
    	        courseContentsArray.add(coursecontent);
    	    } 			

		} catch (JSONException e) {
			// TODO Auto-generated catch block
    	    e.printStackTrace(); 
		} catch (UnsupportedEncodingException e1) { // moodle_enrol_get_users_courses parameters
			// TODO Auto-generated catch block
			e1.printStackTrace();		 
		} 
	}
	
	//public void sendUploadFiles(String serverurl, File uploadFile) {
	//	String urlParameters = ""; // moodle_webservice_get_siteinfo parameters	//core_webservice_get_site_info		
	//	JSONObject jsonobj = getWebServiceResponse(serverurl, "moodle_files_upload", urlParameters, R.raw.uploadinfoxml);
		//siteInfo.populateSiteInfo(jsonobj);
	//}
}