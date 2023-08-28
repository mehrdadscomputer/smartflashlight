package ir.mehrdadscomputer.smartlight;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.nfc.cardemulation.OffHostApduService;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.ActionBarView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Strobe extends ActionBarActivity {

	private ImageView mIVStrobe;
	// private Camera camera;
	// private boolean mIsStrobeOn = false;
	private final Context context = this;
	private Menu mMenu;
	private boolean mFlagStrobe;
	private int mOnTime;
	private int mOffTime;
	private SeekBar mSbOnTime;
	private SeekBar mSbOffTime;

	private AnimationDrawable mAnimStrobe;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_strobe);

		// back button in action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mSbOnTime = (SeekBar) findViewById(R.id.sb_strobe_on);
		mSbOffTime = (SeekBar) findViewById(R.id.sb_strobe_off);

		mIVStrobe = (ImageView) findViewById(R.id.iv_strobe_on_off);
		mIVStrobe.setBackgroundResource(R.drawable.animation_strobe);
		mAnimStrobe = (AnimationDrawable) mIVStrobe.getBackground();
		mIVStrobe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PackageManager pm = context.getPackageManager();
				if (isCameraSupported(pm)) {
					if (isFlashSupported(pm)) {
						if (!GlobalClass.mIsLightOn) {
							GlobalClass.mIsLightOn = true;
							mFlagStrobe = true;
							mAnimStrobe.start();
							Thread mStrobeThread = new Thread(new Runnable() {
								@Override
								public void run() {

									while (mFlagStrobe) {

										// TODO Auto-generated
										// method stub
										Parameters p = GlobalClass.camera
												.getParameters();
										try {
											p.setFlashMode(Parameters.FLASH_MODE_TORCH);
											GlobalClass.camera.setParameters(p);
											GlobalClass.camera.startPreview();
										} catch (Exception e) {
											// TODO: handle exception
										}

										try {
											Thread.sleep(10000 / ((mOnTime) + 10)); // simulate
											// a
											// network call
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										try {
											p.setFlashMode(Parameters.FLASH_MODE_OFF);
											GlobalClass.camera.setParameters(p);
											GlobalClass.camera.stopPreview();
										} catch (Exception e) {
											// TODO: handle exception
										}
										try {
											Thread.sleep(10000 / ((mOffTime) + 10)); // simulate
											// a
											// network call
										} catch (InterruptedException e) {
											e.printStackTrace();
										}

									}
								}
							});
							mStrobeThread.start();
						} else {
							final Parameters p = GlobalClass.camera
									.getParameters();
							p.setFlashMode(Parameters.FLASH_MODE_OFF);
							GlobalClass.camera.setParameters(p);
							GlobalClass.camera.stopPreview();
							mAnimStrobe.stop();
							GlobalClass.mIsLightOn = false;
							mFlagStrobe = false;
						}
					}
					// else if device doesn't support camera
					else {
						Toast.makeText(
								getApplicationContext(),
								"برای استفاده از این بخش گوشی شما باید دارای دوربین و فلاش دوربین باشد",
								Toast.LENGTH_LONG).show();

					}
				} else {
					// این جا نیازه، پاک نشه!ا
					/*
					 * Toast.makeText( getApplicationContext(),
					 * "گوشی شما دارای فلاش یا دوربین نمی باشد. می توانید از صفحه گوشی برای روشنایی استفاده کنید"
					 * , Toast.LENGTH_LONG).show();
					 */
				}

			}
		});

		// OnTime Seekbar
		mSbOnTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				mOnTime = progress;

			}
		});

		// OffTime Seekbar
		mSbOffTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				mOffTime = progress;

			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		mFlagStrobe = false;
		// mAnimStrobe.stop();
		GlobalClass.mIsLightOn = false;

		if (GlobalClass.camera != null) {
			GlobalClass.camera.stopPreview();
			GlobalClass.camera.lock();
			GlobalClass.camera.release();
			GlobalClass.camera = null;
			// mIsStrobeOn = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Need to release if we already have one, or we won't get the camera
		if (GlobalClass.camera != null) {
			GlobalClass.camera.release();
			GlobalClass.camera = null;
		}
		try {
			GlobalClass.camera = Camera.open();
		} catch (Exception e) {
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		this.mMenu = menu;

		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.strobe, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle
	 * presses on the action bar items switch (item.getItemId()) { case
	 * R.id.actionbar_screen: MenuItem mMenuItemScreen =
	 * mMenu.findItem(R.id.actionbar_screen); if (isScreenSelected) {
	 * mMenuItemScreen.setIcon(R.drawable.ic_action_screen_off);
	 * isScreenSelected = false; } else {
	 * mMenuItemScreen.setIcon(R.drawable.ic_action_screen_on); isScreenSelected
	 * = true; } return true; case R.id.actionbar_flashlight: MenuItem
	 * mMenuItemCameraFlash = mMenu .findItem(R.id.actionbar_flashlight); if
	 * (isCameraFlashSelected) { mMenuItemCameraFlash
	 * .setIcon(R.drawable.ic_action_cameraflash_off); isCameraFlashSelected =
	 * false; } else { mMenuItemCameraFlash
	 * .setIcon(R.drawable.ic_action_cameraflash_on); isCameraFlashSelected =
	 * true; } return true; default: return super.onOptionsItemSelected(item); }
	 * }
	 */

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

}
