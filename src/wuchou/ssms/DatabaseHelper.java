package wuchou.ssms;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {




	Context c;
	public DatabaseHelper(Context context) {
		super(context,"sms", null,1);
		this.c = context;
	}

	//Tao bang
	public void taoBang(String sdt){
		if(sdt.startsWith("+")){
			sdt = sdt.substring(1);
		}
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("CREATE TABLE s"+sdt+"("+
				" id integer primary key autoincrement, " +
				" nd text not null," +
				" dt integer not null," +
				" kmh integer not null," +
				" tg text not null);");
	}
	//Them tin nhan vao bang
	public void themNd(String sdt, String nd, int dtinh, int kmh,String tgian){
		if(sdt.startsWith("+")){
			sdt = sdt.substring(1);
		}
		ContentValues in = new ContentValues();
		in.put("nd", nd);
		in.put("dt", dtinh);	
		in.put("kmh", kmh);
		in.put("tg", tgian);
		this.getWritableDatabase().insert("s"+sdt,null,in);
	}

	//Lay tin nhan
	public ArrayList<Tinnhan> layTinNhan(String sdt){
		if(sdt.startsWith("+")){
			sdt = sdt.substring(1);
		}
		ArrayList<Tinnhan> lstTn = new ArrayList<Tinnhan>();
		Cursor c = this.getReadableDatabase().query("s"+sdt, null,null, null, null, null,null);
		c.moveToFirst();
		while(c.isAfterLast()==false){			
			Tinnhan tn = new Tinnhan();
			tn.id = c.getInt(0);
			tn.sdt = sdt;			
			tn.nd = c.getString(1);						
			tn.dtinh = c.getInt(2);
			tn.kmh = c.getInt(3);
			tn.tgian = c.getString(4);
			lstTn.add(tn);
			c.moveToNext();
		}		
		c.close();
		return lstTn;

	}
	//Sua tin nhan
	public boolean suaNdTn(String sdt, String giatricu, String giatrimoi){		


		ContentValues va1 = new ContentValues();
		va1.put("kmh", 0);
		int re1 = this.getWritableDatabase().update("s"+sdt, va1, "nd=?", new String[]{giatricu});

		ContentValues va = new ContentValues();
		va.put("nd", giatrimoi);
		int re = this.getWritableDatabase().update("s"+sdt, va, "nd=?", new String[]{giatricu});

		ContentValues va2 = new ContentValues();
		va2.put("nd", giatrimoi);
		int re2 = this.getWritableDatabase().update("dsht", va2, "nd=?", new String[]{giatricu});

		if(re + re1 + re2 == 3){
			return true;
		}else {
			return false;					
		}
	}

	//Kiem tra bang co ton tai
	public boolean tontai(String sdt) {
		if(sdt.startsWith("+")){
			sdt = sdt.substring(1);
		}
		boolean kq = false;
		try{
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT * FROM s"+sdt,null);
			c.moveToFirst();
			kq = true;
		}catch(Exception e){
			kq = false;			
		}

		return kq;
	}

	// Xoa bang
	public void xoabang(String sdt){
		if(sdt.startsWith("+")){
			sdt = sdt.substring(1);
		}
		this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS s" + sdt);		
	}



	//Lay dsht
	public ArrayList<Mot_Dong> laydsht(){
		ArrayList<Mot_Dong> dsht = new ArrayList<Mot_Dong>();		
		Cursor c = this.getReadableDatabase().query("dsht", null,null, null, null, null,null);
		c.moveToLast();
		while(c.isBeforeFirst()==false){			
			String xt = c.getString(2);
			Mot_Dong row = new Mot_Dong(R.drawable.avatar,c.getString(1),xt,"",c.getString(1));				
			dsht.add(row);
			c.moveToPrevious();
		}		
		c.close();
		return dsht;
	}

	//Luu vao dsht
	public void themDsht(String sdt, String ndxemtruoc){
		if(sdt.startsWith("+")){
			sdt = sdt.substring(1);
		}
		ContentValues in = new ContentValues();
		in.put("sdt",sdt);
		in.put("nd", ndxemtruoc);			
		this.getWritableDatabase().insert("dsht",null,in);	
	}

	//Xoa 1 dong ds ht
	public void xoa1dongDsht(String sdt){
		if(sdt.startsWith("+")){
			sdt = sdt.substring(1);
		}
		this.getWritableDatabase().delete("dsht", "sdt=?", new String[]{sdt});

	}






	////////////////////////////////////////////////////////////////////////////////
	// Bao mat RSA

	//Them key
	public void themPubKey(String sdtKeyTg){
		ContentValues in = new ContentValues();
		String sdt = sdtKeyTg.substring(0,sdtKeyTg.indexOf("\n"));
		String key = sdtKeyTg.substring(sdtKeyTg.indexOf("\n")+1,sdtKeyTg.lastIndexOf("\n"));
		String tg = sdtKeyTg.substring(sdtKeyTg.lastIndexOf("\n")+1);
		in.put("sdt", sdt);
		in.put("pubkey", key);	
		in.put("tg", tg);

		this.getWritableDatabase().insert("lstPublicKey",null,in);	
	}


	//Lay ds public key
	public ArrayList<String> laypubkey(){
		ArrayList<String> lstkey = new ArrayList<String>();
		Cursor c = this.getReadableDatabase().query("lstPublicKey", null,null, null, null, null,null);
		c.moveToFirst();
		while(c.isAfterLast()==false){			
			String s = "Số điện thoại:"+c.getString(1)+"\n"+c.getString(2)+"\nThời điểm lấy: "+c.getString(3);			
			lstkey.add(s);
			c.moveToNext();
		}		
		c.close();
		return lstkey;

	}





	@Override
	public void onCreate(SQLiteDatabase db) {
		//Khoi tao dsht
		db.execSQL("CREATE TABLE dsht(id integer primary key autoincrement, sdt text not null, nd text not null);");	
		//Khoi tao bang chua key
		db.execSQL("CREATE TABLE lstPublicKey(id integer primary key autoincrement,sdt text not null, pubkey text not null, tg text not null);");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}










}
