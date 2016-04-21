package com.ringdroid;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.TextView;

public class MainActivity extends FragmentActivity{
	static final int NUM_ITEMS = 2;
	MyAdapter mAdapter;

	ViewPager mPager;
	
	static Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Tutorial
		SharedPreferences sharedPreferences = getSharedPreferences("a", MODE_PRIVATE);
		int fistViewShow = sharedPreferences.getInt("First", 0);
		
		if(fistViewShow != 1){
			Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
			startActivity(intent);
		}
		
		intent = getIntent();        
		
		mAdapter = new MyAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(
				new ViewPager.SimpleOnPageChangeListener(){
					public void onPageSelected(int position){
						getActionBar().setSelectedNavigationItem(position);
					}
		});
		final ActionBar actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionbar.setCustomView(R.layout.actionbar);		
		TextView tv=(TextView)findViewById(R.id.actionbartext);
		tv.setTypeface(Typeface.createFromAsset(getAssets(), "RobotoCondensed/RobotoCondensed-Bold.ttf"));
		
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			{
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}
		};		
		actionbar.addTab(actionbar.newTab().setIcon(R.drawable.mp3list)
				.setTabListener(tabListener));
		actionbar.addTab(actionbar.newTab().setIcon(R.drawable.keeplist)
				.setTabListener(tabListener));
		
		
//		//headset controls
//		HardButtonReceiver buttonReceiver = new HardButtonReceiver();
//	    IntentFilter iF = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
//	    iF.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY + 1);
//	    registerReceiver(buttonReceiver, iF);   	        
	}
	
	 

	public static class MyAdapter extends FragmentStatePagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment frag = new MediaSelectActivity(intent);
			switch (position) {
			case 0:
				frag = new MediaSelectActivity(intent);
				break;

			case 1:
				frag = new KeepSelectActivity();
				break;
			}
			return frag;
		}
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.mainoverflow, menu);
//		return true;
//	}
	
}
