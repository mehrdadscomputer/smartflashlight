package ir.mehrdadscomputer.smartlight;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Movement extends ActionBarActivity {
	private boolean mMovementFlag;
	// private boolean mIsMovementOn = false;
	// private Camera camera;
	private int mDelayTime;
	private ImageView mIVMovement;

	private AnimationDrawable mAnimMovement;

	private final Context context = this;

	// test
	// TextView tv;

	@Override
	protected void onPause() {
		super.onPause();
		// ****************
		GlobalClass.mIsLightOn = false;
		mMovementFlag = false;
		// movement

		if (GlobalClass.camera != null) {
			GlobalClass.camera.stopPreview();
			GlobalClass.camera.lock();
			GlobalClass.camera.release();
			GlobalClass.camera = null;
		}
		mSensorManager.unregisterListener(mSensorListener);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// movement
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		// Need to release if we already have one, or we won't get the camera
		if (GlobalClass.camera != null) {
			GlobalClass.camera.release();
			GlobalClass.camera = null;
		}
		try {
			GlobalClass.camera = Camera.open();
		} catch (Exception e) {
		}

		// Movement On ImageButton
		mIVMovement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PackageManager pm = context.getPackageManager();
				if (isCameraSupported(pm) && isFlashSupported(pm)) {
					if (!GlobalClass.mIsLightOn) {
						// TODO Auto-generated method stub
						mMovementFlag = true;
						GlobalClass.mIsLightOn = true;
						mAnimMovement.start();
						Thread mMovementThread = new Thread(new Runnable() {
							@Override
							public void run() {

								while (mMovementFlag) {
									Parameters p = GlobalClass.camera
											.getParameters();

									try {
										p.setFlashMode(Parameters.FLASH_MODE_TORCH);
										GlobalClass.camera.setParameters(p);
										GlobalClass.camera.startPreview();
									} catch (Exception e) {

									}

									// mIsMovementOn = true;
									mDelayTime = (int) mAccel * (int) mAccel
											* (int) mAccel * (int) mAccel
											* (int) mAccel * (int) mAccel
											* (int) mAccel * (int) mAccel
											* (int) mAccel * (int) mAccel;

									try {

										Thread.sleep(10000 / (mDelayTime + 20)); // simulate
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

									// mIsMovementOn = false;

									try {

										Thread.sleep(10000 / (mDelayTime + 20)); // simulate
																					// a
										// network call
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

								}
							}
						});
						mMovementThread.start();
					} else {
						mAnimMovement.stop();
						Parameters p = GlobalClass.camera.getParameters();

						p.setFlashMode(Parameters.FLASH_MODE_OFF);

						GlobalClass.camera.setParameters(p);
						GlobalClass.camera.stopPreview();
						GlobalClass.mIsLightOn = false;
						mMovementFlag = false;

						mSensorManager.unregisterListener(mSensorListener);
					}
				} else {
					Toast.makeText(
							getApplicationContext(),
							"برای استفاده از این بخش گوشی شما باید دارای دوربین و فلاش دوربین باشد",
							Toast.LENGTH_LONG).show();
				}
			}

		});

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movement);

		// back button in action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// MOVEMENT PART
		// tv = (TextView) findViewById(R.id.textView1);
		mIVMovement = (ImageView) findViewById(R.id.iv_movement_on_off);

		mIVMovement.setBackgroundResource(R.drawable.animation_movement);
		mAnimMovement = (AnimationDrawable) mIVMovement.getBackground();

		/* do this in onCreate */
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		mAccel = 0.00f;
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;
		// ************

	}

	// Movement Part

	/* put this into your activity class */
	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity

	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			float x = se.values[0];
			float y = se.values[1];
			float z = se.values[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta; // perform low-cut filter

			// if (mAccel < 2) {
			// Toast.makeText(getApplicationContext(),
			// "You have shaken your phone", Toast.LENGTH_SHORT).show();
			// tv.setText(String.valueOf(mAccel));
			// mAccel = 0;
			// }
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	// ************

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
