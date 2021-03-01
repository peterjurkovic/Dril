package sk.peterjurkovic.dril.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import sk.peterjurkovic.dril.R;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Nov 26, 2013
 *
 */
public class ProblematicWordsListFragment extends WordListFragment implements OnClickListener, StatisticsHeader {
	
	private String title;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.v2_problematic_word_fragment, container, false);
		((TextView)view.findViewById(R.id.statisticProblematicWordsHeader)).setText(getString(R.string.header_stats_problematics_words));
		return view;
	}
	
	
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

	
	
	@Override
	public String getTitle() {
		if(title == null){
			return "";
		}
		return title;
	}
		
	

	
}
