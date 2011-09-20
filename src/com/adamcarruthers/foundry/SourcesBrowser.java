package com.adamcarruthers.foundry;

import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.adamcarruthers.foundry.apt.SourceManager;

public class SourcesBrowser extends ListFragment {
	
	private Context mContext;
	private SourceManager srcMan;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		mContext = getActivity().getApplicationContext();
		
		try {
			srcMan = new SourceManager();
		} catch(IOException e) {
			e.printStackTrace();
			// TODO show something in the UI
		}
		
		setListAdapter(new ArrayAdapter<String>(mContext, R.layout.source_list_item, R.id.text1, srcMan.getSourceList()));
		
        View view = inflater.inflate(R.layout.source_browser, container, false);
        return view;
    }
}