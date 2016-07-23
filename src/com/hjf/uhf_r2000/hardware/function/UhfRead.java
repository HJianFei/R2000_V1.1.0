package com.hjf.uhf_r2000.hardware.function;

import com.hjf.uhf_r2000.hardware.assist.UhfReadListener;
import com.hjf.util.Logger;

/**
 * ����ǩ��
 */
public class UhfRead extends Thread {
	/** ��ʼ��ַ */
	private byte mWordPtr;
	/** ��ȡ���ֵĸ��� */
	private byte mNum;
	/** ��ȡ�Ĵ洢�� */
	private byte mMem;
	/** �����������ձ�ǩ��Ϣ�ʹ�����Ϣ */
	private UhfReadListener mListener;

	private int mIntervalTime = 5;// ms

	private boolean mIsLoop = true;
	private boolean mIsPause = false;

	public UhfRead(UhfReadListener listener) {
		mListener = listener;
	}

	@Override
	public void run() {
		byte[] ComAdr = new byte[1];
		ComAdr[0] = (byte) 0x00;
		// === InventoryG2 �Ĳ��� start ====
		byte QValue = 4;// how many cards scan for one time
		byte Session = 0;
		byte MaskMem = 1;// te:must be 1
		byte[] MaskAdr = new byte[2];
		MaskAdr[0] = MaskAdr[1] = 0;
		byte MaskLen = 8;
		byte[] MaskData = new byte[1];
		MaskData[0] = (byte) 0;
		byte MaskFlag = 0;
		byte AdrTID = 0;
		byte LenTID = 15;
		byte TIDFlag = 0;// te:must be zero
		byte[] pEPCList = new byte[1000];
		byte[] Ant1 = new byte[1];
		int[] Totallen = new int[1];
		int[] CardNum = new int[1];
		// === InventoryG2 �Ĳ��� end ====

		StringBuffer sbContent = new StringBuffer();

		// ==== ReadDataG2�Ĳ��� start ====
		int totalLength;
		byte[] pepList;
		byte[] EPC2;
		byte Enum2 = (byte) 6; // EPC�ֳ���
		byte[] Password2 = { (byte) 0, (byte) 0, (byte) 0, (byte) 0 };// 4�ֽڷ�������
		byte MaskMem2 = (byte) 1; // ������,0x01��EPC�洢����0x02��TID�洢����0x03���û��洢����
		byte[] MaskAdr2 = { (byte) 0, (byte) 0 }; // �������ʼλ��ַ
		byte MaskLen2 = (byte) 8; // �����λ����
		byte[] MaskData2 = { (byte) 0 }; // �������ݡ�MaskData�����ֽڳ�����MaskLen/8��

		byte[] Data2 = new byte[256];
		int[] errorcode2 = new int[1];
		int dataLength;
		String strnum;
		String[] contents;
		// ==== ReadDataG2�Ĳ��� end ====

		while (mIsLoop) {
			if (!com.halio.r2000.InventoryG2(UhfComm.sAddr, QValue, Session,
					MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, AdrTID,
					LenTID, TIDFlag, pEPCList, Ant1, Totallen, CardNum)) {
				showErr("InventoryG2 fail");
				Logger.D("fail========");
			} else {
				if (Totallen[0] != 0 && !mIsPause) {
					if (mMem == 1) {
						// �� EPC
						Logger.D("succ");
						totalLength = Totallen[0];
						contents = new String[CardNum[0]];
						int index_contents = 0;
						int index = 0;
						while (totalLength > index) {
							int length = pEPCList[index];
							Logger.D("len:" + length);
							for (int i = index + 1; i < index + length + 1; i++) {
								strnum = Integer
										.toHexString(pEPCList[i] & 0xFF);
								if (strnum.length() == 1) {
									sbContent.append("0");
								}
								sbContent.append(strnum);
								sbContent.append("");
							}
							contents[index_contents++] = sbContent.toString();
							Logger.D("content:" + sbContent.toString());
							index += (length + 2);

							sbContent.setLength(0);
						}
						showContent(contents);
					} else {
						// �� EPC ������
						totalLength = Totallen[0];
						contents = new String[1];
						pepList = new byte[totalLength - 2];
						for (int i = 0; i < totalLength - 2; i++) {
							pepList[i] = pEPCList[i + 1];
						}

						EPC2 = pepList;

						if (!com.halio.r2000.ReadDataG2(UhfComm.sAddr, EPC2,
								Enum2, mMem, mWordPtr, mNum, Password2,
								MaskMem2, MaskAdr2, MaskLen2, MaskData2, Data2,
								errorcode2)) {
							showErr("ReadData fail");
						} else {
							dataLength = mNum * 2;
							for (int i = 0; i < dataLength; i++) {
								strnum = Integer.toHexString(Data2[i] & 0xFF);
								if (strnum.length() == 1) {
									sbContent.append("0");
								}
								sbContent.append(strnum);
								sbContent.append("");
							}
							contents[0] = sbContent.toString();
							showContent(contents);
							sbContent.setLength(0);
						}

					}
				}
				try {
					Thread.sleep(mIntervalTime);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (mIsPause) {
				try {
					synchronized (this) {
						this.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void pause() {
		mIsPause = true;
	}

	public synchronized void reStart() {
		mIsPause = false;
		this.notify();
	}

	public synchronized void destroy() {
		mIsLoop = false;
		mIsPause = false;
		this.notify();
	}

	private void showContent(Object[] obj) {

		if (mListener != null) {
			mListener.onContentCaughted(obj);
		}
	}

	private void showErr(String err) {
		if (mListener != null) {
			mListener.onErrorCaughted(err);
		}
	}

	public byte getmWordPtr() {
		return mWordPtr;
	}

	public void setmWordPtr(byte mWordPtr) {
		this.mWordPtr = mWordPtr;
	}

	public byte getmNum() {
		return mNum;
	}

	public void setmNum(byte mNum) {
		this.mNum = mNum;
	}

	public byte getmMem() {
		return mMem;
	}

	public void setmMem(byte mMem) {
		this.mMem = mMem;
	}

	public void setmIntervalTime(int time) {
		this.mIntervalTime = time;
	}

}
