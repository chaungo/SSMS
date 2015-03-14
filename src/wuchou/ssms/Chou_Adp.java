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
public class Chou_Adp extends ArrayAdapter<Mot_Dong> {
	Context cont;
	ArrayList<Mot_Dong> dsd;


	public Chou_Adp(Context context, ArrayList<Mot_Dong> modelsArrayList){
		super(context, R.layout.mot_dong, modelsArrayList);
		this.cont = context;
		this.dsd = modelsArrayList;	
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inf = (LayoutInflater)cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = null;

		row = inf.inflate(R.layout.mot_dong, parent, false);
		CircularImageView ava = (CircularImageView)row.findViewById(R.id.ImgVava);
		TextView hoten = (TextView)row.findViewById(R.id.tvHoTen);
		TextView xemtruoc = (TextView)row.findViewById(R.id.tvNdxemtruoc);
		TextView chuadoc = (TextView)row.findViewById(R.id.soTnChuaDoc);

		ava.setImageResource(dsd.get(position).ava);
		hoten.setText(dsd.get(position).ten);
		xemtruoc.setText(dsd.get(position).xemtruoc);
		chuadoc.setText(dsd.get(position).bodem);



		return row;
	}
}

