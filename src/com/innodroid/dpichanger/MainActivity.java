package com.innodroid.dpichanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.innodroid.dpichanger.SetupTask.SetupTaskHandler;
import com.stericson.RootTools.RootTools;

public class MainActivity extends Activity implements SetupTaskHandler {

	private static final int DIALOG_NOT_ROOT = 101;
	private static final int DIALOG_ERROR_READ = 102;
	private static final int DIALOG_DONE = 103;
	private EditText mDpiText;
	private Button mSaveButton;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mDpiText = (EditText)findViewById(R.id.dpi_text);
        mSaveButton = (Button)findViewById(R.id.save_button);

        ((TextView)findViewById(R.id.backup_location_text)).setText("Backup will be saved in " + Constants.BACKUP_FILE_NAME);
                
        if (RootTools.isRootAvailable() && RootTools.isAccessGiven())
        	new SetupTask(this, this).execute();
        else
        	showDialog(DIALOG_NOT_ROOT);

        mSaveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});
    }
	
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch (id) {
    		case DIALOG_NOT_ROOT:
    			return createDialog("Root Denied", "Unable to get root access. Exiting application.", android.R.drawable.ic_dialog_alert);
    		case DIALOG_ERROR_READ:
    			return createDialog("Error", "Unable to read configuration file.", android.R.drawable.ic_dialog_alert);
    		case DIALOG_DONE:
    			return createDialog("Done", "DPI setting has been updated. You need to reboot to take effect.", android.R.drawable.ic_dialog_info);
    		default:
    			return super.onCreateDialog(id);
    	}
    }
	
    private Dialog createDialog(String title, String message, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
            .setIcon(icon)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				} });
        return builder.create();
    }

	@Override
	public void onSetupComplete(int dpi) {		
		if (dpi == 0) {
			showDialog(DIALOG_ERROR_READ);
		} else {
			mDpiText.setText(Integer.toString(dpi));
			mDpiText.setEnabled(true);
			mSaveButton.setEnabled(true);
		}
	}
}


