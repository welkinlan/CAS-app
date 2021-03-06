/*
 * The activity for the memory game
 */
package com.cas.activity;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cas.fragment.courseListActivity;
import com.cas.model.Content;
import com.cas.model.Course;
import com.cas.model.Module;
import com.cas.model.User;
import com.cas.utility.AudioRecorder;
import com.cas.utility.CardItem;
import com.cas.utility.Globals;
import com.cas.utility.StringHelper;
import com.cas.utility.TurnAnimation;
import com.cas.utility.UsageLog;
import com.example.cas.R;

// TODO: Auto-generated Javadoc
/**
 * The Class GameActivity.
 */
public class GameActivity extends Activity {
	
	/** The Constant NUM. */
	private static final int NUM = 5;
	
	/** The global application variable. */
	Globals app;
	
	/** The user. */
	User user;
	
	/** The course. */
	Course course;
	
	/** The current module. */
	Module module;
	
	/** The actual_images. */
	ArrayList<Content> actual_images = new ArrayList<Content>(NUM);
	
	/** The images in random order. */
	ArrayList<Content> random_images;
	
	/** The image names. */
	private static String[] image_names = new String[NUM];
	
	/** The string format helper. */
	StringHelper sh = new StringHelper();
	
	/** The current prompt name. */
	String prompt_name= "";
	
	/** The pending_pos. */
	int pending_pos = -1;
	
	/** The current_position. */
	int current_position = 0;
	
	/** The start time of the current exercise. */
	String start_time;
	
	/** The end time of the current exercise. */
	String end_time;

	/** The grid view for the game panel. */
	private GridView gv;
	
	/** The tabby textview. */
	private TextView tabbyTV;
	
	/** The silver medal view2. */
	private ImageView tabbyImage, goldMedalView1, goldMedalView2, silverMedalView1, silverMedalView2;
	
	/** The silver gv. */
	private static GridView goldGV, silverGV;
	
	/** The silver medal gv. */
	private StarGVAdapter goldAdapter, silverAdapter, goldMedalGV, silverMedalGV;

	/** The card items. */
	private CardItem card1, card2;
	
	/** The completed images. */
	private ArrayList<Integer> completedIds = new ArrayList<Integer>();
	
	/** The number of attempts. */
	private int attempts = -1;
	
	/**  The count of correct matches. */
	private int count = -1;
	
	/** The golden star number. */
	private int golden_star_num = 0;
	
	/** The silver star number. */
	private int silver_star_num = 0;
	
	/** if golden/silver is given. */
	private boolean isSilverGiven = false, isGoldenGiven = false;
	
	/** The image resources for the star grid view. */
	private Integer bgImage = R.drawable.question;
	
	/** The silver medal. */
	private static Integer goldenStar = R.drawable.golden_star, silverStar = R.drawable.silver_star, defaultStar = R.drawable.default_star,
			               nextSilverStar = R.drawable.next_silver_star, nextGoldenStar = R.drawable.next_golden_star, 
			               goldMedal = R.drawable.gold_medal, silverMedal = R.drawable.silver_medal;
	
	/** The time spent on the recording. */
	private Long spentTime = null;
	
	/** The reward sentences. */
	private static String[] reward_names = { "Awesome!", "Excellent!", "Good!",
			"Great work!", "Nice job!", "Super!" };
	
	/** The encouraging sentences. */
	private static String[] encourage_names = { "So close!", "Try again :)", "Oooops!"};
	
	/** The reward_file_names. */
	private static String[] reward_file_names = { "Awesome", "Excellent", "Good",
		"greatwork", "nicejob", "Super" };
	
	
	/** The lock. */
	private static Object lock = new Object();
	
	/** The animations. */
	private Animation alphaAnimation, scaleAnimation, flashAnimation;
	
	/** The animation set. */
	private AnimationSet set;
	
	/** The current view. */
	private View currentView;

