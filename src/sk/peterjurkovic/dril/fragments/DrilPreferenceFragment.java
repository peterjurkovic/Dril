package sk.peterjurkovic.dril.fragments;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.preferencies.PreferenceFragment;
import android.os.Bundle;

public  class DrilPreferenceFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		addPreferencesFromResource(R.xml.app_preferencies);
	}
	
	
}
