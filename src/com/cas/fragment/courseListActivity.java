package com.cas.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import com.cas.activity.GameActivity;
import com.cas.activity.ViewActivity;
import com.cas.model.Course;
import com.cas.model.User;
import com.cas.utility.Globals;
import com.cas.utility.UsageLog;
import com.example.cas.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * An activity representing a list of courses. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link courseDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link courseListFragment} and the item details (if present) is a
 * {@link courseDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link courseListFragment.Callbacks} interface to listen for item selections.
 */
public class courseListActivity extends FragmentActivity implements
		courseListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	boolean mTwoPane;
	Handler mHandler;
	static User user;
	static ArrayList<Course> courses;
	Globals app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_course_list);

		//get User
		app = ((Globals)getApplicationContext());
		user = app.user;
    	courses = user.getCourses();
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//loadData();

		if (findViewById(R.id.course_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((courseListFragment) getSupportFragmentManager().findFragmentById(
					R.id.course_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}
	
	/*
	@SuppressLint("HandlerLeak")
	private void loadData() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {
        	public void handleMessage(Message msg) {
        		switch(msg.what) {
	        		case 0:
	        			break;
        		}
        	}
        };
        new Thread() {
        	public void run() {
        		getDataFromServer();
        	}

			private void getDataFromServer() {
				// TODO Auto-generated method stub
				TimerTask t_task = new TimerTask() {
					@Override
					public void run() {
						mHandler.sendEmptyMessage(0);
					}
				};
				
				Timer t = new Timer(false);
			    t.schedule(t_task, 1000);
			}
        }.start();
	}
	*/


	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			
			//save session time
			UsageLog sessionLog = new UsageLog(app.username+"_"+"session.txt");
			String end_time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
			sessionLog.writeSession(app.user_id, app.sessionStartTime, end_time);
			Log.i("usage save", "saving session time");
			
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Callback method from {@link courseListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			LinearLayout wait = (LinearLayout) findViewById(R.id.hand_point);
			wait.setVisibility(View.INVISIBLE);
			
			Bundle arguments = new Bundle();
			arguments.putString(courseDetailFragment.ARG_ITEM_ID, id);
			courseDetailFragment fragment = new courseDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.course_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			
			Intent detailIntent = new Intent(this, courseDetailActivity.class);
			detailIntent.putExtra(courseDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	UsageLog sessionLog = new UsageLog(app.username+"_"+"session.txt");
			String end_time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
			sessionLog.writeSession(app.user_id, app.sessionStartTime, end_time);
			Log.i("usage save", "saving session time");
	    }
	    return super.onKeyDown(keyCode, event);
	}

}