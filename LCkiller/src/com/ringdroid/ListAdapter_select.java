package com.ringdroid;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter_select extends BaseAdapter{

	private Context context;
	private ArrayList<String> arraySrc;
	public ListAdapter_select(Context context, ArrayList<String> arraySrc) {
		this.context = context;
		this.arraySrc = arraySrc;
	}
	@Override
	public int getCount() {
		return arraySrc.size();
	}

	@Override
	public Object getItem(int position) {
		return arraySrc.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String title=arraySrc.get(position);
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.lvi_child, parent,false);
		}
		TextView text = (TextView)convertView.findViewById(R.id.textChild);
		text.setTypeface(Typeface.createFromAsset(context.getAssets(), "RobotoCondensed/RobotoCondensed-Bold.ttf")); //ÆùÆ®
		text.setText(title);
		
		return convertView;
	}
		
}
