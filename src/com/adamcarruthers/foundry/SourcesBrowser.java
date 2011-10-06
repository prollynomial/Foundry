package com.adamcarruthers.foundry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
		
		sources = new ArrayList<String>();
		
		new GetSourcesFromDisk().execute();
		
        View view = inflater.inflate(R.layout.source_browser, container, false);
        return view;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		registerForContextMenu(getListView());
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
			    newFragment.show(getFragmentManager(), "adddialog");
			    return true;
			}
		}
		return false;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo listItem = (AdapterView.AdapterContextMenuInfo)menuInfo;
		menu.setHeaderTitle(R.string.context_menu_title);
		String[] menuItems = getResources().getStringArray(R.array.source_context);
		for(int i = 0; i < menuItems.length; i++){
			if((listItem.position < srcMan.DEFAULTS.length) && (i != Constants.CONTEXT_MENU_OPEN)){
				menu.add(Menu.NONE, i, i, menuItems[i]).setEnabled(false);
			}else{
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem ctxItem){
		int ctxId = ctxItem.getItemId();
		AdapterView.AdapterContextMenuInfo listItem = (AdapterView.AdapterContextMenuInfo)ctxItem.getMenuInfo();
		switch(ctxId) {
			case Constants.CONTEXT_MENU_EDIT: {
				DialogFragment editFragment = new EditSourceDialog().newInstance(listItem.position);
				editFragment.show(getFragmentManager(), "editdialog");
				return true;
			}
			case Constants.CONTEXT_MENU_DELETE: {
				DialogFragment deleteFragment = new DeleteSourceDialog().newInstance(listItem.position);
				deleteFragment.show(getFragmentManager(), "deletedialog");
				return true;
			}
			case Constants.CONTEXT_MENU_OPEN: {
				// See onListItemClick below
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
	
	public class GetSourcesFromDisk extends AsyncTask<Void, Void, ArrayList<String>> {
		@Override
		protected void onPreExecute() {
			// TODO set up custom loading thing
		}
		
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			try {
				srcMan = new SourceManager();
			} catch (IOException e) {
				// TODO change custom loading thing to display error and persist
				e.printStackTrace();
				Log.e("Foundry", "Couldn't create SourceManager!!");
			}
			return srcMan.getSourceList();
		}
		
		
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO clear up loading screen
			sources.clear();
			sources.addAll(result);
			setListAdapter(new CustomArrayAdapter<String>(mContext, R.layout.source_list_item, sources));
		}
	}
	
	// display a Dialog for adding sources
	public class AddSourceDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			View layout = getActivity().getLayoutInflater().inflate(R.layout.add_source_dialog, null);
			final EditText sourceName = (EditText) layout.findViewById(R.id.add_source_dialog_text);
			
			return new AlertDialog.Builder(getActivity())
				.setTitle(getActivity().getResources().getString(R.string.add_source_dialog_title))
				.setView(layout)
				.setPositiveButton(R.string.add_source_dialog_add, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						try{
							srcMan.addSource(sourceName.getText().toString());
							new GetSourcesFromDisk().execute();
						}catch (Exception e) {
							e.printStackTrace();
						}finally {
							dialog.dismiss();
						}
					}
				})
				.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.show();
		}
	}
	
	// Display a dialog for editing a source
	public class EditSourceDialog extends DialogFragment {
		public EditSourceDialog newInstance(int id){
			EditSourceDialog d = new EditSourceDialog();
			Bundle args = new Bundle();
			args.putInt("id", id);
			args.putString("source", sources.get(id));
			d.setArguments(args);
			return d;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState){
			View layout = getActivity().getLayoutInflater().inflate(R.layout.edit_source_dialog, null);
			final int id = getArguments().getInt("id");
			final EditText sourceName = (EditText) layout.findViewById(R.id.edit_source_dialog_text);
			String[] parts = sources.get(id).split(" ");
			sourceName.setText(parts[1]);
			
			return new AlertDialog.Builder(getActivity())
				.setTitle(getActivity().getResources().getString(R.string.edit_source_dialog_title))
				.setView(layout)
				.setPositiveButton(R.string.edit_source_dialog_action, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						try{
							srcMan.editSource(id, sourceName.getText().toString());
							new GetSourcesFromDisk().execute();
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							dialog.dismiss();
						}
					}
				})
				.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						dialog.dismiss();
					}
				})
				.show();
		}
	}
	
	// Display a dialog to delete a source
	public class DeleteSourceDialog extends DialogFragment {
		public DeleteSourceDialog newInstance(int id){
			DeleteSourceDialog d = new DeleteSourceDialog();
			Bundle args = new Bundle();
			args.putInt("id", id);
			d.setArguments(args);
			return d;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState){
			final int id = getArguments().getInt("id");
			return new AlertDialog.Builder(getActivity())
				.setMessage(getActivity().getResources().getString(R.string.delete_source_dialog_message))
			.setPositiveButton(R.string.delete_source_dialog_yes, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					try{
						srcMan.removeSource(id);
						new GetSourcesFromDisk().execute();
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						dialog.dismiss();
					}
				}
			})
			.setNegativeButton(R.string.delete_source_dialog_no, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
				}
			})
			.show();
		}
	}
}