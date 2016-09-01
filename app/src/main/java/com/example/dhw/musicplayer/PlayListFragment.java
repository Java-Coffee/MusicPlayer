package com.example.dhw.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.dhw.musicplayer.MusicLoader.MusicInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayListFragment extends Fragment {

	private List<MusicInfo> musicList;
	private Button addButton, button4, button2;
	private CheckBox cb;
	private MyDatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private ListView lvSongs;	
	private int flag = 2;
	private LinearLayout line;
	private HashMap<Integer, Boolean> selected;
	private MusicAdapter adapter1;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_palylist, null);
		selected = new HashMap<Integer, Boolean>();
		musicList = new ArrayList<MusicInfo>();
		dbHelper = new MyDatabaseHelper(this.getActivity(), "MusicList.db",
				null, 1);
		db = dbHelper.getWritableDatabase();

		line = (LinearLayout) view.findViewById(R.id.layout);
		lvSongs = (ListView) view.findViewById(R.id.lvSongs);
		addButton = (Button) view.findViewById(R.id.button);
		cb = (CheckBox) view.findViewById(R.id.checkbox);
		button2 = (Button) view.findViewById(R.id.button3);
		button4 = (Button) view.findViewById(R.id.button4);

		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),
						AllMusicListActivity.class);
				getActivity().startActivityForResult(intent, 1);
			}
		});

		cb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (cb.isChecked()) {
					for (int i = 0; i < musicList.size(); i++) {
						MusicAdapter.isSelected.put(i, true);
						selected.put(i, true);
					}
					adapter1.notifyDataSetChanged();
				} else {
					for (int i = 0; i < musicList.size(); i++) {
						MusicAdapter.isSelected.put(i, false);
						selected.put(i, false);
					}
					///adapter1.notifyDataSetChanged();
				}
			}
		});

		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				line.setVisibility(View.INVISIBLE);
				addButton.setVisibility(View.VISIBLE);
				for (int i = 0; i < musicList.size(); i++) {
					if (selected.get(i)) {
						MusicInfo musicInfo = musicList.get(i);
						db.delete("musicinfo", "url = ?",
								new String[] { musicInfo.getUrl() });
					}
				}
				musicList.clear();
				MusicList.getMusicList(musicList, dbHelper);
				adapter1 = new MusicAdapter(musicList, getActivity(), 2);
				lvSongs.setAdapter(adapter1);
				cb.setChecked(false);
				flag = 2;
			}
		});
		
		button4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				line.setVisibility(View.INVISIBLE);
				addButton.setVisibility(View.VISIBLE);
				adapter1 = new MusicAdapter(musicList, getActivity(), 2);
				lvSongs.setAdapter(adapter1);
				cb.setChecked(false);
				flag = 2;
			}
		});

//		button3.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				for (int i = 0; i < musicList.size(); i++) {
//					MusicAdapter.isSelected.put(i, false);
//					selected.put(i, false);
//				}
//				adapter1.notifyDataSetChanged();
//			}
//		});

		lvSongs.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (flag == 1) {
					ViewHolder holder = (ViewHolder) view.getTag();
					holder.checkbox.toggle();
					MusicAdapter.isSelected.put(position,
							holder.checkbox.isChecked());
					selected.put(position, holder.checkbox.isChecked());
				} else {
					AppContext.getInstance().getMusicPlayerActivity()
							.changeTab(0);
					Intent sendintent = new Intent();
					sendintent
							.setAction("com.example.musicplayertest.MUSIC_REVICER");
					sendintent.putExtra("msg", "listPlay");
					sendintent.putExtra("path", musicList.get(position)
							.getUrl());
					sendintent.putExtra("name", musicList.get(position)
							.getTitle());
					sendintent.putExtra("positon", position);
					getActivity().sendBroadcast(sendintent);
				}
			}
		});

		lvSongs.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				for (int i = 0; i < musicList.size(); i++) {
					selected.put(i, false);
				}
				adapter1 = new MusicAdapter(musicList, getActivity(), 1);
				lvSongs.setAdapter(adapter1);
				line.setVisibility(View.VISIBLE);
				addButton.setVisibility(View.INVISIBLE);
				flag = 1;
				return true;
			}
		});
		return view;
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

	@Override
	public void onResume() {
		musicList.clear();
		MusicList.getMusicList(musicList, dbHelper);
		MusicAdapter adapter = new MusicAdapter(musicList, getActivity(), 2);
		lvSongs.setAdapter(adapter);
		super.onResume();
	}

}
