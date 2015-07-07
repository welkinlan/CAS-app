/*
 * The utility class for generating flipping animation
 */
package com.cas.utility;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

// TODO: Auto-generated Javadoc
/**
 * The Class TurnAnimation.
 */
public class TurnAnimation extends Animation {  
    
    /** The m from degrees. */
    private final float mFromDegrees;  
    
    /** The m to degrees. */
    private final float mToDegrees;  
    
    /** The m center x. */
    private final float mCenterX;  
    
    /** The m center y. */
    private final float mCenterY;  
    
    /** The m depth z. */
    private final float mDepthZ;  
    
    /** The m reverse. */
    private final boolean mReverse; 
    
    /** The m camera. */
    private Camera mCamera;  
    
    /**
     * Instantiates a new turn animation.
     *
     * @param fromDegrees the from degrees
     * @param toDegrees the to degrees
     * @param centerX the center x
     * @param centerY the center y
     * @param depthZ the depth z
     * @param reverse the reverse
     */
    public TurnAnimation(float fromDegrees, float toDegrees,  
            float centerX, float centerY, float depthZ, boolean reverse) {  
        mFromDegrees = fromDegrees;  
        mToDegrees = toDegrees;  
        mCenterX = centerX;  
        mCenterY = centerY;  
        mDepthZ = depthZ;  
        mReverse = reverse;  
    }  
 
    /* (non-Javadoc)
     * @see android.view.animation.Animation#initialize(int, int, int, int)
     */
    @Override 
    public void initialize(int width, int height, int parentWidth, int parentHeight) {  
        super.initialize(width, height, parentWidth, parentHeight);  
        mCamera = new Camera();  
    }   
    
    /* (non-Javadoc)
     * @see android.view.animation.Animation#applyTransformation(float, android.view.animation.Transformation)
     */
    @Override 
    protected void applyTransformation(float interpolatedTime, Transformation t) {  
        final float fromDegrees = mFromDegrees;  
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);  
 
        final float centerX = mCenterX;  
        final float centerY = mCenterY;  
        final Camera camera = mCamera;  
 
        final Matrix matrix = t.getMatrix();  
 
        camera.save();  
        if (mReverse) {  
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);  
        } else {  
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));  
        }  
        camera.rotateY(degrees);  
        camera.getMatrix(matrix);  
        camera.restore();  
 
        matrix.preTranslate(-centerX, -centerY);  
        matrix.postTranslate(centerX, centerY);  
    }  
} 