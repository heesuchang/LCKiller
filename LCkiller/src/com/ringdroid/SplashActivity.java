package com.ringdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
	Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		handler.postDelayed(splashRunnable, 1300);
	}
	
	Runnable splashRunnable = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.LAUNCHER");
			
			startActivity(intent);
			
			finish();
		}
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		handler.removeCallbacks(splashRunnable);
		super.onDestroy();
	}
}