	/** The buttons. */
	private Button b_record, b_stop, b_play;
	
	/** The image resources for buttons. */
	private Integer recordEn = R.drawable.recordv, recordUn = R.drawable.recordh, stopEn = R.drawable.stopv, 
			stopUn = R.drawable.stoph, playEn = R.drawable.playv, playUn = R.drawable.playh;
	
	/** The state of the recording. */
	private final int FREE = 0, READY_FOR_RECORD = 1, RECORDING = 2,
			RECORD_END = 3, PLAYING = 4, PLAY_END = 5;
	
	/** The current state of the recording. */
	private int state = FREE;
	
	/** The filename for the image. */
	private String filename;

	/** The recorder. */
	private AudioRecorder recorder;
	
	/** The media player for the recording. */
	private MediaPlayer mediaPlayer;
	
	/** The media player for the prompt. */
	private MediaPlayer promptPlayer;
	
	/** The record timer. */
	Timer recordTimer;
	
	/** The recording seconds. */
	int recordingSeconds = 0;
	
	/** The toast. */
	Toast toast;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = ((Globals) getApplicationContext());
		user = (User) getIntent().getParcelableExtra("userObject");
		course = (Course) getIntent().getParcelableExtra("courseObject");
		module = (Module) getIntent().getParcelableExtra("moduleObject");
		setContentView(R.layout.activity_game);
		//Log.i("MODULE ID", module.getId()+"");
		// Show the Up button in the action bar.
		setupActionBar();
		loadCards();
		setView();
		startExerciseTime();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:		
			if(state==RECORDING){
				b_stop.performClick();
			}
			if(recordTimer!=null){
				recordTimer.cancel();
			}
			if(toast!=null&&toast.getView().getWindowToken()!=null){
				toast.cancel();
			}
			saveExerciseTime();
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Initialize the views in the scene.
	 */
	private void setView() {
		// TODO Auto-generated method stub
		
		goldMedalView1 = (ImageView) findViewById(R.id.goldMedalView1);
		goldMedalView2 = (ImageView) findViewById(R.id.goldMedalView2);
		silverMedalView1 = (ImageView) findViewById(R.id.silverMedalView1);
		silverMedalView2 = (ImageView) findViewById(R.id.silverMedalView2);
		goldMedalView1.setImageAlpha(30);
		goldMedalView2.setImageAlpha(30);
		silverMedalView1.setImageAlpha(30);
		silverMedalView2.setImageAlpha(30);
		
		gv = (GridView) findViewById(R.id.game_view);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gv.setAdapter(new ImageAdapter(this));
		gv.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		gv.setOnItemClickListener(gridListener);
		
		tabbyTV = (TextView) findViewById(R.id.tabbyText);
		tabbyImage = (ImageView) findViewById(R.id.tabbyImage);
		//tabbyGif = (GifView) findViewById(R.id.tabbyGif);

		b_record = (Button) findViewById(R.id.button_record);
		b_stop = (Button) findViewById(R.id.button_stop);
		b_play = (Button) findViewById(R.id.button_play);
		b_record.setOnClickListener(b_recordListener);
		b_stop.setOnClickListener(b_stopListener);
		b_play.setOnClickListener(b_playListener);
		setAnimation();
		
		
		goldGV = (GridView) findViewById(R.id.goldGV);
		silverGV = (GridView) findViewById(R.id.silverGV);
		
		/*
		if(app.mode.equals("alone")){
			goldGV.setBackgroundColor(Color.GRAY);
			silverGV.setBackgroundColor(Color.GRAY);
			goldGV.setClickable(false);
			silverGV.setClickable(false);
			return;
		}
		*/
		
		goldGV.setSelector(new ColorDrawable(Color.TRANSPARENT));
		silverGV.setSelector(new ColorDrawable(Color.TRANSPARENT));

		goldAdapter = new StarGVAdapter(this);
		silverAdapter = new StarGVAdapter(this);

		goldGV.setAdapter(goldAdapter);
		silverGV.setAdapter(silverAdapter);

		goldGV.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		goldGV.setOnItemClickListener(ggvListener);
		silverGV.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		silverGV.setOnItemClickListener(sgvListener);

		/*
		 * ImageView thisStar = (ImageView)goldGV.getChildAt(golden_star_num);
		 * thisStar.setImageResource(nextGoldenStar); ImageView thatStar =
		 * (ImageView)silverGV.getChildAt(silver_star_num);
		 * thatStar.setImageResource(nextSilverStar);
		 */
		// cardHandler = new CardsHandler();
	}

