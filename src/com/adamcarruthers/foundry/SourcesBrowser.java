package com.adamcarruthers.foundry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
		
		// commence kludge #1: here we add a blank string to the array for our "Add source" button
		ArrayList<String> sources = srcMan.getSourceList();
		sources.add("");
		// end kludge #1
		
		setListAdapter(new CustomArrayAdapter<String>(mContext, R.layout.source_list_item, sources));
		
        View view = inflater.inflate(R.layout.source_browser, container, false);
        return view;
    }
	
	// create custom adapter to draw images beside sources in the list
	private class CustomArrayAdapter<Item> extends ArrayAdapter<Item> {

		private List<Item> items;
		
		public CustomArrayAdapter(Context context, int resource, List<Item> objects) {
			super(context, resource, objects);
			items = objects;
		}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	View v = convertView;
            if (v == null) {
            	LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	v = vi.inflate(R.layout.source_list_item, null);
            }
            
            ImageView image = (ImageView) v.findViewById(R.id.source_list_item_image);
            TextView text = (TextView) v.findViewById(R.id.source_list_item_text);

            // include the add sources button at the top of the list
            if (position == 0) {
            	text.setText(getResources().getString(R.string.add_source));
            	image.setImageResource(android.R.drawable.ic_menu_add);
            } else {
            	Item it = items.get(position - 1);
            	text.setText((String) it);
            	image.setImageResource(R.drawable.source_icon);
            }
            return v;
        }
	}
}