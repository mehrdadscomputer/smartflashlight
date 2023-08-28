package ir.mehrdadscomputer.smartlight;

import ir.mehrdadscomputer.smartlight.SoundMeter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Music extends ActionBarActivity {

	private final Context context = this;

	private ProgressBar mPBSoundLevel;
	private ImageView mIVSound;
	// private Camera camera;
	private int mDelayTime = 1;
	// private boolean misMusicOn = false;

	private AnimationDrawable mAnimMusic;

	// Music Part
	// constants
	// زمان بین دو اندازه گیری رو مشخص می کنه
	private static final int POLL_INTERVAL = 100;

	// ** running state **//*
	private boolean mAutoResume = false;
	private boolean mRunning = false;
	private boolean mTestMode = false;
	private int mTickCount = 0;
	private int mHitCount = 0;

	// ** config state **//*
	private int mThreshold;
	private int mPollDelay;

	private Handler mMusicHandler = new Handler();

	// data source
	private SoundMeter mSensor;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			start();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {

			// mMusicFlag = true;

			// while (mMusicFlag) {

			double amp = mSensor.getAmplitude();

			mPBSoundLevel.setProgress((int) amp);

			mDelayTime = 750 / ((int) (amp) + 1);

			// mAplitudeTextView.setText(String.valueOf(amp));

			// TODO Auto-generated method stub
			Parameters p = GlobalClass.camera.getParameters();

			p.setFlashMode(Parameters.FLASH_MODE_TORCH);

			GlobalClass.camera.setParameters(p);
			GlobalClass.camera.startPreview();

			try {
				Thread.sleep(mDelayTime); // simulate
											// a
											// network call
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			p.setFlashMode(Parameters.FLASH_MODE_OFF);
			GlobalClass.camera.setParameters(p);
			GlobalClass.camera.stopPreview();

			try {
				Thread.sleep(1000 / (mDelayTime)); // simulate
													// a
													// network call
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// }

			mMusicHandler.postDelayed(mPollTask, POLL_INTERVAL);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);

		// back button in action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mPBSoundLevel = (ProgressBar) findViewById(R.id.pb_sound_level);
		mIVSound = (ImageView) findViewById(R.id.iv_sound_on_off);

		mIVSound.setBackgroundResource(R.drawable.animation_music);
		mAnimMusic = (AnimationDrawable) mIVSound.getBackground();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onStop()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mAnimMusic.stop();

		// music part
		if (GlobalClass.camera != null) {
			GlobalClass.camera.stopPreview();
			GlobalClass.camera.lock();
			GlobalClass.camera.release();
			GlobalClass.camera = null;
			GlobalClass.mIsLightOn = false;
		}

		if (mTestMode) {
			mTestMode = false;
			stop();
			mPBSoundLevel.setProgress(0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Music Part
		if (mAutoResume) {
			start();
		}
		mSensor = new SoundMeter();
		// ***********

		// Need to release if we already have one, or we won't get the camera
		if (GlobalClass.camera != null) {
			GlobalClass.camera.release();
			GlobalClass.camera = null;
		}
		try {
			GlobalClass.camera = Camera.open();
		} catch (Exception e) {
		}

		mIVSound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PackageManager pm = context.getPackageManager();
				if (isCameraSupported(pm) && isFlashSupported(pm)) {
					if (!GlobalClass.mIsLightOn) {
						if (!mTestMode) {
							mTestMode = true;
							GlobalClass.mIsLightOn = true;
							mAnimMusic.start();
							start();
						}
					} else {

						if (mTestMode) {
							mTestMode = false;
							stop();
							mPBSoundLevel.setProgress(0);
						}

						Parameters p = GlobalClass.camera.getParameters();

						p.setFlashMode(Parameters.FLASH_MODE_OFF);

						GlobalClass.camera.setParameters(p);
						GlobalClass.camera.stopPreview();
						GlobalClass.mIsLightOn = false;
						mAnimMusic.stop();

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
	public void onStop() {
		super.onStop();
		stop();
	}

	private void start() {
		mTickCount = 0;
		mHitCount = 0;
		mSensor.start();
		mMusicHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mMusicHandler.removeCallbacks(mSleepTask);
		mMusicHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		mRunning = false;
		mTestMode = false;
	}

	private void sleep() {
		mSensor.stop();
		mMusicHandler.postDelayed(mSleepTask, 1000 * mPollDelay);
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

}
