package com.cas.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cas.model.Content;
import com.cas.model.Course;
import com.cas.model.Module;
import com.cas.model.User;
import com.cas.utility.AudioRecorder;
import com.cas.utility.CardItem;
import com.cas.utility.Globals;
import com.cas.utility.StringHelper;
import com.cas.utility.TurnAnimation;
import com.example.cas.R;

public class GameActivityWithAnimation extends Activity {

	int test = 10;
	
	private static final int NUM = 5;
	Globals app;
	User user;
	Course course;
	Module module;
	ArrayList<Content> actual_images = new ArrayList<Content>(NUM);
	ArrayList<Content> random_images;
	private static String[] image_names = new String[NUM];
	StringHelper sh = new StringHelper();
	String prompt_name="";
	int pending_pos=-1;
	int current_position = 0;

	private GridView gv;
	private TextView tabbyTV;
	private ImageView tabbyImage;
	private static GridView goldGV, silverGV;
	private StarGVAdapter goldAdapter, silverAdapter;

	// ===============Variables for game==================//
	private CardItem card1;
	private CardItem card2;
	// private CardItem card_temp;
	// private static int numCards = 10;
	private ArrayList<Integer> completedIds = new ArrayList<Integer>();
	private int attempts = -1;
	private int count = -1;
	private int golden_star_num = 0;
	private int silver_star_num = 0;
	private static Integer goldenStar = R.drawable.golden_star;
	private static Integer silverStar = R.drawable.silver_star;
	private static Integer defaultStar = R.drawable.default_star;
	private static Integer nextSilverStar = R.drawable.next_silver_star;
	private static Integer nextGoldenStar = R.drawable.next_golden_star;
	private boolean isSilverGiven = false, isGoldenGiven = false;
	private Long spentTime = null;
	private static String[] reward_names = { "Awesome!", "Excellent!", "Good!",
			"Great work!", "Nice job!", "Super!" };
	private static String[] encourage_names = { "So close!", "Try again :)", "Oooops!"};
	private static String[] reward_file_names = { "Awesome", "Excellent", "Good",
		"greatwork", "nicejob", "Super" };
	
	
	private static Object lock = new Object();
	private Integer bgImage = R.drawable.question;

	// ===============Variables for animation===================//
	private Animation alphaAnimation;
	private Animation scaleAnimation;
	private AnimationSet set;
	private View currentView;

	// ===============Variables for audio recording===================//
	private Button b_record, b_stop, b_play;
	private Integer recordEn = R.drawable.recordv;
	private Integer recordUn = R.drawable.recordh;
	private Integer stopEn = R.drawable.stopv;
	private Integer stopUn = R.drawable.stoph;
	private Integer playEn = R.drawable.playv;
	private Integer playUn = R.drawable.playh;
	private final int FREE = 0, READY_FOR_RECORD = 1, RECORDING = 2,
			RECORD_END = 3, PLAYING = 4, PLAY_END = 5;
	private int state = FREE;
	private String filename;

