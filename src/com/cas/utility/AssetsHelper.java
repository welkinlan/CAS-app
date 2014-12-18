package com.cas.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.content.Context;

public class AssetsHelper {
	
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
