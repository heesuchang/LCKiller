package com.ringdroid;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListAdapter extends BaseExpandableListAdapter {
  
  private Context context;
  private ArrayList<String> arrayGroup;
  private HashMap<String, ArrayList<String>> arrayChild;
  private int playingGroupPosition, playingChildPosition;
  private boolean playerStatus;
  
  public ListAdapter(Context context, ArrayList<String> arrayGroup, HashMap<String, ArrayList<String>> arrayChild) {
    super();
    this.context = context;
    this.arrayGroup = arrayGroup;
    this.arrayChild = arrayChild;
    this.playerStatus = false;
  }
  @Override
  public int getGroupCount() {
    return arrayGroup.size();
  }

  @Override
  public int getChildrenCount(int groupPosition) {
    return arrayChild.get(arrayGroup.get(groupPosition)).size();
  }

  @Override
  public Object getGroup(int groupPosition) {
    return arrayGroup.get(groupPosition);
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    return arrayChild.get(arrayGroup.get(groupPosition)).get(childPosition);
  }

  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override
  public boolean hasStableIds() {
    // TODO Auto-generated method stub
    return false;
  }
  
  public void setPlayingChildPosition(int playingChildPosition){
	  this.playingChildPosition = playingChildPosition;
  }
  
  public void setPlayingGroupPosition(int playingGroupPosition){
	  this.playingGroupPosition = playingGroupPosition;
  }

  @Override
  public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    String groupName = arrayGroup.get(groupPosition);
    View v = convertView;
    
    if (v == null) {
      LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = (LinearLayout)inflater.inflate(R.layout.lvi_group, null);
    }
    TextView textGroup = (TextView)v.findViewById(R.id.textGroup);
    textGroup.setTypeface(Typeface.createFromAsset(context.getAssets(), "RobotoCondensed/RobotoCondensed-Bold.ttf"));
    textGroup.setText(groupName);
    TextView childCount = (TextView)v.findViewById(R.id.child_count);
    childCount.setTypeface(Typeface.createFromAsset(context.getAssets(), "RobotoCondensed/RobotoCondensed-Bold.ttf"));
    childCount.setText(Integer.toString(getChildrenCount(groupPosition)));
    return v;
  }

  @Override
  public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    String childName = arrayChild.get(arrayGroup.get(groupPosition)).get(childPosition);
    View v = convertView;
    
    if (v == null) {
      LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = (LinearLayout)inflater.inflate(R.layout.lvi_child, null);
    }
    TextView textChild = (TextView)v.findViewById(R.id.textChild);
    textChild.setTypeface(Typeface.createFromAsset(context.getAssets(), "RobotoCondensed/RobotoCondensed-Bold.ttf"));
    textChild.setText(childName);
    
    if(playerStatus){
    	ImageView imageChild = (ImageView)v.findViewById(R.id.row_options_button);
    	if(groupPosition == playingGroupPosition && childPosition == playingChildPosition){
    		imageChild.setImageResource(R.drawable.playing);
    	} else {
    		imageChild.setImageResource(R.drawable.dot);
    	}
    }
    
    return v;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return true;
  }
  
  public void setPlayerStatus(boolean isPlayer){
	  playerStatus = isPlayer;
  }
}