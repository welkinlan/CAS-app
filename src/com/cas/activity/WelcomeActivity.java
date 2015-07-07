/*
 * The welcome activity
 */
package com.cas.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.cas.fragment.courseListActivity;
import com.cas.model.Course;
import com.cas.model.CourseContent;
import com.cas.model.SiteInfo;
import com.cas.model.User;
import com.cas.server.MoodleWebService;
import com.cas.server.ServerUrl;
import com.cas.server.TokenHttpRequest;
import com.cas.utility.AppStatus;
import com.cas.utility.Globals;
import com.cas.utility.SyncHelper;
import com.example.cas.R;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class WelcomeActivity.
 */
public class WelcomeActivity extends Activity {

	/** The shared preferences saved on the device. */
	static SharedPreferences saved;
	
	/** The saved username/password. */
	SharedPreferences saved_ui;
	
	/** The sync helper. */
	SyncHelper syncHelper;
	
	/** The global application variable. */
	Globals app;
	
	/** The User info. */
	static String UserInfo;
	
	/** The Mode. */
	static String Mode;
	
	/** The progress dialog. */
	ProgressDialog progressDialog;
	
	/** The login button. */
	Button loginButton;
	
	/** The user name editor. */
	EditText nameEdit;
	
	/** The password editor. */
	EditText pwEdit;
	
	/** The radio buttons. */
	RadioButton homeR, cliR, aloneR;
	
	/** The user. */
	User user;
	
	/** The string variables. */
	String username, password, usrUri, pwdUri;
	
	/** The service connection. */
	private ServiceConnection sc;
	
	/** The TAG for debugging. */
	private final static String TAG = "SERVICE_TEST";
	
	/** Whether the sync service has been started. */
	boolean isBound = false;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//startActivity(new Intent(this, WifiWatchdogService.class));
		app = ((Globals)getApplicationContext());
		setupLoginView();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override  
	protected void onRestart() {  
        super.onRestart(); 
        //stopService(new Intent(this, WifiWatchdogService.class));
        //unbindService(sc);
        //isBound=false;
    }  
    

