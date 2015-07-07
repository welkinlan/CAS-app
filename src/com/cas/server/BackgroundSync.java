/*
 * The helper class to download course contents from the server and upload recordings and session files to the server
 */
package com.cas.server;
import java.io.File;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import com.cas.model.Course;
import com.cas.model.CourseContent;
import com.cas.model.SiteInfo;
import com.cas.utility.Globals;
import com.cas.utility.StringHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class BackgroundSync.
 */
public class BackgroundSync {

	/** The files. */
	static File[] files;
	
	/** The download tag. */
	static String DTAG = "DOWNLOAD_SYNC";
	
	/** The upload tag. */
	static String UTAG = "UPLOAD_SYNC";
	
	/** The http connection. */
	static HttpURLConnection up_conn = null;
	
	/** The username and the password. */
	static String username, password;
	
	/** The mode for the shared preferences. */
	final int MODE_PRIVATE = 0;
	
	/** The context. */
	static Context context;
	
	/** The string formatter. */
	StringHelper sh = new StringHelper();
	
	/** The saved information in the shared preferences. */
	static SharedPreferences saved;
	
	/** The global applications. */
	Globals app;
	
	/** The download done. */
	public boolean downloadDone = false;
	
	/** The upload done. */
	public boolean uploadDone = false;

	/**
	 * Instantiates a new background sync.
	 *
	 * @param c the c
	 */
	public BackgroundSync(Context c) {
		app = ((Globals)c.getApplicationContext());
		username = app.username;
		password = app.password;
		context = c;
		//saved = context.getSharedPreferences(username, MODE_PRIVATE);
	}

