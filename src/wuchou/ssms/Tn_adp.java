package wuchou.ssms;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class Tn_adp extends ArrayAdapter<Tinnhan> {

	Context cont;
	ArrayList<Tinnhan> tn;

	public Tn_adp(Context context, ArrayList<Tinnhan> modelsArrayList){	
		super(context, R.layout.noidung_mot_tn, modelsArrayList);
		this.cont = context;
		this.tn = modelsArrayList;	
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inf = (LayoutInflater)cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(tn.get(position).dtinh==0)
		{
			View row = null;
			row = inf.inflate(R.layout.noidung_mot_tn, parent, false);
			TextView noidung = (TextView)row.findViewById(R.id.Ndtinnhan);
			TextView thoigian = (TextView)row.findViewById(R.id.thoigian);
			
			noidung.setText(tn.get(position).nd);
			thoigian.setText(tn.get(position).tgian);
			return row;
		}
		else 
		{
			View row = null;
			row = inf.inflate(R.layout.noidung_mot_tn_2, parent, false);
			TextView noidung = (TextView)row.findViewById(R.id.Noidungtn2);
			TextView thoigian = (TextView)row.findViewById(R.id.thoigian2);
			
			noidung.setText(tn.get(position).nd);
			thoigian.setText(tn.get(position).tgian);
			return row;
		}
		
	}


}
