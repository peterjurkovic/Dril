package sk.peterjurkovic.dril.fragments;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.preferencies.PreferenceFragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public  class DrilPreferenceFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		addPreferencesFromResource(R.xml.app_preferencies);
	}
	
	
}
