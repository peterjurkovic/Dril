package sk.peterjurkovic.dril.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class WordAdapter extends CursorAdapter{
	
	private List<Boolean> mCheckedState;
	private Set<Long> selectedItems = new HashSet<Long>();
	private OnClickListener onClickListener;
	private OnLongClickListener onLongClickListener; 
	private LayoutInflater inflater = null;
	private int selectedColor = 0;
	
	
	public WordAdapter(Context context, Cursor cursor, int flags){
		super(context, cursor, flags);
		for(mCheckedState = new ArrayList<Boolean>(); !cursor.isAfterLast(); cursor.moveToNext()) {
            mCheckedState.add( false );
        }
		initLongClickListener();
		inflater = LayoutInflater.from(context);
		selectedColor = context.getResources().getColor(R.color.selectedItem);
	}
	
	public boolean hasSelectedItems(){
		return selectedItems.size() > 0;
	}
	
	public int getCountOfSelected(){
		return selectedItems.size();
	}
		
	@Override
	public void bindView(View oldView, Context ctx, Cursor c) {
		ViewHolder holder = (ViewHolder) oldView.getTag();
		RowData data = new RowData();
		data.id = c.getLong(c.getColumnIndex(WordDBAdapter.WORD_ID));
		data.isActive = c.getInt(holder.isActiveIndex);
		data.position = c.getPosition();
		holder.checkBoxView.setChecked(mCheckedState.get(data.position));
		holder.checkBoxView.setTag(data);
		if(holder.checkBoxView.isChecked()){
			oldView.setBackgroundColor( selectedColor );
		}else if(data.isActive == 1){
			oldView.setBackgroundResource(R.drawable.word_active);
		}else{
			oldView.setBackgroundResource(R.drawable.word_normal);
		}
	}			

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
    
		if (!mDataValid) {
	        throw new IllegalStateException("this should only be called when the cursor is valid");
	    }
	    if (!mCursor.moveToPosition(position)) {
	        throw new IllegalStateException("couldn't move cursor to position " + position);
	    }
	    if (convertView == null) {
	    	convertView = newView(mContext, mCursor, parent);
	    } 
	    
	    bindView(convertView, mContext, mCursor);
	    return convertView;
	}

	
	
	@Override
	public View newView(Context ctx, Cursor c, ViewGroup root) {
		View view = inflater.inflate(R.layout.v2_word, root, false);
		ViewHolder holder  =   new ViewHolder();
		holder.questonView = (TextView) view.findViewById(R.id.adapter_question);
		holder.answerView = (TextView) view.findViewById(R.id.adapter_answer);
		holder.checkBoxView = (CheckBox) view.findViewById(R.id.myCheckBox);
		final int aIndex = c.getColumnIndex( WordDBAdapter.QUESTION );
		final int qIndex = c.getColumnIndex( WordDBAdapter.ANSWER );
		holder.isActiveIndex = c.getColumnIndex(WordDBAdapter.ACTIVE);
		holder.questonView.setText( c.getString(qIndex) );
		holder.answerView.setText( c.getString(aIndex) );;
		view.setOnClickListener(onClickListener);
		view.setOnLongClickListener(onLongClickListener);
		view.setTag(holder);
		bindView(view, ctx, c);
		return view;
	}	

	 
	
	public long updateItemState(View view){
		CheckBox  checkbox =  (CheckBox)view.findViewById(R.id.myCheckBox);
		boolean isChecked = !checkbox.isChecked();
		RowData data = (RowData) checkbox.getTag();
        mCheckedState.set(data.position, isChecked);
        if(isChecked){
        	selectedItems.add(Long.valueOf( data.id )); 
        	view.setBackgroundResource(R.drawable.v2_word_list_item_selected);
        }else{
        	view.setBackgroundResource(R.drawable.v2_word_list_item); 
        	selectedItems.remove(Long.valueOf( data.id )); 
        }		
        checkbox.setChecked(isChecked);
        return data.id;
	}

	private   static  class   ViewHolder  {
        int	isActiveIndex;
        TextView questonView;
        TextView answerView;
        CheckBox checkBoxView;

   }
	 
	 private static class RowData {
	        long id;
	        int isActive;
	        int position;
	    } 
	 
	 public Set<Long> getCheckedItems(){
		 return selectedItems;
	 }
	
	 public void setOnClickListener(OnClickListener onClickListener){
		 this.onClickListener = onClickListener;
	 }
	 
	 private void initLongClickListener(){
		 this.onLongClickListener = new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					v.getParent().showContextMenuForChild(v);
					return false;
				}
			};
	 }
}
