package sk.peterjurkovic.dril.adapter;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.StatisticDbAdapter;
import sk.peterjurkovic.dril.utils.ConversionUtils;
import sk.peterjurkovic.dril.utils.NumberUtils;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatisticAdapter extends CursorAdapter {

	private LayoutInflater inflater;	
	private Cursor c;
	private int padding;
	
	
	public StatisticAdapter(Context context, Cursor c, boolean autoRequery){
		super(context, c, autoRequery);
		this.c = c;
		inflater = LayoutInflater.from(context);
		padding = ConversionUtils.convertDpToPixel(5, context);
	}
	
	
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent){
		 
		 ViewHolder holder;
		 if(convertView == null){
        	convertView = inflater.inflate(R.layout.v2_stastistic, null);
        	holder = new ViewHolder();
		 	holder.dateView = (TextView) convertView.findViewById(R.id.statisticsDate);
			holder.learnedCardsView = (TextView) convertView.findViewById(R.id.statisticsLearnedCards);
			holder.ratingView = (TextView) convertView.findViewById(R.id.statisticsRating);
			convertView.setTag(holder);
         }else{
        	 holder = (ViewHolder)convertView.getTag();
         }
		 this.c.moveToPosition(position);
		 			 	
		 	holder.createdIndex = c.getColumnIndex( StatisticDbAdapter.CREATED_COLL );
			holder.learnedCardsIndex = c.getColumnIndex(StatisticDbAdapter.LEARNED_CARDS);
			holder.sumOrRatingIndex = c.getColumnIndex(StatisticDbAdapter.SUM_OR_RATING);
			holder.hits = c.getColumnIndex(StatisticDbAdapter.HITS);
			
			final long timestamp = c.getLong(holder.createdIndex);
			final int learnedCards = c.getInt(holder.learnedCardsIndex);
			final int sumOfRating = c.getInt(holder.sumOrRatingIndex);
			final int hits = c.getInt(holder.hits);
			
			double avgRate = (double)sumOfRating / (double)hits;
            
			
			holder.dateView.setText(ConversionUtils.timestampToString(timestamp)); 				
			holder.learnedCardsView.setText(learnedCards+ "");
			holder.ratingView.setText(String.format( "%.2f", avgRate ) + "");
			holder.ratingView.setBackgroundResource(getResourceByRating(avgRate));
			holder.ratingView.setPadding(padding, padding, padding, padding);
			return convertView;
	 }
	
	 
	@Override
	public void bindView(View oldView, Context ctx, Cursor c) {
		
	}
	
	@Override
	public View newView(Context ctx, Cursor c, ViewGroup root) {
		
		return null;
	}
	
	private int getResourceByRating(final double rating){
		if(rating < 2){
			return R.drawable.btn_1;
		}else if(rating < 3){
			return R.drawable.btn_2;
		}else if(rating < 4){
			return R.drawable.btn_3;
		}else if(rating < 4.5){
			return R.drawable.btn_4;
		}
		return R.drawable.btn_5;
	}
	
	
	private static class ViewHolder{
		int createdIndex;
		int learnedCardsIndex;
		int sumOrRatingIndex;
		int hits;
		TextView dateView;
		TextView learnedCardsView;
		TextView ratingView;
	}
	
}
