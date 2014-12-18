package com.cas.activity;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.cas.model.Content;
import com.cas.model.Course;
import com.cas.model.Module;
import com.cas.model.User;
import com.cas.utility.AudioRecorder;
import com.cas.utility.CoverFlow;
import com.cas.utility.Globals;
import com.cas.utility.ReflectingImageAdapter;
import com.cas.utility.ResourceImageAdapter;
import com.cas.utility.StringHelper;
import com.cas.utility.UsageLog;
import com.example.cas.R;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ViewActivity extends Activity {
	Globals app;
	User user;
	Course course;
	Module module;
	ArrayList<Content> module_images;
	ArrayList<Content> random_images;
	Animation mAnimation;

	String prompt_name = "";
	int current_position = 0;
	View current_view = null;
	//int temp_position = 0;
	int isChange=0;
	ArrayList<Integer> completed_list;
	int uncompleted_count = -1;

	private String filename;
	StringHelper sh = new StringHelper();

	String start_time;
	String end_time;

	private int golden_star_num = 0;
	private int silver_star_num = 0;
	private boolean isSilverGiven = false, isGoldenGiven = false;
	private static Integer goldenStar = R.drawable.golden_star;
	private static Integer silverStar = R.drawable.silver_star;
	private static Integer defaultStar = R.drawable.default_star;
	private static Integer nextSilverStar = R.drawable.next_silver_star;
	private static Integer nextGoldenStar = R.drawable.next_golden_star;
	private Integer recordEn = R.drawable.recordv;
	private Integer recordUn = R.drawable.recordh;
	private Integer stopEn = R.drawable.stopv;
	private Integer stopUn = R.drawable.stoph;
	private Integer playEn = R.drawable.playv;
	private Integer playUn = R.drawable.playh;
	private final int FREE = 0, READY_FOR_RECORD = 1, RECORDING = 2,
			RECORD_END = 3, PLAYING = 4, PLAY_END = 5;
	private int state = FREE;
	private String[] image_names;

	private TextView stimuliText;
	private ImageView doneSign, goldMedalView1, goldMedalView2, silverMedalView1, silverMedalView2;
	private Button b_record, b_stop, b_play;
	private static GridView goldGV, silverGV;
	private StarGVAdapter goldAdapter, silverAdapter;
	private BaseAdapter coverImageAdapter;
	private AudioRecorder recorder;
	private MediaPlayer mediaPlayer;
	private MediaPlayer promptPlayer;
	CoverFlow reflectingCoverFlow;

	private Animation alphaAnimation;
	private Animation scaleAnimation;
	private AnimationSet set;

	Timer recordTimer;
	int recordingSeconds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = ((Globals) getApplicationContext());
		user = (User) getIntent().getParcelableExtra("userObject");
		course = (Course) getIntent().getParcelableExtra("courseObject");
		module = (Module) getIntent().getParcelableExtra("moduleObject");
		setContentView(R.layout.activity_view);
		// Show the Up button in the action bar.
		setupActionBar();
		load();
		setViews();
		startExerciseTime();
	}

	private void load() {
		// TODO Auto-generated method stub
		module_images = module.getContents();
		int num_images = module_images.size() - 1;
		Log.i("image number", num_images + "");
		// remove the last index.html
		module_images.remove(num_images);
		ArrayList<String> image_names_al = new ArrayList<String>();
		for (int i = 0; i < num_images; i++) {
			if(isExistImage(module_images.get(i).getFileName())){
				image_names_al.add(module_images.get(i).getFileName());
			}
		}
		num_images = image_names_al.size();

		//check if it is transition
		if(isTransition()){
			image_names = image_names_al.toArray(new String[num_images]);
		}
		else{
			//random images

			random_images = new ArrayList<Content>(num_images);
			for (int i = 0; i < num_images; i++) {
				random_images.add(null);
			}

			image_names = new String[num_images];

			// According to image numbers
			try {
				ArrayList<Integer> list = new ArrayList<Integer>(num_images);
				for (int i = 0; i < num_images; i++) {
					list.add(i);
				}
				Random r = new Random();
				for (int i = num_images; i > 0; i--) {
					int t = 0;
					if (i > 1) {
						t = r.nextInt(i);
					}
					t = list.remove(t).intValue();
					Log.i("random number 0-" + (num_images - 1), "" + t
							% num_images);
					random_images.set(i - 1, module_images.get(t % num_images));
					image_names[i - 1] = image_names_al.get(t % num_images);
				}
			} catch (Exception e) {
				Log.e("load() problem", e + "");
			}
			module_images = random_images;
		}

		//Set up completeness recorder
		completed_list = new ArrayList<Integer>(num_images);
		uncompleted_count = num_images;
	}

	private boolean isTransition() {
		// TODO Auto-generated method stub
		if(module_images.get(0).getTransition()==null || module_images.get(0).getTransition().equals("0")){
			return false;
		}
		return true;
	}

	private boolean isExistImage(String name) {
		// TODO Auto-generated method stub
		InputStream in = null;
		try {
			in = getApplicationContext().getAssets().open("img/"+name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void setViews() {
		// TODO Auto-generated method stub
		stimuliText = (TextView) findViewById(R.id.stimuliText);
		prompt_name = image_names[0].split("\\.")[0].toString().toLowerCase();
		stimuliText.setText(prompt_name);

		goldMedalView1 = (ImageView) findViewById(R.id.goldMedalView1);
		goldMedalView2 = (ImageView) findViewById(R.id.goldMedalView2);
		silverMedalView1 = (ImageView) findViewById(R.id.silverMedalView1);
		silverMedalView2 = (ImageView) findViewById(R.id.silverMedalView2);
		goldMedalView1.setImageAlpha(30);
		goldMedalView2.setImageAlpha(30);
		silverMedalView1.setImageAlpha(30);
		silverMedalView2.setImageAlpha(30);

		goldGV = (GridView) findViewById(R.id.goldGV);
		silverGV = (GridView) findViewById(R.id.silverGV);
		goldGV.setSelector(new ColorDrawable(Color.TRANSPARENT));
		silverGV.setSelector(new ColorDrawable(Color.TRANSPARENT));
		goldGV.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		goldGV.setOnItemClickListener(ggvListener);
		silverGV.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		silverGV.setOnItemClickListener(sgvListener);
		goldAdapter = new StarGVAdapter(this);
		silverAdapter = new StarGVAdapter(this);
		goldGV.setAdapter(goldAdapter);
		silverGV.setAdapter(silverAdapter);

		doneSign = (ImageView) findViewById(R.id.swipe_done);

		reflectingCoverFlow = (CoverFlow) findViewById(R.id.coverflowReflect);
		setupCoverFlow(true);

		b_record = (Button) findViewById(R.id.button_record);
		b_stop = (Button) findViewById(R.id.button_stop);
		b_play = (Button) findViewById(R.id.button_play);
		b_record.setOnClickListener(b_recordListener);
		b_stop.setOnClickListener(b_stopListener);
		b_play.setOnClickListener(b_playListener);

		// stimuliText.setText(imageNames[0]);
		b_record.setClickable(true);
		b_stop.setClickable(false);
		b_play.setClickable(false);
		b_record.setBackgroundResource(recordEn);
		b_stop.setBackgroundResource(stopUn);
		b_play.setBackgroundResource(playUn);
		state = READY_FOR_RECORD;
		isGoldenGiven = false;
		isSilverGiven = false;
		setAnimation();
	}

	// ================FOR THE COVERFLOW=================//
	private void setupCoverFlow(final boolean reflect) {
		if (reflect) {
			coverImageAdapter = new ReflectingImageAdapter(
					new ResourceImageAdapter(this, image_names));
		} else {
			coverImageAdapter = new ResourceImageAdapter(this, image_names);
		}
		reflectingCoverFlow.setAdapter(coverImageAdapter);
		reflectingCoverFlow.setSelection(0, true);
		setupListeners();
	}

	private void setupListeners() {
		reflectingCoverFlow.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				// if(parent.getChildAt(position).isSelected()){
				// Log.i("View","onItemClick called");
				//current_position = position;
				// Make the audio name from image name
				//				if(state==RECORDING ||state==PLAYING ){
				//					reflectingCoverFlow.
				//					current_position = position;
				//					return;
				//				}
				if(position != current_position){
					doneSign.setVisibility(View.INVISIBLE);
				}

				String iname = image_names[position].split("\\.")[0].toString();
				String aname = iname.replace("-", " ").toLowerCase();
				Log.i("audio_name", "prompts/" + aname + ".wav");
				// FileInputStream in = null;
				try {
					if(promptPlayer != null && promptPlayer.isPlaying()){
						//promptPlayer.pause();
						//promptPlayer.stop();
						return;
					}
					promptPlayer = new MediaPlayer();
					AssetFileDescriptor descriptor = getAssets().openFd(
							"prompts/" + aname + ".wav");
					promptPlayer.setDataSource(descriptor.getFileDescriptor(),
							descriptor.getStartOffset(), descriptor.getLength());
					descriptor.close();
					promptPlayer.prepare();
					promptPlayer.start();

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// }
		});

		reflectingCoverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				//				if(state==RECORDING ||state==PLAYING ){
				//					current_position = position;
				//					return;
				//				}
				doneSign.setVisibility(View.INVISIBLE);
				if(completed_list.contains(position)){
					doneSign.setVisibility(View.VISIBLE);
				}

				current_position = position;
				current_view = view;
				//Log.i("image_names["+current_position+"]",image_names[position]);
				prompt_name = image_names[position].split("\\.")[0].toString().toLowerCase();
				Log.i("prompt_name",prompt_name);
				stimuliText.setText(prompt_name);

				if(isChange==1){
					b_record.setClickable(true);
					b_stop.setClickable(false);
					b_play.setClickable(true);
					b_record.setBackgroundResource(recordEn);
					b_stop.setBackgroundResource(stopUn);
					b_play.setBackgroundResource(playEn);
					state = RECORD_END;
				}
				else{
					b_record.setClickable(true);
					b_stop.setClickable(false);
					b_play.setClickable(false);
					b_record.setBackgroundResource(recordEn);
					b_stop.setBackgroundResource(stopUn);
					b_play.setBackgroundResource(playUn);
					state = READY_FOR_RECORD;
				}
				isGoldenGiven = false;
				isSilverGiven = false;
				// Log.i("View","onItemSelected called");

			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
				stimuliText.setText("Nothing clicked!");
			}
		});
		/*
		reflectingCoverFlow.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_MOVE){
					doneSign.setVisibility(View.INVISIBLE);
		            return true;
		        }
		        return false;
			}
		});
		 */
	}

	private void breakListeners() {
		reflectingCoverFlow.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				//new_position = position;
				if(position != current_position){
					doneSign.setVisibility(View.INVISIBLE);
				}

				prompt_name = image_names[position].split("\\.")[0].toString().toLowerCase();
				stimuliText.setText(prompt_name);
				isChange = 1;

			}
		});

		reflectingCoverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				//new_position = position;
				doneSign.setVisibility(View.INVISIBLE);
				if(completed_list.contains(position)){
					doneSign.setVisibility(View.VISIBLE);
				}

				prompt_name = image_names[position].split("\\.")[0].toString().toLowerCase();
				stimuliText.setText(prompt_name);
				isChange = 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view, menu);
		return true;
	}

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
			if(state==RECORDING){
				b_stop.performClick();
			}
			if(recordTimer!=null){
				recordTimer.cancel();
			}
			saveExerciseTime();
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// =======================FOR THE STAR==============================//

	OnItemClickListener ggvListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Log.i("state",state+"");
			if ((state == RECORD_END || state == PLAY_END) && isGoldenGiven == false) {
				int goldMedalNum = golden_star_num / 20;
				int goldStarNumInView = golden_star_num % 20;
				//if silver stars are to be full in view, set silver medal and clear all the stars in view
				if (goldStarNumInView == 19){ 
					switch (goldMedalNum) {
					//put the first/second medal and clear all the silver stars
					case 0: 
					case 1:
						//for medal
						isGoldenGiven = true;
						if (goldMedalNum==0)
							goldMedalView1.setImageAlpha(255);
						else 
							goldMedalView2.setImageAlpha(255);
						//for stars
						ImageView starView = (ImageView) goldGV.getChildAt(0);
						starView.setImageResource(nextGoldenStar);
						for (int i=1; i<20; i++){
							starView = (ImageView) goldGV.getChildAt(i);
							starView.setImageResource(defaultStar);
						}
						++golden_star_num;
						break;
						//if no more stars available, give toast
					case 2:
						Toast toast = Toast.makeText(getApplicationContext(), "No more stars available, further recordings cannot be evaluated", Toast.LENGTH_LONG);
						LinearLayout toastLayout = (LinearLayout) toast.getView();
						TextView toastTV = (TextView) toastLayout.getChildAt(0);
						toastTV.setTextSize(30);
						toast.show();
						break;
					default:
						break;
					}
				}
				else{
					isGoldenGiven = true;
					ImageView thisStar = (ImageView) goldGV
							.getChildAt(goldStarNumInView);
					thisStar.setImageResource(goldenStar);
					ImageView nextStar = (ImageView) goldGV
							.getChildAt(goldStarNumInView+1);
					nextStar.setImageResource(nextGoldenStar);
					golden_star_num ++;
					//check if changed from a silver star
					//make sure sil
					if (isSilverGiven && silver_star_num < 59 && (silver_star_num % 20) != 0) {
						int silverStarNumInView = silver_star_num % 20;
						isSilverGiven = false;
						nextStar = (ImageView) silverGV
								.getChildAt(silverStarNumInView);
						nextStar.setImageResource(defaultStar);
						thisStar = (ImageView) silverGV
								.getChildAt(silverStarNumInView-1);
						thisStar.setImageResource(nextSilverStar);
						silver_star_num --;
					}
				}
				//saved to sp
				SharedPreferences saved = getSharedPreferences(app.username, MODE_PRIVATE);
				SharedPreferences.Editor e = saved.edit();
				e.putString(filename+"star", "gold");
				e.commit();
				Log.v("golden_star_given", filename+": "+ saved.getString(filename+"star", "none"));
			}

		}
	};

	OnItemClickListener sgvListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Log.i("state",state+"");
			// check if it is time to give stars
			if ((state == RECORD_END || state == PLAY_END) && isSilverGiven == false) {
				int silverMedalNum = silver_star_num / 20;
				int silverStarNumInView = silver_star_num % 20;
				//if silver stars are to be full in view, set silver medal and clear all the stars in view
				if (silverStarNumInView == 19){ 
					switch (silverMedalNum) {
					//put the first/second medal and clear all the silver stars
					case 0: 
					case 1:
						//for medal
						isSilverGiven = true;
						if (silverMedalNum==0)
							silverMedalView1.setImageAlpha(255);
						else 
							silverMedalView2.setImageAlpha(255);
						//for stars
						ImageView starView = (ImageView) silverGV.getChildAt(0);
						starView.setImageResource(nextSilverStar);
						for (int i=1; i<20; i++){
							starView = (ImageView) silverGV.getChildAt(i);
							starView.setImageResource(defaultStar);
							starView.refreshDrawableState();
						}
						++silver_star_num;
						break;
						//if no more stars available, give toast
					case 2:
						Toast toast = Toast.makeText(getApplicationContext(), "No more stars available, further recordings cannot be evaluated", Toast.LENGTH_LONG);
						LinearLayout toastLayout = (LinearLayout) toast.getView();
						TextView toastTV = (TextView) toastLayout.getChildAt(0);
						toastTV.setTextSize(30);
						toast.show();
						break;
					default:
						break;
					}
				}
				//add one silver star in view
				else {
					isSilverGiven = true;
					ImageView thisStar = (ImageView) silverGV
							.getChildAt(silverStarNumInView);
					thisStar.setImageResource(silverStar);
					ImageView nextStar = (ImageView) silverGV
							.getChildAt(silverStarNumInView+1);
					nextStar.setImageResource(nextSilverStar);
					silver_star_num ++;
					//check if changed from a golden star
					//do not allow changing from a new gold medal
					if (isGoldenGiven && golden_star_num < 59 && (golden_star_num % 20) != 0) {
						int goldStarNumInView = golden_star_num % 20;
						isGoldenGiven = false;
						nextStar = (ImageView) goldGV
								.getChildAt(goldStarNumInView);
						nextStar.setImageResource(defaultStar);
						thisStar = (ImageView) goldGV
								.getChildAt(goldStarNumInView-1);
						thisStar.setImageResource(nextGoldenStar);
						golden_star_num --;
					}
				}

				//save data to sp
				SharedPreferences saved = getSharedPreferences(app.username, MODE_PRIVATE);
				SharedPreferences.Editor e = saved.edit();
				e.putString(filename+"star", "silver");
				e.commit();
				Log.v("silver_star_given", filename + ": "+saved.getString(filename+"star", "none"));

			}

		}
	};

	android.view.View.OnClickListener b_recordListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (state) {
			case READY_FOR_RECORD:
			case RECORD_END:
			case PLAY_END:
				state = RECORDING;
				isGoldenGiven = false;
				isSilverGiven = false;
				isChange = 0;
				filename = sh.fileNameHelper(app.username, prompt_name);
				// filename = sh.fileNameHelper(app.username, prompt_name);
				Time t = new Time();
				t.setToNow();
				// Stop playing prompt
				if (promptPlayer != null && promptPlayer.isPlaying()) {
					try {
						promptPlayer.pause();
						promptPlayer.stop();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
				}
				recorder = new AudioRecorder(filename);
				// store the info of that file for upload
				try {
					recorder.start();
					recordTimer = new Timer();
					recordingSeconds = 0;
					recordTimer.scheduleAtFixedRate(new TimerTask() {         
						@Override
						public void run() {
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									++recordingSeconds;  
									Log.i("recording seconds", recordingSeconds+"");
									if(recordingSeconds>60){
										b_stop.performClick();
									}
								}
							});
						}
					}, 0, 1000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				b_record.setBackgroundResource(recordUn);
				b_record.setClickable(false);
				b_stop.setBackgroundResource(stopEn);
				b_stop.setClickable(true);
				b_play.setBackgroundResource(playUn);
				b_play.setClickable(false);

				breakListeners();
				//set flash button
				b_stop.startAnimation(mAnimation);

				break;
			default:
				break;
			}
		}
	};

	android.view.View.OnClickListener b_stopListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (state) {
			case RECORDING:
				recordTimer.cancel();
				b_stop.clearAnimation();
				state = RECORD_END;
				try {
					recorder.stop();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				doneSign.setVisibility(View.VISIBLE);
				doneSign.startAnimation(set);

				saveUploadUrl();
				if(!completed_list.contains(current_position)){
					completed_list.add(current_position);
					uncompleted_count -= 1;
					if(uncompleted_count == 0){
						SharedPreferences saved = getSharedPreferences(app.username, MODE_PRIVATE);
						int mod_completed_times = saved.getInt("mod"+module.getId()+"completed times",0);
						SharedPreferences.Editor e = saved.edit();
						e.putInt("mod"+module.getId()+"completed times", mod_completed_times+1);
						e.commit();
					}
				};

				b_record.setBackgroundResource(recordEn);
				b_stop.setBackgroundResource(stopUn);
				b_play.setBackgroundResource(playEn);
				// b_record.setClickable(true);
				b_record.setClickable(true);
				b_stop.setClickable(false);
				b_play.setClickable(true);

				reflectingCoverFlow.setSelection(current_position);
				setupListeners();
				break;

			case PLAYING:
				state = PLAY_END;
				try {
					mediaPlayer.stop();
					mediaPlayer.release();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
				b_record.setBackgroundResource(recordEn);
				b_stop.setBackgroundResource(stopUn);
				b_play.setBackgroundResource(playEn);
				b_record.setClickable(true);
				b_stop.setClickable(false);
				b_play.setClickable(true);

				reflectingCoverFlow.setSelection(current_position);
				setupListeners();
				break;
			}
		}
	};

	android.view.View.OnClickListener b_playListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (state) {
			case RECORD_END:
			case PLAY_END:
			case FREE:
				state = PLAYING;
				isChange = 0;
				b_record.setBackgroundResource(recordUn);
				b_stop.setBackgroundResource(stopEn);
				b_play.setBackgroundResource(playUn);
				b_record.setClickable(false);
				b_stop.setClickable(true);
				b_play.setClickable(false);
				mediaPlayer = new MediaPlayer();
				try {
					mediaPlayer.setDataSource(Environment
							.getExternalStorageDirectory().getPath()
							+ "/CAS_Audio/" + filename);
					mediaPlayer.prepare();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mediaPlayer.start();
				mediaPlayer.setOnCompletionListener(compListener);

				breakListeners();
				break;
			default:
				break;
			}
		}
	};

	OnCompletionListener compListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			state = PLAY_END;
			b_record.setBackgroundResource(recordEn);
			b_stop.setBackgroundResource(stopUn);
			b_play.setBackgroundResource(playEn);
			b_record.setClickable(true);
			b_stop.setClickable(false);
			b_play.setClickable(true);

			reflectingCoverFlow.setSelection(current_position);
			setupListeners();
		}
	};

	static class StarGVAdapter extends BaseAdapter {
		private Context mContext;

		public StarGVAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return 20;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView star;
			if (convertView == null) {
				star = new ImageView(mContext);
				star.setLayoutParams(new GridView.LayoutParams(40, 40));
				star.setScaleType(ImageView.ScaleType.CENTER_CROP);
				star.setPadding(2, 2, 2, 2);
				star.setImageResource(defaultStar);
				if (position == 0) {
					if (parent.equals(goldGV)) {
						star.setImageResource(nextGoldenStar);
					} else {
						star.setImageResource(nextSilverStar);
					}

				}
			} else {
				star = (ImageView) convertView;
			}
			return star;
		}
	}

	protected void saveUploadUrl() {
		// TODO Auto-generated method stub
		Log.i("current_position",current_position+"");
		//Log.i("url for 0",random_images.get(1).getFileUrl());
		Content current_content = module_images.get(current_position);
		String file_url = current_content.getFileUrl();
		SharedPreferences saved = getSharedPreferences(app.username, MODE_PRIVATE);
		SharedPreferences.Editor e = saved.edit();
		e.putString(filename, file_url);
		e.putString(filename+"completed", "1");
		e.putString(filename+"mode", app.mode);
		e.commit();
		Log.i("View upload url", saved.getString(filename, ""));
	}

	public void setAnimation() {
		mAnimation = new AlphaAnimation(1, 0);
		mAnimation.setDuration(400);
		mAnimation.setInterpolator(new LinearInterpolator());
		mAnimation.setRepeatCount(Animation.INFINITE);
		mAnimation.setRepeatMode(Animation.REVERSE); 

		scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		alphaAnimation = new AlphaAnimation(0.1f, 11f);
		set = new AnimationSet(true);
		set.addAnimation(scaleAnimation);
		set.addAnimation(alphaAnimation);
		set.setDuration(200);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(state==RECORDING){
				b_stop.performClick();
			}
			if(recordTimer!=null){
				recordTimer.cancel();
			}
			saveExerciseTime();
		}
		return super.onKeyDown(keyCode, event);
	}


	private void startExerciseTime() {
		start_time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
	}

	private void saveExerciseTime() {
		UsageLog exerciseLog = new UsageLog(app.username+"_"+"exercise.txt");
		String end_time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
		exerciseLog.writeExercise(app.user_id, module.getId(), start_time, end_time);
		Log.i("usage save", "flashcard saving exercise time");
	}

}