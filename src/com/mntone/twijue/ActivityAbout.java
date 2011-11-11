package com.mntone.twijue;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityAbout extends Activity
{	
	private String[] supportsId   = { "erudot", "blaanc", "coproce", "niconiokikka", "rin_ne_01", "YDKKK" };
	private String[] supportsName = { "える", "ぶらん", "やまねこ", "キッカ", "凛廻™" };
	
	protected void onCreate( Bundle bundle )
	{
    	// テーマの設定
    	setTheme( new MyTheme( Const.THEME, "Middle", "" ).getTheme() );
    	setTitle( R.string.aboutTitle );
		
		super.onCreate( bundle );
		setContentView( R.layout.about );
		
		String versionName = "";
		PackageManager pm = getPackageManager();
		try
		{
			PackageInfo info = pm.getPackageInfo( "com.mntone.twijue", 0 );
			versionName = info.versionName;
		}
		catch( NameNotFoundException ex ) {}
		
		ImageView aboutIcon = ( ImageView )findViewById( R.id.aboutIcon );
		aboutIcon.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View v )
			{
				goTo( "http://mntone.ykt.wjg.jp/twijue.html" );
			}
		});
		
		TextView aboutTwijue = ( TextView )findViewById( R.id.aboutTwijue );
		aboutTwijue.setText( getText( R.string.appName ) + " Ver. " + versionName );
		aboutTwijue.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View v )
			{
				goTo( "http://mntone.ykt.wjg.jp/twijue.html" );
			}
		});
		
		TextView aboutSupportMsg = ( TextView )findViewById( R.id.aboutSupport );
		aboutSupportMsg.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View v )
			{
				goTo( "http://twitter.com/twijue" );
			}
		});
		
		TextView aboutDeveloper1 = ( TextView )findViewById( R.id.aboutDeveloper1 );
		aboutDeveloper1.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View v )
			{
				goTo( "http://twitter.com/mntone" );
			}
		});
		
		/*for( int i = 0; i < supportsId.length; i++ )
		{
			TextView 
		}*/
		
		
		ImageView aboutTwitter4J = ( ImageView )findViewById( R.id.aboutTwitter4J );
		aboutTwitter4J.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View v )
			{
				goTo( "http://twitter4j.org/" );
			}
		});
	}
	
	private void goTo( String uri )
	{
		Intent intent = new Intent( Intent.ACTION_VIEW );
		intent.setData( Uri.parse( uri ) );
    	startActivity( intent );
	}

}
