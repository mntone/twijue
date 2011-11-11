package com.mntone.twijue;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.app.ListActivity;
//import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
//import android.widget.Toast;

public class ActivityTalk extends ListActivity
{
	//private Context context;
	private ListView myLv;
	private TwitterAdapter adapter;
	private ArrayList < MyStatus > list = new ArrayList< MyStatus >();

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		// テーマの設定
		setTheme( new MyTheme( Const.THEME, Const.FONT_SIZE, "HalfTransparent" ).getTheme() );
		setTitle( R.string.talkTitle );
		
		super.onCreate( savedInstanceState );
		//context = this.getApplicationContext();
		
		Intent intent = getIntent();
		long id = intent.getExtras().getLong( Const.STATUS_ID );
		
		adapter = new TwitterAdapter( this, R.layout.tweetsmall, list );
		setListAdapter( adapter );
		
		myLv = this.getListView();
		getReply( id );
	}
	
	private void getReply( long id )
	{
		Configuration conf = new ConfigurationBuilder()
			.setOAuthAccessToken( Const.MAIN_AOUTH_KEY_TOKEN )
			.setOAuthAccessTokenSecret( Const.MAIN_AOUTH_KEY_SECRET )
			.build()
		;
		Twitter twitter = new TwitterFactory( conf ).getInstance();
		
		new GetReply( twitter, id ).execute();
	}
	
	public class GetReply extends AsyncTask< Void, Void, Void >
	{
		private Twitter twitter;
		private long myId;
		
		public GetReply( Twitter twitter, long id )
		{
			this.twitter = twitter;
			this.myId = id;
		}
		
		@Override
		protected Void doInBackground( Void... params )
		{
			long id = myId;
			while( true )
			{
				twitter4j.Status myStatus = null;
				try
				{
					myStatus = twitter.showStatus( id );
				}
				catch( TwitterException e )
				{
					break;
				}
				list.add( new MyStatus( myStatus ) );
				id = myStatus.getInReplyToStatusId();
				//if( myStatus.getInReplyToScreenName() == null ) break;
				publishProgress();
			}
			return null;
		}
		
		@Override
		public void onProgressUpdate( Void... v )
		{
			new Thread()
			{
				public void run()
				{
					runOnUiThread( new Runnable()
					{
						public void run()
						{
							myLv.invalidateViews();
						}
					});
				}
			}.start();
		}
	}
	
}
