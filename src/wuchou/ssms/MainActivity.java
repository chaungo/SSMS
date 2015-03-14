package wuchou.ssms;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {

	static DatabaseHelper db;

	String DesKey, Pubkey;
	static ListView lst;	
	Button btnguinhanh, btnTnMoi,btnDong;
	EditText edsdtnhanh, edNdGuiNhanh;
	RelativeLayout main;
	Dialog boxdstn;

	static SharedPreferences pre;
	static SharedPreferences.Editor edtor;	

	static ArrayList<Mot_Dong> ds;



	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lst = (ListView)findViewById(R.id.listView1);
		pre = getSharedPreferences("user",MODE_PRIVATE);
		edtor = pre.edit();

		final Intent Adstn = new Intent(getApplicationContext(), DanhSachTinNhan.class);	



		db = new DatabaseHelper(getApplicationContext());

		//Xet dn
		if(pre.getBoolean("DaDk", false)) boxdn();
		else {
			boxDangKy();
		}


		main = (RelativeLayout)findViewById(R.id.MainLayout);
		main.setVisibility(View.INVISIBLE);

		btnguinhanh = (Button)findViewById(R.id.btnGuiNhanh);
		edsdtnhanh = (EditText)findViewById(R.id.edsdtnhanh);
		edNdGuiNhanh = (EditText)findViewById(R.id.edNdGuiNhanh);	
		btnDong = (Button)findViewById(R.id.butDong);

		btnguinhanh.setVisibility(View.GONE);
		edsdtnhanh.setVisibility(View.GONE);
		edNdGuiNhanh.setVisibility(View.GONE);
		btnDong.setVisibility(View.GONE);

		btnTnMoi = (Button)findViewById(R.id.btnThemTn);
		

		btnTnMoi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnguinhanh.setVisibility(View.VISIBLE);
				edsdtnhanh.setVisibility(View.VISIBLE);
				edNdGuiNhanh.setVisibility(View.VISIBLE);
				btnDong.setVisibility(View.VISIBLE);
				btnTnMoi.setVisibility(View.INVISIBLE);

			}
		});
		
		btnDong.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				btnguinhanh.setVisibility(View.GONE);
				edsdtnhanh.setVisibility(View.GONE);
				edNdGuiNhanh.setVisibility(View.GONE);
				btnDong.setVisibility(View.GONE);
				btnTnMoi.setVisibility(View.VISIBLE);
				
			}
		});

		btnguinhanh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//DanhSachTinNhan.guitn(edsdtnhanh.getText().toString(),edNdGuiNhanh.getText().toString(),0,null);				
				if(edNdGuiNhanh.getText().toString().equals("")){					
					edNdGuiNhanh.setError("Mời nhập nội dung");
					return;
				}
				if(edsdtnhanh.getText().toString().equals(""))
				{
					edsdtnhanh.setError("Mời nhập số điện thoại");
					return;
				}							
				Dialog chonPP = chonPpMh(edNdGuiNhanh.getText().toString(),edsdtnhanh.getText().toString());
				chonPP.show();	
				chonPP.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						//Adstn.putExtra("sdt",edsdtnhanh.getText().toString());
						edNdGuiNhanh.setText("");
						edsdtnhanh.setText("");
						lst = (ListView)findViewById(R.id.listView1);
						capnhatsdht(lst);
					}
				});			

			}
		});





		ds = db.laydsht();


		lst = (ListView)findViewById(R.id.listView1);
		lst.setDivider(null);
		Chou_Adp adp = new Chou_Adp(this, ds);
		lst.setAdapter(adp);		



		lst.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Adstn.putExtra("sdt", ds.get(position).sdt);
				startActivity(Adstn);		
				//Show_dstn(ds.get(position).sdt);
			}
		});

		lst.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {

				boxacnhan("Xoá tin nhắn","Bạn có muốn xoá chuỗi tin nhắn này").setOnCancelListener(new OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						db.xoabang(ds.get(position).sdt);
						db.xoa1dongDsht(ds.get(position).sdt);		
						lst = (ListView)findViewById(R.id.listView1);
						capnhatsdht(lst);					
					}
				});
				return false;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.taokey) {			
			Intent intent = new Intent("eu.michalu.ENCODE");			
			intent.putExtra("ENCODE_FORMAT", "QR_CODE");
			intent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
			try {
				intent.putExtra("ENCODE_DATA",laysdt()+"\n"+Bao_mat.taoKeyRSA());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				startActivityForResult(intent,0);
			} catch (ActivityNotFoundException e) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.google.zxing.client.android")));
			}
			return true;
		}
		if (id == R.id.themkey) {
			Intent intent = new Intent("eu.michalu.SCAN");			
			intent.putExtra("SCAN_MODE", "QR_CODE");
			startActivityForResult(intent, 111);	
			return true;
		}
		if (id == R.id.dsPublicKey) {			
			Dialog aa = ShowDspubKey();	
			aa.show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		/**
		 * Back from scanner view
		 */
		if (requestCode == 111) {
			if (resultCode == RESULT_OK) {
				String pubkey = intent.getStringExtra("SCAN_RESULT");
				db.themPubKey(pubkey);

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Log.i(MainActivity.class.getSimpleName(), "Handle cancel");
			}
		}
	}

	//ON RESUME
	@Override
	protected void onResume() {		
		lst = (ListView)findViewById(R.id.listView1);
		capnhatsdht(lst);		
		btnguinhanh.setVisibility(View.GONE);
		edsdtnhanh.setVisibility(View.GONE);
		edNdGuiNhanh.setVisibility(View.GONE);
		btnDong.setVisibility(View.GONE);
		btnTnMoi.setVisibility(View.VISIBLE);
		super.onResume();
	}



	//box dang ky
	void boxDangKy(){

		final Dialog boxDangKy = new Dialog(this);
		boxDangKy.setContentView(R.layout.gd_dang_ky);
		boxDangKy.setTitle("Đăng ký");
		boxDangKy.setCancelable(false);
		boxDangKy.setCanceledOnTouchOutside(false);


		Button btnDk = (Button)boxDangKy.findViewById(R.id.btnDangKy);
		Button btnlamlai = (Button)boxDangKy.findViewById(R.id.btnXoaTrang);		
		final EditText tendk = (EditText)boxDangKy.findViewById(R.id.ed_tendangky);
		final EditText mkdk1 = (EditText)boxDangKy.findViewById(R.id.ed_passDk1);
		final EditText mkdk2 = (EditText)boxDangKy.findViewById(R.id.ed_passDk2);

		btnDk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(tendk.getText().toString().equals("")){
					tendk.setError("Mời nhập tên");	
					return;
				}else tendk.setError(null);	

				if(mkdk1.getText().toString().equals("")){
					mkdk1.setError("Mời nhập mật khẩu");					
					return;
				}else mkdk1.setError(null);				

				if(mkdk2.getText().toString().equals(""))
				{
					mkdk2.setError("Mời nhập mật khẩu lần nữa");
					return;
				} else mkdk2.setError(null);

				if(!mkdk1.getText().toString().equals(mkdk2.getText().toString())){
					mkdk2.setError("Mật khẩu không khớp");
					return;		
				} else mkdk2.setError(null);

				edtor.putString("username", Bao_mat.SHA256(tendk.getText().toString()));
				edtor.putString("pass", Bao_mat.SHA256(mkdk2.getText().toString()));
				edtor.putBoolean("DaDk", true);
				edtor.commit();


				Toast.makeText(getBaseContext(), "Hoàn tất đăng ký", Toast.LENGTH_SHORT).show();	
				boxDangKy.dismiss();
				boxdn();
			}
		}
				);


		btnlamlai.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tendk.setText("");
				mkdk1.setText("");
				mkdk2.setText("");
			}
		});
		boxDangKy.show();
	}





	/////////////////////////////////giao dien dang nhap
	void boxdn(){

		final Dialog boxdn = new Dialog(this);
		boxdn.setContentView(R.layout.dangnhap);
		boxdn.setTitle("Đăng nhập");
		//boxdn.setCancelable(false);	//vo hieu nut back
		//boxdn.setCanceledOnTouchOutside(false);
		boxdn.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				Toast.makeText(getApplicationContext(), "Đăng nhập KHÔNG thành công", Toast.LENGTH_SHORT).show();
				finish();

			}
		});




		Button btndn = (Button)boxdn.findViewById(R.id.btnDn);
		Button btnthoat = (Button)boxdn.findViewById(R.id.btnthoat);
		final EditText uname = (EditText)boxdn.findViewById(R.id.username);				
		final EditText pass = (EditText)boxdn.findViewById(R.id.pass);

		btndn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					switch (xetdn(uname,pass)) {
					case 1:{
						main.setVisibility(View.VISIBLE);
						boxdn.dismiss();
						//Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
					}						
					break;
					case 2: Toast.makeText(getApplicationContext(), "Mời nhập tên người dùng và mật khẩu", Toast.LENGTH_SHORT).show();	
					break;
					case 0:Toast.makeText(getApplicationContext(), "Đăng nhập KHÔNG thành công", Toast.LENGTH_SHORT).show();	
					break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		//Bat su kien nut Thoat
		btnthoat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), "Đăng nhập KHÔNG thành công", Toast.LENGTH_SHORT).show();	
				boxdn.dismiss();				
				finish();
			}
		} );

		boxdn.show();
	}








	////////////////////////////////// box xac nhan
	Dialog boxacnhan(String t,String nd ){

		final Dialog xacnhan = new Dialog(this);
		xacnhan.setContentView(R.layout.xacnhan);
		xacnhan.setTitle(t);
		xacnhan.setCanceledOnTouchOutside(false);
		xacnhan.setCancelable(false);



		Button btnDy = (Button)xacnhan.findViewById(R.id.btnDY);
		Button btnKDy = (Button)xacnhan.findViewById(R.id.btnKh);
		TextView tvThongDiep = (TextView)xacnhan.findViewById(R.id.tvxacnhan);
		tvThongDiep.setText(nd);
		xacnhan.show();
		btnDy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				xacnhan.cancel();
			}

		});
		btnKDy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				xacnhan.dismiss();
			}
		});


		return xacnhan;


	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////

	//Cac ham chuc nang

	/////////xet dang nhap
	int xetdn(EditText uname, EditText pass) throws Exception{			
		if(Bao_mat.SHA256(uname.getText().toString()).equals(pre.getString("username",null)) & Bao_mat.SHA256(pass.getText().toString()).equals(pre.getString("pass", null))) return 1;
		else {
			if(uname.getText().toString().equals("") & pass.getText().toString().equals("")) return 2;
			else return 0;
		}
	}












	public static String laytg(){
		Time time = new Time();
		time.setToNow();
		int m = time.month + 1;
		return time.hour +":"+ time.minute +"-"+time.monthDay+ "/" + m;

	}

	public static void capnhatsdht(ListView lst){		
		ds = db.laydsht();
		Chou_Adp adp = new Chou_Adp(lst.getContext(), ds);
		lst.setAdapter(adp);		
	}







	//Show danh sach public key
	public Dialog ShowDspubKey(){
		final Dialog dp = new Dialog(this);
		dp.setCanceledOnTouchOutside(false);
		dp.setCancelable(false);	
		dp.setContentView(R.layout.ds_key);
		dp.setTitle("Danh sách Public key");
		ListView lkey = (ListView)dp.findViewById(R.id.lst_public_key);
		final ArrayList<String> keyA = db.laypubkey();
		ArrayAdapter<String> adp = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, keyA);
		lkey.setAdapter(adp);
		lkey.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {				
				Pubkey = keyA.get(position).substring(keyA.get(position).indexOf("\n")+1,keyA.get(position).lastIndexOf("\n"));
				Toast.makeText(getBaseContext(), Pubkey, Toast.LENGTH_LONG).show();
				dp.dismiss();
			}
		});

		Button btnThoat = (Button)dp.findViewById(R.id.btnThoatDsKey);
		btnThoat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dp.cancel();

			}
		});
		return dp;
	}




	//lay so dien thoai
	public String laysdt(){
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager)
				getSystemService(Context.TELEPHONY_SERVICE); 
		return mTelephonyMgr.getLine1Number();
	}

	public Dialog chonPpMh(final String ndtn, final String sdt){
		DesKey = null;
		Pubkey = null;

		final Dialog d = new Dialog(this);		
		d.setContentView(R.layout.chon_pp_ma_hoa);
		d.setTitle("Chọn phương pháp mã hoá");

		ArrayList<String> dspp = new ArrayList<String>();
		dspp.add("Mã hoá DES");
		dspp.add("Mã hoá RSA");		
		ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dspp);

		ListView lv = (ListView)d.findViewById(R.id.lst_con_pp_bao_mat);			
		lv.setAdapter(adp);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {				
				if(position==0){
					Dialog nhapKey = boxnhapDeskey();
					nhapKey.show();
					nhapKey.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							// TODO Auto-generated method stub							
							try{
								guitn(sdt, ndtn, 1,DesKey);	
								d.dismiss();
							}catch(Exception e){

							}

						}
					});					


				}
				//////
				if(position==1){
					Dialog rsa = ShowDspubKey();
					rsa.show();
					rsa.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							if(Pubkey!=null){
								guitn(sdt,ndtn,2,Pubkey);													
							}
						}
					});

				}
			}
		});

		return d;
	}

	Dialog boxnhapDeskey(){		
		final Dialog boxnhapkey = new Dialog(this);
		boxnhapkey.setCancelable(false);
		boxnhapkey.setCanceledOnTouchOutside(false);
		boxnhapkey.setContentView(R.layout.nhap_key);		
		boxnhapkey.setTitle("Nhập key mã hoá");

		final EditText edKey = (EditText)boxnhapkey.findViewById(R.id.eduKey);
		Button btnDY = (Button)boxnhapkey.findViewById(R.id.btnDongYNhapKey);
		Button btnThoat = (Button)boxnhapkey.findViewById(R.id.btnThoatNhapKey);

		btnDY.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(edKey.getText().toString().length()<8){
					edKey.setError("Phải ít nhất 8 ký tự");
				}else{
					DesKey = edKey.getText().toString();
					boxnhapkey.dismiss();
				}
			}
		});

		btnThoat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boxnhapkey.cancel();

			}
		});

		return boxnhapkey;
	}

	void guitn(String sdt,String nd, int kmh, String keymahoa){		
		String ndl = nd;//noi dung dc luu		
		if(kmh==1){
			nd = Bao_mat.mahoaDES(keymahoa, nd);
		}
		if(kmh==2){
			try {
				nd = Bao_mat.MaHoaRsa(nd, keymahoa);
			} catch (Exception e) {
				System.out.println(e);  
				e.printStackTrace();
			}
		}
		String t = "" + kmh;
		String ndg = "ssms"+nd+t;// nd se gui di

		SmsManager sms = SmsManager.getDefault();
		try{
			sms.sendTextMessage(sdt,null,ndg,null,null);

			try{
				if(db.tontai(sdt)){						
					db.themNd(sdt, ndl, 0, 0,laytg());
					db.xoa1dongDsht(sdt);
					db.themDsht(sdt, ndl);

				} else {
					db.taoBang(sdt);
					db.themNd(sdt, ndl, 0, 0,laytg());
					db.themDsht(sdt, ndl);
				}				
			}catch (Exception e){
				//Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
				Log.d("Loi db", e.toString());
				e.printStackTrace();
			}

		}catch (Exception e){
			//Toast.makeText(getApplicationContext(),"Không gửi được, xin kiểm tra lại!",Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}


	}

	public static void luuPriKey(String key){
		edtor.putString("priKey", key);
		edtor.commit();
	}

	public static String layPriKey(){
		return pre.getString("priKey",null);

	}
	public static String layMaDes(){
		return pre.getString("pass", null);
	}

}
