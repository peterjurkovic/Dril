package sk.peterjurkovic.dril.fragments;

import sk.peterjurkovic.dril.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class ProblematicWordsListFragment extends WordListFragment implements OnClickListener {
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.v2_problematic_word_fragment, container, false);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
		
	

	
}
