package com.example.dhw.musicplayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.dhw.musicplayer.MusicLoader.MusicInfo;

import java.util.List;



public class MusicList {
	private static SQLiteDatabase db;
	public  static void getMusicList(List<MusicInfo> musicList,MyDatabaseHelper dbHelper){
		
		
		db = dbHelper.getWritableDatabase();
		
		Cursor cursor = db.query("musicinfo", null, null, null, null, null,
				"title");
		if (cursor.moveToFirst()) {
			do {
				String title = cursor.getString(cursor.getColumnIndex("title"));
				String album = cursor.getString(cursor.getColumnIndex("album"));
				long id = cursor.getLong(cursor.getColumnIndex("id"));
				int duration = cursor.getInt(cursor.getColumnIndex("duration"));
				long size = cursor.getLong(cursor.getColumnIndex("size"));
				String artist = cursor.getString(cursor
						.getColumnIndex("artist"));
				String url = cursor.getString(cursor.getColumnIndex("url"));

				MusicInfo musicInfo = new MusicInfo(id, title);
				musicInfo.setAlbum(album);
				musicInfo.setDuration(duration);
				musicInfo.setSize(size);
				musicInfo.setArtist(artist);
				musicInfo.setUrl(url);
				musicList.add(musicInfo);
			} while (cursor.moveToNext());
		}
		
	}

}
