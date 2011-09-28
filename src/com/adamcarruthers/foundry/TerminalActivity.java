package com.adamcarruthers.foundry;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TerminalActivity extends Fragment {
	
	private TextView terminalOutput;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {		
        View view = inflater.inflate(R.layout.terminal, container, false);
        
        terminalOutput = (TextView) view.findViewById(R.id.terminal_output);
        
        // TODO: do some more, this is an awesome thing to do
        new ExecuteInShell().execute(Constants.WORKING_DIRECTORY + "apt-get --version");
        return view;
    }
	
   /*
    * Courtesy of Daniel Huckaby (HandlerExploit)
    */
	private class ExecuteInShell extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... cmds) {
			final Process p;
	        try {
	        	p = Utils.execute(cmds[0]);
	        	
	            BufferedReader stdInput = new BufferedReader(
	                    new InputStreamReader(p.getInputStream()));
	        	
	        	Thread thread = new Thread(new Runnable() {
	            	  public void run() {
	            		  try {
	            			  p.waitFor();
	            		  } catch (InterruptedException e) {
	            			  e.printStackTrace();
	            		  }
	            	  }
	              });
	              thread.start();
	        	
	        	final StringBuilder status = new StringBuilder();
	    		while (thread.isAlive() || stdInput.ready()) {
	    			final String newLine = stdInput.readLine();
	    			if (newLine != null) {
	    				status.append(newLine + "\n");
	    			}
	    			getActivity().runOnUiThread(new Runnable() {
	    				public void run() {
	    					terminalOutput.setText(status.toString());
	    				}
	    			});
	    		}
	        	
	            stdInput.close();
	        } catch (Exception e) {
	     	   e.printStackTrace();
	        }
			return null;
		}
	}
}
