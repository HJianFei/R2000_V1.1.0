package com.hjf.uhf_r2000;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.hjf.r2000.R;
import com.hjf.uhf_r2000.adapter.FragmentAdapter;
import com.hjf.uhf_r2000.constants.Constants;
import com.hjf.uhf_r2000.fragment.ScranFragment;
import com.hjf.uhf_r2000.fragment.SetFragment;
import com.hjf.uhf_r2000.fragment.WriteFragment;
import com.hjf.uhf_r2000.hardware.function.UhfComm;
import com.hjf.util.SharedPreferencesUtils;

public class MainActivity extends FragmentActivity implements
		OnPageChangeListener, OnCheckedChangeListener {
	private ViewPager mViewPager;
	private FragmentAdapter mAdapter;
	private List<Fragment> mFragments;
	private RadioGroup mRadioGroup;
	private UhfComm mUhfComm;
	private ActivityManager mActivityManager;
	private static final String BARCODE_PROCESS = "com.hyipc.service.barcode";
	private boolean isHasBarcodeService = false;
	private List<RadioButton> mRadioButtons = null;
	private RadioButton mRbRead, mRbWrite, mRbSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();

		mActivityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> services = mActivityManager
				.getRunningServices(100);
		for (int i = 0; i < services.size(); i++) {
			ActivityManager.RunningServiceInfo serviceInfo = services.get(i);
			String process = serviceInfo.process;
			if (process.contentEquals(BARCODE_PROCESS)) {
				// BarcodeService正在运行，向服务发送关闭串口的广播
				isHasBarcodeService = true;
				IntentFilter filter = new IntentFilter();
				filter.addAction(ACTION_CLOSE_SUCC);
				registerReceiver(portReceive, filter);
				this.sendBroadcast(new Intent("portOff"));
				return;
			}
		}

		// BarcodeService未安装，直接打开串口
		boolean succ = mUhfComm.init(MainActivity.this);
		if (!succ) {
			Toast.makeText(MainActivity.this, "UHF init fail",
					Toast.LENGTH_LONG).show();
			return;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_ip_address) {
			final EditText editText = new EditText(this);
			editText.setHint("服务器IP:example.example.com");
			editText.setTextSize(20);
			new AlertDialog.Builder(this).setTitle("设置服务器IP地址")
					.setIcon(R.drawable.ic_launcher).setView(editText)
					.setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							SharedPreferencesUtils.setParam(MainActivity.this,
									Constants.DB_ADDRESS, editText.getText()
											.toString().trim());
							Toast.makeText(MainActivity.this, "地址设置成功",
									Toast.LENGTH_SHORT).show();
						}
					}).setNegativeButton("取消", null).show();
			return true;
		}
		if (id == R.id.now_ip_address) {
			new AlertDialog.Builder(this)
					.setTitle("当前服务器IP地址")
					.setIcon(R.drawable.ic_launcher)
					.setMessage(
							(String) SharedPreferencesUtils.getParam(
									MainActivity.this, Constants.DB_ADDRESS,
									Constants.DEFAULT_DB_ADDRESS))
					.setPositiveButton("确定", null).show();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void init() {
		mUhfComm = new UhfComm();

		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		mRbRead = (RadioButton) findViewById(R.id.rbRead);
		mRbWrite = (RadioButton) findViewById(R.id.rbWrite);
		mRbSetting = (RadioButton) findViewById(R.id.rbSet);
		mRadioButtons = new ArrayList<RadioButton>();
		mRadioButtons.add(mRbRead);
		mRadioButtons.add(mRbWrite);
		mRadioButtons.add(mRbSetting);

		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mFragments = new ArrayList<Fragment>();
		mFragments.add(new ScranFragment());
		mFragments.add(new WriteFragment());
		mFragments.add(new SetFragment());
		mAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragments);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(0);

		mRadioGroup.setOnCheckedChangeListener(this);
		mViewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rbRead:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.rbWrite:
			mViewPager.setCurrentItem(1);
			break;
		case R.id.rbSet:
			mViewPager.setCurrentItem(2);
			break;
		default:
			break;
		}

	}

	@Override
	public void onPageSelected(int arg0) {
		mRadioButtons.get(arg0).setChecked(true);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	private final long INVER_TIME = 1000;
	private long lastTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long currTime = System.currentTimeMillis();
			if (currTime - lastTime < INVER_TIME) {
				this.finish();
			} else {
				Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_LONG).show();
				lastTime = currTime;
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 虚拟机KO整个应用进程，目的：杀死readThread的引用
		mUhfComm.unInit();
		if (isHasBarcodeService) {
			this.sendBroadcast(new Intent("portOn"));
			unregisterReceiver(portReceive);
		}
		portReceive = null;
		System.exit(0);
	}

	private final String ACTION_CLOSE_SUCC = "portCloseSucc";
	BroadcastReceiver portReceive = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			Log.d("BARCODE_SERVICE", "onReceive");
			String action = intent.getAction();
			if (TextUtils.isEmpty(action)) {
				return;
			}

			if (ACTION_CLOSE_SUCC.equalsIgnoreCase(action)) {
				// 条码服务关闭串口成功
				boolean succ = mUhfComm.init(MainActivity.this);
				if (!succ) {
					Toast.makeText(MainActivity.this, "UHF init fail",
							Toast.LENGTH_LONG).show();
					return;
				}
			}
		}
	};

}
