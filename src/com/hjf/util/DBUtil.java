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
 * 数据库连接类
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

	// 拿到数据库连接的对象
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

					// 批量插入数据
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
	 * 批量插入数据
	 * 
	 * @param conn
	 */
	@SuppressLint("SimpleDateFormat")
	public void insert(Connection conn) {

		// sql前缀
		String prefix = "INSERT INTO sto_tmp_check_repository (epc,timestamp,barcode,serial) VALUES ";
		try {
			// 保存sql后缀
			StringBuffer suffix = new StringBuffer();
			// 设置事务为非自动提交
			conn.setAutoCommit(false);
			// Statement st = conn.createStatement();
			// 比起st，pst会更好些
			PreparedStatement pst = (PreparedStatement) conn
					.prepareStatement("");
			Date currentTime;
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			// 外层循环，总提交事务次数
			for (int i = 0; i < mEPCcode.size(); i++) {
				// 构建sql后缀
				currentTime = new Date();
				suffix.append("('" + mEPCcode.get(i).getEpc() + "','"
						+ formatter.format(currentTime) + "','"
						+ mEPCcode.get(i).getBarcode() + "','"
						+ mEPCcode.get(i).getSerial() + "'),");
				sql = prefix + suffix.substring(0, suffix.length() - 1);
			}
			// 添加执行sql
			pst.addBatch(sql);
			// 执行操作
			pst.executeBatch();
			// 提交事务
			conn.commit();
			// 清空上一次添加的数据
			suffix = new StringBuffer();

			// 断开连接，资源回收
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
