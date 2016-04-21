/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ringdroid;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ringdroid.soundfile.CheapSoundFile;

/**
 * Main screen that shows up when you launch Ringdroid.  Handles selecting
 * an audio file or using an intent to record a new one, and then
 * launches RingdroidEditActivity from here.
 */
public class KeepSelectActivity
    extends Fragment
{
    /**
     * Called when the activity is first created.
     */
	ExpandableListView listView;
	ListAdapter listAdapter;

	private ArrayList<String> arrayGroup = new ArrayList<String>();
	
	private HashMap<String, ArrayList<String>> arrayChild = new HashMap<String, ArrayList<String>>();
    private static final int REQUEST_CODE_EDIT = 1;
	private static final int KEEP_SELECT=0;
	
	// Context menu
    private static final int CMD_EDIT = 4;
    private static final int CMD_DELETE = 5;
    private static final int CMD_SET_AS_DEFAULT = 6;
    private static final int CMD_SET_AS_CONTACT = 7;
    
    int groupposition;
	int childposition;
	String groupname;
	String childname;

   
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	

    	View inflatedView = inflater.inflate(R.layout.keep_select, container, false);
    	
        listView = (ExpandableListView)inflatedView.findViewById(R.id.expandableListView);    
        
        listView.setOnChildClickListener(new OnChildClickListener() {
        	
          @Override 
          public boolean onChildClick(ExpandableListView parent, View v,
              int groupPosition, int childPosition, long id) {              
        	  String parentfilename=arrayGroup.get(groupPosition);
        	  String childfilename=arrayChild.get(parentfilename).get(childPosition);
        	  
        	  
        	  
        	  String filepath=Environment.getExternalStorageDirectory()+"/media/audio/music/"+parentfilename+"/"+childfilename;
        	  try {
                  Intent intent = new Intent(Intent.ACTION_EDIT,
                		  
                          Uri.parse(filepath));
                  intent.putExtra("was_get_content_intent",
                          false);
                  intent.setClassName(
                          "com.ringdroid",
                  "com.ringdroid.KeepPlayer");
                  intent.putExtra("select_name", KEEP_SELECT);
//                  intent.putStringArrayListExtra("arrayGroup", arrayGroup);//20150114 array 그룹을 intent로 보냄
                  intent.putExtra("groupPosition", groupPosition);
                  intent.putExtra("childPosition", childPosition);
//                  intent.putExtra("arrayChild", arrayChild); //childlist도 보냄
                  
                  startActivityForResult(intent, REQUEST_CODE_EDIT);
              } catch (Exception e) {
                  Log.e("Ringdroid", "Couldn't start editor");
              }
        	  
            return false;
          }
        });
        
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Log.e("133","longclick parent"+parent+"\nview:"+view+"\nposition:"+position+"id"+id);
				confirmDelete(position);//findfilepath
				return false;
			}
		});
