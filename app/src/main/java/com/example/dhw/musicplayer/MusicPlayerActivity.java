package com.example.dhw.musicplayer;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import java.util.ArrayList;

public class MusicPlayerActivity extends FragmentActivity implements
		ActionBar.TabListener, OnPageChangeListener {
	private ActionBar mActionBar;
	private ViewPager mViewPager;
	private ViewPageAdapter mViewPageAdapter;
	private ArrayList<Fragment> mFragments;
	private ArrayList<Tab> mTabs;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_musicplayer);
		if (AppContext.getInstance() == null) {
			AppContext.setInstance(new AppContext());
		}
		AppContext.getInstance().setMusicPlayerActivity(this);
		// 获取ActionBar
		mActionBar = getActionBar();
		Log.i("info","actionbar1");
		// 以tab导航的方式
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		Log.i("info","actionbar2");
		// 禁用actionbar标题栏
		mActionBar.setDisplayShowTitleEnabled(false);
		// 禁用图标
		mActionBar.setDisplayUseLogoEnabled(false);
		// 禁用返回键
		mActionBar.setDisplayShowHomeEnabled(false);
		// ActionBar添加tabs
		mTabs = new ArrayList<Tab>();

		@SuppressWarnings("deprecation")
		Tab tab0 = mActionBar.newTab();
		tab0.setText(R.string.tab0_name);
		tab0.setTabListener(this);
		mTabs.add(tab0);
		mActionBar.addTab(tab0);

		Tab tab1 = mActionBar.newTab();
		tab1.setText(R.string.tab1_name);
		tab1.setTabListener(this);
		mTabs.add(tab1);
		mActionBar.addTab(tab1);

		// 获取viewpager
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		// 初始化mFragments
		mFragments = new ArrayList<Fragment>();
		mFragments.add(new NowPlayingFragment());
		mFragments.add(new PlayListFragment());

		// 初始化madapter
		mViewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

		mViewPager.setAdapter(mViewPageAdapter);
		mViewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	// 监听正被选中的页面，然后设置当前的fragment和tab
	@Override
	public void onPageSelected(int arg0) {
		mViewPager.setCurrentItem(arg0);
		mActionBar.selectTab(mTabs.get(arg0));
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

	}

	// 另一种情况：tab被选中，就显示对应的fragment
	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		if (mViewPager != null) {
			System.out.println("position" + arg0.getPosition());
			mViewPager.setCurrentItem(arg0.getPosition());
		}
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

	}

	public void changeTab(int fragment) {
		mViewPager.setCurrentItem(fragment);
		mActionBar.selectTab(mTabs.get(fragment));
	}
	

@Override
public void onBackPressed() { 
    //实现BACK键效果:下一次打开显示动画
    //super.onBackPressed();
    Intent i= new Intent(Intent.ACTION_MAIN); 
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    i.addCategory(Intent.CATEGORY_HOME); 
    startActivity(i);  
}
}
