package com.u8.sdk.base;

import android.content.Intent;
import android.content.res.Configuration;

public interface IActivityCallback {

	public void onActivityResult(int requestCode, int resultCode, Intent data);
	public void onCreate();
	public void onStart();
	public void onPause();
	public void onResume();
	public void onNewIntent(Intent newIntent);
	public void onStop();
	public void onDestroy();
	public void onRestart();
	public void onBackPressed();
	public void onConfigurationChanged(Configuration newConfig);
	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults);
}
