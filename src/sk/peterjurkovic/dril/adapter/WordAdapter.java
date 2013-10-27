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
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class WordAdapter extends CursorAdapter{
	
	private List<Boolean> mCheckedState;
	private Set<Long> selectedItems = new HashSet<Long>();
	
	private OnClickListener onClickListener;
	
	public WordAdapter(Context context, Cursor cursor, int flags){
		super(context, cursor, flags);
		for(mCheckedState = new ArrayList<Boolean>(); !cursor.isAfterLast(); cursor.moveToNext()) {
            mCheckedState.add( false );
        }
		
	}
		
	@Override
	public void bindView(View oldView, Context ctx, Cursor c) {
		ViewHolder holder = (ViewHolder) oldView.getTag();
		RowData data = new RowData();
		data.id = c.getLong(c.getColumnIndex(WordDBAdapter.WORD_ID));
		data.question = c.getString(holder.wordQuestionIndex);
		data.answer = c.getString(holder.wordAnswerIndex);
		data.isActive = c.getInt(holder.isActiveIndex);
		data.position = c.getPosition();
		holder.questonView.setText(data.question);
		holder.answerView.setText(data.answer);
		holder.checkBoxView.setChecked(mCheckedState.get(data.position));
		holder.checkBoxView.setTag(data);
		
		if(data.isActive == 1)
			oldView.setBackgroundResource(R.drawable.word_active);
		else
			oldView.setBackgroundResource(R.drawable.word_normal);
	}			

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (!mDataValid) {
	        throw new IllegalStateException("this should only be called when the cursor is valid");
	    }
	    if (!mCursor.moveToPosition(position)) {
	        throw new IllegalStateException("couldn't move cursor to position " + position);
	    }
	    View v;
	    if (convertView == null) {
	        v = newView(mContext, mCursor, parent);
	    } else {
	        v = convertView;
	    }
	    bindView(v, mContext, mCursor);
	    return v;
	}

	
	
	@Override
	public View newView(Context ctx, Cursor c, ViewGroup root) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View view = inflater.inflate(R.layout.v2_word, root, false);
		ViewHolder holder  =   new ViewHolder();
		holder.questonView = (TextView) view.findViewById(R.id.adapter_question);
		holder.answerView = (TextView) view.findViewById(R.id.adapter_answer);
		holder.checkBoxView = (CheckBox) view.findViewById(R.id.myCheckBox);
		holder.wordQuestionIndex = c.getColumnIndex( WordDBAdapter.QUESTION );
		holder.wordAnswerIndex = c.getColumnIndex( WordDBAdapter.ANSWER );
		holder.isActiveIndex = c.getColumnIndex(WordDBAdapter.ACTIVE);
		view.setOnClickListener(onClickListener);
		view.setTag(holder);
		bindView(view, ctx, c);
		return view;
	}	

	 
	
	public long updateItemState(View view){
		CheckBox  checkbox =  (CheckBox)view.findViewById(R.id.myCheckBox);
		boolean isChecked = view.isSelected();
        RowData data = (RowData) checkbox.getTag();
        mCheckedState.set(data.position, isChecked);
        if(!isChecked){
        	selectedItems.add(Long.valueOf( data.id )); 
        }else{
        	selectedItems.remove(Long.valueOf( data.id )); 
        }		
        checkbox.setChecked(!isChecked);
        view.setSelected(!isChecked);
        return data.id;
	}

	

	
	
	private   static  class   ViewHolder  {
        int    wordQuestionIndex;
        int    wordAnswerIndex;
        int	isActiveIndex;
        TextView   questonView;
        TextView   answerView;
        CheckBox	checkBoxView;

   }
	 
	 private static class RowData {
	        long id;
	        String question;
	        String answer;
	        int isActive;
	        int position;
	    } 
	 
	 public Set<Long> getCheckedItems(){
		 return selectedItems;
	 }
	
	 public void setOnClickListener(OnClickListener onClickListener){
		 this.onClickListener = onClickListener;
	 }
}
