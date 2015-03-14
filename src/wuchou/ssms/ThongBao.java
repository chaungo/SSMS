package wuchou.ssms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;


@SuppressLint("NewApi")
public class ThongBao extends DialogFragment {

	@SuppressLint("NewApi")
	@Override

	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Message").setPositiveButton("OK!", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				

			}
		}).setNegativeButton("Exit", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		return builder.create();


	}

	
}
