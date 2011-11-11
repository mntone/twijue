package com.mntone.twijue;

//import com.mntone.twijue.R;

import java.util.ArrayList;
import java.util.List;

import twitter4j.ConnectionLifeCycleListener;
import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserStreamAdapter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

public class ActivityHome extends Activity
{
	//private TextView message;
	//private EditText inputText;
	private Context context;
	
	private Twitter twitter = null;
	private RequestToken requestToken = null;
	private TwitterAdapter adapter = null;
	private ArrayList< MyStatus > list = null;
	private ListView listView;
	private long lastShowId = 0;
	private boolean streamFlg = false;
	private boolean streamChangeFlg = false;
	//private boolean backgroundFlg = false;
	private NotificationManager notificationManager;
	//private long streamTime = 0;
	private TwitterStream twitterStream = null;
	
	private Button tweetBtn;
	private Button loadBtn;
	private Button streamBtn;

	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		// 設定読み込み
		SharedPreferences pref 		= getSharedPreferences( Const.PREFERENCE_NAME, MODE_PRIVATE );
		Const.MAIN_MY_SCREEN_NAME	= pref.getString( Const.PREF_MAIN_MY_SCREEN_NAME, "" );
		Const.MAIN_AOUTH_KEY_TOKEN 	= pref.getString( Const.PREF_MAIN_AOUTH_KEY_TOKEN, "" );
		Const.MAIN_AOUTH_KEY_SECRET	= pref.getString( Const.PREF_MAIN_AOUTH_KEY_SECRET, "" );
		Const.SUB_MY_SCREEN_NAME	= pref.getString( Const.PREF_SUB_MY_SCREEN_NAME, "" );
		Const.SUB_AOUTH_KEY_TOKEN 	= pref.getString( Const.PREF_SUB_AOUTH_KEY_TOKEN, "" );
		Const.SUB_AOUTH_KEY_SECRET	= pref.getString( Const.PREF_SUB_AOUTH_KEY_SECRET, "" );
		Const.ENTER_POST			= pref.getBoolean( Const.PREF_ENTER_POST, true );
		Const.QUOTE_TWEET			= pref.getString( Const.PREF_QUOTE_TWEET, "unofficialRetweet" );
		Const.THEME					= pref.getString( Const.PREF_THEME, "Black" );
		Const.FONT_SIZE				= pref.getString( Const.PREF_FONT_SIZE, "Middle" );
		Const.ICON_SIZE				= pref.getString( Const.PREF_ICON_SIZE, "Middle" );
		Const.SKELETON_POST_SCREEN	= pref.getBoolean( Const.PREF_SKELETON_POST_SCREEN, false );
		Const.BACK_LIGHT			= pref.getBoolean( Const.PREF_BACK_LIGHT, true );
		Const.ORIENTATION			= pref.getString( Const.PREF_ORIENTATION, "default" );
		 
		// 向きの設定
		if( Const.ORIENTATION.equals( "portrait" ) )
			setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		else if( Const.ORIENTATION.equals( "landscape" ) )
			setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
		
		// テーマの設定
		setTheme( new MyTheme( Const.THEME, Const.FONT_SIZE, "NoTitleBar" ).getTheme() );
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		context = this;
		
		// notificationManager をとる
		notificationManager = ( NotificationManager )getSystemService( NOTIFICATION_SERVICE );

		// データベースチェック
		//new CacheSqlChecker( context ).execute();
		
		// ListView へセット
		list = new ArrayList< MyStatus >();
		for( int i = 0; i < 60; i++ )
			list.add( new MyStatus() );
		
