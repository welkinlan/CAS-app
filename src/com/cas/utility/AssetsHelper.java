/*
 * The utility class to get file names by extension
 */
package com.cas.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.content.Context;

// TODO: Auto-generated Javadoc
/**
 * The Class AssetsHelper.
 */
public class AssetsHelper {
	
	/**
	 * Gets all files in asset by extension.
	 *
	 * @param context the context
	 * @param path the path
	 * @param extension the extension
	 * @return all files in asset by extension
	 */
	public static String[] getAllFilesInAssetByExtension(Context context, String path, String extension){
        Assert.assertNotNull(context);

        try {
            String[] files = context.getAssets().list(path);

            List<String> filesWithExtension = new ArrayList<String>();

            for(String file : files){
                if(file.endsWith(extension)){
                    filesWithExtension.add(file);
                }
            }

            return filesWithExtension.toArray(new String[filesWithExtension.size()]);  
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