	/**
	 * Load cards in random order.
	 */
	private void loadCards() {
		// TODO Auto-generated method stub
		card1 = null;
		card2 = null;
		count = attempts = 0;

		ArrayList<Content> tmp_module_images = module.getContents();
		int num_images = tmp_module_images.size() - 1;
		
		// remove the last index.html
		tmp_module_images.remove(num_images);
		//check exist
		ArrayList<Content> module_images = new ArrayList<Content>();
		for (int i = 0; i < num_images; i++) {
			if(isExistImage(tmp_module_images.get(i).getFileName())){
				module_images.add(tmp_module_images.get(i));
			}
		}
		num_images = module_images.size();
		Log.i("image number", num_images + "");
		
		random_images = new ArrayList<Content>(10);

		// the 5 images
		if (num_images > 5) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < num_images; i++) {
				list.add(i);
			}
			Random r = new Random();
			for (int i = num_images; i > num_images - 5; i--) {
				int t = 0;
				if (i > 0) {
					t = r.nextInt(i);
				}
				t = list.remove(t).intValue();
				actual_images.add(module_images.get(t));
			}
		} else {
			Random r = new Random();
			actual_images.addAll(0, module_images);
			// randomly add duplicate images
			for (int i = 0; i < 5 - num_images; i++) {
				int t = r.nextInt(num_images);
				actual_images.add(module_images.get(t));
			}
			Log.i("actual_image_size", actual_images.size() + "");
		}

		// initialize the random images and names
		for (int i = 0; i < 10; i++) {
			random_images.add(null);
		}
		image_names = new String[10];

		try {
			ArrayList<Integer> l = new ArrayList<Integer>();
			Random r = new Random();
			for (int i = 0; i < 10; i++) {
				l.add(i);
			}
			for (int i = 10; i > 0; i--) {
				int t = 0;
				if (i > 0) {
					t = r.nextInt(i);
				}
				t = l.remove(t).intValue();
				Log.i("random number 0-" + num_images + "", "" + t % 5);
				random_images.set(i - 1, actual_images.get(t % 5));
				image_names[i - 1] = actual_images.get(t % 5).getFileName();
			}
		} catch (Exception e) {
			Log.e("load()", e + "");
		}

	}
	
	/**
	 * Checks if the image is existed.
	 *
	 * @param name the name
	 * @return true, if the image exists
	 */
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

	/**
	 * Flip a image.
	 *
	 * @param id the id of the image to be flipped
	 * @param view the view of the image to be flipped
	 */
	private void turn(int id, View view) {
		if (card1 == null) {
			card1 = new CardItem(id, view);
			// setDialog();
		}
		else if (card2 == null) {
			if (card1.id == id) {
				return;
			}
			card2 = new CardItem(id, view);
		}
		current_position = id;
		prompt_name = image_names[id];
		currentView = view;
		turnAround();
		//applyTurnRotation(id, 0, 180);
		b_record.setClickable(true);
		b_stop.setClickable(false);
		b_play.setClickable(false);
		b_record.setBackgroundResource(recordEn);
		b_stop.setBackgroundResource(stopUn);
		b_play.setBackgroundResource(playUn);
		state = READY_FOR_RECORD;
		isGoldenGiven = false;
		isSilverGiven = false;
	}

	/**
	 * Show the image content.
	 */
	private void turnAround() {
		// TODO Auto-generated method stub
		ImageView image = (ImageView) currentView.findViewById(R.id.icon);
		TextView text = (TextView) currentView.findViewById(R.id.text);
		text.setVisibility(View.VISIBLE);

		InputStream in = null;
		String name = image_names[current_position];
		try {
			in = getAssets().open("img/" + name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final Bitmap bitmap = BitmapFactory.decodeStream(in);
		// Display image and text
		image.setImageBitmap(bitmap);
		text.setText(image_names[current_position].split("\\.")[0].toString().toLowerCase());
	}

	/**
	 * Check two images to see if they are the same.
	 */
	public void checkCards() {
		// Log.i("card1", "" + cards_uid[card1.id]);
		// Log.i("card2", "" + cards_uid[card2.id]);
		if (image_names[card2.id].equals(image_names[card1.id])) {
			ImageView checkImage1 = (ImageView) card1.view
					.findViewById(R.id.match);
			ImageView checkImage2 = (ImageView) card2.view
					.findViewById(R.id.match);
			checkImage1.setVisibility(View.VISIBLE);
			checkImage2.setVisibility(View.VISIBLE);
			checkImage1.startAnimation(set);
			checkImage2.startAnimation(set);
			// card1.view.startAnimation(set);
			// card2.view.startAnimation(set);
			completedIds.add(card1.id);
			completedIds.add(card2.id);
			Log.i("attempts:", "" + attempts);
			Log.i("spentTime:", "" + spentTime);
			count++;
			if (count == 5) {
				toast = Toast.makeText(getApplicationContext(), "Congratulations, You win! Go back and choose another course", Toast.LENGTH_LONG);
				LinearLayout toastLayout = (LinearLayout) toast.getView();
				TextView toastTV = (TextView) toastLayout.getChildAt(0);
				toastTV.setTextSize(30);
				toast.show();
				//update the complete counts into this module
				SharedPreferences saved = getSharedPreferences(app.username, MODE_PRIVATE);
				int mod_completed_times = saved.getInt("mod"+module.getId()+"completed times",0);
				SharedPreferences.Editor e = saved.edit();
				e.putInt("mod"+module.getId()+"completed times", mod_completed_times+1);
				e.commit();
			}
			final MediaPlayer rewardPlayer = new MediaPlayer();
			try {
				String text = "";
				String rewardfile = "";
				if (count == 5) {
					text = "applause";
					rewardfile = "applause";
				} else {
					Random r = new Random();
					int t = r.nextInt(6);
					text = reward_names[t];
					rewardfile = reward_file_names[t];
				}
				tabbyTV.setText("Tabby says:" + "\n" + text + "!");
				AssetFileDescriptor descriptor = getAssets().openFd(
						"audio/" + rewardfile + ".mp3");
				rewardPlayer.setDataSource(descriptor.getFileDescriptor(),
						descriptor.getStartOffset(), descriptor.getLength());
				descriptor.close();
				rewardPlayer.prepare();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			rewardPlayer.start();
			rewardPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					rewardPlayer.release();
				}
			});
		} else {
			String text = "";
			Random r = new Random();
			int t = r.nextInt(3);
			text = encourage_names[t];
			tabbyTV.setText("Tabby says:" + "\n" + text + "!");
			ImageView image1 = (ImageView) card1.view.findViewById(R.id.icon);
			TextView text1 = (TextView) card1.view.findViewById(R.id.text);
			image1.setImageResource(bgImage);
			text1.setVisibility(View.GONE);
			ImageView image2 = (ImageView) card2.view.findViewById(R.id.icon);
			TextView text2 = (TextView) card2.view.findViewById(R.id.text);
			image2.setImageResource(bgImage);
			text2.setVisibility(View.GONE);
		}
		card1 = null;
		card2 = null;
		state = RECORD_END;
		b_record.setBackgroundResource(recordUn);
		b_stop.setBackgroundResource(stopUn);
		b_play.setBackgroundResource(playEn);
		b_record.setClickable(false);
		b_stop.setClickable(false);
		b_play.setClickable(true);
	}

	/**
	 * Initialize the animations.
	 */
	public void setAnimation() {
		flashAnimation = new AlphaAnimation(1, 0);
	    flashAnimation.setDuration(400);
	    flashAnimation.setInterpolator(new LinearInterpolator());
	    flashAnimation.setRepeatCount(Animation.INFINITE);
	    flashAnimation.setRepeatMode(Animation.REVERSE); 
		
		scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		alphaAnimation = new AlphaAnimation(0.1f, 11f);
		set = new AnimationSet(true);
		set.addAnimation(scaleAnimation);
		set.addAnimation(alphaAnimation);
		set.setDuration(300);
		//set.setFillEnabled(true);
		//set.setFillAfter(true);
	}

	
	/**
	 * The Class ImageAdapter for the game panel.
	 */
	static class ImageAdapter extends BaseAdapter {
		
		/** The m context. */
		private Context mContext;

		/**
		 * Instantiates a new image adapter.
		 *
		 * @param context the context
		 */
		public ImageAdapter(Context context) {
			this.mContext = context;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return image_names.length;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return image_names[position];
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.text_image_layout, null);
			}
			return convertView;
		}
	}

	/**
	 * The Class StarGVAdapter for the golden/silver star grid view.
	 */
	static class StarGVAdapter extends BaseAdapter {
		
		/** The m context. */
		private Context mContext;

		/**
		 * Instantiates a new star gv adapter.
		 *
		 * @param context the context
		 */
		public StarGVAdapter(Context context) {
			this.mContext = context;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return 20;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return position;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
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

	/** The recording grid listener. */
	// EVENT LISTENERS
	OnItemClickListener gridListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//Log.i("state",state+"");
			tabbyTV.setText("");
			if (completedIds.contains(position)) {
				playPrompt(position);
				return;
			}
			if (state == RECORDING || state == PLAYING) {
				return;
			}
			if (state == READY_FOR_RECORD) {
				if(card1!= null && position==card1.id){
					playPrompt(position);
				}	
				else if(card2!= null  && position==card2.id){
					playPrompt(position);
				}	
				else{
					if(toast!=null&&toast.getView().getWindowToken()!=null){
						toast.cancel();
					}
					else{
						toast = Toast.makeText(getApplicationContext(), "Please record your voice to move on", Toast.LENGTH_SHORT);
						LinearLayout toastLayout = (LinearLayout) toast.getView();
						TextView toastTV = (TextView) toastLayout.getChildAt(0);
						toastTV.setTextSize(30);
						toast.show();
					}
				}
				return;
			}
			if (state == FREE || state == RECORD_END || state == PLAY_END) {
				if(card1!= null && position==card1.id){
					playPrompt(position);
				}	
				else if(card2!= null && position==card2.id){
					playPrompt(position);
				}
				else{
					synchronized (lock) {
						pending_pos = position;
						turn(position, view);
						//b_record.startAnimation(flashAnimation);
					}
				}
				return;
			}
		}
	};

	/** The golden star grid view listener. */
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

	/** The silver grid view listener. */
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

	/** The record button listener. */
	android.view.View.OnClickListener b_recordListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (state) {
			case READY_FOR_RECORD:
				if(toast!=null&&toast.getView().getWindowToken()!=null){
					toast.cancel();
				}
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
				state = RECORDING;
				String promptname = prompt_name.split("\\.")[0].toString()
						.toLowerCase();
				filename = sh.fileNameHelper(app.username, promptname);
				recorder = new AudioRecorder(filename);
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
				
				b_stop.startAnimation(flashAnimation);
				break;
			default:
				break;
			}
		}
	};

	/** The stop button listener. */
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
				saveUploadUrl();
				b_record.setBackgroundResource(recordUn);
				b_stop.setBackgroundResource(stopUn);
				b_play.setBackgroundResource(playEn);
				// b_record.setClickable(true);
				b_record.setClickable(false);
				b_stop.setClickable(false);
				b_play.setClickable(true);

				if (card2 != null) {
					checkCards();
				}
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
				b_record.setBackgroundResource(recordUn);
				b_stop.setBackgroundResource(stopUn);
				b_play.setBackgroundResource(playEn);
				b_record.setClickable(false);
				b_stop.setClickable(false);
				b_play.setClickable(true);
				break;
			}
		}
	};

	/** The play button listener. */
	android.view.View.OnClickListener b_playListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (state) {
			case RECORD_END:
			case PLAY_END:
			case FREE:
				if(promptPlayer != null && promptPlayer.isPlaying()){
					promptPlayer.pause();
					promptPlayer.stop();
			    }
				state = PLAYING;
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
				break;
			default:
				break;
			}
		}
	};

	/** The completion listener for the recording. */
	OnCompletionListener compListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			state = PLAY_END;
			b_record.setBackgroundResource(recordUn);
			b_stop.setBackgroundResource(stopUn);
			b_play.setBackgroundResource(playEn);
			b_record.setClickable(false);
			b_stop.setClickable(false);
			b_play.setClickable(true);
		}

	};
	
	/**
	 * Save upload URL for a recording.
	 */
	protected void saveUploadUrl() {
		// TODO Auto-generated method stub
		Log.i("current_position",current_position+"");
		//Log.i("url for 0",random_images.get(1).getFileUrl());
		Content current_content = random_images.get(current_position);
		String file_url = current_content.getFileUrl();
		SharedPreferences saved = getSharedPreferences(app.username,
				MODE_PRIVATE);
		SharedPreferences.Editor e = saved.edit();
		e.putString(filename, file_url);
		e.putString(filename+"completed", "1");
		e.putString(filename+"mode", app.mode);
		e.commit();
		Log.i("Game upload url", saved.getString(filename, ""));
	}

	/**
	 * Play prompt.
	 *
	 * @param position the position
	 */
	protected void playPrompt(int position) {
		// TODO Auto-generated method stub
		String iname = image_names[position].split("\\.")[0].toString();
		String aname = iname.replace("-", " ").toLowerCase();
		Log.i("audio_name", "prompts/" + aname + ".mp3");
		// FileInputStream in = null;
		try {
			if(promptPlayer != null && promptPlayer.isPlaying()){
				//promptPlayer.pause();
				//promptPlayer.stop();
				return;
		    }
			promptPlayer = new MediaPlayer();
			AssetFileDescriptor descriptor = getAssets().openFd(
					"prompts/" + aname + ".mp3");
			promptPlayer.setDataSource(descriptor.getFileDescriptor(),
							descriptor.getStartOffset(),
							descriptor.getLength());
			descriptor.close();
			promptPlayer.prepare();
			promptPlayer.start();
			return;
			// promptPlayer.setOnCompletionListener(pcompListener);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	if(state==RECORDING){
				b_stop.performClick();
			}
			if(recordTimer!=null){
				recordTimer.cancel();
			}
			if(toast!=null&&toast.getView().getWindowToken()!=null){
				toast.cancel();
			}
			saveExerciseTime();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Save start exercise time .
	 */
	private void startExerciseTime() {
		start_time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
	}
	
	/**
	 * Save end exercise time, write to the log.
	 */
	private void saveExerciseTime() {
		UsageLog exerciseLog = new UsageLog(app.username+"_"+"exercise.txt");
		String end_time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
		exerciseLog.writeExercise(app.user_id, module.getId(), start_time, end_time);
		Log.i("usage save", "game saving exercise time");
	}
	
}