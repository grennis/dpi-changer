package com.innodroid.dpichanger;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class SetupTask extends BaseTask {

	public interface SetupTaskHandler {
		void onSetupComplete(int dpi);
	}
	
	private SetupTaskHandler mHandler;
	
	public SetupTask(Context context, SetupTaskHandler handler) {
		super(context);
		mHandler = handler;
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
        String source = Constants.CONFIG_PATH + Constants.CONFIG_FILE_NAME;
        String dest = new File(Environment.getExternalStorageDirectory(), Constants.CONFIG_FILE_NAME).getAbsolutePath();

        try {
			copyFileAsRoot(source, dest);
        	return parseDpiFromConfig(dest);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		mHandler.onSetupComplete(result);		
	}
	
	private int parseDpiFromConfig(String file) {
		return 3;
	}
}
