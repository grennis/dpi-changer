package com.innodroid.dpichanger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;

import com.stericson.RootTools.RootTools;

public class CommitTask extends BaseTask {
	private int mDpi;
	
	public interface CommitTaskHandler {
		void onCommitComplete(boolean result);
	}
	
	private CommitTaskHandler mHandler;
	
	public CommitTask(Context context, CommitTaskHandler handler, int dpi) {
		super(context);
		mHandler = handler;
		mDpi = dpi;
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
    	if (!RootTools.remount("/system", "rw"))
    		return 1;
    	
    	int result = copyBackToSystemWithNewDpi();
    	
    	if (!RootTools.remount("/system", "ro"))
    		return 1;
    	
    	return result;
	}

	private int copyBackToSystemWithNewDpi() {
		try {
			return doCopyBackToSystemWithNewDpi();
		} catch (Exception ex) {
			ex.printStackTrace();
			return 1;
		}
	}
	
	private int doCopyBackToSystemWithNewDpi() throws IOException {
		String line;
		int result = 0;
		FileReader reader = new FileReader(new File(Constants.BACKUP_FILE_NAME));		
		BufferedReader buffer = new BufferedReader(reader);		
		FileWriter writer = new FileWriter(new File(Constants.CONFIG_FILE_NAME), false);
				
		while ((line = buffer.readLine()) != null) {
			if (line.startsWith(Constants.DPI_PREFIX)) {
				writer.write(Constants.DPI_PREFIX + "=" + mDpi);
			} else {
				writer.write(line);
			}
		}
		
		writer.close();
		buffer.close();
		reader.close();
		return result;
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		mHandler.onCommitComplete(result == 0);		
	}
}
