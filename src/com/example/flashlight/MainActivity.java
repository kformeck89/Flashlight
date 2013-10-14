package com.example.flashlight;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	// Fields -----------------------------------------------------------------
	private boolean isFlashOn;
	private boolean hasFlash;
	private ImageButton btnSwitch = null;
	private Camera camera;
	private Parameters params;
	private MediaPlayer mediaPlayer;

	// Methods ----------------------------------------------------------------
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// Base implementation
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get the button and set it up
		btnSwitch = (ImageButton)findViewById(R.id.btnSwitch);
		btnSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isFlashOn) {
					turnOffFlash();
				} else {
					turnOnFlash();
				}
			}
		});
		
		// Check if the device has a camera with flash
		hasFlash = getApplicationContext().getPackageManager()
										  .hasSystemFeature(
												  PackageManager.FEATURE_CAMERA_FLASH);
		
		// If there is no flash, alert the user
		if (!hasFlash) {
			AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
											   .create();
			alert.setTitle("Error");
			alert.setMessage("Sorry, your device doesn't support flashlight");
			alert.setButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					// Close the application
					finish();
					
				}
			});
			alert.show();
			return;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	protected void onPause() {
		super.onPause();
		turnOffFlash();
	}
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (hasFlash) {
			turnOnFlash();
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
		getCamera();
	}
	@Override
	protected void onStop() {
		super.onStop();
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}
	private void getCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
			} catch (RuntimeException ex) {
				Log.e("Camera Error.  Failed to open. Error: ", 
						ex.getMessage());
			}
		}
	}
	private void turnOnFlash() {
		if (!isFlashOn) {
			if (camera == null || params == null) {
				return;
			} 
			
			// Play the flash on sound
			playSound();
			
			// Set the camera flash mode to torch
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
			isFlashOn = true;
			
			// Toggle the image
			toggleButtonImage();
		}
	}
	private void turnOffFlash() {
		if (isFlashOn) {
			if (camera == null || params == null) {
				return;
			}
			
			// Play the flash off sound
			playSound();
			
			// Set the camera flash mode to off
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			isFlashOn = false;
			
			// Toggle the image
			toggleButtonImage();
		}
	}
	private void toggleButtonImage() {
		if (isFlashOn) {
			btnSwitch.setImageResource(R.drawable.light_on);
		} else {
			btnSwitch.setImageResource(R.drawable.light_off);
		}
	}
	private void playSound() {
		mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.flash_sound);
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer player) {
				player.release();
			}
		});
		mediaPlayer.start();
	}
	
}
