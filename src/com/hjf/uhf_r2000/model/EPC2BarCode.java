package com.hjf.uhf_r2000.model;

public class EPC2BarCode {

	private String epc;
	private String barcode;
	private String serial;

	public EPC2BarCode() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	public EPC2BarCode(String epc, String barcode, String serial) {
		super();
		this.epc = epc;
		this.barcode = barcode;
		this.serial = serial;
	}


	public String getEpc() {
		return epc;
	}

	public void setEpc(String epc) {
		this.epc = epc;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	@Override
	public String toString() {
		return "EPC2BarCode [epc=" + epc + ", barcode=" + barcode + ", serial="
				+ serial + "]";
	}

}
