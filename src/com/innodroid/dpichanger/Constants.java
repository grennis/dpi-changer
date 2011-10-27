package com.innodroid.dpichanger;

import java.io.File;

import android.os.Environment;

public class Constants {
	public static final String CONFIG_FILE_NAME = "/system/build.prop";
	public static final String BACKUP_FILE_NAME = new File(Environment.getExternalStorageDirectory(), "build.prop.bak").getAbsolutePath();
	public static final String DPI_PREFIX = "ro.sf.lcd_density";
	
	public static final int[] DPI_VALUES = new int[] {
		120,
		160,
		240,
		320,
		0
	};
	
	public static final String[] DPI_NAMES = new String[] {
		"LDPI (120)",
		"MDPI (160)",
		"HDPI (240)",
		"XHDPI (320)",
		"Custom"
	};	
}
