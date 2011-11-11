package com.mntone.twijue;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class GetImageTask extends AsyncTask< String, Void, byte[] >
{
	private Context _context;
	private String _screenName;
	private ImageView _image;
	
	public GetImageTask( Context context, String screenName, ImageView image )
	{
		this._context = context;
		this._screenName = screenName;
		this._image = image;
	}
	
	@Override
	protected byte[] doInBackground( String... urls )
	{
		return HttpClient.getByteArrayFromURL( urls[0] );
	}
	
	@Override
	protected void onPostExecute( byte[] bitmap )
	{
		SQLiteDatabase db = null;
		long er = 0;
		try
		{
			CacheSqlHelper helper = new CacheSqlHelper( _context );
			db = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put( Const.CACHE_SCREEN_NAME, _screenName );
			values.put( Const.CACHE_PROFILE_IMAGE, bitmap );
			values.put( Const.CACHE_TIME, Calendar.getInstance().getTimeInMillis() / 1000 );
			
			er = db.insert( Const.CACHE_TABLE_NAME, null, values );
		}
		catch( SQLiteConstraintException ex )
		{
		}
		finally 
		{
			if( db != null ) db.close();
		}
		android.util.Log.d( "com.mntone.twijue.DownloadImageTask", "saved: " + _screenName );
		if( er == -1 ) Toast.makeText( _context, "insert error", Toast.LENGTH_SHORT ).show();
	}
}
