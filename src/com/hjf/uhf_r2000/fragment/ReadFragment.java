package com.hjf.uhf_r2000.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjf.r2000.R;
import com.hjf.uhf_r2000.adapter.ContentAdapter;
import com.hjf.uhf_r2000.hardware.assist.UhfReadListener;
import com.hjf.uhf_r2000.hardware.assist.UhfSharedPreferenceUtil;
import com.hjf.uhf_r2000.hardware.function.UhfRead;
import com.hjf.uhf_r2000.model.PojoCard;
import com.hjf.util.DialogUtil;
import com.hjf.util.ToneUtil;

public class ReadFragment extends Fragment {
	private ListView mLvContent;
	private TextView mTvCardCount;
	private Button mBtnScan, mBtnClear;
	private UhfRead mUhfRead;
	private Context mCtx;
	private Map<String, Integer> mMapContent;
	private ContentAdapter mAdapter;
	private List<PojoCard> mArrCard;
	private int mVoice = UhfSharedPreferenceUtil.VOICE_SYSTEM;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mCtx = getActivity();
		View view = inflater.inflate(R.layout.fragment_read, null);
		mLvContent = (ListView) view.findViewById(R.id.lvContent);
		mTvCardCount = (TextView) view.findViewById(R.id.tvCardCount);
		mBtnScan = (Button) view.findViewById(R.id.btnScan);
		mBtnClear = (Button) view.findViewById(R.id.btnClear);

		mBtnScan.setOnClickListener(listener);
		mBtnClear.setOnClickListener(listener);

		if (mUhfRead == null) {
			mUhfRead = new UhfRead(new UhfReadListener() {
				@Override
				public void onErrorCaughted(String error) {
				}

				@Override
				public void onContentCaughted(Object[] obj) {
					Message.obtain(mHandler, MSG_CONTENT, obj).sendToTarget();
				}
			});
		}
		// 设置读信息参数
		mUhfRead.setmMem((byte) UhfSharedPreferenceUtil.getInstance(mCtx)
				.getMem_read());
		mUhfRead.setmWordPtr((byte) UhfSharedPreferenceUtil.getInstance(mCtx)
				.getStartAddr_read());
		mUhfRead.setmNum((byte) UhfSharedPreferenceUtil.getInstance(mCtx)
				.getNum_read());
		mVoice = UhfSharedPreferenceUtil.getInstance(mCtx).getVoice();

		mMapContent = new HashMap<String, Integer>();

		mArrCard = new ArrayList<PojoCard>();
		mAdapter = new ContentAdapter(mCtx, mArrCard);
		mLvContent.setAdapter(mAdapter);

		return view;
	}

	private String content;
	private final int MSG_CONTENT = 1;
	private final int MSG_CLEAN = 2;
	private final int MSG_ERROR = 3;
	private Handler mHandler = new Handler() {
		private ProgressDialog mDialog;

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CONTENT:
				String[] contents = (String[]) (msg.obj);
				synchronized (mMapContent) {
					for (int i = 0; i < contents.length; i++) {
						content = contents[i];
						if (mMapContent.containsKey(content)) {
							int count = Integer.parseInt(mMapContent.get(
									content).toString());
							count++;
							mMapContent.put(content, count);
						} else {
							mMapContent.put(content, 1);
						}
					}

					updatemArrCard(mMapContent);
					mAdapter.notifyDataSetChanged();
				}

				mTvCardCount.setText(mArrCard.size() + "");
				playTone();
				break;
			case MSG_CLEAN:
				clear();
				mDialog = (ProgressDialog) msg.obj;
				mDialog.dismiss();
				Toast.makeText(mCtx, "数据处理成功", Toast.LENGTH_LONG).show();
				break;
			case MSG_ERROR:
				mDialog = (ProgressDialog) msg.obj;
				mDialog.dismiss();
				Toast.makeText(mCtx, "数据库连接失败，请检查网络是否连接正确", Toast.LENGTH_LONG)
						.show();
				break;

			default:
				break;
			}
		};
	};
	private void playTone() {
		switch (mVoice) {
		case UhfSharedPreferenceUtil.VOICE_SYSTEM:
			// 使用系统声音
			ToneUtil.getInstace(mCtx).play(ToneUtil.TYPE_SYSTEM);
			break;
		case UhfSharedPreferenceUtil.VOICE_CUSTOM:
			// 使用自定义声音（播放声音文件）
			ToneUtil.getInstace(mCtx).play(ToneUtil.TYPE_CUSTOM);
			break;

		default:
			break;
		}
	}

	private boolean isStart = false;
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnScan:
				String text = mBtnScan.getText().toString().trim();
				if (text.equalsIgnoreCase("Scan")) {
					mBtnScan.setText("Stop");
//				// 测试数据
//					PojoCard pojoCard;
//					for (int i = 0; i < 10; i++) {
//						pojoCard = new PojoCard();
//						pojoCard.setContent("3072c9f693ce3f2007b1d0cb");
//						pojoCard.setCount(2);
//						mArrCard.add(pojoCard);
//					}

					if (!isStart) {
						mUhfRead.start();
						isStart = true;
					} else {
						mUhfRead.reStart();
					}
				} else {
					mBtnScan.setText("Scan");
					mUhfRead.pause();
					if (mArrCard.size() > 0) {
						DialogUtil dialog = new DialogUtil(mCtx, mArrCard,
								mHandler);
						// 弹出提示对话框
						dialog.dialog();
					}
				}
				break;
			case R.id.btnClear:
				clear();
				break;

			default:
				break;
			}
		}
	};

	private void clear() {
		if (mMapContent == null || mArrCard == null || mAdapter == null
				|| mTvCardCount == null) {
			return;
		}

		synchronized (mMapContent) {
			mMapContent.clear();
			mArrCard.clear();
			mAdapter.notifyDataSetChanged();
		}
		mTvCardCount.setText(mArrCard.size() + "");
	}

	/**
	 * @todo 更新 mArrCard 内容
	 * @param map
	 */
	private void updatemArrCard(Map<String, Integer> map) {
		if (map == null) {
			return;
		}

		mArrCard.clear();
		for (Entry entry : map.entrySet()) {
			String content = entry.getKey().toString();
			int count = Integer.parseInt(entry.getValue().toString());
			mArrCard.add(new PojoCard(content, count));
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		ToneUtil.getInstace(mCtx).release();
		if (mUhfRead != null) {
			mUhfRead.destroy();
			mUhfRead = null;
		}

		super.onDestroyView();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (mUhfRead == null) {
				mUhfRead = new UhfRead(new UhfReadListener() {
					@Override
					public void onErrorCaughted(String error) {
					}

					@Override
					public void onContentCaughted(Object[] obj) {
						Message.obtain(mHandler, MSG_CONTENT, obj)
								.sendToTarget();
					}
				});
			}

			if (mCtx == null) {
				mCtx = getActivity();
			}
			// 设置读信息参数
			mUhfRead.setmMem((byte) UhfSharedPreferenceUtil.getInstance(mCtx)
					.getMem_read());
			mUhfRead.setmWordPtr((byte) UhfSharedPreferenceUtil.getInstance(
					mCtx).getStartAddr_read());
			mUhfRead.setmNum((byte) UhfSharedPreferenceUtil.getInstance(mCtx)
					.getNum_read());
			mVoice = UhfSharedPreferenceUtil.getInstance(mCtx).getVoice();

			isStart = false;
			clear();
			if (mBtnScan != null) {
				mBtnScan.setText("Scan");
			}

		} else {
			if (mUhfRead != null) {
				mUhfRead.destroy();
				mUhfRead = null;
			}
		}
	}

}
