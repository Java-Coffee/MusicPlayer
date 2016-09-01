package com.example.dhw.musicplayer;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPageAdapter extends FragmentPagerAdapter {
	private final static int NowPalying = 0;
	private final static int PalyList = 1;

	public ViewPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment = null;
		switch (arg0) {
		case NowPalying:
			fragment = new NowPlayingFragment();
			break;
		case PalyList:
			fragment = new PlayListFragment();
			break;
		default:
			break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

}
