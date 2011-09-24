package com.adamcarruthers.foundry;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class Homepage extends Fragment {
	
	private View view;
	private Context mContext;
	private LayoutInflater mInflater;
	private ViewGroup mContainer;
	private WebView mWebView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		mContext = getActivity().getApplicationContext();

		mInflater = inflater;
		mContainer = container;
		
		view = mInflater.inflate(R.layout.homepage, mContainer, false);
		mWebView = (WebView) view.findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		
		new LoadWebPage().execute();
	
		return view;
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
	
	public class LoadWebPage extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			mWebView.loadUrl("http://lifedropper.appspot.com/foundry-" + getVersionName(mContext, Homepage.class) + ".html");
			return null;
		}
	}
}
