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
 * 对话框
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
		builder.setMessage("一共扫描到: " + mArrCard.size() + " 件货物\n确认校对吗？");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("提示");
		builder.setCancelable(false);

		builder.setPositiveButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ProgressDialog progressDialog = progressDialog();
				progressDialog.show();
				// 启动转码线程进行转码
				EPC_BarCodeUtil mBarCodeUtil = new EPC_BarCodeUtil(mContext,
						mArrCard, mHandler, progressDialog,flag);
				mBarCodeUtil.getEpc2carcode();
			}
		});

		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	public ProgressDialog progressDialog() {

		ProgressDialog mProgressDialog = new ProgressDialog(mContext);
		// 设置进度条风格，风格为圆形，旋转的
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		// 设置ProgressDialog 标题
		mProgressDialog.setTitle("提示");

		// 设置ProgressDialog提示信息
		mProgressDialog.setMessage("正在处理，请稍等...");

		// 设置ProgressDialog标题图标
		mProgressDialog.setIcon(R.drawable.ic_launcher);

		// 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
		mProgressDialog.setIndeterminate(false);

		// 设置ProgressDialog 是否可以按退回键取消
		mProgressDialog.setCancelable(false);
		return mProgressDialog;
	}
}
