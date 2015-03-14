package wuchou.ssms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {



		Bundle bundle = intent.getExtras();        
		SmsMessage[] msgs = null;
		String messageReceived = ""; 
		String add = null;
		DatabaseHelper db = new DatabaseHelper(context);

		if (bundle != null)
		{
			//---retrieve the SMS message received---
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];         

			for (int i=0; i<msgs.length; i++)

			{
				msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
				add = msgs[i].getOriginatingAddress().replaceAll("\\s", "").replaceAll("-", "");
				messageReceived += msgs[i].getMessageBody().toString();
			}


			if(messageReceived.substring(0,4).equals("ssms")){				
				String nd = messageReceived.substring(4,messageReceived.length()-1);	

				if(messageReceived.endsWith("1")) {					
					luuTnNhanVe(db, add, nd,1);
				}else{ 					
					luuTnNhanVe(db, add, nd,2);
				}

				//Luu tin nhan




				//---display the new SMS message---
				Toast.makeText(context, messageReceived, Toast.LENGTH_LONG).show();
			}
			// Get the Sender Phone Number			

		}

	}


	public void luuTnNhanVe(DatabaseHelper db,String add,String nd,int kmh){
		if(db.tontai(add)) {
			db.themNd(add, nd,1,kmh,MainActivity.laytg());				
			db.xoa1dongDsht(add);
			db.themDsht(add,nd);
			MainActivity.capnhatsdht(MainActivity.lst);		
			DanhSachTinNhan.capnhatdstn(DanhSachTinNhan.ds_tn);

		}else {
			db.taoBang(add);
			db.themNd(add, nd, 1,kmh,MainActivity.laytg());				
			db.themDsht(add,nd);
			MainActivity.capnhatsdht(MainActivity.lst);
		}
	}


}

