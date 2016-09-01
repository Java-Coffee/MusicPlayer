package com.example.dhw.musicplayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dhw.musicplayer.MusicLoader.MusicInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NowPlayingFragment extends Fragment implements OnClickListener {

	int position;
	int mode = LOOP_MODE;

	String path = "";
	String name = "null";
	String endTime = "00:00";

	Boolean isUpdate = false;

	private TextView startTimeText, endTimeText, musicName;
	private Button play, pause, next, previous, single, loop, random;
	private SeekBar seekBar;

	private MusicService musicService;
	private MusicRevicer musicRevicer;
	private List<MusicInfo> musicList;
	private MyDatabaseHelper dbHelper;

	private final static int UPDATE_END_TIME = 9999;
	private final static int UPDATE_START_TIME = 9998;
	private final static int UPDATE_START_BUTTON = 10000;
	private final static int UPDATE_PAUSE_BUTTON = 10001;
	private final static int COMPLETE = 10002;
	private final static int SINGLE_MODE = 10003;
	private final static int LOOP_MODE = 10004;
	private final static int RANDOM_MODE = 10005;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_nowplaying, null);

		// 缁戝畾鏈嶅姟
		Intent intent = new Intent(getActivity(), MusicService.class);
		getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);

		Intent intent2 = getActivity().getIntent();
		path = intent2.getStringExtra("path");
		name = intent2.getStringExtra("name");

		startTimeText = (TextView) view.findViewById(R.id.startTime);
		endTimeText = (TextView) view.findViewById(R.id.endTime);
		musicName = (TextView) view.findViewById(R.id.musicName);
		play = (Button) view.findViewById(R.id.play);
		next = (Button) view.findViewById(R.id.next);
		previous = (Button) view.findViewById(R.id.previous);
		pause = (Button) view.findViewById(R.id.pause);
		single = (Button) view.findViewById(R.id.singleMode);
		loop = (Button) view.findViewById(R.id.loopMode);
		random = (Button) view.findViewById(R.id.randomMode);
		seekBar = (SeekBar) view.findViewById(R.id.seekBar);

		musicList = new ArrayList<MusicLoader.MusicInfo>();
		dbHelper = new MyDatabaseHelper(getActivity(), "MusicList.db", null, 1);

		musicList.clear();
		MusicList.getMusicList(musicList, dbHelper);

		if (musicList.toString() != "[]") {
			path = musicList.get(0).getUrl();
			name = musicList.get(0).getTitle();
			endTime = "%02d:%02d";
			endTime = String.format(endTime,
					musicList.get(0).getDuration() / 1000 / 60, musicList
							.get(0).getDuration() / 1000 % 60);

			// musicName.setText(name);
			Intent serviceintent = new Intent(getActivity(), MusicService.class);
			serviceintent.putExtra("path", path);
			serviceintent.putExtra("msg", "pause");
			getActivity().startService(serviceintent);

		}

		Message message = new Message();
		message.what = UPDATE_END_TIME;
		handler.sendMessage(message);

		// musicName.setText(name);

		play.setOnClickListener(this);
		pause.setOnClickListener(this);
		next.setOnClickListener(this);
		previous.setOnClickListener(this);
		single.setOnClickListener(this);
		loop.setOnClickListener(this);
		random.setOnClickListener(this);

		// 娉ㄥ唽骞挎挱
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.example.musicplayertest.MUSIC_REVICER");
		musicRevicer = new MusicRevicer();
		getActivity().registerReceiver(musicRevicer, intentFilter);

		OnSeekBarChangeListener seekbarListener = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

				int toTime = arg0.getProgress()
						* musicService.mediaPlayer.getDuration()
						/ arg0.getMax();

				musicService.mediaPlayer.seekTo(toTime);
				musicService.start();

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub

			}
		};
		seekBar.setOnSeekBarChangeListener(seekbarListener);

		return view;
	}

	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			musicService = ((MusicService.musicBinder) service).getService();
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_END_TIME: {
				musicName.setText(name);
				musicName.setGravity(Gravity.CENTER);
				endTimeText.setText(endTime);
				break;
			}
			case UPDATE_START_TIME: {
				if (isUpdate) {
					startTimeText.setText(musicService.getCurrentTime());
					seekBar.setProgress(seekBar.getMax()
							* musicService.mediaPlayer.getCurrentPosition()
							/ musicService.mediaPlayer.getDuration());
					Message message = new Message();
					message.what = UPDATE_START_TIME;
					handler.sendMessageDelayed(message, 1000);
				}
				break;
			}
			case UPDATE_START_BUTTON: {
				play.setVisibility(View.INVISIBLE);
				pause.setVisibility(View.VISIBLE);
				break;
			}
			case UPDATE_PAUSE_BUTTON: {
				play.setVisibility(View.VISIBLE);
				pause.setVisibility(View.INVISIBLE);
				isUpdate = false;
				break;
			}
			case COMPLETE: {
				if (mode == SINGLE_MODE) {
					play();
				} else if (mode == LOOP_MODE) {
					next();
				} else if (mode == RANDOM_MODE) {
					next();
				}

				break;
			}
			default:
				break;
			}
		}
	};

	@SuppressLint("ShowToast")
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play: {
			play();
			break;
		}
		case R.id.pause: {
			Intent intent = new Intent(getActivity(), MusicService.class);
			intent.putExtra("path", path);
			intent.putExtra("msg", "pause");
			getActivity().startService(intent);
			break;
		}
		case R.id.next: {
			next();
			break;
		}
		case R.id.previous: {
			previous();
			break;
		}
		case R.id.loopMode: {
			loop.setVisibility(View.INVISIBLE);
			single.setVisibility(View.VISIBLE);
			mode = SINGLE_MODE;
			Toast toast = new Toast(getActivity());
			toast = Toast.makeText(musicService, R.string.singleMode,
					Toast.LENGTH_SHORT);
			 toast.setGravity(Gravity.TOP|Gravity.LEFT,0, 150);
			 toast.show();
			break;
		}
		case R.id.singleMode: {
			single.setVisibility(View.INVISIBLE);
			random.setVisibility(View.VISIBLE);
			mode = RANDOM_MODE;
			Toast toast = new Toast(getActivity());
			toast = Toast.makeText(musicService, R.string.randomMode,
					Toast.LENGTH_SHORT);
			 toast.setGravity(Gravity.TOP|Gravity.LEFT,0, 150);
			 toast.show();
			break;
		}
		case R.id.randomMode: {
			random.setVisibility(View.INVISIBLE);
			loop.setVisibility(View.VISIBLE);
			mode = LOOP_MODE;
			Toast toast = new Toast(getActivity());
			toast = Toast.makeText(musicService, R.string.loopMode,
					Toast.LENGTH_SHORT);
			 toast.setGravity(Gravity.TOP|Gravity.LEFT,0, 150);
			 toast.show();

			break;
		}
		default:
			break;
		}
	}

	public class MusicRevicer extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String msg = intent.getStringExtra("msg");
			if (msg.equals("updateEndTime")) {
				endTime = intent.getStringExtra("totalTime");
				Message message = new Message();
				message.what = UPDATE_END_TIME;
				handler.sendMessage(message);
			} else if (msg.equals("updateStartTime")) {
				Message message = new Message();
				message.what = UPDATE_START_TIME;
				handler.sendMessage(message);
			} else if (msg.equals("start")) {
				Message message = new Message();
				message.what = UPDATE_START_BUTTON;
				handler.sendMessage(message);
				isUpdate = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						Message message = new Message();
						message.what = UPDATE_START_TIME;
						handler.sendMessage(message);
					}
				}).start();
			} else if (msg.equals("pause")) {
				Message message = new Message();
				message.what = UPDATE_PAUSE_BUTTON;
				handler.sendMessage(message);
			} else if (msg.equals("complete")) {
				Message message = new Message();
				message.what = COMPLETE;
				handler.sendMessage(message);
			} else if (msg.equals("listPlay")) {
				path = intent.getStringExtra("path");
				name = intent.getStringExtra("name");
				position = intent.getIntExtra("positon", 0);
				Intent serviceintent = new Intent(getActivity(),
						MusicService.class);
				serviceintent.putExtra("path", path);
				serviceintent.putExtra("msg", "play");
				getActivity().startService(serviceintent);
			}
		}

	}

	private void play() {
		// TODO Auto-generated method stub
		if (path != null) {
			Intent intent = new Intent(getActivity(), MusicService.class);
			intent.putExtra("path", path);
			intent.putExtra("msg", "play");
			getActivity().startService(intent);
		} else {
			musicList.clear();
			MusicList.getMusicList(musicList, dbHelper);

			if (musicList.toString() != "[]") {
				path = musicList.get(0).getUrl();
				name = musicList.get(0).getTitle();
				musicName.setText(name);
				Intent intent = new Intent(getActivity(), MusicService.class);
				intent.putExtra("path", path);
				intent.putExtra("msg", "play");
				getActivity().startService(intent);
			} else {
				AppContext.getInstance().getMusicPlayerActivity().changeTab(1);
				Toast.makeText(getActivity(), R.string.select,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void next() {
		// TODO Auto-generated method stub
		musicList.clear();
		MusicList.getMusicList(musicList, dbHelper);

		if (mode == RANDOM_MODE) {
			int tempPosition = 0;
			System.out.println("positon = " + position);
			while (true) {
				tempPosition = new Random().nextInt(musicList.size());
				System.out.println("temppositon = " + tempPosition);

				if (position != tempPosition) {
					position = tempPosition;
					System.out.println("------>positon = " + position);
					break;
				}
			}
		} else if ((position + 1) < musicList.size()) {
			position = position + 1;

		} else {
			position = 0;
		}
		path = musicList.get(position).getUrl();
		name = musicList.get(position).getTitle();
		musicName.setText(name);

		Intent intent = new Intent(getActivity(), MusicService.class);
		intent.putExtra("path", path);
		intent.putExtra("msg", "next");
		getActivity().startService(intent);
	}

	private void previous() {
		// TODO Auto-generated method stub
		musicList.clear();
		musicList = new ArrayList<MusicLoader.MusicInfo>();
		// dbHelper = new MyDatabaseHelper(getActivity(), "MusicList.db",
		// null, 1);

		MusicList.getMusicList(musicList, dbHelper);

		if ((position - 1) >= 0) {
			position = position - 1;

		} else {
			position = musicList.size() - 1;
		}

		path = musicList.get(position).getUrl();
		name = musicList.get(position).getTitle();
		musicName.setText(name);

		Intent intent = new Intent(getActivity(), MusicService.class);
		intent.putExtra("path", path);
		intent.putExtra("msg", "previous");
		getActivity().startService(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(musicRevicer);
	}
}
