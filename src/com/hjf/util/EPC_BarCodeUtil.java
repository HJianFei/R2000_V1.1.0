package com.hjf.util;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import com.hjf.uhf_r2000.model.EPC2BarCode;
import com.hjf.uhf_r2000.model.PojoCard;

public class EPC_BarCodeUtil {

	private List<PojoCard> mPojoCard;
	private List<EPC2BarCode> mEPC2BarCode;
	private Handler mHandler;
	private ProgressDialog mDialog;
	private Context mContext;

	public EPC_BarCodeUtil(Context mContext, List<PojoCard> mPojoCard,
			Handler mHandler, ProgressDialog mDialog) {

		this.mPojoCard = mPojoCard;
		this.mHandler = mHandler;
		this.mDialog = mDialog;
		this.mContext = mContext;
		mEPC2BarCode = new ArrayList<EPC2BarCode>();
	}

	public void getEpc2carcode() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String barcode = "0000000";
				String serial = "0000000";
				String companyPrefix;
				String itemReference;
				String decoderString = null;
				String a;
				String b;
				EPC2BarCode mBarCode;
				for (int j = 0; j < mPojoCard.size(); j++) {
					String epc = mPojoCard.get(j).getContent();
					// 如果epc开头是30就转换
					if (epc.startsWith("30")) {
						String binaryepc = hexString2binaryString(epc);
						String string = binaryepc.substring(11, 14);
						// 2进制转10进制
						int d = Integer.parseInt(string, 2);
						if (d == 0) {
							// 40 4 12 1
							companyPrefix = binaryepc.substring(14, 54);
							itemReference = binaryepc.substring(54, 58);
							a = Long.parseLong(companyPrefix, 2) + "";
							b = Long.parseLong(itemReference, 2) + "";
							StringBuffer sb = new StringBuffer();
							if (a.length() != 12) {
								for (int i = 0; i < 12 - a.length(); i++) {
									sb.append("0");
								}
								a = sb.toString() + a;
								sb.setLength(0);
							}
							if (b.length() != 1) {
								for (int i = 0; i < 1 - b.length(); i++) {
									sb.append("0");
								}
								b = sb.toString() + b;
							}
							decoderString = a + "." + b; // 2进制转10进制
						} else if (d == 1) {
							// 37 7 11 2
							companyPrefix = binaryepc.substring(14, 51);
							itemReference = binaryepc.substring(51, 58);
							a = Long.parseLong(companyPrefix, 2) + "";
							b = Long.parseLong(itemReference, 2) + "";
							StringBuffer sb = new StringBuffer();
							if (a.length() != 11) {
								for (int i = 0; i < 11 - a.length(); i++) {
									sb.append("0");
								}
								a = sb.toString() + a;
								sb.setLength(0);
							}
							if (b.length() != 2) {
								for (int i = 0; i < 2 - b.length(); i++) {
									sb.append("0");
								}
								b = sb.toString() + b;
							}
							decoderString = a + "." + b; // 2进制转10进制
						} else if (d == 2) {
							// 34 10 10 3
							companyPrefix = binaryepc.substring(14, 48);
							itemReference = binaryepc.substring(48, 58);
							a = Long.parseLong(companyPrefix, 2) + "";
							b = Long.parseLong(itemReference, 2) + "";
							StringBuffer sb = new StringBuffer();
							if (a.length() != 10) {
								for (int i = 0; i < 10 - a.length(); i++) {
									sb.append("0");
								}
								a = sb.toString() + a;
								sb.setLength(0);
							}
							if (b.length() != 3) {
								for (int i = 0; i < 3 - b.length(); i++) {
									sb.append("0");
								}
								b = sb.toString() + b;
							}
							decoderString = a + "." + b; // 2进制转10进制
						} else if (d == 3) {
							// 30 14 9 4
							companyPrefix = binaryepc.substring(14, 44);
							itemReference = binaryepc.substring(44, 58);
							a = Long.parseLong(companyPrefix, 2) + "";
							b = Long.parseLong(itemReference, 2) + "";
							StringBuffer sb = new StringBuffer();
							if (a.length() != 9) {
								for (int i = 0; i < 9 - a.length(); i++) {
									sb.append("0");
								}
								a = sb.toString() + a;
								sb.setLength(0);
							}
							if (b.length() != 4) {
								for (int i = 0; i < 4 - b.length(); i++) {
									sb.append("0");
								}
								b = sb.toString() + b;
							}
							decoderString = a + "." + b; // 2进制转10进制
						} else if (d == 4) {
							// 27 17 8 5
							companyPrefix = binaryepc.substring(14, 41);
							itemReference = binaryepc.substring(41, 58);
							a = Long.parseLong(companyPrefix, 2) + "";
							b = Long.parseLong(itemReference, 2) + "";
							StringBuffer sb = new StringBuffer();
							if (a.length() != 8) {
								for (int i = 0; i < 8 - a.length(); i++) {
									sb.append("0");
								}
								a = sb.toString() + a;
								sb.setLength(0);
							}
							if (b.length() != 5) {
								for (int i = 0; i < 5 - b.length(); i++) {
									sb.append("0");
								}
								b = sb.toString() + b;
							}
							decoderString = a + "." + b; // 2进制转10进制
						} else if (d == 5) {
							// 24 20 7 6
							companyPrefix = binaryepc.substring(14, 38);
							itemReference = binaryepc.substring(38, 58);
							a = Long.parseLong(companyPrefix, 2) + "";
							b = Long.parseLong(itemReference, 2) + "";
							StringBuffer sb = new StringBuffer();
							if (a.length() != 7) {
								for (int i = 0; i < 7 - a.length(); i++) {
									sb.append("0");
								}
								a = sb.toString() + a;
								sb.setLength(0);
							}
							if (b.length() != 6) {
								for (int i = 0; i < 6 - b.length(); i++) {
									sb.append("0");
								}
								b = sb.toString() + b;
							}
							decoderString = a + "." + b; // 2进制转10进制
						} else if (d == 6) {
							// 20 24 6 7
							companyPrefix = binaryepc.substring(14, 34);
							itemReference = binaryepc.substring(34, 58);
							a = Long.parseLong(companyPrefix, 2) + "";
							b = Long.parseLong(itemReference, 2) + "";
							StringBuffer sb = new StringBuffer();
							if (a.length() != 6) {
								for (int i = 0; i < 6 - a.length(); i++) {
									sb.append("0");
								}
								a = sb.toString() + a;
								sb.setLength(0);
							}
							if (b.length() != 7) {
								for (int i = 0; i < 7 - b.length(); i++) {
									sb.append("0");
								}
								b = sb.toString() + b;
							}
							decoderString = a + "." + b; // 2进制转10进制
						}
						barcode = taguri2gtin14(decoderString);
						serial = binaryepc.substring(58);
						// 2进制转10进制
						serial = Long.parseLong(serial, 2) + "";
						mBarCode = new EPC2BarCode(epc, barcode, serial);
						mEPC2BarCode.add(mBarCode);
					} else {
						// 如果不是epc以30开头，就直接写全0的默认值到数据表
						mBarCode = new EPC2BarCode(epc, "0000000", "0000000");
						mEPC2BarCode.add(mBarCode);
					}
				}
				// for循环结束 启动线程，进行数据插入操作
				DBUtil dbUtil = new DBUtil(mContext, mEPC2BarCode, mHandler,
						mDialog);
				// 连接数据库，插入数据
				dbUtil.getConnection();

			}
		}).start();

	}

	/**
	 * 十六进制转二进制
	 * 
	 * @param hexString
	 * @return
	 */
	public String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000"
					+ Integer.toBinaryString(Integer.parseInt(
							hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}

	/**
	 * 计算barcode
	 * 
	 * @param taguri
	 * @return
	 */
	public String taguri2gtin14(String taguri) {
		String decode13;
		String[] decoderarray = taguri.substring(0, taguri.length()).split(
				"\\.");
		decode13 = decoderarray[1].substring(0, 1) + decoderarray[0]
				+ decoderarray[1].substring(1); // decoder length=13[0-12] no
		// check
		int sumj = 0;
		int sumo = 0;
		for (int i = 0; i < 13; i += 2) {
			sumo += decode13.charAt(i) - '0';
		}
		for (int i = 1; i < 13; i += 2) {
			sumj += decode13.charAt(i) - '0';
		}
		int check = (10 - (sumj + sumo * 3) % 10) % 10;
		return decode13 + Integer.toString(check);
	}

}
