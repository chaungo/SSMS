package wuchou.ssms;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class DanhSachTinNhan extends Activity {

	ListView ls;
	Button btnG;
	EditText ed;
	static DatabaseHelper db;
	static ListView ds_tn;
	static Tn_adp adp ;
	String DesKey;
	static String Pubkey,Prikey;
	SMSReceiver smsr;
	static String sdt;
	static ArrayList<Tinnhan> ds ;



	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_danh_sach_tin_nhan);
		sdt = getIntent().getStringExtra("sdt");
		setTitle(sdt);
		
		android.app.ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));

		db = new DatabaseHelper(getApplicationContext());
		btnG = (Button)findViewById(R.id.ds_tn_btnGui);		
		Toast.makeText(this, sdt, Toast.LENGTH_LONG).show();
		ds_tn = (ListView)findViewById(R.id.dstn);	
		//ds_tn.setDivider(null);

		try {
			ds = db.layTinNhan(sdt);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		adp = new Tn_adp(getBaseContext(), ds);		
		ds_tn.setAdapter(adp);

		ds_tn.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {

				if(ds.get(position).kmh==1){
					Dialog nhapKeyGmDes =  boxnhapDeskey();
					nhapKeyGmDes.show();
					nhapKeyGmDes.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							try{
								String saugiaima = Bao_mat.giaimaDES(DesKey, ds.get(position).nd);
								db.suaNdTn(sdt, ds.get(position).nd, saugiaima);
								//capnhat
								ds = db.layTinNhan(sdt);
								adp = new Tn_adp(getBaseContext(), ds);
								ds_tn.setAdapter(adp);								
								//Intent intent = new Intent(getApplicationContext(), MainActivity.class);	
								//startActivity(intent);

							}catch(Exception e){
								Toast.makeText(getBaseContext(), "Nhập lại Key!", Toast.LENGTH_LONG).show();
							}
							//Toast.makeText(getBaseContext(), Bao_mat.giaimaDES(DesKey, ds.get(position).nd), Toast.LENGTH_LONG).show();
						}
					});	
					return;
				}	


				//Giai ma rsa
				if(ds.get(position).kmh==2){					
					try {						
						String saugiaimarsa = Bao_mat.GiaiMaRsa(ds.get(position).nd);
						db.suaNdTn(sdt, ds.get(position).nd, saugiaimarsa);
						//cap nhat danh sach
						ds = db.layTinNhan(sdt);
						adp = new Tn_adp(getBaseContext(), ds);
						ds_tn.setAdapter(adp);	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		btnG.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText ndtn = (EditText)findViewById(R.id.ds_tn_edNd);

				if(ndtn.getText().toString().equals("")){					
					ndtn.setError("Mời nhập nội dung");
				}else {
					Dialog chonPP = chonPpMh(ndtn,sdt);
					chonPP.show();		
				}
			}
		});



	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.danh_sach_tin_nhan, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		finish();
		return false;		
	};







































	/////////////////
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
	public Dialog chonPpMh(final EditText ndtn, final String sdt){
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
								guitn(sdt, ndtn.getText().toString(), 1, DesKey);	
								d.dismiss();
							}catch(Exception e){

							}
							//cap nhat
							ndtn.setText("");				
							ListView ds_tn = (ListView)findViewById(R.id.dstn);	
							ds_tn.setDivider(null);
							ArrayList<Tinnhan> atn = null;
							try {
								atn = db.layTinNhan(sdt);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Tn_adp adp = new Tn_adp(getBaseContext(), atn);
							ds_tn.setAdapter(adp);
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
								try {
									guitn(sdt, ndtn.getText().toString(),2,Pubkey);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}	

								//cap nhat
								d.dismiss();
								ndtn.setText("");				
								ListView ds_tn = (ListView)findViewById(R.id.dstn);	
								ds_tn.setDivider(null);
								ArrayList<Tinnhan> atn = null;
								try {
									atn = db.layTinNhan(sdt);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Tn_adp adp = new Tn_adp(getBaseContext(), atn);
								ds_tn.setAdapter(adp);
							}
						}
					});
				}
			}
		});

		return d;
	}
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



	///////////////////////////////////////ham gui tin nhan
	static void guitn(String sdt,String nd, int kmh, String keymahoa){		
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
					db.themNd(sdt, ndl, 0, 0, MainActivity.laytg());
					db.xoa1dongDsht(sdt);
					db.themDsht(sdt, ndl);

				} else {
					db.taoBang(sdt);
					db.themNd(sdt, ndl, 0, 0, MainActivity.laytg());
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

	public static void capnhatdstn(ListView lst){
		try {
			ds = db.layTinNhan(sdt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adp = new Tn_adp(lst.getContext(), ds);		
		lst.setAdapter(adp);
	}

}