// Inflate the layout for this fragment
    	
    	
    	return inflatedView;
    	
    }
   
    private void confirmDelete(final int position){
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
            .setTitle("Delete")
            .setMessage("Are you sure?")
            .setPositiveButton(
                R.string.delete_ok_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int whichButton) {
                        onDelete(position);
                    }
                })
            .setNegativeButton(
                R.string.delete_cancel_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int whichButton) {
                    }
                })
            .setCancelable(true)
            .show();
    }
    
    private void onDelete(int position) {
    	String filepath= findfilepath(position);
    	Log.e("217",filepath);
    	File deletefile = new File(filepath);
    	
    	//폴더인 경우 안에 파일이 있으면 삭제되지 않음, 파일이 없을 때만 삭제됨 
    	if(deletefile.isDirectory()){
    		for(File file:deletefile.listFiles()){
                file.delete();
             }
             deletefile.delete();
    	}
    	
    	if(deletefile.isFile()){
    		deletefile.delete();//파일이고 퐁더 안에 마지막 파일이 아닌 경우 파일만 삭제   							
    	
    		String directoryfilepath = Environment.getExternalStorageDirectory()+"/media/audio/music/"+groupname;
    		File directoryFile = new File(directoryfilepath);
    		
    		if(directoryFile.listFiles().length==0){//파일이고 폴더 안에 마지막 파일인 경우 폴더도 삭제
    			directoryFile.delete();
    		}
    	}
    	onResume();
    }
    @Override
    public void onResume() {
    	super.onResume();
    	
    	arrayChild.clear();
    	arrayGroup.clear();
    	getArrayData();
    	
    	listAdapter = new com.ringdroid.ListAdapter(getActivity(), arrayGroup, arrayChild);
    	
    	listView.setAdapter(listAdapter);
    	
    	 for(int i=0; i<listAdapter.getGroupCount(); i++)	{
         	if(!listView.isGroupExpanded(i))
         		listView.expandGroup(i);
         }
    	
    }
    
    private void getArrayData() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
          return;
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
          return;
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
          return;
        }

        File sdCardPath = Environment.getExternalStorageDirectory();
        File loadParentFiles = new File(sdCardPath, "/media/audio/music/");
        if (!loadParentFiles.exists()) {
            loadParentFiles.mkdir();
        }

        int index = 0;
        for (File parentFiles : loadParentFiles.listFiles()) {      
          String dirParentPath = null;
          int fileNameLength = parentFiles.getName().length();
          
          if (parentFiles.isDirectory()) {
            arrayGroup.add(parentFiles.getName());
            
       
            
            File loadChildFiles = null;
         
            loadChildFiles = new File(sdCardPath, "/media/audio/music/" + parentFiles.getName());

            
            ArrayList<String> arrayList = new ArrayList<String>();
            for (File childFiles : loadChildFiles.listFiles()) {
              if (childFiles.isFile()) {
            	  arrayList.add(childFiles.getName());
//            	  String temp = childFiles.getName().substring(childFiles.getName().length()-6,childFiles.getName().length()-4);
//            	 String temp1=null;
//            	 String printname=null;
//            	  if(childFiles.getName().length()==7){
//            		  temp1 = childFiles.getName().substring(1,2);
//            	  }
//                  else if(childFiles.getName().length()==8){
//                	  temp1 = childFiles.getName().substring(1,1);
//                	  temp1 = "0"+ temp1;
//                  }
//            		  
//            	  temp = ":"+temp;
//            	  printname=temp1.concat(temp);
//            	  
//            	  
//            	  arrayList.add(printname);
              }
            }
            arrayChild.put(arrayGroup.get(index), arrayList);
            index++;
          }
        }
      }
   
    private String findfilepath(int index){
    	String filepath="";
    	
    	int numofgroup=arrayGroup.size();
    	int numofchild;
    	int[] g = new int[numofgroup];
    	int[][] c = new int[numofgroup][];
   	
    	SparseIntArray groupPosition=new SparseIntArray();
    	SparseIntArray childPosition=new SparseIntArray();
    	
    	for(int i=0,oldnumofchild=0 ; i<numofgroup; i++){
    		String name=arrayGroup.get(i);
    		numofchild=arrayChild.get(name).size();
    		if(i==0)
    			g[i]=0;
    		else{
    			
    			g[i]=c[i-1][oldnumofchild-1]+1;//i번째 그룹의 인덱스
    		}
    		groupPosition.put(g[i], i);//
    		childPosition.put(g[i], -1);//g[i]라는 인덱스는 그룹이라  child가 없어요
    		Log.e("first loop", "g"+i+":"+g[i]);
    		c[i]= new int[numofchild];
    		for(int j=0;j<numofchild;j++)
    		{
    			c[i][j]=g[i]+j+1;//i번쨰 그룹의 j번째 child의 인덱스
        		Log.e("second loop", "c"+j+":"+c[i][j]);

    			groupPosition.put(c[i][j], i);//c 라는 인덱스의 그룹은 i
    			childPosition.put(c[i][j], j);//c 라는 인덱스의 child는 j
    			
    		}	
    		oldnumofchild=numofchild;
    		
    	}
    	
    	
    	
    	if(childPosition.get(index)==-1){// 그룹이면
    		groupposition= groupPosition.get(index);
    		groupname= arrayGroup.get(groupposition);
    		
    		filepath = Environment.getExternalStorageDirectory()+"/media/audio/music/"+groupname;
    	}else{//child 이면
    		 groupposition= groupPosition.get(index);
        	 childposition = childPosition.get(index);
        	 groupname= arrayGroup.get(groupposition);
        	 childname = arrayChild.get(groupname).get(childposition);
        	 
        	 filepath =  Environment.getExternalStorageDirectory()+"/media/audio/music/"+groupname+"/"+childname;
    	}
    	return filepath; 
    }

}
