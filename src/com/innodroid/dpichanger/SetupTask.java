package com.innodroid.dpichanger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
        try {
			copyFileAsRoot(Constants.CONFIG_FILE_NAME, Constants.BACKUP_FILE_NAME);
        	return parseDpiFromConfig(Constants.BACKUP_FILE_NAME);
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
	
	private int parseDpiFromConfig(String path) throws IOException {
		String line;
		int result = 0;
		FileReader reader = new FileReader(new File(path));		
		BufferedReader buffer = new BufferedReader(reader);
		
		while ((line = buffer.readLine()) != null) {
			if (line.startsWith(Constants.DPI_PREFIX)) {
				result = Integer.parseInt(line.substring(line.indexOf('=') + 1));
				break;
			}
		}
		
		buffer.close();
		reader.close();
		return result;
	}
}
