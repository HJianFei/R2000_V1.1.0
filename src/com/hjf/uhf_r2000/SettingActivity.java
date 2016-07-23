package com.hjf.uhf_r2000;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hjf.r2000.R;
import com.hjf.uhf_r2000.constants.Constants;
import com.hjf.util.SharedPreferencesUtils;

public class SettingActivity extends Activity {

	Button btn_set_ip, btn_the_ip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		btn_set_ip = (Button) findViewById(R.id.btn_set_ip);
		btn_the_ip = (Button) findViewById(R.id.btn_the_ip);
		btn_set_ip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final EditText editText = new EditText(SettingActivity.this);
				editText.setHint("服务器IP:example.example.com");
				editText.setTextSize(20);
				new AlertDialog.Builder(SettingActivity.this)
						.setTitle("设置服务器IP地址")
						.setIcon(R.drawable.ic_launcher)
						.setView(editText)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										// TODO Auto-generated method stub

										SharedPreferencesUtils.setParam(
												SettingActivity.this,
												Constants.DB_ADDRESS, editText
														.getText().toString()
														.trim());
										Toast.makeText(SettingActivity.this,
												"地址设置成功", Toast.LENGTH_SHORT)
												.show();
										finish();
									}
								}).setNegativeButton("取消", null).show();
			}
		});
		btn_the_ip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(SettingActivity.this)
						.setTitle("当前服务器IP地址")
						.setIcon(R.drawable.ic_launcher)
						.setMessage(
								(String) SharedPreferencesUtils.getParam(
										SettingActivity.this,
										Constants.DB_ADDRESS,
										Constants.DEFAULT_DB_ADDRESS))
						.setPositiveButton("确定", null).show();

			}
		});

	}
}
