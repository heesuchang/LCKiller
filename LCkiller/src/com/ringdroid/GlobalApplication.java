package com.ringdroid;

import java.lang.reflect.Field;
import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;

public class GlobalApplication extends Application {

	  public void onCreate() {
	        super.onCreate();
	    	 Log.d("TestApp", "GlobalApplication");
	        initDefaultTypeface();
	    }

	    private void initDefaultTypeface() {

	        try {
	            Typeface defaultTypeface = Typeface.createFromAsset(getAssets(), "RobotoCondensed/RobotoCondensed-Bold.ttf");
	            final Field field = Typeface.class.getDeclaredField("DEFAULT");
	            field.setAccessible(true);
	            field.set(null, defaultTypeface);
	        } catch ( NoSuchFieldException e ) {
	             e.printStackTrace();
	        } catch ( IllegalArgumentException e ) {
	            e.printStackTrace();
	        } catch ( IllegalAccessException e ) {
	            e.printStackTrace();
	         }
	    }
	}
