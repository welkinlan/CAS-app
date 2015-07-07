/*
 * The helper for parsing JSON object into an arraylist
 */
package com.cas.utility;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.cas.model.Course;
import com.cas.model.CourseContent;

// TODO: Auto-generated Javadoc
/**
 * The Class SyncHelper.
 */
public class SyncHelper {
	
	/**
	 * Gets the user courses, parse JSON to array.
	 *
	 * @param jsonobj the json string
	 * @param coursesArray the courses array
	 * @return 
	 */
	public void getUserCourses(JSONObject jsonobj,
			ArrayList<Course> coursesArray) {
		try {
			JSONArray courses = jsonobj.getJSONArray("courses");
			// looping through All Contacts
			for (int i = 0; i < courses.length(); i++) {
				JSONObject c = courses.getJSONObject(i);
				Course course = new Course();
				course.populateCourse(c);
				// Toast.makeText(context.getApplicationContext(),
				// course.getShortName(), Toast.LENGTH_LONG).show();
				// Storing each json item in variable
				coursesArray.add(course);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the course contents, parse JSON to array..
	 *
	 * @param jsonobj the jsonobj
	 * @param courseContentsArray the course contents array
	 * @return the course contents
	 */
	public void getCourseContents(JSONObject jsonobj,
			ArrayList<CourseContent> courseContentsArray) {

		try {

			JSONArray coursecontents = jsonobj.getJSONArray("coursecontents");
			// looping through All Course Content
			for (int i = 0; i < coursecontents.length(); i++) {
				JSONObject c = coursecontents.getJSONObject(i);
				CourseContent coursecontent = new CourseContent();
				coursecontent.populateCourseContent(c);
				// Toast.makeText(context.getApplicationContext(),
				// coursecontent.getName(), Toast.LENGTH_LONG).show();
				// Storing each json item in variable
				courseContentsArray.add(coursecontent);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