	private AudioRecorder recorder;
	private MediaPlayer mediaPlayer;
	private MediaPlayer promptPlayer;
	private TurnAnimation tabbyRotation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = ((Globals) getApplicationContext());
		user = (User) getIntent().getParcelableExtra("userObject");
		course = (Course) getIntent().getParcelableExtra("courseObject");
		module = (Module) getIntent().getParcelableExtra("moduleObject");
		setContentView(R.layout.activity_game);
		// Show the Up button in the action bar.
		setupActionBar();
		loadCards();
		setView();
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
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setView() {
		// TODO Auto-generated method stub
		goldGV = (GridView) findViewById(R.id.goldGV);
		silverGV = (GridView) findViewById(R.id.silverGV);
		gv = (GridView) findViewById(R.id.game_view);
		goldGV.setSelector(new ColorDrawable(Color.TRANSPARENT));
		silverGV.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));

		goldAdapter = new StarGVAdapter(this);
		silverAdapter = new StarGVAdapter(this);

		gv.setAdapter(new ImageAdapter(this));
		goldGV.setAdapter(goldAdapter);
		silverGV.setAdapter(silverAdapter);

		gv.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		gv.setOnItemClickListener(gridListener);
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
		tabbyTV = (TextView) findViewById(R.id.tabbyText);
		tabbyImage = (ImageView) findViewById(R.id.tabbyImage);

		b_record = (Button) findViewById(R.id.button_record);
		b_stop = (Button) findViewById(R.id.button_stop);
		b_play = (Button) findViewById(R.id.button_play);
		b_record.setOnClickListener(b_recordListener);
		b_stop.setOnClickListener(b_stopListener);
		b_play.setOnClickListener(b_playListener);
		setAgreeAnimation();
	}

	private void loadCards() {
		// TODO Auto-generated method stub
		card1 = null;
		card2 = null;
		count = attempts = 0;

		ArrayList<Content> module_images = module.getContents();
		int num_images = module_images.size() - 1;
		Log.i("image number", num_images + "");
		// remove the last index.html
		module_images.remove(num_images);
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

	// ===========================FOR THE
	// GAME=================================//

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
		applyTurnRotation(id, 0, 180);
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
			Toast.makeText(getApplicationContext(), "You win!",
					Toast.LENGTH_SHORT).show();
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
				//tabbyImage.setAnimation(tabbyRotation);
				AssetFileDescriptor descriptor = getAssets().openFd(
						"audio/" + rewardfile + ".wav");
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

	// ===========================FOR THE
	// ANIMATION=================================//
	@SuppressLint("NewApi")
	public void setAgreeAnimation() {
		// translateAnimation = new TranslateAnimation(0.1f, 100.0f, 0.1f,
		// 100.0f);
		// rotateAnimation = new RotateAnimation(0.0f, +360.0f);
		scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		alphaAnimation = new AlphaAnimation(0.1f, 11f);
		set = new AnimationSet(true);
		set.addAnimation(scaleAnimation);
		set.addAnimation(alphaAnimation);
		set.setDuration(300);
		set.setFillEnabled(true);
		set.setFillAfter(true);

		final float centerX = tabbyImage.getWidth() / 2.0f;
		final float centerY = tabbyImage.getHeight() / 2.0f;

		tabbyRotation = new TurnAnimation(0, 360, centerX, centerY, 310.0f,
				true);
		tabbyRotation.setDuration(200);
		tabbyRotation.setFillAfter(true);
		tabbyRotation.setInterpolator(new AccelerateInterpolator());

	}

	// For the turn image animation
	private void applyTurnRotation(int position, float start, float end) {
		final float centerX = currentView.getWidth() / 2.0f;
		final float centerY = currentView.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final TurnAnimation rotation = new TurnAnimation(start, end, centerX,
				centerY, 310.0f, true);
		rotation.setDuration(300);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(position));
		currentView.startAnimation(rotation);
	}

	/**
	 * This class listens for the end of the first half of the animation. It
	 * then posts a new action that effectively swaps the views when the
	 * container is rotated 90 degrees and thus invisible.
	 */
	private final class DisplayNextView implements Animation.AnimationListener {
		private final int mPosition;

		private DisplayNextView(int position) {
			mPosition = position;
		}

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			currentView.post(new SwapViews(mPosition));
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	/**
	 * This class is responsible for swapping the views and start the second
	 * half of the animation.
	 */
	@SuppressLint("DefaultLocale")
	private final class SwapViews implements Runnable {
		private final int mPosition;

		public SwapViews(int position) {
			mPosition = position;
		}

		@SuppressLint("DefaultLocale")
		public void run() {
			ImageView image = (ImageView) currentView.findViewById(R.id.icon);
			TextView text = (TextView) currentView.findViewById(R.id.text);
			text.setVisibility(View.VISIBLE);

			InputStream in = null;
			// Log.i("filename", "img/"+image_names[mPosition]);
			String name = image_names[mPosition];
			StringBuilder sb = new StringBuilder(name);
			char c = sb.charAt(0);
			if (Character.isLowerCase(c)) {
				sb.setCharAt(0, Character.toUpperCase(c));
				Log.i("new_name", sb.toString());
			}
			try {
				in = getAssets().open("img/" + sb.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final Bitmap bitmap = BitmapFactory.decodeStream(in);
			// Display image and text
			image.setImageBitmap(bitmap);
			text.setText(image_names[mPosition].split("\\.")[0].toString()
					.toLowerCase());

			final float centerX = currentView.getWidth() / 2.0f;
			final float centerY = currentView.getHeight() / 2.0f;
			TurnAnimation rotation;
			rotation = new TurnAnimation(180, 0, centerX, centerY, 310.0f,
					false);
			rotation.setDuration(300);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());
			currentView.startAnimation(rotation);
		}
	}

	// *=======================FOR THE IMAGE
	// ADAPTER==============================*//
	static class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return image_names.length;
		}

		@Override
		public Object getItem(int position) {
			return image_names[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.text_image_layout, null);
			}
			return convertView;
		}
	}

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

	// =======================EVENT LISTENERS======================//
	OnItemClickListener gridListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//Log.i("state",state+"");
			if (completedIds.contains(position)) {
				playPrompt(position);
				return;
			}
			if (state == RECORDING || state == PLAYING) {
				return;
			}
			if (state == READY_FOR_RECORD) {
				if(position==pending_pos){
					playPrompt(position);
				}	
				return;
			}
			if (state == FREE || state == RECORD_END || state == PLAY_END) {
				if(position==pending_pos && card1!=null){
					playPrompt(position);
				}	
				else{
					synchronized (lock) {
						pending_pos = position;
						turn(position, view);
					}
				}
				return;
			}
		}
	};

	OnItemClickListener ggvListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (state == RECORD_END || state == PLAY_END) {
				Log.v("golden_star_num", golden_star_num + "");
				if (golden_star_num < 20 && isGoldenGiven == false) {
					isGoldenGiven = true;
					ImageView thisStar = (ImageView) goldGV
							.getChildAt(golden_star_num);
					thisStar.setImageResource(goldenStar);
					if (golden_star_num < 19) {
						ImageView nextStar = (ImageView) goldGV
								.getChildAt(++golden_star_num);
						nextStar.setImageResource(nextGoldenStar);
					}
					if (isSilverGiven) {
						Log.v("silver_star_num", silver_star_num + "");
						isSilverGiven = false;
						ImageView oldStar = (ImageView) silverGV
								.getChildAt(silver_star_num);
						oldStar.setImageResource(defaultStar);
						ImageView nextsStar = (ImageView) silverGV
								.getChildAt(--silver_star_num);
						nextsStar.setImageResource(nextSilverStar);
						if (silver_star_num >= 1) {
							ImageView thatStar = (ImageView) silverGV
									.getChildAt(silver_star_num - 1);
							thatStar.setImageResource(silverStar);
						}
					}
				}
			}

		}
	};

	OnItemClickListener sgvListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (state == RECORD_END || state == PLAY_END) {
				if (silver_star_num < 20 && isSilverGiven == false) {
					Log.v("silver_star_num", silver_star_num + "");
					isSilverGiven = true;
					ImageView thisStar = (ImageView) silverGV
							.getChildAt(silver_star_num);
					thisStar.setImageResource(silverStar);
					if (silver_star_num < 19) {
						ImageView nextStar = (ImageView) silverGV
								.getChildAt(++silver_star_num);
						nextStar.setImageResource(nextSilverStar);
					}
					if (isGoldenGiven) {
						Log.v("golden_star_num", golden_star_num + "");
						isGoldenGiven = false;
						ImageView oldStar = (ImageView) goldGV
								.getChildAt(golden_star_num);
						oldStar.setImageResource(defaultStar);
						ImageView nextsStar = (ImageView) goldGV
								.getChildAt(--golden_star_num);
						nextsStar.setImageResource(nextGoldenStar);
						if (golden_star_num >= 1) {
							ImageView thatStar = (ImageView) goldGV
									.getChildAt(golden_star_num - 1);
							thatStar.setImageResource(goldenStar);
						}
					}
				}
			}

		}
	};

	android.view.View.OnClickListener b_recordListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (state) {
			case READY_FOR_RECORD:
				state = RECORDING;
				String promptname = prompt_name.split("\\.")[0].toString()
						.toLowerCase();
				filename = sh.fileNameHelper(app.username, promptname);
				Time t = new Time();
				t.setToNow();
				recorder = new AudioRecorder(filename);
				try {
					recorder.start();
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
				break;
			default:
				break;
			}
		}
	};

	android.view.View.OnClickListener b_stopListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (state) {
			case RECORDING:
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

	android.view.View.OnClickListener b_playListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (state) {
			case RECORD_END:
			case PLAY_END:
			case FREE:
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
		e.commit();
		Log.i("Game upload url", saved.getString(filename, ""));
	}

	protected void playPrompt(int position) {
		// TODO Auto-generated method stub
		String name = image_names[position].split("\\.")[0].toString();
		StringBuilder sb = new StringBuilder(name);
		// Make the audio name first letter be lowercase
		char c = sb.charAt(0);
		if (Character.isUpperCase(c)) {
			sb.setCharAt(0, Character.toLowerCase(c));
			// Log.i("new_name", sb.toString());
		}
		Log.i("audio_name", "prompts/" + sb.toString() + ".wav");
		// FileInputStream in = null;
		try {
			promptPlayer = new MediaPlayer();
			AssetFileDescriptor descriptor = getAssets().openFd(
					"prompts/" + sb.toString() + ".wav");
			promptPlayer
					.setDataSource(descriptor.getFileDescriptor(),
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
}