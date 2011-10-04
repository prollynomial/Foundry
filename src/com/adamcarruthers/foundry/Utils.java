package com.adamcarruthers.foundry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
	public static void loadFilesFromSubsystemPackage() throws Exception {
		// check if subsystem is already extracted. We can use any arbitrary file in the subsystem
		if (!(new File(Constants.WORKING_DIRECTORY, "bin/dpkg").exists())) {
			// check that subsystem.zip exists
			if(!(new File(Environment.getExternalStorageDirectory(), "subsystem.zip").exists())) {
				//download zip
				// TODO: upload finalized subsystem.zip so that we can download it here
			} else {
				//extract zip
				extractSubsystemZip();
			}
		}
	}
	
   private static void extractSubsystemZip() throws Exception {
	   final String extractLocation = Constants.WORKING_DIRECTORY;
	   
	   ZipInputStream zipin = new ZipInputStream(
			   					new FileInputStream(
			   					new File(Environment.getExternalStorageDirectory(), "subsystem.zip")));
	   ZipEntry ze = null;
	   while ((ze = zipin.getNextEntry()) != null) {
		   if(ze.isDirectory()) { 
			   File f = new File(extractLocation + ze.getName());
			   f.mkdirs();
		   } else { 
			   OutputStream outputStream = new FileOutputStream(extractLocation + ze.getName());
			   byte buffer[] = new byte[1024];
			   int length;
			   
			   while((length = zipin.read(buffer)) > 0) {
				   outputStream.write(buffer, 0, length);
			   }
		 
			   zipin.closeEntry();
			   outputStream.close();
		   } 
	   }
	   zipin.close();
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
    
    public static void createSubsystem(Resources res) throws Exception {
    	// create the Unix subsystem
    	List<String> dirs = Arrays.asList(res.getStringArray(R.array.subsystem_folders));
    	new File(Constants.WORKING_DIRECTORY).delete();
		new File(Constants.WORKING_DIRECTORY).mkdirs();
    	for (String f : dirs) {
    		new File(Constants.WORKING_DIRECTORY + f).mkdirs();
    	}
    	
    	loadFilesFromSubsystemPackage();
    	
    	// chmod 777 bin and methods
    	execute("chmod 777 " + Constants.WORKING_DIRECTORY + "bin/*");
    	execute("chmod 777 " + Constants.WORKING_DIRECTORY + "usr/lib/apt/methods/*");
    	execute("echo \"\" > " + Constants.WORKING_DIRECTORY + "var/dpkg/status");
    	execute("echo \"\" > " + Constants.WORKING_DIRECTORY + "var/dpkg/available");
    }
}