package com.hjf.uhf_r2000.hardware.function;

import com.halio.r2000;
import com.hjf.uhf_r2000.hardware.assist.UhfMappingRelation;

/**
 * ����Ƶ������
 */
public class UhfSetting {
	/** ������ */
	private byte mBaud;
	/** ���Ƶ�� */
	private byte mMaxFre;
	/** ��СƵ�� */
	private byte mMinFre;
	/** ���� */
	private byte mRfPower;
	/** ѯ��������Ӧʱ�� */
	private int mScanTime;
	/** ���������� */
	private boolean mIsBeepOn;

	public UhfSetting() {
	}

	/**
	 * @todo ���ò�����
	 * @return
	 */
	public boolean updateBaud() {
		if (!r2000.SetBaudRate(UhfComm.sAddr, mBaud)) {
			return false;
		} else {
			// �Ͽ�����
			boolean succ = r2000.DisConnectReader();
			if (succ) {
				// ����
				if (!r2000.ConnectReader(UhfComm.sAddr, mBaud)) {
					return false;
				} else {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * @todo ��������Ƶ��
	 * @return
	 */
	public boolean updateRegion() {
		return r2000.SetRegion(UhfComm.sAddr, mMaxFre, mMinFre);
	}

	/**
	 * @todo ���ö�д������
	 * @return
	 */
	public boolean updateRfPower() {
		return r2000.SetRfPower(UhfComm.sAddr, mRfPower);
	}

	/**
	 * @todo ����ѯ������������Ӧʱ��
	 * @return
	 */
	public boolean updateInventoryScanTime() {
		return r2000.SetInventoryScanTime(UhfComm.sAddr, (byte) mScanTime);
	}

	public boolean updateBeepNotification() {
		if (mIsBeepOn) {
			return openBeep();
		} else {
			return closeBeep();
		}
	}

	/**
	 * @todo �򿪷�����
	 * @return
	 */
	private boolean openBeep() {
		return r2000.SetBeepNotification(UhfComm.sAddr,
				UhfMappingRelation.BEEP_ON);
	}

	/**
	 * @todo �رշ�����
	 * @return
	 */
	private boolean closeBeep() {
		return r2000.SetBeepNotification(UhfComm.sAddr,
				UhfMappingRelation.BEEP_OFF);
	}

	/**
	 * @todo ����������������
	 * @return
	 */
	public boolean updateAll() {
		if (!updateBaud()) {
			return false;
		}

		if (!updateRegion()) {
			return false;
		}

		if (!updateRfPower()) {
			return false;
		}

		if (!updateInventoryScanTime()) {
			return false;
		}

		if (!updateBeepNotification()) {
			return false;
		}

		return true;
	}

	public byte getmBaud() {
		return mBaud;
	}

	public void setmBaud(byte mBaud) {
		this.mBaud = mBaud;
	}

	public byte getmMaxFre() {
		return mMaxFre;
	}

	public void setmMaxFre(byte mMaxFre) {
		this.mMaxFre = mMaxFre;
	}

	public byte getmMinFre() {
		return mMinFre;
	}

	public void setmMinFre(byte mMinFre) {
		this.mMinFre = mMinFre;
	}

	public byte getmRfPower() {
		return mRfPower;
	}

	public void setmRfPower(byte mRfPower) {
		this.mRfPower = mRfPower;
	}

	public int getmScanTime() {
		return mScanTime;
	}

	public void setmScanTime(int mScanTime) {
		this.mScanTime = mScanTime;
	}

	public boolean ismIsBeepOn() {
		return mIsBeepOn;
	}

	public void setmIsBeepOn(boolean mIsBeepOn) {
		this.mIsBeepOn = mIsBeepOn;
	}
}
