package com.example.dhw.musicplayer;

public class AppContext {
	private static AppContext instance;

	public static AppContext getInstance() {
		return instance;
	}

	public static void setInstance(AppContext appContext) {
		instance = appContext;
	}

	private MusicPlayerActivity musicPlayerActivity;

	public MusicPlayerActivity getMusicPlayerActivity() {
		return musicPlayerActivity;
	}

	public void setMusicPlayerActivity(MusicPlayerActivity musicPlayerActivity) {
		this.musicPlayerActivity = musicPlayerActivity;
	}
}
