package com.example.dhw.musicplayer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.dhw.musicplayer.MusicLoader.MusicInfo;

import java.util.HashMap;
import java.util.List;

public class AllMusicListActivity extends ActionBarActivity {

	private List<MusicInfo> musicList;
	private ListView lvSongs;
	private Button allSelectButton, confirmButton, noneSelectButton;
	private HashMap<Integer, Boolean> selected;
	private MyDatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private MusicAdapter adapter;

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

		allSelectButton = (Button) findViewById(R.id.button1);
		confirmButton = (Button) findViewById(R.id.button2);
		noneSelectButton = (Button) findViewById(R.id.button3);

		selected = new HashMap<Integer, Boolean>();
		dbHelper = new MyDatabaseHelper(this, "MusicList.db", null, 1);
		db = dbHelper.getWritableDatabase();

		MusicLoader musicLoader = MusicLoader.instance(getContentResolver());
		musicList = musicLoader.getMusicList();

		for (int i = 0; i < musicList.size(); i++) {
			selected.put(i, false);
		}

		adapter = new MusicAdapter(musicList, AllMusicListActivity.this, 1);
		lvSongs = (ListView) findViewById(R.id.lvSongs);
		lvSongs.setAdapter(adapter);

		lvSongs.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViewHolder holder = (ViewHolder) view.getTag();
				holder.checkbox.toggle();
				MusicAdapter.isSelected.put(position,
						holder.checkbox.isChecked());
				selected.put(position, holder.checkbox.isChecked());
			}
		});

		allSelectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < musicList.size(); i++) {
					MusicAdapter.isSelected.put(i, true);
					selected.put(i, true);
				}
				adapter.notifyDataSetChanged();
			}
		});

		noneSelectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < musicList.size(); i++) {
					MusicAdapter.isSelected.put(i, false);
					selected.put(i, false);
				}
				adapter.notifyDataSetChanged();
			}
		});

		confirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				ContentValues values = new ContentValues();
				for (int i = 0; i < musicList.size(); i++) {
					if (selected.get(i)) {
						MusicInfo musicInfo = musicList.get(i);
						values.put("title", musicInfo.getTitle());
						values.put("album", musicInfo.getAlbum());
						values.put("duration", musicInfo.getDuration());
						values.put("size", musicInfo.getSize());
						values.put("artist", musicInfo.getArtist());
						values.put("url", musicInfo.getUrl());
						values.put("id", musicInfo.getId());
						db.insert("musicinfo", null, values);
						values.clear();
						Log.d("ll",
								musicInfo.getUrl() + "  "
										+ musicInfo.getTitle() + "  "
										+ musicInfo.getId());
					}
				}
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
