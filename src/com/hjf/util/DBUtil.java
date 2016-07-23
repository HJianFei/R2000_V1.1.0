package com.hjf.util;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.hjf.uhf_r2000.constants.Constants;
import com.hjf.uhf_r2000.model.EPC2BarCode;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
/**
 * ���ݿ�������
 * @author Administrator
 *
 */
public class DBUtil {
	private List<EPC2BarCode> mEPCcode;
	private Connection con;
	private Handler mHandler;
	private ProgressDialog mDialog;
	private String sql;
	private Context mContext;
	private String db_Address = "jdbc:mysql://" + Constants.DEFAULT_DB_ADDRESS
			+ ":3306/epcdb";

	public DBUtil(Context mContext, List<EPC2BarCode> mEPCcode,
			Handler mHandler, ProgressDialog mDialog) {
		super();
		this.mEPCcode = mEPCcode;
		this.mHandler = mHandler;
		this.mDialog = mDialog;
		this.mContext = mContext;

	} 

	// �õ����ݿ����ӵĶ���
	public void getConnection() {
		con = null;
		String db_address = (String) SharedPreferencesUtils.getParam(mContext,
				Constants.DB_ADDRESS, Constants.DEFAULT_DB_ADDRESS);
		if (!db_address.equals(Constants.DEFAULT_DB_ADDRESS)) {
			db_Address = "jdbc:mysql://" + db_address + ":3306/epcdb";

		}
		new Thread() {

			@Override
			public void run() {
				try {
					Class.forName(Constants.DB_DRIVER);
					con = (Connection) DriverManager.getConnection(db_Address,
							Constants.DB_USER, Constants.DB_PASSWORD);

					// ������������
					insert(con);
				} catch (Exception e) {
					Message message = Message.obtain();
					message.what = 3;
					message.obj = mDialog;
					mHandler.sendMessage(message);
					e.printStackTrace();
				}

			}

		}.start();
	}

	/**
	 * ������������
	 * 
	 * @param conn
	 */
	@SuppressLint("SimpleDateFormat")
	public void insert(Connection conn) {

		// sqlǰ׺
		String prefix = "INSERT INTO sto_tmp_check_repository (epc,timestamp,barcode,serial) VALUES ";
		try {
			// ����sql��׺
			StringBuffer suffix = new StringBuffer();
			// ��������Ϊ���Զ��ύ
			conn.setAutoCommit(false);
			// Statement st = conn.createStatement();
			// ����st��pst�����Щ
			PreparedStatement pst = (PreparedStatement) conn
					.prepareStatement("");
			Date currentTime;
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			// ���ѭ�������ύ�������
			for (int i = 0; i < mEPCcode.size(); i++) {
				// ����sql��׺
				currentTime = new Date();
				suffix.append("('" + mEPCcode.get(i).getEpc() + "','"
						+ formatter.format(currentTime) + "','"
						+ mEPCcode.get(i).getBarcode() + "','"
						+ mEPCcode.get(i).getSerial() + "'),");
				sql = prefix + suffix.substring(0, suffix.length() - 1);
			}
			// ���ִ��sql
			pst.addBatch(sql);
			// ִ�в���
			pst.executeBatch();
			// �ύ����
			conn.commit();
			// �����һ����ӵ�����
			suffix = new StringBuffer();

			// �Ͽ����ӣ���Դ����
			pst.close();
			conn.close();
			Message message = Message.obtain();
			message.what = 2;
			message.obj = mDialog;
			mHandler.sendMessage(message);
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}
}
