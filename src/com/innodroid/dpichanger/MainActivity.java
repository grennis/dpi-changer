package com.innodroid.dpichanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.innodroid.dpichanger.CommitTask.CommitTaskHandler;
import com.innodroid.dpichanger.SetupTask.SetupTaskHandler;
import com.stericson.RootTools.RootTools;

public class MainActivity extends Activity implements SetupTaskHandler, CommitTaskHandler {

	private static final int DIALOG_NOT_ROOT = 101;
	private static final int DIALOG_ERROR_READ = 102;
	private static final int DIALOG_ERROR_COMMIT = 103;
	private static final int DIALOG_DONE = 104;
	private ArrayAdapter<String> mDpiAdapter;
	private EditText mCustomDpiText;
	private TextView mCustomDpiWarning;
	private ViewGroup mCustomDpiLayout;
	private Spinner mDpiSpinner;
	private Button mSaveButton;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mCustomDpiText = (EditText)findViewById(R.id.custom_dpi_text);
        mSaveButton = (Button)findViewById(R.id.save_button);
        mCustomDpiLayout = (ViewGroup)findViewById(R.id.custom_dpi);
        mDpiSpinner = (Spinner)findViewById(R.id.dpi_spinner);
        mCustomDpiWarning = (TextView)findViewById(R.id.custom_warning);

        mDpiAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.DPI_NAMES);
        mDpiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDpiSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int visibility = (arg2 == Constants.DPI_NAMES.length-1) ? View.VISIBLE : View.GONE;
				mCustomDpiLayout.setVisibility(visibility);
				mCustomDpiWarning.setVisibility(visibility);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}        	
        });
        mDpiSpinner.setAdapter(mDpiAdapter);

        ((TextView)findViewById(R.id.backup_location_text)).setText("Backup will be saved in " + Constants.BACKUP_FILE_NAME);
                
        if (RootTools.isRootAvailable() && RootTools.isAccessGiven())
        	new SetupTask(this, this).execute();
        else
        	showDialog(DIALOG_NOT_ROOT);

        mSaveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				commit();
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
    		case DIALOG_ERROR_COMMIT:
    			return createDialog("Error", "Failed to update DPI setting.", android.R.drawable.ic_dialog_info);
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
			boolean found = false;
			
			for (int i=0; i<Constants.DPI_VALUES.length; i++) {
				if (Constants.DPI_VALUES[i] == dpi) {
					found = true;
					mDpiSpinner.setSelection(i);
					break;
				}
			}
			
			if (!found) {
				mDpiSpinner.setSelection(Constants.DPI_VALUES.length-1);
			}
			
			mCustomDpiText.setText(Integer.toString(dpi));
			mCustomDpiText.setEnabled(true);
			mDpiSpinner.setEnabled(true);
			mSaveButton.setEnabled(true);
		}
	}
	
	private void commit() {
		int dpi = getSelectedDpi();

		if (dpi == 0) {
			Toast.makeText(this, "Enter a valid DPI value", Toast.LENGTH_SHORT).show();
			return;
		}
		
		new CommitTask(this, this, dpi).execute();
	}

	private int getSelectedDpi() {
		try {
			int selected = mDpiSpinner.getSelectedItemPosition();
			int dpi = Constants.DPI_VALUES[selected];
			
			if (dpi == 0)
				dpi = Integer.parseInt(mCustomDpiText.getText().toString()); 

			// LDPI to XHDPI
			if (dpi < 120 || dpi > 320) 
				return 0;
			
			return dpi;			
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public void onCommitComplete(boolean result) {
		if (result)
			showDialog(DIALOG_DONE);
		else
			showDialog(DIALOG_ERROR_COMMIT);
	}
}


