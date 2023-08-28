package ir.mehrdadscomputer.smartlight;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.widget.RemoteViews;
import android.widget.Toast;

public class FlashlightWidgetReceiver extends BroadcastReceiver {
	// private static boolean isLightOn = false;

	// public static Camera camera;

	@Override
	public void onReceive(Context context, Intent intent) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget);

		if (GlobalClass.mIsLightOn) {
			views.setImageViewResource(R.id.imgbtn_widget,
					R.drawable.ic_lightbulb_off);
		} else {
			views.setImageViewResource(R.id.imgbtn_widget,
					R.drawable.ic_lightbulb_on);
		}

		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		appWidgetManager.updateAppWidget(new ComponentName(context,
				FlashlightWidgetProvider.class), views);

		if (GlobalClass.mIsLightOn) {
			if (GlobalClass.camera != null) {
				GlobalClass.camera.stopPreview();
				GlobalClass.camera.release();
				GlobalClass.camera = null;
				GlobalClass.mIsLightOn = false;
			}

		} else {
			// Open the default i.e. the first rear facing camera.
			GlobalClass.camera = Camera.open();

			if (GlobalClass.camera == null) {
				Toast.makeText(context, "دوربین یافت نشد", Toast.LENGTH_SHORT)
						.show();
			} else {
				// Set the torch flash mode
				Parameters param = GlobalClass.camera.getParameters();
				param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				try {
					GlobalClass.camera.setParameters(param);
					GlobalClass.camera.startPreview();
					GlobalClass.mIsLightOn = true;
				} catch (Exception e) {
					Toast.makeText(context, "فلش دوربین یافت نشد",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
