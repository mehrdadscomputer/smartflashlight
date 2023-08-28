package ir.mehrdadscomputer.smartlight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.ActionBarView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Flashlight extends ActionBarActivity {

	private ImageView mImgBtnFlashlight;
	// private Camera camera;
	// private boolean mIsLightOn = false;
	private final Context context = this;
	private boolean isScreenSelected = false;
	private Menu mMenu;
	private boolean isCameraFlashSelected = true;

	private ImageButton mImgBtnBattery;

	private int mBatteryLevel;

	private RelativeLayout mRLFlashlight;

	// Handler handler;

	// A method that get remaining battery percentage
	public void getBatteryPercentage() {
		BroadcastReceiver mBatteryLevelReceiver = new BroadcastReceiver() {
			public void onReceive(Context mContext, Intent mIntent) {
				// mContext.unregisterReceiver(this);
				int mCurrentLevel = mIntent.getIntExtra(
						BatteryManager.EXTRA_LEVEL, -1);
				int mScale = mIntent
						.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				mBatteryLevel = -1;// Remaining battery percentage(in method)
				if (mCurrentLevel >= 0 && mScale > 0) {
					mBatteryLevel = (mCurrentLevel * 100) / mScale;
				}
				if (mBatteryLevel >= 80)
					mImgBtnBattery
							.setImageResource(R.drawable.ic_action_battery_4);
				else if (mBatteryLevel >= 60)
					mImgBtnBattery
							.setImageResource(R.drawable.ic_action_battery_3);
				else if (mBatteryLevel >= 40)
					mImgBtnBattery
							.setImageResource(R.drawable.ic_action_battery_2);
				else if (mBatteryLevel >= 20)
					mImgBtnBattery
							.setImageResource(R.drawable.ic_action_battery_1);
				else if (mBatteryLevel >= 0) {
					mImgBtnBattery
							.setImageResource(R.drawable.ic_action_battery_0);
					Toast.makeText(getApplicationContext(),
							"شارژ باطری کمتر از ۲۰ درصد است", Toast.LENGTH_LONG)
							.show();
				}
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mBatteryLevelReceiver, batteryLevelFilter);
	}

	// ************

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flashlight);

		// back button in action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mImgBtnFlashlight = (ImageView) findViewById(R.id.imgv_flashlight);
		mImgBtnBattery = (ImageButton) findViewById(R.id.ib_battery);

		getBatteryPercentage();

		mRLFlashlight = (RelativeLayout) findViewById(R.id.rl_flashlight);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (GlobalClass.camera != null) {
			GlobalClass.camera.stopPreview();
			GlobalClass.camera.lock();
			GlobalClass.camera.release();
			GlobalClass.camera = null;
			GlobalClass.mIsLightOn = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// yesssss! برای مشکل کانفیلیکت با ویدجت
		// if (GlobalClass.camera != null)
		// this.camera = GlobalClass.camera;

		// else
		// camera = Camera.open();
		
		if (GlobalClass.camera == null)
			GlobalClass.camera = Camera.open();
		

		mImgBtnFlashlight.setImageResource(R.drawable.ic_lightbulb_off);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		this.mMenu = menu;

		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.flashlight, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.actionbar_screen:
			MenuItem mMenuItemScreen = mMenu.findItem(R.id.actionbar_screen);
			if (isScreenSelected) {
				mMenuItemScreen.setIcon(R.drawable.ic_action_screen_off);
				isScreenSelected = false;
			} else {
				mMenuItemScreen.setIcon(R.drawable.ic_action_screen_on);
				isScreenSelected = true;
			}
			return true;
		case R.id.actionbar_flashlight:
			MenuItem mMenuItemCameraFlash = mMenu
					.findItem(R.id.actionbar_flashlight);
			if (isCameraFlashSelected) {
				mMenuItemCameraFlash
						.setIcon(R.drawable.ic_action_cameraflash_off);
				isCameraFlashSelected = false;
			} else {
				mMenuItemCameraFlash
						.setIcon(R.drawable.ic_action_cameraflash_on);
				isCameraFlashSelected = true;
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void FlashlightOnClick(View view) {
		if (isCameraFlashSelected && !isScreenSelected) {
			PackageManager pm = context.getPackageManager();
			if (isCameraSupported(pm)) {
				if (isFlashSupported(pm)) {
					if (!GlobalClass.mIsLightOn) {
						final Parameters p = GlobalClass.camera.getParameters();
						p.setFlashMode(Parameters.FLASH_MODE_TORCH);
						GlobalClass.camera.setParameters(p);
						GlobalClass.camera.startPreview();
						mImgBtnFlashlight
								.setImageResource(R.drawable.ic_lightbulb_on);
						GlobalClass.mIsLightOn = true;

						// keep screen on
						// getWindow().addFlags(
						// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					} else {
						final Parameters p = GlobalClass.camera.getParameters();
						p.setFlashMode(Parameters.FLASH_MODE_OFF);
						GlobalClass.camera.setParameters(p);
						GlobalClass.camera.stopPreview();
						mImgBtnFlashlight
								.setImageResource(R.drawable.ic_lightbulb_off);
						GlobalClass.mIsLightOn = false;
					}
				}
				// else if device doesn't support camera
				else {
					Toast.makeText(
							getApplicationContext(),
							"گوشی شما دارای فلاش یا دوربین نمی باشد. می توانید از صفحه گوشی برای روشنایی استفاده کنید",
							Toast.LENGTH_LONG).show();
					MenuItem mMenuItemScreen = mMenu
							.findItem(R.id.actionbar_screen);
					MenuItem mMenuItemCameraFlash = mMenu
							.findItem(R.id.actionbar_flashlight);
					mMenuItemScreen.setIcon(R.drawable.ic_action_screen_on);
					isScreenSelected = true;
					mMenuItemCameraFlash
							.setIcon(R.drawable.ic_action_cameraflash_off);
					isCameraFlashSelected = false;

				}
			} else {
				// این جا نیازه، پاک نشه!ا
				/*
				 * Toast.makeText( getApplicationContext(),
				 * "گوشی شما دارای فلاش یا دوربین نمی باشد. می توانید از صفحه گوشی برای روشنایی استفاده کنید"
				 * , Toast.LENGTH_LONG).show();
				 */
			}
		} else if (!isCameraFlashSelected && isScreenSelected) {
			if (!GlobalClass.mIsLightOn) {
				// to increase screen brightness to max
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.screenBrightness = 100 / 100.0f;
				getWindow().setAttributes(lp);

				mImgBtnFlashlight.setImageResource(R.drawable.ic_lightbulb_on);

				GlobalClass.mIsLightOn = true;
				mRLFlashlight
						.setBackgroundResource(R.drawable.background_light);
			} else {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.screenBrightness = 50 / 100.0f;
				getWindow().setAttributes(lp);

				mImgBtnFlashlight.setImageResource(R.drawable.ic_lightbulb_off);
				GlobalClass.mIsLightOn = false;

				mRLFlashlight.setBackgroundResource(R.drawable.background_dark);
			}
		} else if (!isCameraFlashSelected && !isScreenSelected) {
			Toast.makeText(
					getApplicationContext(),
					"شما باید حداقل یکی از گزینه های صفحه گوشی یا فلاش گوشی را که دربالای صفحه می باشند، انتخاب کنید",
					Toast.LENGTH_LONG).show();
		} else {
			PackageManager pm = context.getPackageManager();
			if (isCameraSupported(pm)) {
				if (isFlashSupported(pm)) {
					if (!GlobalClass.mIsLightOn) {
						final Parameters p = GlobalClass.camera.getParameters();
						p.setFlashMode(Parameters.FLASH_MODE_TORCH);
						GlobalClass.camera.setParameters(p);
						GlobalClass.camera.startPreview();
						mImgBtnFlashlight
								.setImageResource(R.drawable.ic_lightbulb_on);
						GlobalClass.mIsLightOn = true;

						WindowManager.LayoutParams lp = getWindow()
								.getAttributes();
						lp.screenBrightness = 100 / 100.0f;
						getWindow().setAttributes(lp);
						mRLFlashlight
								.setBackgroundResource(R.drawable.background_light);
					} else {
						final Parameters p = GlobalClass.camera.getParameters();
						p.setFlashMode(Parameters.FLASH_MODE_OFF);
						GlobalClass.camera.setParameters(p);
						GlobalClass.camera.stopPreview();
						mImgBtnFlashlight
								.setImageResource(R.drawable.ic_lightbulb_off);
						GlobalClass.mIsLightOn = false;

						WindowManager.LayoutParams lp = getWindow()
								.getAttributes();
						lp.screenBrightness = 50 / 100.0f;
						getWindow().setAttributes(lp);

						mRLFlashlight
								.setBackgroundResource(R.drawable.background_dark);
					}
				}
				// else if device doesn't support camera
				else {
					MenuItem mMenuItemScreen = mMenu
							.findItem(R.id.actionbar_screen);
					MenuItem mMenuItemCameraFlash = mMenu
							.findItem(R.id.actionbar_flashlight);
					mMenuItemScreen.setIcon(R.drawable.ic_action_screen_on);
					isScreenSelected = true;
					mMenuItemCameraFlash
							.setIcon(R.drawable.ic_action_cameraflash_off);
					isCameraFlashSelected = false;

				}
			} else {
				MenuItem mMenuItemScreen = mMenu
						.findItem(R.id.actionbar_screen);
				MenuItem mMenuItemCameraFlash = mMenu
						.findItem(R.id.actionbar_flashlight);
				mMenuItemScreen.setIcon(R.drawable.ic_action_screen_on);
				isScreenSelected = true;
				mMenuItemCameraFlash
						.setIcon(R.drawable.ic_action_cameraflash_off);
				isCameraFlashSelected = false;
			}
		}

	}

	/**
	 * @param packageManager
	 * @return true <b>if the device support camera flash</b><br/>
	 *         false <b>if the device doesn't support camera flash</b>
	 */
	private boolean isFlashSupported(PackageManager packageManager) {
		// if device support camera flash?
		if (packageManager
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			return true;
		}
		return false;
	}

	/**
	 * @param packageManager
	 * @return true <b>if the device support camera</b><br/>
	 *         false <b>if the device doesn't support camera</b>
	 */
	private boolean isCameraSupported(PackageManager packageManager) {
		// if device support camera?
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		}
		return false;
	}

	public void showBatteryPercent(View view) {
		Toast.makeText(getApplicationContext(),
				"شارژ باطری " + mBatteryLevel + "٪", Toast.LENGTH_SHORT).show();
	}
}