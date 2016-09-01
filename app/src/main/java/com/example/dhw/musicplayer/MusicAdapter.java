package com.example.dhw.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dhw.musicplayer.MusicLoader.MusicInfo;

import java.util.HashMap;
import java.util.List;

class MusicAdapter extends BaseAdapter {

	private List<MusicInfo> musicList;
	public static HashMap<Integer, Boolean> isSelected;
	private Context context;
	private int flag;

	public MusicAdapter(List<MusicInfo> musicList2, Context context, int flag) {
		musicList = musicList2;
		this.context = context;
		this.flag = flag;
		isSelected = new HashMap<Integer, Boolean>();
		init(isSelected);
	}

	public void init(HashMap<Integer, Boolean> isSelected2isSelected) {
		for (int i = 0; i < musicList.size(); i++) {
			isSelected.put(i, false);
		}
	}

	@Override
	public int getCount() {
		return musicList.size();
	}

	@Override
	public Object getItem(int position) {
		return musicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return musicList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.music_item2, null);
			ImageView pImageView = (ImageView) convertView
					.findViewById(R.id.albumPhoto);
			TextView pTitle = (TextView) convertView.findViewById(R.id.title);
			TextView pDuration = (TextView) convertView
					.findViewById(R.id.duration);
			TextView pArtist = (TextView) convertView.findViewById(R.id.artist);
			CheckBox pCheckbox = (CheckBox) convertView
					.findViewById(R.id.checkBox1);
			viewHolder = new ViewHolder(pImageView, pTitle, pDuration, pArtist,
					pCheckbox);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (flag == 1) {
			viewHolder.checkbox.setVisibility(View.VISIBLE);
		}

		viewHolder.imageView.setImageResource(R.drawable.music_icon);
		viewHolder.title.setText(musicList.get(position).getTitle());
		viewHolder.duration.setText(FormatHelper.formatDuration(musicList.get(
				position).getDuration()));
		viewHolder.artist.setText(musicList.get(position).getArtist());
		viewHolder.checkbox.setChecked(isSelected.get(position));

		return convertView;
	}
}

class ViewHolder {
	public ViewHolder(ImageView pImageView, TextView pTitle,
			TextView pDuration, TextView pArtist, CheckBox pCheckBox) {
		imageView = pImageView;
		title = pTitle;
		duration = pDuration;
		artist = pArtist;
		checkbox = pCheckBox;
	}

	ImageView imageView;
	TextView title;
	TextView duration;
	TextView artist;
	CheckBox checkbox;
}
