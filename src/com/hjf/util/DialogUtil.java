package com.hjf.util;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;

import com.hjf.r2000.R;
import com.hjf.uhf_r2000.model.PojoCard;

/**
 * �Ի���
 * 
 * @author Administrator
 * 
 */
public class DialogUtil {
	private Context mContext;
	private List<PojoCard> mArrCard;
	private Handler mHandler;
	private String flag;

	public DialogUtil(Context mContext, List<PojoCard> mArrCard,
			Handler mHandler, String flag) {
		super();
		this.mContext = mContext;
		this.mArrCard = mArrCard;
		this.mHandler = mHandler;
		this.flag = flag;
	}

	public void dialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setMessage("һ��ɨ�赽: " + mArrCard.size() + " ������\nȷ��У����");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("��ʾ");
		builder.setCancelable(false);

		builder.setPositiveButton("ȷ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ProgressDialog progressDialog = progressDialog();
				progressDialog.show();
				// ����ת���߳̽���ת��
				EPC_BarCodeUtil mBarCodeUtil = new EPC_BarCodeUtil(mContext,
						mArrCard, mHandler, progressDialog,flag);
				mBarCodeUtil.getEpc2carcode();
			}
		});

		builder.setNegativeButton("ȡ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	public ProgressDialog progressDialog() {

		ProgressDialog mProgressDialog = new ProgressDialog(mContext);
		// ���ý�������񣬷��ΪԲ�Σ���ת��
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		// ����ProgressDialog ����
		mProgressDialog.setTitle("��ʾ");

		// ����ProgressDialog��ʾ��Ϣ
		mProgressDialog.setMessage("���ڴ������Ե�...");

		// ����ProgressDialog����ͼ��
		mProgressDialog.setIcon(R.drawable.ic_launcher);

		// ����ProgressDialog �Ľ������Ƿ���ȷ false ���ǲ�����Ϊ����ȷ
		mProgressDialog.setIndeterminate(false);

		// ����ProgressDialog �Ƿ���԰��˻ؼ�ȡ��
		mProgressDialog.setCancelable(false);
		return mProgressDialog;
	}
}