		listView = ( ListView )findViewById( R.id.mainList );
		listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int pos, long id )
			{
				new DialogTweet( context, list.get( pos ) ).show();
			}
			
		});
		
		LayoutInflater inflater = LayoutInflater.from( this );
		final LinearLayout footer = ( LinearLayout )inflater.inflate( R.layout.space, null );
		listView.addFooterView( footer );
		
		adapter = new TwitterAdapter( this, R.layout.tweetsmall, list );
		listView.setAdapter( adapter );
		
		// ボタンのセット
		tweetBtn = ( Button )findViewById( R.id.tweetBtn );
		tweetBtn.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View view )
			{
				activityPost( "", 0, 0 );
			}
		});
		
		loadBtn = ( Button )findViewById( R.id.loadBtn );
		loadBtn.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View view )
			{
				//myTweetLoad();
				loadBtn.setEnabled( false );
				new loadTwitterHome( context ).execute();
			}
		});
		
		streamBtn = ( Button )findViewById( R.id.streamBtn );
		streamBtn.setOnClickListener( new View.OnClickListener()
		{
			public void onClick( View view )
			{				
				userStreamChange();
			}
		});

		// Twitter 認証開始
		new startTwitter( context ).execute();
	}
	
	private void userStreamChange()
	{
		if( streamFlg )	twitterStream.cleanUp();
		else new loadTwitterStream( context ).execute();
		streamBtn.setEnabled( false );
		streamChangeFlg = true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu( Menu menu )
	{
		if( streamFlg )
			menu.findItem( R.id.menuMainUS )
				.setTitle( getString( R.string.userStreamConnectedText ) )
				.setIcon( R.drawable.play )
			;
		else
			menu.findItem( R.id.menuMainUS )
				.setTitle( getString( R.string.userStreamDisconnectedText ) )
				.setIcon( R.drawable.stop )
			;
		
		if( streamChangeFlg ) menu.findItem( R.id.menuMainUS ).setEnabled( false );
		else menu.findItem( R.id.menuMainUS ).setEnabled( true );
		
		return super.onPrepareOptionsMenu( menu );
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.main, menu );
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		boolean ret = true;
		switch( item.getItemId() )
		{
		case R.id.menuMainUS:
			userStreamChange();
			break;
		case R.id.menuMainAbout:
			Intent intentAbout = new Intent( this, ActivityAbout.class );
			this.startActivity( intentAbout );
			break;
		case R.id.menuMainSettings:
			Intent intentSettings = new Intent( this, ActivitySettings.class );
			this.startActivityForResult( intentSettings, Const.INTENT_SETTINGS_CODE );
			break;
		default:
			ret = super.onOptionsItemSelected( item );
			break;
		}
		return ret;
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		//backgroundFlg = false;

		// バックライト消灯制御
		if( Const.BACK_LIGHT )
			getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
		else
			getWindow().clearFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
		
		notificationManager.cancelAll();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		//backgroundFlg = true;

		Notification notification = new Notification(
			R.drawable.icon,
			getText( R.string.backgroundMsg ),
			System.currentTimeMillis()
		);
		Intent intent = new Intent();
		intent.setClass( this, ActivityHome.class );
		PendingIntent contentIntent = PendingIntent.getActivity( context, 0, intent, 0 );
		notification.setLatestEventInfo( context, "twijue", getText( R.string.forwordMsg ), contentIntent );
		notificationManager.notify( R.string.appName, notification );
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if( streamFlg ) twitterStream.cleanUp();
		notificationManager.cancelAll();
		
		new CacheSqlChecker( context ).execute();
	}
	
	protected void onActivityResult( int requestCode, int resultCode, Intent intent )
	{
		switch( requestCode )
		{
		case Const.INTENT_OAUTH_CODE:
			if( resultCode == RESULT_OK )
			{
				super.onActivityResult( requestCode, resultCode, intent );
				
				AccessToken accessToken = null;
				try
				{
					accessToken = twitter.getOAuthAccessToken(
						requestToken,
						intent.getExtras().getString( Const.IEXTRA_OAUTH_VERIFIER )
					);
					
					Const.MAIN_MY_SCREEN_NAME = twitter.getScreenName();
					Const.MAIN_AOUTH_KEY_TOKEN  = accessToken.getToken();
					Const.MAIN_AOUTH_KEY_SECRET = accessToken.getTokenSecret();
					
					SharedPreferences pref = getSharedPreferences( Const.PREFERENCE_NAME, MODE_PRIVATE );
					SharedPreferences.Editor editor = pref.edit();
					editor.putString( Const.PREF_MAIN_MY_SCREEN_NAME,	Const.MAIN_MY_SCREEN_NAME );
					editor.putString( Const.PREF_MAIN_AOUTH_KEY_TOKEN,	Const.MAIN_AOUTH_KEY_TOKEN );
					editor.putString( Const.PREF_MAIN_AOUTH_KEY_SECRET,	Const.MAIN_AOUTH_KEY_SECRET );
					// 標準設定
					editor.putBoolean( Const.PREF_ENTER_POST,			true );
					editor.putString( Const.PREF_QUOTE_TWEET,			"unofficialRetweet" );
					editor.putString( Const.PREF_THEME,					"Black" );
					editor.putString( Const.PREF_FONT_SIZE,				"Middle" );
					editor.putString( Const.PREF_ICON_SIZE,				"Middle" );
					editor.putBoolean( Const.PREF_SKELETON_POST_SCREEN,	false );
					editor.putBoolean( Const.PREF_BACK_LIGHT,			true );
					editor.putString( Const.PREF_ORIENTATION,			"default" );
					editor.commit();
					
					Toast.makeText(
						context,
						String.format( "" + getText( R.string.verifiedMsg ), Const.MAIN_MY_SCREEN_NAME ),
						Toast.LENGTH_SHORT
					).show();
					//new loadTwitterHome( context ).execute();

					tweetBtn.setEnabled( true );
					loadBtn.setEnabled( true );
					streamBtn.setEnabled( true );
					new checkFollowersBlocks( context ).execute();
				}
				catch( TwitterException ex )
				{
					ex.printStackTrace();
				}
			}
			break;
			
		case Const.INTENT_POST_CODE:
			if( resultCode == RESULT_OK )
			{
				super.onActivityResult( requestCode, resultCode, intent );
				String tweet = intent.getExtras().getString( Const.TWEET_MSG );
				long id = intent.getExtras().getLong( Const.IN_REPLY_TO );
				new postTwitter( this, tweet, id ).execute();
			}
			break;
		case Const.INTENT_SETTINGS_CODE:
			Intent myIntent = getIntent();
			//overridePendingTransition( 0, 0 );
			//intent.addFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION );
			finish();

			//overridePendingTransition(0, 0);
			startActivity( myIntent );
			break;
		}
	}
	
	private void activityPost( String msg, int pos, long id )
	{
		Intent intent = new Intent( this, ActivityPost.class );
		intent.putExtra( Const.TWEET_MSG, msg );
		intent.putExtra( Const.TWEET_MSG_POS, pos );
		intent.putExtra( Const.IN_REPLY_TO, id );
		
		this.startActivityForResult( intent, Const.INTENT_POST_CODE );
	}
	
	private void activityLogin( String authorizationURL )
	{
		Intent intent = new Intent( this, ActivityLogin.class );
		intent.putExtra( Const.IEXTRA_AUTH_URL, authorizationURL );
		
		this.startActivityForResult( intent, Const.INTENT_OAUTH_CODE );
	}
	
	private void activityTalk( long myId )
	{
		Intent intent = new Intent( this, ActivityTalk.class );
		intent.putExtra( Const.STATUS_ID, myId );
		
		this.startActivity( intent );
	}
	
	public class DialogTweet extends AlertDialog implements DialogInterface
	{
		private Context myContext;
		private String screenName = "";
		private String text = "";
		private long id = 0;
		private String inReplyToName = "";
		private String quoteTweetType = Const.QUOTE_TWEET;
		private twitter4j.UserMentionEntity[] mentions = null;
		private twitter4j.URLEntity[] urls = null;
		private twitter4j.HashtagEntity[] hashtags = null;
		protected DialogTweet( Context context, MyStatus status )
		{
			super( context );
			setTitle( "" );
			setTheme( R.style.Black );
			
			this.myContext = context;
			this.screenName = status.getScreenName();
			this.text = status.getTweetRaw();
			this.id = status.getStatusId();
			this.inReplyToName = status.getReplyToScreenName();
			this.mentions = status.getUserMentionEntities();
			this.urls = status.getURLEntities();
			this.hashtags = status.getHashtagEntities();
			
			ScrollView scrollView = new ScrollView( context );
			
			LinearLayout layout = new LinearLayout( context );
			layout.setOrientation( LinearLayout.VERTICAL );
			
			Button replyBtn = new Button( context );
			replyBtn.setText( R.string.replyText );
			replyBtn.setGravity( Gravity.LEFT | Gravity.CENTER );
			replyBtn.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick( View v )
				{
					String buf = "@" + screenName + " ";
					activityPost( buf, buf.length(), id );
					close();
				}
			});
			layout.addView( replyBtn );
			
			if( !status.isProtect() )
			{
				
				Button retweetBtn = new Button( context );
				retweetBtn.setText( R.string.retweetText );
				retweetBtn.setGravity( Gravity.LEFT | Gravity.CENTER );
				retweetBtn.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v )
					{
						new RFDTwitter( myContext, Const.RETWEET, id ).execute();
						close();
					}
				});
				layout.addView( retweetBtn );
				
				Button quoteBtn = new Button( context );
				if( quoteTweetType.contains( "unofficialRetweet" ) ) quoteBtn.setText( "非公式リツイート" );
				else if( quoteTweetType.contains( "quotedTweet" ) ) quoteBtn.setText( "引用ツイート" );
				else quoteBtn.setText( "(不明な形式)" );
				quoteBtn.setGravity( Gravity.LEFT | Gravity.CENTER );
				quoteBtn.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v )
					{
						String buf = "";
						if( quoteTweetType.equals( "unofficialRetweet" ) ) buf = "RT @" + screenName + ": " + text;
						else if( quoteTweetType.equals( "unofficialRetweetVia" ) ) buf = "RT " + text + " (via @" + screenName + ")";
						else if( quoteTweetType.equals( "quotedTweet" ) ) buf = " QT @" + screenName + ": " + text;
						else if( quoteTweetType.equals( "quotedTweetVia" ) ) buf = " QT " + text + " (via @" + screenName + ")";
						else if( quoteTweetType.equals( "quotedTweetMark" ) ) buf = " “@" + screenName + ": " + text;
						else if( quoteTweetType.equals( "quotedTweetMark2" ) ) buf = " “@" + screenName + ": " + text + "”";
						
						if( quoteTweetType.contains( "unofficialRetweet" )
							|| quoteTweetType.contains( "quotedTweetMark" ) ) activityPost( buf, 0, 0 );
						else if( quoteTweetType.contains( "quotedTweet" ) ) activityPost( buf, 0, id );
						close();
					}
				});
				layout.addView( quoteBtn );
				
			}
			
			if( status.isFav() )
			{
				Button unfavoriteBtn = new Button( context );
				unfavoriteBtn.setText( R.string.unfavoriteText ); 
				unfavoriteBtn.setGravity( Gravity.LEFT | Gravity.CENTER );
				unfavoriteBtn.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v )
					{
						new RFDTwitter( myContext, Const.UNFAVORITE, id ).execute();
						close();
					}
				});
				layout.addView( unfavoriteBtn );
			}
			else
			{
				Button favoriteBtn = new Button( context );
				favoriteBtn.setText( R.string.favoriteText ); 
				favoriteBtn.setGravity( Gravity.LEFT | Gravity.CENTER );
				favoriteBtn.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v )
					{
						new RFDTwitter( myContext, Const.FAVORITE, id ).execute();
						close();
					}
				});
				layout.addView( favoriteBtn );
			}
			
			if( screenName.equals( Const.MAIN_MY_SCREEN_NAME ) )
			{
				Button deleteBtn = new Button( context );
				deleteBtn.setText( R.string.deleteText ); 
				deleteBtn.setGravity( Gravity.LEFT | Gravity.CENTER );
				deleteBtn.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v )
					{
						new RFDTwitter( myContext, Const.DELETE, id ).execute();
						close();
					}
				});
				layout.addView( deleteBtn );
			}
			
			// 会話ボタン
			if( inReplyToName != null )
			{
				Button talkBtn = new Button( context );
				talkBtn.setText( R.string.talkText );
				talkBtn.setGravity( Gravity.LEFT | Gravity.CENTER );
				talkBtn.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v )
					{
						activityTalk( id );
						close();
					}
				});
				
				layout.addView( talkBtn );
			}
			
			// Mention リスト
			layout.addView( createMentionBtn( screenName ) );
			if( mentions != null )
			{
				String names = screenName;
				for( twitter4j.UserMentionEntity mention : mentions )
				{
					if( !names.contains( mention.getScreenName() ) )
						layout.addView( createMentionBtn( mention.getScreenName() ) );
					names += mention.getScreenName();
				}
			}
				
			// URL リスト
			if( urls != null )
				for( twitter4j.URLEntity url : urls )
				{
					layout.addView( createUrlBtn( url ) );
				}
			
			// Hashtag リスト
			if( hashtags != null )
				for( twitter4j.HashtagEntity hashtag : hashtags )
				{
					layout.addView( createHashTagBtn( hashtag ) );
				}
			
			scrollView.addView( layout );
			setView( scrollView );
		}
		
		private Button createMentionBtn( String screenName )
		{
			Button mentionBtn = new Button( context );
			mentionBtn.setTag( "http://twitter.com/" + screenName );
			mentionBtn.setText( "@" + screenName ); 
			mentionBtn.setGravity( Gravity.LEFT | Gravity.CENTER );
			mentionBtn.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick( View v )
				{
					close();
					Intent intent = new Intent( Intent.ACTION_VIEW );
					intent.setData( Uri.parse( ( String )v.getTag() ) );
					startActivity( intent );
				}
			});
			return mentionBtn;
		}
		
		private Button createUrlBtn( twitter4j.URLEntity url )
		{
			String viewText;
			try
			{
				viewText = url.getExpandedURL().toString();
				if( viewText.length() > 31 ) viewText = viewText.substring( 0, 30 ) + "…";
			}
			catch( NullPointerException ex )
			{
				viewText = url.getURL().toString();
			}
			
			Button urlBtn = new Button( context );
			urlBtn.setTag( url.getURL().toString() );
			urlBtn.setText( viewText ); 
			urlBtn.setGravity( Gravity.LEFT | Gravity.CENTER );
			urlBtn.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick( View v )
				{
					close();
					Intent intent = new Intent( Intent.ACTION_VIEW );
					intent.setData( Uri.parse( ( String )v.getTag() ) );
					startActivity( intent );
				}
			});
			return urlBtn;
		}
		
		private Button createHashTagBtn( twitter4j.HashtagEntity hashtag )
		{
			String viewText = "#" + hashtag.getText();
			if( viewText.length() > 26 ) viewText = viewText.substring( 0, 25 ) + "…";
			
			Button hashtagBtn = new Button( context );
			hashtagBtn.setTag( "http://twitter.com/#!/search/%23" + hashtag.getText() );
			hashtagBtn.setText( viewText ); 
			hashtagBtn.setGravity( Gravity.LEFT | Gravity.CENTER );
			hashtagBtn.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick( View v )
				{
					close();
					Intent intent = new Intent( Intent.ACTION_VIEW );
					intent.setData( Uri.parse( ( String )v.getTag() ) );
					startActivity( intent );
				}
			});
			return hashtagBtn;
		}
		
		private void close()
		{
			this.dismiss();
		}
	}

	
	private class startTwitter extends AsyncTask< Void, String, Void >
	{
		private Context _context;
		private boolean flg = false;
		
		public startTwitter( Context context )
		{
			this._context = context;
		}
		
		@Override
		protected Void doInBackground( Void... v )
		{
			twitter = new TwitterFactory().getInstance();
			
			if( Const.MAIN_AOUTH_KEY_TOKEN.length() > 0 && Const.MAIN_AOUTH_KEY_SECRET.length() > 0)
			{
				flg = true;
				twitter.setOAuthAccessToken( new AccessToken( Const.MAIN_AOUTH_KEY_TOKEN, Const.MAIN_AOUTH_KEY_SECRET ) );
			}
			else
			{
				twitter.setOAuthAccessToken( null );
				   
				try
				{
					requestToken = twitter.getOAuthRequestToken( Const.CALLBACK_URL );
					
					activityLogin( requestToken.getAuthorizationURL() );
				}
				catch( TwitterException ex )
				{
					// -1: インターネットに接続できないなど  それ以外は oAuth 関係エラー
					publishProgress( "" + ex.getStatusCode() );
				}
			}
			return ( Void )null;
		}
		
		@Override
		protected void onProgressUpdate( String... error )
		{
			Toast.makeText( _context, getString( R.string.cannotConnectMsg ) + ": " + error[0], Toast.LENGTH_LONG ).show();
		}
		
		@Override
		protected void onPostExecute( Void v )
		{
			if( flg )
			{
				tweetBtn.setEnabled( true );
				loadBtn.setEnabled( true );
				streamBtn.setEnabled( true );
				new checkFollowersBlocks( context ).execute();
			}
			//if( flg ) new loadTwitterHome( _context ).execute();
		}
	}
	
	public class loadTwitterHome extends AsyncTask< Void, String, Void >
	{
		private Context context;
		
		public loadTwitterHome( Context context )
		{
			this.context = context;
		}
		
		@Override
		protected void onPreExecute()
		{
			Toast.makeText( context, R.string.loadingMsg, Toast.LENGTH_SHORT ).show();
		}
		
		@Override
		protected Void doInBackground( Void... v )
		{			
			List< twitter4j.Status >statuses = null;
			int i = 0;
			try
			{
				statuses = twitter.getHomeTimeline( new Paging( 1, 60 ) );
				//Collections.reverse( statuses );
				
				if( statuses.get( 0 ).getId() != lastShowId )
					for( twitter4j.Status status : statuses )
					{
						long myId = status.getId();
						MyStatus stat = new MyStatus( status );
						if( stat.isBlock() ) continue;
						
						if( myId > lastShowId )
						{
							list.add( i, stat );
							i++;
						}
					}
				
				int lSize = list.size();
				if( lSize > 60 )
					list.subList( 61, lSize ).clear();
				
				lastShowId = statuses.get( 0 ).getId();
			}
			catch( TwitterException ex )
			{
				publishProgress( getString( R.string.cannotConnectMsg ) );
			}
			catch( NullPointerException ex )
			{
				publishProgress( getString( R.string.cannotConnectMsg ) );
			}
			
			return ( Void )null;
		}
		
		@Override
		protected void onProgressUpdate( String... error )
		{
			Toast.makeText( context, error[0], Toast.LENGTH_LONG ).show();
		}

		@Override
		protected void onPostExecute( Void v )
		{
			new Thread()
			{
				public void run()
				{
					runOnUiThread( new Runnable()
					{
						public void run()
						{
							listView.invalidateViews();
						}
					});
				}
			}.start();
			loadBtn.setEnabled( true );
		}
		
	}
	
	private class loadTwitterStream extends AsyncTask< Void, String, Void >
	{
		private Context context;
		
		public loadTwitterStream( Context context )
		{
			this.context = context;
		}
		
		private ConnectionLifeCycleListener connectionLifeCycleListener = new ConnectionLifeCycleListener()
		{
			@Override  
			public void onConnect()
			{  
				//Log.d(getClass().getSimpleName(), "TwitterStream.onConnect!"); 
				//Log.d( "mntone-stream", "connect" );
				streamChangeFlg = false;
				streamFlg = true;
				publishProgress( "onConnect" );
			}  
		  
			@Override  
			public void onDisconnect()
			{  
				//streamBtn.setText( "US ■" );
				//Log.d(getClass().getSimpleName(), "TwitterStream.onDisconnect!");  
				//Log.d( "mntone-stream", "disconnect" );
				streamChangeFlg = streamFlg = false;
				publishProgress( "onDisconnect" );
			}  
		  
			@Override  
			public void onCleanUp()
			{  
				//Log.d(getClass().getSimpleName(), "TwitterStream.onCleanUp!");  
				//Log.d( "mntone-stream", "cleanup" );
			}
		};
		
		private UserStreamAdapter userStreamAdapter = new UserStreamAdapter()
		{
			@Override
			public void onStatus( twitter4j.Status status )
			{
				lastShowId = status.getId();
				MyStatus stat = new MyStatus( status );
				if( !stat.isBlock() )
				{
					list.add( 0, new MyStatus( status ) );
					
					int lSize = list.size();
					if( lSize > 60 )
						list.subList( 60, lSize ).clear();
					
					publishProgress( "onStatus" );
				}
			}
			
			@Override
			public void onFavorite( User source, User target, twitter4j.Status favoritedStatus )
			{
				//if( !source.getScreenName().contains( myScreenName ) )
				String sName = source.getName();
				if( !sName.equals( Const.MAIN_MY_SCREEN_NAME ) )
					publishProgress( "onFavorite", sName, favoritedStatus.getText() );
			}
			
			@Override
			public void onDirectMessage( DirectMessage directMessage )
			{
				//if( !directMessage.getSender().getScreenName().contains( myScreenName ) )
					publishProgress( "onDirectMessage", directMessage.getSender().getName(), directMessage.getText() );
			}
			
			@Override
			public void onFollow( User source, User followedUser )
			{
				publishProgress( "onFollow", source.getName() );
			}
			
			/*@Override
			public void onScrubGeo( long userId, long upToStatusId )
			{
				while( listLock )
					try { Thread.sleep( 400, 0 ); } catch( InterruptedException ex ) {}

				// 更新
				listLock = true;
					
				for( int i = 0; i < list.size(); i++ )
				{
					if( list.get( i ).getUser().getId() == userId && list.get( i ).getId() == upToStatusId )
					{
						list.remove( i );
						break;
					}
				}
					
				publishProgress( "onScrubGeo", "" + userId, "" + upToStatusId );
			}*/
		};
		
		@Override
		public void onProgressUpdate( String... str )
		{
			if( str[0] == "onStatus" )
			{
				new Thread()
				{
					public void run()
					{
						runOnUiThread( new Runnable()
						{
							public void run()
							{
								listView.invalidateViews();
							}
						});
					}
				}.start();
			}
			/*else if( str[0] == "onScrubGeo" )
			{
				listUpdate();
				listLock = false;
			}*/	
			else if( str[0] == "onFavorite" )
				Toast.makeText(
					context, 
					String.format( getString( R.string.favoriteReceiveMsg ), str[1], str[2] ),
					Toast.LENGTH_LONG
				).show();
			
			else if( str[0] == "onDirectMessage" )
				Toast.makeText(
					context,
					String.format( getString( R.string.directMessageReceiveMsg ), str[1], str[2] ),
					Toast.LENGTH_LONG
				).show();
			
			else if( str[0] == "onFollow" )
				Toast.makeText(
					context,
					String.format( getString( R.string.followReceiveMsg ), str[1] ),
					Toast.LENGTH_LONG
				).show();
			
			else if( str[0] == "onDisconnect" )
			{
				streamBtn.setText( R.string.userStreamDisconnectedText );
				streamBtn.setEnabled( true );
				Toast.makeText( context, R.string.userStreamDisconnectMsg, Toast.LENGTH_LONG ).show();
			}
			
			else if( str[0] == "onConnect" )
			{
				streamBtn.setText( R.string.userStreamConnectedText );
				streamBtn.setEnabled( true );
				Toast.makeText( context, R.string.userStreamConnectMsg, Toast.LENGTH_LONG ).show();
			}
		}
		
		@Override
		protected Void doInBackground( Void... v )
		{	
			Configuration conf = new ConfigurationBuilder()
				.setOAuthAccessToken( Const.MAIN_AOUTH_KEY_TOKEN )
				.setOAuthAccessTokenSecret( Const.MAIN_AOUTH_KEY_SECRET )
				.build()
			;
			
			twitterStream = new TwitterStreamFactory( conf ).getInstance();
			twitterStream.addListener( userStreamAdapter );
			twitterStream.addConnectionLifeCycleListener( connectionLifeCycleListener );
			twitterStream.user();
			
			return ( Void )null;
		}
		
		@Override
		protected void onPostExecute( Void v )
		{
			Toast.makeText( context, R.string.userStreamConnectMsg, Toast.LENGTH_SHORT ).show();
		}
	}
	
	public class postTwitter extends AsyncTask< Void, String, Void >
	{
		private Context context;
		private String tweet = "";
		private long id = 0;
		
		public postTwitter( Context context, String tweet, long id )
		{
			this.context = context;
			this.tweet = tweet;
			this.id = id;
		}
		
		@Override
		protected void onPreExecute()
		{
			Toast.makeText( context, R.string.tweetPostingMsg, Toast.LENGTH_SHORT ).show();
		}
		
		@Override
		protected Void doInBackground( Void... v )
		{
			try
			{
				StatusUpdate statusUpdate = new StatusUpdate( tweet );
				if( id != 0 ) statusUpdate.inReplyToStatusId( id );
				twitter.updateStatus( statusUpdate );
			}
			catch( TwitterException ex )
			{
				if( !Const.SUB_MY_SCREEN_NAME.equals( "" ) )
				{
					Configuration conf = new ConfigurationBuilder()
						.setOAuthAccessToken( Const.SUB_AOUTH_KEY_TOKEN )
						.setOAuthAccessTokenSecret( Const.SUB_AOUTH_KEY_SECRET )
						.build()
					;
					Twitter twitter2 = new TwitterFactory( conf ).getInstance();	
					try
					{
						StatusUpdate statusUpdate = new StatusUpdate( tweet );
						if( id != 0 ) statusUpdate.inReplyToStatusId( id );
						twitter2.updateStatus( statusUpdate );
					}
					catch( TwitterException ex2 )
					{
						publishProgress( "ex2 err" );//ex2.getMessage() );
					}
				}
				else
					publishProgress( ex.getMessage() );
			}
			return ( Void )null;
		}
		
		@Override
		protected void onProgressUpdate( String... str )
		{
			if( str[0].contains( "User is over daily status update limit." ) )
				Toast.makeText( context, getText( R.string.overDailyStatusMsg ), Toast.LENGTH_SHORT ).show();
			else
				Toast.makeText( context, str[0], Toast.LENGTH_SHORT ).show();
		}
	}
	
	public class RFDTwitter extends AsyncTask< Void, Void, Void >
	{
		private Context context;
		private int mode = 0;
		private long id = 0;
		
		public RFDTwitter( Context context, int mode, long id )
		{
			this.context = context;
			this.mode = mode;
			this.id = id;
		}
		
		@Override
		protected void onPreExecute()
		{
			switch( mode )
			{
			case Const.RETWEET:
				Toast.makeText( context, R.string.retweetPostingMsg, Toast.LENGTH_SHORT ).show();
				break;
			case Const.FAVORITE:
				Toast.makeText( context, R.string.favoritePostingMsg, Toast.LENGTH_SHORT ).show();
				break;
			case Const.UNFAVORITE:
				Toast.makeText( context, R.string.unfavoritePostingMsg, Toast.LENGTH_SHORT ).show();
				break;
			case Const.DELETE:
				Toast.makeText( context, R.string.deletePostingMsg, Toast.LENGTH_SHORT ).show();
				break;
			}
		}
		
		@Override
		protected Void doInBackground( Void... v )
		{
			switch( mode )
			{
			case Const.RETWEET:
				try
				{
					twitter.retweetStatus( id );
				}
				catch( TwitterException ex ){}
				break;
			case Const.FAVORITE:
				try
				{
					twitter.createFavorite( id );
				}
				catch( TwitterException ex ){}
				break;
			case Const.UNFAVORITE:
				try
				{
					twitter.destroyFavorite( id );
				}
				catch( TwitterException ex ){}
				break;
			case Const.DELETE:
				try
				{
					twitter.destroyStatus( id );
				}
				catch( TwitterException ex ){}
				break;
			}
			return ( Void )null;
		}
	}
	
	public class checkFollowersBlocks extends AsyncTask< Void, String, Void >
	{
		private Context _context;
		private ProgressDialog _dialog;
		public checkFollowersBlocks( Context context )
		{
			this._context = context;
		}
		
		@Override
		protected void onPreExecute()
		{
			_dialog = ProgressDialog.show( _context, "", getText( R.string.getFollowersMsg ) );
		}
		
		@Override
		protected Void doInBackground( Void... v )
		{
			Const.FOLLOWER_LIST = new GetFollowers().getList();
			publishProgress( "" + getText( R.string.getBlockUsersMsg ) );
			Const.BLOCK_LIST = new GetBlockUsers().getList();
			return ( Void )null;
		}
		
		@Override
		protected void onProgressUpdate( String... error )
		{
			_dialog.setMessage( error[0] );
		}
		
		@Override
		protected void onPostExecute( Void v )
		{
			_dialog.hide();
		}
	}
}