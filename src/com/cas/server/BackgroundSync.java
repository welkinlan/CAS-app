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

public class BackgroundSync {

	static File[] files;
	static String DTAG = "DOWNLOAD_SYNC";
	static String UTAG = "UPLOAD_SYNC";
	static HttpURLConnection up_conn = null;
	static String username, password;
	final int MODE_PRIVATE = 0;
	static Context context;
	StringHelper sh = new StringHelper();
	static SharedPreferences saved;
	Globals app;
	public boolean downloadDone = false;
	public boolean uploadDone = false;

	public BackgroundSync(Context c) {
		app = ((Globals)c.getApplicationContext());
		username = app.username;
		password = app.password;
		context = c;
		//saved = context.getSharedPreferences(username, MODE_PRIVATE);
	}

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

	public void upload() {
		//upload session and exercise logs
		Log.i(UTAG, "try upload sessions and exercises");
		File fileDir = new File(Environment.getExternalStorageDirectory()
				+ "/CAS_Log/");
		files = fileDir.listFiles();
		if (files == null) {
			Log.i(UTAG, "no directory");
			return;
		}
		if (files.length == 0) {
			Log.i(UTAG, "no files");
			return;
		}
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
				return;
			}
		}
		
		
		//upload audio files
		Log.i(UTAG, "try upload audios");
		fileDir = new File(Environment.getExternalStorageDirectory()
				+ "/CAS_Audio/");
		files = fileDir.listFiles();
		if (files == null) {
			Log.i(UTAG, "no directory");
			return;
		}
		if (files.length == 0) {
			Log.i(UTAG, "no files");
			return;
		}
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
				return;
			}
		}
		app.isUploading = false;
	}
	// Upload section with HttpUrlConnection
	/*

	public void upload() {
		// start upload
		File fileDir = new File(Environment.getExternalStorageDirectory()
				+ "/CAS_Audio/");
		files = fileDir.listFiles();
		HttpURLConnection up_conn = null;
		try {
			for (File file : files) {
				String upload_url = saved.getString(file.getName(), "");
				up_conn = (HttpURLConnection) new URL(upload_url).openConnection();
				up_conn.setConnectTimeout(30000);
				up_conn.setReadTimeout(30000);
				String end = "\r\n";
				String hyphens = "--";
				String boundary = "*****";
				up_conn.setChunkedStreamingMode(128 * 1024);
				up_conn.setDoInput(true);
				up_conn.setDoOutput(true);
				up_conn.setUseCaches(false);
				up_conn.setRequestMethod("POST");
				up_conn.setRequestProperty("Charset", "UTF-8");
				up_conn.setRequestProperty("up_connection", "Keep-Alive");
				up_conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				Log.i(UTAG + "begin upload", file.getName());
				DataOutputStream ds = new DataOutputStream(
						up_conn.getOutputStream());
				ds.writeBytes(hyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; "
						+ "name=\"file\";filename=\"" + file.getName() + "\""
						+ end);
				ds.writeBytes(end);
				FileInputStream input = new FileInputStream(file);
				int size = 1024;
				byte[] buffer = new byte[size];
				int length = -1;
				while ((length = input.read(buffer)) != -1) {
					ds.write(buffer, 0, length);
				}
				input.close();
				ds.writeBytes(end);
				ds.writeBytes(hyphens + boundary + hyphens + end);
				ds.flush();
				Log.i(UTAG, up_conn.getResponseCode() + "=======");
				Log.i(UTAG + "end upload", file.getName());
				// delete uploaded recordings
				file.delete();
				up_conn.getInputStream().close();
				Log.i(UTAG + "file deleted", file.getName());
			}
			uploadDone = true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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

	public void stop() {
		app.isDownloading = false;
		app.isUploading = false;
	}
	
}