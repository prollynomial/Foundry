package com.adamcarruthers.foundry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.adamcarruthers.foundry.apt.SourceManager;

public class SourcesBrowser extends ListFragment {
	
	private ArrayList<String> sources;
	
	private Context mContext;
	private static SourceManager srcMan;
	
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
		
		setListAdapter(new CustomArrayAdapter<String>(mContext, R.layout.source_list_item, srcMan.getSourceList()));
		
        View view = inflater.inflate(R.layout.source_browser, container, false);
        return view;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		menu.add(0, Constants.MENU_ADD_SOURCE_ID, 0, R.string.add_source).setIcon(android.R.drawable.ic_menu_add);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case Constants.MENU_ADD_SOURCE_ID: {
				DialogFragment newFragment = new AddSourceDialog();
			    newFragment.show(getFragmentManager(), "dialog");
			    return true;
			}
		}
		
		return false;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO display all the packages offered by selected repo
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
            TextView name = (TextView) v.findViewById(R.id.source_list_item_name);
            TextView address = (TextView) v.findViewById(R.id.source_list_item_address);

            Item it = items.get(position);
            String[] parts = ((String) it).split(" ");
            name.setText(parts[1]);
            address.setText(parts[2]);
            image.setImageResource(R.drawable.source_icon);

            return v;
        }
	}
	
	// display a Dialog for adding sources
	public static class AddSourceDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			View layout = getActivity().getLayoutInflater().inflate(R.layout.add_source_dialog, null);
			final EditText sourceName = (EditText) layout.findViewById(R.id.add_source_dialog_text);
			
			return new AlertDialog.Builder(getActivity())
				.setTitle(getActivity().getResources().getString(R.string.add_source_dialog_title))
				.setView(layout)
				.setPositiveButton(R.string.add_source_dialog_add, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						try {
							srcMan.addSource(sourceName.getText().toString());
						} catch (IOException e) {
							e.printStackTrace();
							// TODO show Toast for error
						}
						dialog.dismiss();
					}
				})
				.setNegativeButton(R.string.add_source_dialog_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.show();
		}
	}
}