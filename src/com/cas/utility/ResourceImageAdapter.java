/*
 *  This class is an adapter that provides images from a fixed set of resource
 *  ids. Bitmaps and ImageViews are kept as weak references so that they can be
 *  cleared by garbage collection when not needed.
 */
package com.cas.utility;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cas.activity.GameActivity;
import com.cas.fragment.courseListActivity;
import com.example.cas.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * This class is an adapter that provides images from a fixed set of resource
 * ids. Bitmaps and ImageViews are kept as weak references so that they can be
 * cleared by garbage collection when not needed.
 * 
 */
public class ResourceImageAdapter extends AbstractCoverFlowImageAdapter {

    /** The Constant TAG. */
    private static final String TAG = ResourceImageAdapter.class.getSimpleName();

    /** The Constant DEFAULT_LIST_SIZE. */
    private static final int DEFAULT_LIST_SIZE = 5;

    /** The Constant IMAGE_NAMES. */
    private static final List<String> IMAGE_NAMES = new ArrayList<String>();

    /** The Constant DEFAULT_RESOURCE_LIST. 
    private static final int[] DEFAULT_RESOURCE_LIST = { R.drawable.question_1,
		R.drawable.card1, R.drawable.card2, R.drawable.card3,
		R.drawable.card4 };
     */
    
    /** The bitmap map. */
    private final Map<Integer, WeakReference<Bitmap>> bitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();

    /** The context. */
    private final Context context;

    /**
     * Creates the adapter with default set of resource images.
     *
     * @param context            context
     * @param image_resource_list the image_resource_list
     */
    public ResourceImageAdapter(final Context context, String[] image_resource_list) {
        super();
        this.context = context;
        setResources(image_resource_list);
    }

    /**
     * Replaces resources with those specified.
     *
     * @param resourceNames the new resources
     */
    public final synchronized void setResources(String[] resourceNames) {
        IMAGE_NAMES.clear();
        for (final String resourceName : resourceNames) {
            IMAGE_NAMES.add(resourceName);
        }
        notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public synchronized int getCount() {
        return IMAGE_NAMES.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.polidea.coverflow.AbstractCoverFlowImageAdapter#createBitmap(int)
     */
    @Override
    protected Bitmap createBitmap(final int position) {
        //Log.v(TAG, "creating item " + position);
        InputStream in = null;
        Log.i("file_name", "img/"+IMAGE_NAMES.get(position));
        String name = IMAGE_NAMES.get(position);
//        StringBuilder sb = new StringBuilder(name);
//        char c = sb.charAt(0);
//        if (Character.isLowerCase(c)) {
//            sb.setCharAt(0, Character.toUpperCase(c));
//            Log.i("new_name", sb.toString());
//        }
//        String new_name = sb.toString();
//        
		try {
			/*
			String[] imgs = context.getAssets().list("img");
			List<String> imgList = new ArrayList<String>(Arrays.asList(imgs));
			if(imgList.contains(new_name)){
				in = context.getAssets().open("img/"+new_name);
			}
			else{
				//Download and store in img/
				
			}
			*/		
			in = context.getAssets().open("img/"+name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        final Bitmap bitmap = BitmapFactory.decodeStream(in);
        bitmapMap.put(position, new WeakReference<Bitmap>(bitmap));
        return bitmap;
    }
    
}