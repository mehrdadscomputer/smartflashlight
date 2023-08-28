package ir.mehrdadscomputer.smartlight;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	// Here I define Buttons
	private Button mBtnFlashlight;
	private Button mBtnStrobe;
	private Button mBtnMusic;
	private Button mBtnMovement;

	// Here I define Text Views
	private TextView mTvFlashlight;
	private TextView mTvStrobe;
	private TextView mTvMusic;
	private TextView mTvMovement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

		// Here I link views in activity with views in xml file
		mBtnFlashlight = (Button) findViewById(R.id.btn_flashlight);
		mBtnStrobe = (Button) findViewById(R.id.btn_strobe);
		mBtnMusic = (Button) findViewById(R.id.btn_music);
		mBtnMovement = (Button) findViewById(R.id.btn_movement);

		mTvFlashlight = (TextView) findViewById(R.id.tv_flashlight);
		mTvStrobe = (TextView) findViewById(R.id.tv_strobe);
		mTvMusic = (TextView) findViewById(R.id.tv_music);
		mTvMovement = (TextView) findViewById(R.id.tv_movement);

		// Here I define my custom font, "Roya.ttf"
		Typeface face = Typeface.createFromAsset(getAssets(), "B_Roya_0.ttf");
		mTvFlashlight.setTypeface(face);
		mTvStrobe.setTypeface(face);
		mTvMusic.setTypeface(face);
		mTvMovement.setTypeface(face);

		// OnClicks Flashlight button
		mBtnFlashlight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						Flashlight.class);
				startActivity(intent);
			}
		});

		// OnClick Strobe button
		mBtnStrobe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						Strobe.class);
				startActivity(intent);
			}
		});

		// OnClick Music button
		mBtnMusic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Music.class);
				startActivity(intent);
			}
		});

		// OnClick Movement button
		mBtnMovement.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						Movement.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.actionbar_overflow:
			// Toast.makeText(getApplicationContext(), "Setting clicked",
			// Toast.LENGTH_SHORT).show();
			return true;
		case R.id.actionbar_overflow_about:
			Intent intent = new Intent(getApplicationContext(), About.class);
			startActivity(intent);
			return true;
		case R.id.actionbar_overflow_help:
			Intent intentHelp = new Intent(getApplicationContext(), Help.class);
			startActivity(intentHelp);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
