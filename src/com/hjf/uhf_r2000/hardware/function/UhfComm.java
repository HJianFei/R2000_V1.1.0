package com.hjf.uhf_r2000.hardware.function;

import android.content.Context;

import com.halio.r2000;
import com.hjf.uhf_r2000.hardware.assist.UhfSharedPreferenceUtil;

public class UhfComm {
	/** ¶ÁÐ´Æ÷µØÖ· */
	public static byte[] sAddr = { (byte) 0xFF };

	public boolean init(Context ctx) {
		r2000.ModulePowerOn();
		int baud = UhfSharedPreferenceUtil.getInstance(ctx).getBaud();
		return r2000.ConnectReader(sAddr, (byte) baud);
	}

	public boolean unInit() {
		if (r2000.DisConnectReader()) {
			r2000.ModulePowerOff();
			return true;
		}
		return false;
	}

}
