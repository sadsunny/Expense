package com.appxy.pocketexpensepro.overview.transaction;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MEntity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class ViewPhotoActivity extends Activity {

	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_photo);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mImageView = (ImageView) findViewById(R.id.imageView1);
		Intent intent = getIntent();
		String picPath = intent.getStringExtra("picPath");
		if (picPath != null && picPath.length() > 0) {
			Bitmap camorabitmap = MEntity.decodeSampledBitmapFromResource(picPath, 480, 800);
			mImageView.setImageBitmap(camorabitmap);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
	    super.attachBaseContext(new CalligraphyContextWrapper(newBase));
	}
	
	
}
