package sk.peterjurkovic.dril.v2.adapters;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.LectureDBAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 *
 */
public class LectureAdapter extends CursorAdapter {

	public LectureAdapter(Context context, Cursor c, int flags){
		super(context, c, flags);
	}
	
	
	@Override
	public void bindView(View oldView, Context ctx, Cursor c) {
		
		int lectureNameIndex = c.getColumnIndex( LectureDBAdapter.LECTURE_NAME );
		int wordCountIndex = c.getColumnIndex(LectureDBAdapter.WORDS_IN_LECTURE);
		int activeWordCountIndex = c.getColumnIndex(
										LectureDBAdapter.ACTIVE_WORDS_IN_LECTURE);
		
		((TextView) oldView.findViewById(R.id.adapter_lecture_name)).
											setText(c.getString(lectureNameIndex));
		Resources res = ctx.getResources();		
		((TextView) oldView.findViewById(R.id.adapter_lecture_descr))
		.setText(res.getString(
						R.string.word_count, 
						c.getInt(wordCountIndex),
						c.getInt(activeWordCountIndex)
					));

	}

	@Override
	public View newView(Context ctx, Cursor c, ViewGroup root) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View view = inflater.inflate(R.layout.v2_lecture, root, false);
		bindView(view, ctx, c);
		return view;
	}

}
