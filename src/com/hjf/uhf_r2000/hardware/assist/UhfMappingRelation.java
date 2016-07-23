package com.hjf.uhf_r2000.hardware.assist;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.text.TextUtils;

import com.hjf.util.Logger;

/**
 * ����Ƶ�����еĲ��� �� ������ʾ����ӳ���ϵ
 */
public class UhfMappingRelation {
	public static final byte ERR1 = (byte) -1;
	public static final String ERR2 = "";

	public static final byte BEEP_ON = 1;
	public static final byte BEEP_OFF = 0;

	private final String UNIT_BAUD = "bps";
	private final String UNIT_FRE = "MHz";
	private final String UNIT_SCAN_TIME = "*100ms";

	/** ������ */
	private Map<Byte, String> mMapBaud;
	/** �� */
	private Map<Byte, String> mMapMem;

	/**
	 * baudrate ʵ�ʲ����� 0 9600bps 1 19200 bps 2 38400 bps 5 57600 bps 6 115200 bps
	 */
	private void initBaud() {
		mMapBaud = new HashMap<Byte, String>();
		mMapBaud.put((byte) 0, "9600" + UNIT_BAUD);
		mMapBaud.put((byte) 1, "19200" + UNIT_BAUD);
		mMapBaud.put((byte) 2, "38400" + UNIT_BAUD);
		mMapBaud.put((byte) 5, "57600" + UNIT_BAUD);
		mMapBaud.put((byte) 6, "115200" + UNIT_BAUD);
	}

	/**
	 * 0x00: �������� 0x01��EPC�洢���� 0x02��TID�洢���� 0x03���û��洢����
	 */
	private void initMem() {
		mMapMem = new HashMap<Byte, String>();
		mMapMem.put((byte) 0, "Reservations");
		mMapMem.put((byte) 1, "EPC");
		mMapMem.put((byte) 2, "TID");
		mMapMem.put((byte) 3, "Users");
	}

	public String[] getAllBaudValue() {
		if (mMapBaud == null) {
			initBaud();
		}

		String[] result = new String[mMapBaud.size()];
		int index = 0;
		for (Entry entry : mMapBaud.entrySet()) {
			result[index++] = (String) (entry.getValue());
		}
		return result;
	}

	public String getBaudValue(byte Key) {
		if (mMapBaud == null) {
			initBaud();
		}
		if (mMapBaud.containsKey(Key)) {
			return mMapBaud.get(Key);
		}
		return ERR2;
	}

	public byte getBaudKey(String value) {
		if (TextUtils.isEmpty(value)) {
			return ERR1;
		}
		if (mMapBaud == null) {
			initBaud();
		}
		for (Entry entry : mMapBaud.entrySet()) {
			if (value.equalsIgnoreCase((String) (entry.getValue()))) {
				return (Byte) (entry.getKey());
			}
		}
		return ERR1;
	}

	/**
	 * 1��Ƶ�� + Ƶ�� = Ƶ�� 2�����Ƶ�����СƵ��ȡֵ��Χ����0~49
	 * 3����ͬƵ���Ӧ�����Ƶ�ʺ���СƵ�ʵ�ֵ��һ������Ϊ���Ƶ�κ���СƵ�β�һ�� 4��Ƶ�β��� US band ���Ƶ�ι���Bit7:0
	 * Bit6:0 ��СƵ�ι���Bit7:1 Bit6:0 5������Ƶ�η����еĲ�����Ƶ��
	 * 6�����Ƶ�ʺ���СƵ���ڽ�����ʾ��MHz��һ���ģ�ԭ�������Ƶ�����СƵ��ȡֵ��Χ����0~49
	 */

	/**
	 * Fs = 902.75 + N * 0.5 (MHz) ����N��[0,49]
	 * 
	 * @todo Ƶ��תMHz
	 * @param key
	 *            N
	 * @return Fs
	 */
	public String getFreValue(byte key) {
		if (key > 49 || key < 0) {
			return ERR2;
		}
		return Double.toString(902.75 + (double) key * 0.5) + UNIT_FRE;
	}

	public String[] getAllFreValue() {
		// 0~49
		String[] result = new String[50];
		for (int i = 0; i < result.length; i++) {
			String freValue = getFreValue((byte) i);
			result[i] = freValue;
		}
		return result;
	}

	/**
	 * @todo ��ȡ�������Ƶ�ʣ�Ƶ��+Ƶ�㣩
	 * @param value
	 *            Fs
	 * @return
	 */
	public byte getMaxFreKey(String value) {
		byte key = getFreKey(value);
		if (key == ERR1) {
			return ERR1;
		}
		return toMaxFre(key);
	}

