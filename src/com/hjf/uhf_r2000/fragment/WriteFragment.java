package com.hjf.uhf_r2000.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hjf.r2000.R;
import com.hjf.uhf_r2000.hardware.function.UhfWrite;

public class WriteFragment extends Fragment {
	private RadioGroup mRgMem;
	private EditText mEtAddr, mEtData;
	private Button mBtnWrite;
	private TextView mTvInfo;
	private UhfWrite mWrite;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_write, null);
		mRgMem = (RadioGroup) view.findViewById(R.id.rgMem);
		mEtAddr = (EditText) view.findViewById(R.id.etAddr);
		mEtData = (EditText) view.findViewById(R.id.etData);
		mBtnWrite = (Button) view.findViewById(R.id.btnWrite);
		mTvInfo = (TextView) view.findViewById(R.id.tvInfo);

		mBtnWrite.setOnClickListener(onClickListener);

		mWrite = new UhfWrite();
		return view;
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnWrite:
				mTvInfo.setText("");
				int checkId = mRgMem.getCheckedRadioButtonId();
				byte bMem = 0;
				switch (checkId) {
				case R.id.rbReser:
					bMem = 0;
					break;
				case R.id.rbEpc:
					bMem = 1;
					break;
				case R.id.rbUser:
					bMem = 3;
					break;
				default:
					break;
				}

				byte bAddr = 0;
				String strAddr = mEtAddr.getText().toString().trim();
				if (TextUtils.isEmpty(strAddr)) {
					mTvInfo.setText("please input the start address for writing");
					mTvInfo.setTextColor(Color.RED);
					break;
				} else {
					bAddr = Byte.parseByte(strAddr);
				}

				String strData = mEtData.getText().toString().trim();
				if (TextUtils.isEmpty(strData)) {
					mTvInfo.setText("please input the data for writing");
					mTvInfo.setTextColor(Color.RED);
					break;
				}

				mWrite.setmMem(bMem);
				mWrite.setmWordPtr(bAddr);
				mWrite.setmContent(strData);

				boolean isSucc = mWrite.doWrite();
				if (isSucc) {
					mTvInfo.setText("write succ");
					mTvInfo.setTextColor(0xFF005500);
				} else {
					mTvInfo.setText("write fail");
					mTvInfo.setTextColor(Color.RED);
				}

				break;

			default:
				break;
			}
		}
	};
}