	/**
	 * Download course contents.
	 */
	// Download
	public void download() {
		app.isDownloading=true;
		Log.i(DTAG, "Downloading...");
		String usrUri = Uri.encode(username);
		String pwdUri = Uri.encode(password);
		//get token
		String url = ServerUrl.server_url + "/login/token.php?username="
				+ usrUri + "&password=" + pwdUri + "&service=moodle_mobile_app";
		TokenHttpRequest tokenRequest = new TokenHttpRequest();
		// store token to local
		String token = tokenRequest.doHTTPRequest(url);
		saved = context.getSharedPreferences(username, MODE_PRIVATE);
		SharedPreferences.Editor e = saved.edit();
		e.putString("user_token", token);
		e.commit();
		//if login success
		if (token != null && token != "") {
			//get user id by siteinfo
			String serverurl = ServerUrl.server_url
					+ "/webservice/rest/server.php" + "?wstoken=" + token
					+ "&wsfunction=";

			MoodleWebService webService = new MoodleWebService(context);
			SiteInfo siteInfo = new SiteInfo();
			webService.getSiteinfo(serverurl, siteInfo);
			ArrayList<Course> courses = new ArrayList<Course>();
			webService.getUserCourses(serverurl, siteInfo.getUserid(), courses);

			if (courses.size() > 0) {
				//Log.i("size course", courses.size() + "");
				for (int i = 0; i < courses.size(); i++) {
					Course c = courses.get(i);
					ArrayList<CourseContent> coursecontents = new ArrayList<CourseContent>();
					webService.getCourseContents(serverurl, c.getId(),
							coursecontents);

				}
				app.isDownloading=false;
				Log.i(DTAG, "Download course info successful");
			} else {
				Log.e("Course Error", "User is not enrolled in any courses");
			}

		}
		// Login failure
		else {
			// Toast.makeText(getApplicationContext(),
			// "Username and password do not match, please try again",
			// Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Upload recordings and session files.
	 */
	public void upload() {
		//upload session and exercise logs
		Log.i(UTAG, "try upload sessions and exercises");
		File fileDir = new File(Environment.getExternalStorageDirectory()
				+ "/CAS_Log/");
		files = fileDir.listFiles();
		if (files == null) {
			Log.i(UTAG, "no directory");
		}
		else if (files.length == 0) {
			Log.i(UTAG, "no files");
		}
		else {
			app.isUploading = true;
			for (File file : files) {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(ServerUrl.usage_upload_url);
				MultipartEntity multipartEntity = new MultipartEntity();
				//upload with http
				try {
					multipartEntity.addPart("file", new FileBody(file));
					//set entities
					post.setEntity(multipartEntity);
					//get responses
					HttpResponse resp = client.execute(post);
					file.delete();
					Log.i(UTAG, ServerUrl.usage_upload_url + ": " + resp.getStatusLine() + "");
				} catch (Exception e) {
					Log.e(UTAG, "Error" + e.getMessage());
					e.printStackTrace();
					app.isUploading = false;
				}
			}
		}
		
		
		//upload audio files
		Log.i(UTAG, "try upload audios");
		fileDir = new File(Environment.getExternalStorageDirectory()
				+ "/CAS_Audio/");
		files = fileDir.listFiles();
		if (files == null) {
			Log.i(UTAG, "no directory");
		}
		else if (files.length == 0) {
			Log.i(UTAG, "no files");
		}
		else {
			app.isUploading = true;
			for (File file : files) {
				String file_username = file.getName().split("_")[0];
				// Log.i(UTAG,file_username );
				saved = context.getSharedPreferences(file_username, MODE_PRIVATE);
				String recording_completed = saved.getString(file.getName()+"completed","0");
				if(recording_completed.equals("0")){
					Log.i(UTAG, file_username+" is still recording this file.");
					continue;
				}
				String upload_recording_url = saved.getString(file.getName(),"");
				// Log.i(UTAG,upload_url );
				String[] contents = sh.uploadUrlHelper(upload_recording_url);
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(ServerUrl.audio_upload_url);
				MultipartEntity multipartEntity = new MultipartEntity();
				//upload with http
				try {
					multipartEntity.addPart("contextid",
							new StringBody(contents[0]));
					multipartEntity.addPart("component",
							new StringBody(contents[1]));
					multipartEntity
							.addPart("filearea", new StringBody(contents[2]));
					multipartEntity
							.addPart("filename", new StringBody(contents[3]));
					
					String star = saved.getString(file.getName()+"star","none");
					multipartEntity
				    		.addPart("star", new StringBody(star));
					
					String mode = saved.getString(file.getName()+"mode","none");
					multipartEntity
				    		.addPart("mode", new StringBody(mode));
					multipartEntity.addPart("file", new FileBody(file));

					post.setEntity(multipartEntity);
					HttpResponse resp = client.execute(post);
					Log.i(UTAG, upload_recording_url + ": " + resp.getStatusLine()
							+ "");
					
					moveFile(file.getName());
					// if (resp.getStatusLine().getStatusCode() == 200) {
					// Log.i("File Upload","Success");
					file.delete();
					// }

				} catch (Exception e) {
					Log.e(UTAG, "Error" + e.getMessage());
					e.printStackTrace();
					app.isUploading = false;
				}
			}
			app.isUploading = false;
		}
	}

	/**
	 * Move the uploaded files to another folder.
	 *
	 * @param filename the filename
	 */
	private void moveFile(String filename) {
		// TODO Auto-generated method stub
		// your sd card
		String sdCard = Environment.getExternalStorageDirectory().toString();
		// the file to be moved
		File sourceLocation = new File(sdCard + "/CAS_Audio/" + filename);
		File targetLocation = new File(sdCard + "/CAS_Audio_Uploaded/"
				+ filename);
		try {
			if (sourceLocation.renameTo(targetLocation)) {
				Log.v(UTAG, "Move file " +filename+" successful.");
			} else {
				Log.v(UTAG, "Move file " +filename+" failed.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop the synchronization.
	 */
	public void stop() {
		app.isDownloading = false;
		app.isUploading = false;
	}
	
}