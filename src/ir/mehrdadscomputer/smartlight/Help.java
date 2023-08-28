package ir.mehrdadscomputer.smartlight;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class Help extends ActionBarActivity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		// back button in action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

}
