package com.hjf.uhf_r2000.hardware.function;

import com.hjf.uhf_r2000.hardware.assist.UhfUtil;

public class UhfWrite {
	// === ��Ա���� start ====
	/** ��ʼ��ַ */
	private byte mWordPtr;
	/** ��ȡ�Ĵ洢�� */
	private byte mMem;
	/** д������� */
	private String mContent;
	// === ��Ա���� end ====

	// === InventoryG2 �Ĳ��� start ====
	private byte QValue = 0;// how many cards scan for one time
	private byte Session = 0;
	private byte MaskMem = 1;// te:must be 1
	private byte[] MaskAdr = { 0, 0 };
	private byte MaskLen = 8;
	private byte[] MaskData = { 0 };
	private byte MaskFlag = 0;
	private byte AdrTID = 0;
	private byte LenTID = 15;
	private byte TIDFlag = 0;// te:must be zero
	private byte[] pEPCList = new byte[256];
	private byte[] Ant1 = new byte[1];
	private int[] Totallen = new int[1];
	private int[] CardNum = new int[1];
	// === InventoryG2 �Ĳ��� end ====

	// === WriteDataG2 �Ĳ��� start ===
	private int totalLength;
	private byte[] pepList;
	private int dataLength;
	private byte[] Data;
	private byte[] EPC2;
	private byte Enum2 = (byte) 6; // EPC�ֳ���
	private byte Wnum2;
	private byte[] Writedata2;
	private byte[] Password2 = { (byte) 0, (byte) 0, (byte) 0, (byte) 0 };// 4�ֽڷ�������
	private byte MaskMem2 = (byte) 1; // ������,0x01��EPC�洢����0x02��TID�洢����0x03���û��洢����
	private byte[] MaskAdr2 = { (byte) 0, (byte) 0 }; // �������ʼλ��ַ
	private byte MaskLen2 = (byte) 8; // �����λ����
	private byte[] MaskData2 = { (byte) 0 }; // �������ݡ�MaskData�����ֽڳ�����MaskLen/8��
	private int[] errorcode2 = { -2 };

	// === WriteDataG2 �Ĳ��� end ===

	public UhfWrite() {
	}

	public boolean doWrite() {
		if (!com.halio.r2000.InventoryG2(UhfComm.sAddr, QValue, Session,
				MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, AdrTID, LenTID,
				TIDFlag, pEPCList, Ant1, Totallen, CardNum)) {
			// communicate fail
			return false;
		} else {
			if (Totallen[0] != 0) {
				totalLength = Totallen[0];
				pepList = new byte[totalLength - 2];
				for (int i = 0; i < totalLength - 2; i++) {
					pepList[i] = pEPCList[i + 1];
				}

				if (mContent == null) {
					return false;
				}
				dataLength = mContent.length();
				Writedata2 = UhfUtil.hexStringToBytes(mContent);
				if (dataLength % 2 != 0) {
					dataLength += 1;
				}
				EPC2 = pepList;
				Wnum2 = (byte) (dataLength / 2); // д���ָ��� 6

				if (!com.halio.r2000.WriteDataG2(UhfComm.sAddr, EPC2, Wnum2,
						Enum2, mMem, mWordPtr, Writedata2, Password2, MaskMem2,
						MaskAdr2, MaskLen2, MaskData2, errorcode2)) {
					return false;
				} else {
					return true;
				}
			}
			return false;
		}
	}

	public byte getmWordPtr() {
		return mWordPtr;
	}

	public void setmWordPtr(byte mWordPtr) {
		this.mWordPtr = mWordPtr;
	}

	public byte getmMem() {
		return mMem;
	}

	public void setmMem(byte mMem) {
		this.mMem = mMem;
	}

	public String getmContent() {
		return mContent;
	}

	public void setmContent(String mContent) {
		this.mContent = mContent;
	}

}
