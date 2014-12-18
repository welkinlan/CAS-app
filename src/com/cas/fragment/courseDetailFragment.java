package com.cas.fragment;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.cas.activity.GameActivity;
import com.cas.activity.ViewActivity;
import com.cas.model.Course;
import com.cas.model.CourseContent;
import com.cas.model.Module;
import com.cas.model.User;
import com.cas.utility.Globals;
import com.example.cas.R;
//import com.kilobolt.*;
//import com.kilobolt.ZombieBird.MainActivity;

/**
 * A fragment representing a single course detail screen. This fragment is
 * either contained in a {@link courseListActivity} in two-pane mode (on
 * tablets) or a {@link courseDetailActivity} on handsets.
 */
public class courseDetailFragment extends Fragment implements
		OnItemClickListener {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	public static ListView course_list;
	public static LinearLayout hand_layout;
	private CourseDetailAdapter course_adapter;
	private int currentPosition = -1;
	private static User user;
	private static ArrayList<Course> courses;

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Course course;
	int MODE_PRIVATE = 0;
	private static Integer goldenStar = R.drawable.golden_star;
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public courseDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Globals app = ((Globals)getActivity().getApplicationContext());
		user = app.user;
		courses = user.getCourses();

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			String id = getArguments().getString(ARG_ITEM_ID);
			course = courses.get(Integer.parseInt(id));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		currentPosition = position;
		course_adapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_course_detail,
				container, false);

		// Show the course content as ListView.
		if (course != null) {
			ArrayList<CourseContent> course_content = new ArrayList<CourseContent>();
			course_content = course.getCourseContent();
			ArrayList<Module> course_modules = new ArrayList<Module>();
			course_modules = course_content.get(0).getModules();
			course_list = (ListView) rootView.findViewById(R.id.course_detail);
			course_list.setOnItemClickListener(this);
		
			if (course_adapter == null) {
				course_adapter = new CourseDetailAdapter(getActivity(),
						course_modules);
				course_list.setAdapter(course_adapter);
			} else {
				course_adapter.notifyDataSetChanged();
			}
		}
		return rootView;
	}

	
	class CourseDetailAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;
		private List<Module> course_modules;

		class ViewHolder {
			public TextView exerciseName;
			//public TextView count;
			public TableLayout excerciseStars;
			public LinearLayout linearLayout;
			public LinearLayout viewLinearLayout;
			public LinearLayout playLinearLayout;
			public LinearLayout flappyLinearLayout;
		}

		public CourseDetailAdapter(Activity activity, List<Module> course_modules) {
			this.context = activity;
			this.course_modules = course_modules;
			inflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return course_modules.size();
		}

		public int getItemViewType(int position) {
			return position;
		}

		public Object getItem(int arg0) {
			return course_modules.get(arg0);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.course_detail_item,
						parent, false);
				holder = new ViewHolder();
				holder.exerciseName = (TextView) convertView
						.findViewById(R.id.item_name);
				//holder.count = (TextView) convertView
				//		.findViewById(R.id.completed_times);
				holder.excerciseStars = (TableLayout) convertView.findViewById(R.id.completed_times);
				holder.linearLayout = (LinearLayout) convertView
						.findViewById(R.id.expand);
				holder.viewLinearLayout = (LinearLayout) convertView
						.findViewById(R.id.item_view);
				holder.playLinearLayout = (LinearLayout) convertView
						.findViewById(R.id.item_play);
				//holder.flappyLinearLayout = (LinearLayout) convertView
				//		.findViewById(R.id.item_flappy);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Module module = course_modules.get(position);
			Globals app = ((Globals)getActivity().getApplicationContext());
			SharedPreferences saved = getActivity().getSharedPreferences(app.username, MODE_PRIVATE);
			int mod_completed_times = saved.getInt("mod"+module.getId()+"completed times",0);
			holder.exerciseName.setText(module.getName());
			
			//mod_completed_times = 11;
			int rowNum = mod_completed_times/10;
			holder.excerciseStars.removeAllViews();
			for(int i=0; i<=rowNum ; i++){
				TableRow tr = new TableRow(context);
		        tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		        for (int j = 0; j < Math.min(10,mod_completed_times-i*10); j++) {
		            ImageView star = new ImageView(context);
		            star.setLayoutParams(new TableRow.LayoutParams(40, 40));
					star.setScaleType(ImageView.ScaleType.CENTER_CROP);
					star.setPadding(2, 2, 2, 2);
					star.setImageResource(goldenStar);
		            tr.addView(star);
		        }
	            holder.excerciseStars.addView(tr);
			}

			if (position == currentPosition) {
				Boolean Game=true, Flash=true;
				String game_mode = module.getContents().get(0).getMode();
				String transition_mode = module.getContents().get(0).getTransition();
				if(game_mode == null || game_mode.equals("1")){
					holder.playLinearLayout.setClickable(true);
				}
				else if (game_mode.equals("0")){
					holder.playLinearLayout.setClickable(false);
					holder.playLinearLayout.setBackgroundColor(Color.GRAY);
					TextView tv = (TextView)holder.playLinearLayout.findViewById(R.id.game_text);
					tv.setText("Memory Game(disabled)");
					Game = false;
				}
				if(transition_mode == null || transition_mode.equals("1")){
					holder.viewLinearLayout.setClickable(true);
					TextView tv = (TextView)holder.viewLinearLayout.findViewById(R.id.view_text);
					tv.setText("Flash Cards(transition)");
				}
				else if (transition_mode.equals("0")){
					holder.viewLinearLayout.setClickable(true);
				}
				
				
							
				ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1f,
						0.1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f,
						Animation.RELATIVE_TO_SELF, 0f);
				AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
				AnimationSet set = new AnimationSet(true);
				set.addAnimation(scaleAnimation);
				set.addAnimation(alphaAnimation);
				set.setDuration(500);
				holder.linearLayout.setAnimation(set);
				holder.linearLayout.setVisibility(View.VISIBLE);

				if(Flash){
					holder.viewLinearLayout
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent viewIntent = new Intent(getActivity(),
									ViewActivity.class);
							viewIntent.putExtra("moduleObject", module); 
							viewIntent.putExtra("userObject", user);
							viewIntent.putExtra("courseObject", course);
							startActivity(viewIntent);
							currentPosition = -1;
						}
					});
				}
				
				if(Game){
					holder.playLinearLayout
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									
									Intent gameIntent = new Intent(getActivity(),
											GameActivity.class);
									gameIntent.putExtra("moduleObject", module); 
									gameIntent.putExtra("userObject", user);
									gameIntent.putExtra("courseObject", course);
									startActivity(gameIntent);
									currentPosition = -1;
								}
							});
				}
				
				/*
				holder.flappyLinearLayout.setClickable(false);
				holder.flappyLinearLayout.setBackgroundColor(Color.GRAY);
				holder.flappyLinearLayout
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent viewIntent = new Intent(getActivity(),
								MainActivity.class);
						viewIntent.putExtra("moduleObject", module); 
						viewIntent.putExtra("userObject", user);
						viewIntent.putExtra("courseObject", course);
						startActivity(viewIntent);
						currentPosition = -1;
					}
				});
				*/

			} else {
				holder.linearLayout.setVisibility(View.GONE);
				holder.viewLinearLayout.setClickable(false);
				holder.playLinearLayout.setClickable(false);
			}
			return convertView;
		}
	}
	
	/*
	static class StarAdapter extends BaseAdapter {
		private Context mContext;
		private int completed_times;

		public StarAdapter(Context applicationContext, int mod_completed_times) {
			// TODO Auto-generated constructor stub
			this.mContext = applicationContext;
			this.completed_times  =mod_completed_times;
		}

		@Override
		public int getCount() {
			return this.completed_times;
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
				star.setImageResource(goldenStar);
			} else {
				star = (ImageView) convertView;
			}
			star.setClickable(false);
			star.setPressed(false);
			star.setEnabled(false);
			return star;
		}
	}
*/

}