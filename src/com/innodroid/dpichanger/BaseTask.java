package com.innodroid.dpichanger;

import java.io.IOException;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class BaseTask extends AsyncTask<Void, Void, Integer> {
	private Context mContext;
	private ProgressDialog mProgressDialog;
	
	public BaseTask(Context context) {
		mContext = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		mProgressDialog = ProgressDialog.show(mContext, null, "Please Wait", true, false);
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		
		mProgressDialog.dismiss();
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();

		mProgressDialog.dismiss();
	}
	
    protected void copyFileAsRoot(String src, String dest) throws IOException, InterruptedException, RootToolsException {
	    RootTools.sendShell("cp -f " + src + " " + dest + "\n");
	    RootTools.sendShell("chmod 777 " + dest + "\n");
    }
}

