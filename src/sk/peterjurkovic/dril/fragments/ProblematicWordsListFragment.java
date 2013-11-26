package sk.peterjurkovic.dril.fragments;


import sk.peterjurkovic.dril.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

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
		title = getActivity().getString(R.string.header_stats_problematics_words);
		return inflater.inflate(R.layout.v2_problematic_word_fragment, container, false);
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
