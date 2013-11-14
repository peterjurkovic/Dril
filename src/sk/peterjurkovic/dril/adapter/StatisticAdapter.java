package sk.peterjurkovic.dril.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.StatisticDbAdapter;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class StatisticAdapter extends CursorAdapter {

	private double prevAvg = 0;
	
	LayoutInflater inflater;
	
	Cursor c;
	
	public StatisticAdapter(Context context, Cursor c, boolean autoRequery){
		super(context, c, autoRequery);
		this.c = c;
		inflater = LayoutInflater.from(context);
	}
	
	
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent){
		 
		 ViewHolder holder;
		 if(convertView == null){
        	convertView = inflater.inflate(R.layout.statistic, null);
        	holder = new ViewHolder();
		 	holder.dateView = (TextView) convertView.findViewById(R.id.statisticDate);
			holder.avgRateView = (TextView) convertView.findViewById(R.id.statisticRate);
			holder.imgStateView = (ImageView) convertView.findViewById(R.id.imgState);
			convertView.setTag(holder);
         }else{
        	 holder = (ViewHolder)convertView.getTag();
         }
		 this.c.moveToPosition(position);
		 			 	
		 	holder.drilDateIndex = c.getColumnIndex( StatisticDbAdapter.DATE_LOCALTIME );
			holder.rateIndex = c.getColumnIndex(StatisticDbAdapter.RATE);
			holder.hitIndex = c.getColumnIndex(StatisticDbAdapter.HITS);
			
		 	String date = customDateFormat(c.getString( holder.drilDateIndex ));
			double rate = c.getDouble(holder.rateIndex);
			double hit = c.getDouble(holder.hitIndex);
			double avgRate = (rate / hit) * 100.0;
			avgRate = Math.round(avgRate); 

			holder.dateView.setText(date);
			
			holder.avgRateView.setText((avgRate / 100) + "");
			holder.imgStateView.setImageResource(getResource(avgRate));
			holder.dateView.setText(date); 				
			prevAvg = avgRate;
			return convertView;
	 }
	
	 
	@Override
	public void bindView(View oldView, Context ctx, Cursor c) {
		
	}
	
	@Override
	public View newView(Context ctx, Cursor c, ViewGroup root) {
		return null;
	}
	
	
	
	public static String customDateFormat(String date){
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd mm:ss"); 
		Date dateObj = null;
		try {
			dateObj = curFormater.parse(date);
		} catch (ParseException e) {
			Log.d("StatisticAdapter", e.getMessage());
		} 
		SimpleDateFormat postFormater = new SimpleDateFormat("dd.MM.yyyy / mm:ss"); 
		return postFormater.format(dateObj);
	}
	
	
	private int getResource(double val){
		if(prevAvg == 0 || prevAvg == val){
			return R.drawable.neutral;
		}
		if(prevAvg < val && prevAvg != 0){
			return R.drawable.bed;
		}
		
		if(prevAvg > val && prevAvg != 0){
			return R.drawable.good;
		}
		return R.drawable.neutral;
	}
	
	
	private static class ViewHolder{
		int drilDateIndex;
		int rateIndex;
		int hitIndex;
		TextView dateView;
		TextView avgRateView;
		ImageView imgStateView;
	}
	
}
