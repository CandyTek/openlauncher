package com.benny.openlauncher;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.benny.openlauncher.util.AppSettings;
import com.benny.openlauncher.util.LauncherAction;

public class AppObject extends Application {
	private static final String TAG = AppObject.class.getSimpleName();
	private static AppObject _instance;

	public static AppObject get() {
		return _instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		_instance = this;

		// 启动时可以做一些初始化操作
		try {
			Intent intent = new Intent();
			ComponentName componentName = new ComponentName("com.dazzle.videoconferencesystem",
					"com.dazzle.videoconferencesystem.service.SideMenuBarService");
			intent.setComponent(componentName);
			startService(intent);
		}
		catch (Exception e) {
			Log.e(TAG,"onCreate: 启动失败");
		}

		if (!AppSettings.get().getDefaultPreferences().contains("pref_key__gesture_swipe_up")) {
			AppSettings.get().setString("pref_key__gesture_swipe_up",LauncherAction.Action.AppDrawer.toString());
			AppSettings.get().setString("pref_key__gesture_swipe_down",LauncherAction.Action.ShowNotifications.toString());
		}

	}
}
