package com.adamcarruthers.foundry.apt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.os.Build;
import android.os.Environment;

import com.adamcarruthers.foundry.Constants;
import com.adamcarruthers.foundry.Utils;

public class SourceManager {
	
	private ArrayList<String> sourceList = new ArrayList<String>();
	File foundryList;
	
	public SourceManager() throws IOException {
		// open /system/etc/apt/sources.list.d/foundry.list
		File listLocation = new File(Constants.SOURCE_LIST_LOCATION);
		foundryList = new File(listLocation, "sources.list");
		
		if (!foundryList.exists()) {
			// create foundry.list from scratch
			listLocation.mkdirs();
			foundryList.createNewFile();
			
			addSource("http://apt.sudoadam.com/");
		} else {
			// foundry.list exists, so populate sourceList! NAO!
			populateSourceListFromFile();
		}
	}

	private void writeSourcesToDisk() throws IOException {
		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(foundryList));
			
			for (String source : sourceList) {
				out.write(source);
				out.write("\n");
			}
		} finally {
			out.close();
		}
	}

	private ArrayList<String> populateSourceListFromFile() throws IOException {
		InputStream in = null;
		
		try {
			in = new FileInputStream(foundryList);
			BufferedReader bf = new BufferedReader(new InputStreamReader(in));
			
			String line = "";
			
			while (((line = bf.readLine()) != null) && (line != "")) {
				sourceList.add(line);
			}
		} finally {
			in.close();
		}
		return null;
	}
	
	protected String formatSource(String source) {
		return "deb "
		+ (source.startsWith("http://") ? source : ("http://" + source))
		+ (source.endsWith("/") ? "" : "/")
		+ " android/"
		+ Utils.versionNameToString(Build.VERSION.SDK_INT)
		+ " main";
	}

	public void addSource(String source) throws IOException {
		sourceList.add(formatSource(source));
		writeSourcesToDisk();
	}
	
	public boolean editSource(int id, String source) throws IOException {
		// Cannot edit imaginary or main sources
		if(id <= 0)
			return false;
		sourceList.set(id, formatSource(source));
		writeSourcesToDisk();
		return true;
	}
	
	public ArrayList<String> getSourceList() {
		return sourceList;
	}
	
	public boolean removeSource(int id) throws IOException {
		if (id > 0) {
			sourceList.remove(id);
			writeSourcesToDisk();
			return true;
		} else {
			// you can't delete mah repo! <Trollface.tiff>
			return false;
		}
	}
}