	/**
	 * @todo ��ȡ��СƵ�ʣ�Ƶ��+Ƶ�㣩
	 * @param value
	 *            Fs
	 * @return
	 */
	public byte getMinFreKey(String value) {
		byte key = getFreKey(value);
		if (key == ERR1) {
			return ERR1;
		}
		return toMinFre(key);
	}

	/**
	 * Fs = 902.75 + N * 0.5 (MHz) ����N��[0,49]
	 * 
	 * @todo MHzתƵ��
	 * @param value
	 *            Fs
	 * @return N
	 */
	private byte getFreKey(String value) {
		if (TextUtils.isEmpty(value)) {
			return ERR1;
		}
		if (value.endsWith(UNIT_FRE)) {
			int index = value.indexOf(UNIT_FRE);
			value = value.substring(0, index);
		}

		double dValue = Double.parseDouble(value);
		byte key = (byte) ((dValue - 902.75) / 0.5);
		if (key >= 0 & key <= 49) {
			return key;
		}
		return ERR1;
	}

	/**
	 * @todo ���Ƶ��תƵ�� (Ƶ��+Ƶ�� = Ƶ�ʣ�Ƶ�β��� US band Bit7:0 Bit6:0)
	 * @param key
	 *            Ƶ��
	 * @return Ƶ��
	 */
	private byte toMaxFre(byte key) {
		return key;
	}

	/**
	 * @todo ���Ƶ��תƵ�� (Ƶ��+Ƶ�� = Ƶ�ʣ�Ƶ�β��� US band Bit7:0 Bit6:0)
	 * @param key
	 *            Ƶ��
	 * @return Ƶ��
	 */
	public byte reverseMaxFre(byte key) {
		return key;
	}

	/**
	 * @todo ��СƵ��תƵ�� (Ƶ��+Ƶ�� = Ƶ�ʣ�Ƶ�β��� US band Bit7:1 Bit6:0)
	 * @param key
	 *            Ƶ��
	 * @return Ƶ��
	 */
	private byte toMinFre(byte key) {
		return (byte) (0x80 | key);
	}

	/**
	 * @todo ��СƵ��תƵ�� (Ƶ��+Ƶ�� = Ƶ�ʣ�Ƶ�β��� US band Bit7:1 Bit6:0)
	 * @param key
	 *            Ƶ��
	 * @return Ƶ��
	 */
	public byte reverseMinFre(byte key) {
		return (byte) (0x3F & key);
	}

	public String[] getAllRfPowerValue() {
		// 0~30
		String[] result = new String[31];
		for (int i = 0; i < result.length; i++) {
			result[i] = i + "";
		}
		return result;
	}

	public String[] getAllScanTimeValue() {
		// 3~255
		String[] result = new String[253];
		int index = 0;
		for (int i = 3; i <= 255; i++) {
			result[index++] = getScanTimeValue(i);
		}
		return result;
	}

	public String getScanTimeValue(int key) {
		Logger.D("key:" + key);
		if (key > 255 || key < 3) {
			return ERR2;
		}
		return key + UNIT_SCAN_TIME;
	}

	public int getScanTimeKey(String value) {
		if (TextUtils.isEmpty(value)) {
			return ERR1;
		}
		if (value.endsWith(UNIT_SCAN_TIME)) {
			int index = value.indexOf(UNIT_SCAN_TIME);
			value = value.substring(0, index);
		}

		int iValue = Integer.parseInt(value);
		if (iValue > 255 || iValue < 3) {
			return ERR1;
		}
		return iValue;
	}

	public boolean isBeepOn(byte key) {
		if (key == BEEP_ON) {
			return true;
		} else {
			return false;
		}
	}

	public byte getBeepKey(boolean isBeepOn) {
		if (isBeepOn) {
			return BEEP_ON;
		} else {
			return BEEP_OFF;
		}
	}

	/**
	 * @todo ����
	 * @param key
	 * @return
	 */
	public String getMemValue(byte key) {
		if (mMapMem == null) {
			initMem();
		}

		if (mMapMem.containsKey(key)) {
			return mMapMem.get(key);
		}
		return ERR2;
	}

	/**
	 * @todo ����
	 * @param
	 * @return
	 */
	public byte getMemKey(String value) {
		if (TextUtils.isEmpty(value)) {
			return ERR1;
		}
		if (mMapMem == null) {
			initMem();
		}
		for (Entry entry : mMapMem.entrySet()) {
			if (value.equalsIgnoreCase((String) (entry.getValue()))) {
				return (Byte) (entry.getKey());
			}
		}
		return ERR1;
	}

	public String[] getAllMen() {
		if (mMapMem == null) {
			initMem();
		}
		String[] result = new String[mMapMem.size()];
		int index = 0;
		for (Entry entry : mMapMem.entrySet()) {
			result[index++] = (String) (entry.getValue());
		}
		return result;
	}

}
