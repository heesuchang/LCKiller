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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
public class MediaSelectActivity
    extends ListFragment
{
	Intent intent;
	
    private SearchView mFilter;
    private SimpleCursorAdapter mAdapter;
    private boolean mWasGetContentIntent;
    private boolean mShowAll;

    private static final int REQUEST_CODE_EDIT = 1;
    private static final int REQUEST_CODE_CHOOSE_CONTACT = 2;

    // Menu commands
    private static final int CMD_ABOUT = 1;
    private static final int CMD_PRIVACY = 2;
    private static final int CMD_SHOW_ALL = 3;

    // Context menu
    private static final int CMD_EDIT = 4;
    private static final int CMD_DELETE = 5;
    private static final int CMD_SET_AS_DEFAULT = 6;
    private static final int CMD_SET_AS_CONTACT = 7;
    private Typeface typeface = null;
    private static final int MEDIA_SELECT=1;
    
    public MediaSelectActivity(){
    }
    public MediaSelectActivity(Intent intent) {
    	this.intent = intent;
    }

    /**
     * Called when the activity is first created.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
// Inflate the layout for this fragment
    	
    	View inflatedView = inflater.inflate(R.layout.media_select, container, false);
       	
    	return inflatedView;   	
    	
    }
    
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        
        super.onActivityCreated(savedInstanceState);

        mShowAll = false;
        mWasGetContentIntent = intent.getAction().equals(
                Intent.ACTION_GET_CONTENT);
        

        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            showFinalAlert(getResources().getText(R.string.sdcard_readonly));
            return;
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
            showFinalAlert(getResources().getText(R.string.sdcard_shared));
            return;
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            showFinalAlert(getResources().getText(R.string.no_sdcard));
            return;
        }
        
        
        
        // Inflate our UI from its XML layout description.

        try {           
            mAdapter = new SimpleCursorAdapter(
            		
                    getActivity(),
                    // Use a template that displays a text view
                    R.layout.media_select_row,
                    // Give the cursor to the list adatper
                    createCursor(""),
                    // Map from database columns...
                    new String[] {
//                        MediaStore.Audio.Media.ARTIST,
//                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.TITLE,
//                        MediaStore.Audio.Media.DURATION,
//                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media._ID},
                        // To widget ids in the row layout...
                        
                        new int[] {
//                        R.id.row_artist,
//                        R.id.row_album,
                        R.id.row_title,
//                        R.id.file_duration,
//                        R.id.row_icon,
                        R.id.row_options_button}
                    );
            
//        	TextView tv = (TextView)getActivity().findViewById(R.id.row_title);
//        	tv.setTypeface(Typeface.createFromAsset(getActivity().getAssets() , "RobotoCondensed/RobotoCondensed-Bold.ttf"));
        	
            setListAdapter(mAdapter);

            getListView().setItemsCanFocus(true);

            // Normal click - open the editor
            getListView().setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView parent,
                        View view,
                        int position,
                        long id) {
                    startRingdroidEditor();
                }                         
            });

        } 
        catch (SecurityException e) {
            // No permission to retrieve audio?
            Log.e("Ringdroid", e.toString());

            // todo error 1
        } 
        catch (IllegalArgumentException e) {
            // No permission to retrieve audio?
            Log.e("Ringdroid", e.toString());

            // todo error 2
        }

////List select button        
//        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//            public boolean setViewValue(View view,
//                    Cursor cursor,
//                    int columnIndex) {
//                if (view.getId() == R.id.row_options_button){
//                    // Get the arrow image view and set the onClickListener to open the context menu.
//                    ImageView iv = (ImageView)view;
//                    iv.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            getActivity().openContextMenu(v);
//                        }
//                    });
//                    return true;
//                } else if (view.getId() == R.id.row_icon) {
//                    setSoundIconFromCursor((ImageView) view, cursor);
//                    return true;
//                }
//
//                return false;
//            }
//        });
//
//        // Long-press opens a context menu
//        registerForContextMenu(getListView());
    }

//search_fillter    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	super.onCreateOptionsMenu(menu, inflater);

        mFilter = (SearchView) menu.findItem(R.id.action_search_filter).getActionView();
        if (mFilter != null) {
            mFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
		public boolean onQueryTextChange(String newText) {
		    refreshListView();
                    return true;
		}
		public boolean onQueryTextSubmit(String query) {
		    refreshListView();
                    return true;
		}
	    });
        }

    }

    private void setSoundIconFromCursor(ImageView view, Cursor cursor) {
        if (0 != cursor.getInt(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_RINGTONE))) {
            view.setImageResource(R.drawable.type_ringtone);
            ((View) view.getParent()).setBackgroundColor(
                    getResources().getColor(R.drawable.type_bkgnd_ringtone));
        } else if (0 != cursor.getInt(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_ALARM))) {
            view.setImageResource(R.drawable.type_alarm);
            ((View) view.getParent()).setBackgroundColor(
                    getResources().getColor(R.drawable.type_bkgnd_alarm));
        } else if (0 != cursor.getInt(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_NOTIFICATION))) {
            view.setImageResource(R.drawable.type_notification);
            ((View) view.getParent()).setBackgroundColor(
                    getResources().getColor(R.drawable.type_bkgnd_notification));
        } else if (0 != cursor.getInt(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_MUSIC))) {
//            view.setImageResource(R.drawable.type_music);
            ((View) view.getParent()).setBackgroundColor(
                    getResources().getColor(R.drawable.type_bkgnd_music));
        }

        String filename = cursor.getString(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.DATA));
        if (!CheapSoundFile.isFilenameSupported(filename)) {
            ((View) view.getParent()).setBackgroundColor(
                    getResources().getColor(R.drawable.type_bkgnd_unsupported));
        }
    }

   

    
    @Override
    public void onCreateContextMenu(ContextMenu menu,
            View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        Cursor c = mAdapter.getCursor();
        String title = c.getString(c.getColumnIndexOrThrow(
                MediaStore.Audio.Media.TITLE));

        menu.setHeaderTitle(title);

        menu.add(0, CMD_EDIT, 0, R.string.context_menu_edit);
        menu.add(0, CMD_DELETE, 0, R.string.context_menu_delete);

        // Add items to the context menu item based on file type
        if (0 != c.getInt(c.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_RINGTONE))) {
            menu.add(0, CMD_SET_AS_DEFAULT, 0, R.string.context_menu_default_ringtone);
            menu.add(0, CMD_SET_AS_CONTACT, 0, R.string.context_menu_contact);
        } else if (0 != c.getInt(c.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_NOTIFICATION))) {
            menu.add(0, CMD_SET_AS_DEFAULT, 0, R.string.context_menu_default_notification);
	}
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info =
            (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case CMD_EDIT:
            startRingdroidEditor();
            return true;
        case CMD_DELETE:
            confirmDelete();
            return true;
        case CMD_SET_AS_CONTACT:
            return chooseContactForRingtone(item);
        default:
            return super.onContextItemSelected(item);
        }
    }

    private void showPrivacyDialog() {
        try {
            Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse(""));
            intent.putExtra("privacy", true);
            intent.setClassName("com.ringdroid",
				"com.ringdroid.KeepPlayer");
            intent.putExtra("select_name",MEDIA_SELECT);
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        } catch (Exception e) {
            Log.e("Ringdroid", "Couldn't show privacy dialog");
        }
    }

   
    private Uri getUri(){
        //Get the uri of the item that is in the row
        Cursor c = mAdapter.getCursor();
        int uriIndex = c.getColumnIndex(
                "\"" + MediaStore.Audio.Media.INTERNAL_CONTENT_URI + "\"");
        if (uriIndex == -1) {
            uriIndex = c.getColumnIndex(
                    "\"" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "\"");
        }
        String itemUri = c.getString(uriIndex) + "/" +
        c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
        return (Uri.parse(itemUri));
    }

    private boolean chooseContactForRingtone(MenuItem item){
        try {
            //Go to the choose contact activity
            Intent intent = new Intent(Intent.ACTION_EDIT, getUri());
            intent.setClassName(
                    "com.ringdroid",
            "com.ringdroid.ChooseContactActivity");
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_CONTACT);
        } catch (Exception e) {
            Log.e("Ringdroid", "Couldn't open Choose Contact window");
        }
        return true;
    }

    private void confirmDelete() {
        // See if the selected list item was created by Ringdroid to
        // determine which alert message to show
        Cursor c = mAdapter.getCursor();
        int artistIndex = c.getColumnIndexOrThrow(
                MediaStore.Audio.Media.ARTIST);
        String artist = c.getString(c.getColumnIndexOrThrow(
                MediaStore.Audio.Media.ARTIST));
        CharSequence ringdroidArtist =
            getResources().getText(R.string.artist_name);

        CharSequence message;
        if (artist.equals(ringdroidArtist)) {
            message = getResources().getText(
                    R.string.confirm_delete_ringdroid);
        } else {
            message = getResources().getText(
                    R.string.confirm_delete_non_ringdroid);
        }

        CharSequence title;
        if (0 != c.getInt(c.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_RINGTONE))) {
            title = getResources().getText(R.string.delete_ringtone);
        } else if (0 != c.getInt(c.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_ALARM))) {
            title = getResources().getText(R.string.delete_alarm);
        } else if (0 != c.getInt(c.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_NOTIFICATION))) {
            title = getResources().getText(R.string.delete_notification);
        } else if (0 != c.getInt(c.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_MUSIC))) {
            title = getResources().getText(R.string.delete_music);
        } else {
            title = getResources().getText(R.string.delete_audio);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                R.string.delete_ok_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int whichButton) {
                        onDelete();
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

    private void onDelete() {
        Cursor c = mAdapter.getCursor();
        int dataIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        String filename = c.getString(dataIndex);

        int uriIndex = c.getColumnIndex(
                "\"" + MediaStore.Audio.Media.INTERNAL_CONTENT_URI + "\"");
        if (uriIndex == -1) {
            uriIndex = c.getColumnIndex(
                    "\"" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "\"");
        }
        if (uriIndex == -1) {
            showFinalAlert(getResources().getText(R.string.delete_failed));
            return;
        }

        if (!new File(filename).delete()) {
            showFinalAlert(getResources().getText(R.string.delete_failed));
        }

        String itemUri = c.getString(uriIndex) + "/" +
        c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
        getActivity().getContentResolver().delete(Uri.parse(itemUri), null, null);
    }

    private void showFinalAlert(CharSequence message) {
        new AlertDialog.Builder(getActivity())
        .setTitle(getResources().getText(R.string.alert_title_failure))
        .setMessage(message)
        .setPositiveButton(
                R.string.alert_ok_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int whichButton) {
                        getActivity().finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

//    private void onRecord() {
//        try {
//            Intent intent = new Intent(Intent.ACTION_EDIT,
//                    Uri.parse("record"));
//            intent.putExtra("was_get_content_intent",
//                    mWasGetContentIntent);
//            intent.setClassName(
//                    "com.ringdroid",
//            "com.ringdroid.KeepPlayer");
//            startActivityForResult(intent, REQUEST_CODE_EDIT);
//        } catch (Exception e) {
//            Log.e("Ringdroid", "Couldn't start editor");
//        }
//    }

    private void startRingdroidEditor() {
        Cursor c = mAdapter.getCursor();
        int dataIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        String filename = c.getString(dataIndex);
        try {
            Intent intent = new Intent(Intent.ACTION_EDIT,
                    Uri.parse(filename));
            intent.putExtra("was_get_content_intent",
                    mWasGetContentIntent);
            intent.setClassName(
                    "com.ringdroid",
            "com.ringdroid.KeepPlayer");
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        } catch (Exception e) {
            Log.e("Ringdroid", "Couldn't start editor");
        }
    }

    private Cursor getInternalAudioCursor(String selection,
            String[] selectionArgs) {
        return getActivity().managedQuery(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                INTERNAL_COLUMNS,
                selection,
                selectionArgs,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    private Cursor getExternalAudioCursor(String selection,
            String[] selectionArgs) {
        return getActivity().managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                EXTERNAL_COLUMNS,
                selection,
                selectionArgs,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    Cursor createCursor(String filter) {
        ArrayList<String> args = new ArrayList<String>();
          String path = Environment.getExternalStorageDirectory().getPath();

          String selection = MediaStore.Audio.Media.IS_MUSIC + " !=" + 0
                  + " AND " + MediaStore.Audio.Media.DATA + " LIKE '" + path
                  + "/Music/%'";



          if (filter != null && filter.length() > 0) {
              filter = "%" + filter + "%";
              selection =
                  "(" + selection + " AND " +
                  "((TITLE LIKE ?) OR (ARTIST LIKE ?) OR (ALBUM LIKE ?)))";
              args.add(filter);
              args.add(filter);
              args.add(filter);
          }

          String[] argsArray = args.toArray(new String[args.size()]);

          getExternalAudioCursor(selection, argsArray);
          getInternalAudioCursor(selection, argsArray);

          Cursor c = new MergeCursor(new Cursor[] {
                  getExternalAudioCursor(selection, argsArray),
                  getInternalAudioCursor(selection, argsArray)});
          
       getActivity().startManagingCursor(c);
       return c;
   }

    private void refreshListView() {
        String filterStr = mFilter.getQuery().toString();
        mAdapter.changeCursor(createCursor(filterStr));
    }
    
//    private static String formatDuration(long x){
//    	long duration = x;
//    	long convert =0;
//    	double convert1=0;
//    	
//    	convert = duration / 60000;
//    	
//    	convert1 = duration % 60000;
//    	if(convert1<10)
//    	{
//    		return convert + " : 0" + convert1;
//        	
//    	}
//    	
//    	else 
//    		return convert + " : " +  Math.floor(convert1*100)/100;
//    	
//    }

    private static final String[] INTERNAL_COLUMNS = new String[] {
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.IS_RINGTONE,
        MediaStore.Audio.Media.IS_ALARM,
        MediaStore.Audio.Media.IS_NOTIFICATION,
//        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.IS_MUSIC,
        "\"" + MediaStore.Audio.Media.INTERNAL_CONTENT_URI + "\""
    };

    private static final String[] EXTERNAL_COLUMNS = new String[] {
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.IS_RINGTONE,
        MediaStore.Audio.Media.IS_ALARM,
        MediaStore.Audio.Media.IS_NOTIFICATION,
//        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.IS_MUSIC,
        "\"" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "\""
    };
    
}
