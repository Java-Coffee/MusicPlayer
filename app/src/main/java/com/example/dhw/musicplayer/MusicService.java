package com.example.dhw.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

//service主要负责功能的实现
//broadcast主要负责通知主界面做一些对应的UI操作
//（broadcast收到广播后根据intent的内容利用handler更新UI）
public class MusicService extends Service {

	MediaPlayer mediaPlayer;
	String mpath = "";

	private final static int TIME = 10002;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TIME: {
				String currentTime = mediaPlayer.getCurrentPosition() / 1000
						+ "";
				Intent sendintent = new Intent();
				sendintent
						.setAction("com.example.musicplayertest.MUSIC_REVICER");
				sendintent.putExtra("msg", "updateStartTime");
				sendintent.putExtra("currentTime", currentTime);
				sendBroadcast(sendintent);
				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intnet) {
		return new musicBinder();
	}

	public class musicBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	// 首先获取当前的播放路径，如果获取的路径不是现有的路径，则发起广播，更新UI上的播放曲目的整体时间
	// 再获取当前的intent传来的msg，是开始播放还是暂停。根据这些进行操作
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String path = intent.getStringExtra("path");
		String msg = intent.getStringExtra("msg");
		if (!mpath.equals(path)) {
			mpath = path;
			System.out.println("service mpath:" + mpath);
			initMediaPlayer();
			String totalTime = getTotalTime();
			Log.d("service ", "total time" + totalTime);
			Intent sendintent = new Intent();
			sendintent.setAction("com.example.musicplayertest.MUSIC_REVICER");
			sendintent.putExtra("msg", "updateEndTime");
			sendintent.putExtra("totalTime", totalTime);
			sendBroadcast(sendintent);
		}

		if (msg.equals("play") || msg.equals("previous") || msg.equals("next")) {
			start();

		} else if (msg.equals("pause")) {
			pause();

		}

		// 播放结束
		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer arg0) {
						Intent sendintent = new Intent();
						sendintent
								.setAction("com.example.musicplayertest.MUSIC_REVICER");
						sendintent.putExtra("msg", "complete");
						sendBroadcast(sendintent);
						
					}
				});

		return super.onStartCommand(intent, flags, startId);
	}

	// 播放暂停
	private void pause() {
		// TODO Auto-generated method stub
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			Intent sendintent = new Intent();
			sendintent.setAction("com.example.musicplayertest.MUSIC_REVICER");
			sendintent.putExtra("msg", "pause");
			sendBroadcast(sendintent);
			getCurrentTime();
		}
	}

	// 播放开始
	public void start() {
		if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
			mediaPlayer.start();
			Intent sendintent = new Intent();
			sendintent.setAction("com.example.musicplayertest.MUSIC_REVICER");
			sendintent.putExtra("msg", "start");
			sendBroadcast(sendintent);
			Message message = new Message();
			message.what = TIME;
		}
	}

	// 获取歌曲的总长度
	private String getTotalTime() {
		int time = mediaPlayer.getDuration() / 1000;

		String resultTime = "%02d:%02d";

		resultTime = String.format(resultTime, time / 60, time % 60);

		return resultTime;
	}

	private void format(String timeTemp, int i, int j) {
	}

	// 初始化MediaPlay
	public void initMediaPlayer() {
		try {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.release();
			}
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(mpath);
			mediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取当前的时间
	String getCurrentTime() {
		String time = "%02d:%02d";
		time = String.format(time,
				mediaPlayer.getCurrentPosition() / 1000 / 60,
				mediaPlayer.getCurrentPosition() / 1000 % 60);
		return time;
	}

	// 如果服务结束，则关掉音乐，并且释放资源
	@Override
	public void onDestroy() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			// 释放mediaPlayer对象所占有的资源
			mediaPlayer.release();
		}
		super.onDestroy();
	}
}
