package com.adamcarruthers.foundry;

import java.io.DataOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

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
		   p = Runtime.getRuntime().exec("su");
		     
		   DataOutputStream os = new DataOutputStream(p.getOutputStream());
		   os.writeBytes("echo \"Do I have root?\" >/system/temporary.txt\n");
		     
		   os.writeBytes("exit\n");
		   os.flush();
		   try {
		      p.waitFor();
		      if (p.exitValue() != 255) {
		    	  // remove the file we just wrote
		    	  os = new DataOutputStream(p.getOutputStream());
				  os.writeBytes("rm /system/temporary.txt\n");
				  os.writeBytes("exit\n");
				  os.flush();
				  
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
}