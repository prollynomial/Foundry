package com.adamcarruthers.foundry;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class Homepage extends Fragment {
	
	private View view;
	private Context mContext;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		mContext = getActivity().getApplicationContext();

		view = inflater.inflate(R.layout.homepage, container, false);
		
	    WebView mWebView = (WebView) view.findViewById(R.id.webview);
	    mWebView.getSettings().setJavaScriptEnabled(true);
	    mWebView.loadUrl("http://lifedropper.appspot.com/foundry-" + getVersionName(mContext, Homepage.class) + ".html");
		        
        return view;
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		((WebView) view.findViewById(R.id.webview)).saveState(outState);
		super.onSaveInstanceState(outState);
	}
	
	public static String getVersionName(Context context, Class<?> cls) {
		try {
			ComponentName comp = new ComponentName(context, cls);
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
			return pinfo.versionName;
		} catch (android.content.pm.PackageManager.NameNotFoundException e) {
			return null;
		}
	}
}
