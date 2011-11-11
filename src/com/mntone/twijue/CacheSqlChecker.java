package com.mntone.twijue;

import java.util.Calendar;

//import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
//import android.widget.Toast;

public class CacheSqlChecker extends AsyncTask< Void, Void, Void >
{	
	private Context context;
//	private ProgressDialog progressDialog;
	
	public CacheSqlChecker( Context context )
	{
		this.context = context;
	}
	
/*	@Override
	protected void onPreExecute()
	{
		progressDialog = new ProgressDialog( context );
		progressDialog.setMessage( "データベースの処理中…" );
		progressDialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
		progressDialog.setCancelable( false );
		progressDialog.show();
	}*/
	
	@Override
	protected Void doInBackground( Void... v )
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		
		long time = Calendar.getInstance().getTimeInMillis() / 1000;
		long delta = 72 * 60 * 60;
		
		android.util.Log.d( "com.mntone.twijue.CacheSqlChecker", "start" );
		
		try
		{
			CacheSqlHelper helper = new CacheSqlHelper( context );
			db = helper.getWritableDatabase();
			String sql = String.format( "select %s, %s from `%s`", Const.CACHE_SCREEN_NAME, Const.CACHE_TIME, Const.CACHE_TABLE_NAME );
			
			cursor = db.rawQuery( sql, new String[]{} );
			if( cursor.moveToFirst() )
				while( cursor.moveToNext() )
				{
					long myTime		= cursor.getLong( cursor.getColumnIndex( Const.CACHE_TIME ) );
					String mySName	= cursor.getString( cursor.getColumnIndex( Const.CACHE_SCREEN_NAME ) );
					if( time - myTime >= delta  )
					{
						db.delete( Const.CACHE_TABLE_NAME, Const.CACHE_SCREEN_NAME + "=?", new String[]{ mySName } );
						android.util.Log.d( "com.mntone.twijue.CacheSqlChecker", "delete: " + mySName + " (" + time + " - " + myTime + " >= " + delta + ")" );
					}
				}
		}
		catch( Exception ex )
		{
		}
		finally
		{
			if( cursor != null ) cursor.close();
			if( db != null ) db.close();
		}
		
		android.util.Log.d( "com.mntone.twijue.CacheSqlChecker", "end" );
		return null;
	}

/*	@Override
	protected void onPostExecute( Void v )
	{
		progressDialog.hide();
	}*/
}