	/**
	 * Setup login views.
	 */
	private void setupLoginView()
	{
		setContentView(R.layout.activity_welcome);	
		
		saved_ui = getSharedPreferences("user_info", MODE_PRIVATE);
		String saved_username = saved_ui.getString("usr", "");
		String saved_password = saved_ui.getString("pwd", "");
		
		loginButton = (Button) findViewById(R.id.loginButton);
		nameEdit = (EditText) findViewById(R.id.name_edit);
		pwEdit = (EditText) findViewById(R.id.passwd_edit);
		nameEdit.setHint("your name here");
		pwEdit.setHint("your password");
		nameEdit.setText(saved_username);
		pwEdit.setText(saved_password);
		homeR = (RadioButton) findViewById(R.id.homeRadio);
		cliR = (RadioButton) findViewById(R.id.clinicRadio);
		aloneR = (RadioButton) findViewById(R.id.aloneRadio);
		
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub		
				getFromLocal();
			}
		});
	}
	
	/**
	 * Gets the saved information from the device.
	 */
	protected void getFromLocal() {
		// TODO Auto-generated method stub
		username = nameEdit.getText().toString().trim();
		password = pwEdit.getText().toString().trim();
		usrUri = Uri.encode(username);
		pwdUri = Uri.encode(password);
		UserInfo = username;
		app.username = username;
		app.password = password;
		saved = getSharedPreferences(UserInfo, MODE_PRIVATE);
					
		SharedPreferences.Editor se = saved_ui.edit();
		//e.putString("siteUrlVal", site);
		se.putString("usr", username);
		se.putString("pwd", password);
		se.commit();
		
		user = new User();
        user.setUsername(username);
        user.setPassword(password);
        String token = saved.getString("user_token", "null");
        int user_id = saved.getInt("user_id", 0);
        app.user_id = user_id;
        user.setToken(token);
        user.setTokenCreateDate();
        syncHelper = new SyncHelper();
        
        try {
	        ArrayList<Course> courses = new ArrayList<Course>();
	        String s_courses = saved.getString("user_courses",null);
	        if(s_courses != null){
	        	JSONObject jo_courses = new JSONObject(s_courses);
	        	//Log.i("jo_courses", jo_courses.toString());
		        syncHelper.getUserCourses(jo_courses, courses);
		       		        	
		        if (courses.size() > 0) {		        	
		        	for(int i = 0; i < courses.size(); i++) { 
		        		
		    	        Course c = courses.get(i); 
		    	        ArrayList<CourseContent> coursecontents = new ArrayList<CourseContent>();
		    	        String s_cc = saved.getString("course_contents"+c.getId(), "null");
		    	        JSONObject jo_cc = new JSONObject(s_cc);
		    	        syncHelper.getCourseContents(jo_cc, coursecontents);
		    	        
		    	        if (coursecontents.size() > 0) {
		    	        	c.setCourseContent(coursecontents);
		    	        }
		    	    } 
		        	user.setCourses(courses);
		        	setupService();
					nextPage();
		        }
		        else{
		        	firstDownload();
		        }
	        }
	        else{
	        	String conType = AppStatus
						.getInstance(this)
						.getConnectionType(this);
				conType = conType == null ? "Unknown" : conType;
				if(conType.equals("Unknown")){
		        	Toast.makeText(
		        			getApplicationContext(), 
		        			"No courses found,  connect to the wifi to download courses.",
		        			Toast.LENGTH_LONG).show();
		        	return;
				}
				else{
					firstDownload();
				}
	        }
	        
        }catch (JSONException e) {
			// TODO Auto-generated catch block
    	    e.printStackTrace(); 
		} 
	}


	/** The handler for handling login progress. */
	@SuppressLint("HandlerLeak")
	private final Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			progressDialog.dismiss();
			if(msg.what==1){
				setupService();
				nextPage();
			}
			else if(msg.what==2){
				Toast.makeText(getApplicationContext(), "Username and password do not match, please try again",
	        			Toast.LENGTH_LONG).show();
				return;
			}
			else if(msg.what==0){
				Toast.makeText(getApplicationContext(), "This user is not enrolled in any course.",
	        			Toast.LENGTH_LONG).show();
				
				return;
			}
		}
	};

	/**
	 * First time download information from the server.
	 */
	protected void firstDownload() {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(this, "", "First time fetching information...", true);			
		new Thread(new Runnable()
		{			
			@Override
			public void run()
			{		
				Looper.prepare();
				//Login into server
				String url = ServerUrl.server_url + "/login/token.php?username=" + usrUri + "&password=" + pwdUri + 
							"&service=moodle_mobile_app";	
				TokenHttpRequest tokenRequest = new TokenHttpRequest();
				
				//store token for offline
				String token = tokenRequest.doHTTPRequest(url);
				saved = getSharedPreferences(UserInfo, MODE_PRIVATE);		
				SharedPreferences.Editor e = saved.edit();
				e.putString("user_token", token);
				e.commit();
				//Login Success
				if (token != null && token != "") {
		        	String serverurl = ServerUrl.server_url + "/webservice/rest/server.php" + 
		        						"?wstoken=" + token + "&wsfunction=";
			        
			        user = new User();
			        user.setUsername(username);
			        user.setPassword(password);
			        user.setToken(token);
			        user.setTokenCreateDate();
			        user.setUrl(url);
			        
			        MoodleWebService webService = new MoodleWebService(WelcomeActivity.this);
			        SiteInfo siteInfo = new SiteInfo();
			        webService.getSiteinfo(serverurl, siteInfo);
					//save user_id and session num
			        e.putInt("user_id", siteInfo.getUserid());
			        app.user_id = siteInfo.getUserid();
			        e.putInt("session_num", 0);
					e.commit();
			        user.setSiteInfo(siteInfo);
			        ArrayList<Course> courses = new ArrayList<Course>();
			        webService.getUserCourses(serverurl, siteInfo.getUserid(), courses);
			       		        	
			        if (courses.size() > 0) {		        	
			        	for(int i = 0; i < courses.size(); i++) { 
			    	        Course c = courses.get(i); 
			    	        ArrayList<CourseContent> coursecontents = new ArrayList<CourseContent>();
			    	        webService.getCourseContents(serverurl, c.getId(), coursecontents);
			    	        
			    	        if (coursecontents.size() > 0) {
			    	        	c.setCourseContent(coursecontents);
			    	        }
			    	    } 
			        	user.setCourses(courses);
			        	progressHandler.sendEmptyMessage(1);
			        }
			        
			        else {  
			        	progressHandler.sendEmptyMessage(0);
			        	Log.e("Course Error", "This user is not enrolled in any courses");
			        }
		        		
		        } 
				//Login failure
		        else {
		        	progressHandler.sendEmptyMessage(2);
		        	Log.e("Login failure", "Error authenticating...Check username or password.");
		        }	
			}
		}).start();
	}

	
	/**
	 * Setup background download/upload service.
	 */
	private void setupService() {
		// TODO Auto-generated method stub
		
		if(!isBound){
			sc = new ServiceConnection() {           
	            @Override
	            public void onServiceConnected(ComponentName name, IBinder service) {
	                WifiWatchdogService.SynBinder sBinder = (WifiWatchdogService.SynBinder)service;
	                Log.i("onServiceConnected",username);
	                sBinder.setContext(WelcomeActivity.this);
	            }

				@Override
				public void onServiceDisconnected(ComponentName arg0) {
					// TODO Auto-generated method stub
					sc = null;
					Log.v(TAG, "Service disconnected");
				}
	        };
	        bindService(new Intent(this, WifiWatchdogService.class), sc, Context.BIND_AUTO_CREATE);
	        isBound=true;
		}
		
	}
	
	/**
	 * Go to the course selection screen.
	 */
	private void nextPage() {
		// TODO Auto-generated method stub
		if(homeR.isChecked()){
			app.mode = "home";
		}
		else if(cliR.isChecked()){
			app.mode = "clinic";
		}
		else{
			app.mode = "alone";
		}
		app.user = user;
		Intent nextPage;
    	nextPage = new Intent(WelcomeActivity.this, courseListActivity.class);
		//nextPage.putExtra("userObject", user); 
    	saveSessionStartTime();
		startActivity(nextPage);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Dialog dialog = new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.btn_default)
					.setTitle("Do you want to quit?")
					.setPositiveButton("Quit",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									/*
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
									System.exit(0);
									*/
									finish();  
							    	System.exit(0); 
								}

							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).create();
			dialog.show();
		}
		return false;
	}
	
	/**
	 * Save session start time.
	 */
	private void saveSessionStartTime() {
		// TODO Auto-generated method stub
		app.sessionStartTime = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
	}

}