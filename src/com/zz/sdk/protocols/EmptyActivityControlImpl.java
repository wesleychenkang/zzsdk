package com.zz.sdk.protocols;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;

public class EmptyActivityControlImpl implements ActivityControlInterface {

	@Override
	public void onRestoreInstanceStateControl(Bundle paramBundle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostCreateControl(Bundle paramBundle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateControl(Bundle paramBundle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestartControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResumeControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostResumeControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewIntentControl(Intent paramIntent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSaveInstanceStateControl(Bundle paramBundle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPauseControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserLeaveHintControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLowMemoryControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onKeyLongPressControl(int paramInt, KeyEvent paramKeyEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyUpControl(int paramInt, KeyEvent paramKeyEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyMultipleControl(int paramInt1, int paramInt2,
			KeyEvent paramKeyEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEventControl(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTrackballEventControl(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onUserInteractionControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWindowFocusChangedControl(boolean paramBoolean) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrepareDialogControl(int paramInt, Dialog paramDialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroyControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Boolean onKeyDownControl(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onContentChangedControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDetachedFromWindowControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View onCreatePanelViewControl(int paramInt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreatePanelMenuControl(int paramInt, Menu paramMenu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onCreateOptionsMenuControl(Menu paramMenu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCreateContextMenuControl(ContextMenu paramContextMenu,
			View paramView, ContextMenuInfo paramContextMenuInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onContextItemSelectedControl(MenuItem paramMenuItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onContextMenuClosedControl(Menu paramMenu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dialog onCreateDialogControl(int paramInt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onConfigurationChangedControl(Configuration paramConfiguration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onBackPressedControl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onAttachedToWindowControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onApplyThemeResourceControl(Theme paramTheme, int paramInt,
			boolean paramBoolean) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTitleChangedControl(CharSequence paramCharSequence,
			int paramInt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChildTitleChangedControl(Activity paramActivity,
			CharSequence paramCharSequence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View onCreateViewControl(String paramString, Context paramContext,
			AttributeSet paramAttributeSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreateThumbnailControl(Bitmap paramBitmap,
			Canvas paramCanvas) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CharSequence onCreateDescriptionControl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onRetainNonConfigurationInstanceControl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onWindowAttributesChangedControl(LayoutParams paramLayoutParams) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPreparePanelControl(int paramInt, View paramView,
			Menu paramMenu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMenuOpenedControl(int paramInt, Menu paramMenu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMenuItemSelectedControl(int paramInt,
			MenuItem paramMenuItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPanelClosedControl(int paramInt, Menu paramMenu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPrepareOptionsMenuControl(Menu paramMenu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onOptionsItemSelectedControl(MenuItem paramMenuItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onOptionsMenuClosedControl(Menu paramMenu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSearchRequestedControl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onActivityResultControl(int requestCode, int resultCode,
			Intent data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void overridePendingTransitionControl(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dispatchKeyEventControl(KeyEvent paramKeyEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dispatchTouchEventControl(MotionEvent paramMotionEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRequestedOrientationControl() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasWindowFocusControl() {
		// TODO Auto-generated method stub
		return false;
	}
}
