package com.hjf.uhf_r2000.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.hjf.r2000.R;
import com.hjf.uhf_r2000.CheckActivity;
import com.hjf.uhf_r2000.InActivity;
import com.hjf.uhf_r2000.OutActivity;

public class ScranFragment extends Fragment implements OnClickListener {
	private Button btn_in, btn_out, btn_check;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_scarn, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		btn_in = (Button) view.findViewById(R.id.btn_in);
		btn_out = (Button) view.findViewById(R.id.btn_out);
		btn_check = (Button) view.findViewById(R.id.btn_check);
		btn_check.setOnClickListener(this);
		btn_in.setOnClickListener(this);
		btn_out.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_in:
			intent=new Intent(getActivity(),InActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_out:
			intent=new Intent(getActivity(),OutActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_check:
			intent=new Intent(getActivity(),CheckActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

}
