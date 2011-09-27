package com.adamcarruthers.foundry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;

public class Utils {
	public static String versionNameToString(int versionCode) {
		if (versionCode < Build.VERSION_CODES.DONUT) {
			return "unsupported";
		}
		if (versionCode > 13) {
			return "unsupported";
		}
		
		switch (versionCode) {
		case Build.VERSION_CODES.DONUT: {
			return "donut";
		}
		case 5: {
			return "eclair";
		}
		case 6: {
			return "eclair";
		}
		case 7: {
			return "eclair";
		}
		case 8: {
			return "froyo";
		}
		case 9: {
			return "gingerbread";
		}
		case 10: {
			return "gingerbread";
		}
		case 11: {
			return "honeycomb";
		}
		case 12: {
			return "honeycomb";
		}
		case 13: {
			return "honeycomb";
		}
		}
		return null;
	}
	
	public static Intent share(Context context) {
	     final Intent intent = new Intent(Intent.ACTION_SEND);

	     intent.setType("text/plain");
	     intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_text));
	     
	     return Intent.createChooser(intent, context.getResources().getString(R.string.share_dialog_title));
	}
	
	public static boolean isRooted() {
		Process p;
		try {
		   p = new ProcessBuilder("su").start();
		   BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		   BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
		   
		   stdin.write("whoami");
		   stdin.newLine();
		   stdin.write("exit");
		   stdin.newLine();
		   stdin.close();
		   try {
		      p.waitFor();
			  if(!stdout.ready())
				  return false;
			  String user = stdout.readLine(); //We only expect one line of output
			  stdout.close();
		      if (user == "root") {
		    	  return true;
		      } else {
		    	  return false;
		      }
		   } catch (InterruptedException e) {
			   e.printStackTrace();
			   return false;
		   }
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
   /*
	* Courtesy of Daniel Huckaby (HandlerExploit)
	*/
	public static void loadBinaryFromAssets(AssetManager assMan, String item) throws Exception {
		String path = getPath(item);
		InputStream inputStream = assMan.open(item);
		OutputStream outputStream = new FileOutputStream(new File(path));
		byte buffer[] = new byte[1024];
		int length;
		while((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		outputStream.close();
		inputStream.close();
		execute("chmod 777 " + path);
	}
	
   /*
	* Courtesy of Daniel Huckaby (HandlerExploit)
	*/
	public static Process execute(String command) throws Exception {
		final Process process = new ProcessBuilder("sh").redirectErrorStream(true).start();
		
        BufferedWriter stdOutput = new BufferedWriter(
        		new OutputStreamWriter(process.getOutputStream()));

        stdOutput.write(command + "; exit\n");
        stdOutput.flush();
        stdOutput.close();
        
		return process;
	}
	
    private static String getPath(String item) {
		return Constants.WORKING_DIRECTORY + item;
	}
}