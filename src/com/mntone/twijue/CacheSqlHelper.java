package com.mntone.twijue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CacheSqlHelper extends SQLiteOpenHelper
{
	private static final String DbName = "twijue_cache.db";
	private static final int DbVersion = 1;
	//private static final int DbCacheTime = 96; // 96 時間
	
	public CacheSqlHelper( Context context )
	{
		super( context, DbName, null, DbVersion );
	}
	
	@Override
	public void onCreate( SQLiteDatabase db )
	{
		// テーブル生成
		StringBuilder createCacheSql = new StringBuilder();
		createCacheSql.append( "create table " + Const.CACHE_TABLE_NAME + " (" );
		createCacheSql.append( Const.CACHE_SCREEN_NAME + " text primary key not null," );
		createCacheSql.append( Const.CACHE_PROFILE_IMAGE + " blob not null," );
		createCacheSql.append( Const.CACHE_TIME + " integer not null" );
		createCacheSql.append( ");" );
		
		db.execSQL( createCacheSql.toString() );
	}
	
	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
	{
		// db.execSQL( "drop table mytable;" );
		// onCreate( db );
	}

}
