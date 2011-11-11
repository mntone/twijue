package com.mntone.twijue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityPost extends Activity
{
	private EditText tweetMsg;
	private TextView tweetCnt;
	private Button tweetBtn;
	private Button shortenBtn;
	private int len = 140;
	private String tweetMsgText = "";
	private long inReplyTo = 0;
	
	@Override
	protected void onCreate( Bundle bundle )
	{
    	// テーマの設定
    	if( Const.SKELETON_POST_SCREEN )
    		setTheme( new MyTheme( Const.THEME, Const.FONT_SIZE, "Transparent" ).getTheme() );	
    	else
    		setTheme( new MyTheme( Const.THEME, Const.FONT_SIZE, "" ).getTheme() );
		setTitle( R.string.tweetTitle );
		
		super.onCreate( bundle );
		
		Intent intent = getIntent();
		tweetMsgText = intent.getExtras().getString( Const.TWEET_MSG );
		int tweetMsgPos = intent.getExtras().getInt( Const.TWEET_MSG_POS );
		inReplyTo = intent.getExtras().getLong( Const.IN_REPLY_TO );
		
		// タイトルバー消し
		DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics( metrics );
		if( metrics.widthPixels > metrics.heightPixels || Const.SKELETON_POST_SCREEN )
			requestWindowFeature( Window.FEATURE_NO_TITLE );
		
    	if( Const.SKELETON_POST_SCREEN )
    		setContentView( R.layout.spost );
    	else
    		setContentView( R.layout.post );	
    	
		tweetMsg = ( EditText )findViewById( R.id.postTweetMsg );
		tweetMsg.setText( tweetMsgText );
		tweetMsg.setSelection( tweetMsgPos );
		if( Const.ENTER_POST )
			tweetMsg.setOnKeyListener( new View.OnKeyListener() {
				
				@Override
				public boolean onKey( View v, int keyCode, KeyEvent event )
				{
					if( event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER )
					{
						postTweet();
						return true;
					}
					return false;
				}
			});
		tweetMsg.addTextChangedListener( new TextWatcher() {
			@Override
			public void onTextChanged( CharSequence s, int start, int bfore, int count )
			{
				checkText();
			}

			@Override
			public void afterTextChanged( Editable s )
			{
			}

			@Override
			public void beforeTextChanged( CharSequence s, int start, int count, int after )
			{
			}
		});
		
		tweetCnt = ( TextView )findViewById( R.id.postTweetCnt );
		tweetCnt.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD_ITALIC ) );
		
		tweetBtn = ( Button )findViewById( R.id.postTweetBtn );
        tweetBtn.setOnClickListener( new View.OnClickListener()
        {
        	public void onClick( View view )
        	{
        		postTweet();
        	}
        });
		
		shortenBtn = ( Button )findViewById( R.id.postShortenBtn );
		shortenBtn.setOnClickListener( new View.OnClickListener()
        {
        	public void onClick( View view )
        	{
        		shortenUrl();
        	}
        });
		
		checkText();
	}
	
	private void checkText()
	{
		len = 140 - tweetMsg.getText().length();
		tweetCnt.setText( "" + len );
		if( len < 0 )
			tweetCnt.setTextColor( Color.RED );
		else if( len == 0 )
			tweetCnt.setTextColor( Color.GRAY );
		else if( Const.THEME.equals( "White" ) )
			tweetCnt.setTextColor( Color.BLACK );
		else
			tweetCnt.setTextColor( Color.WHITE );
		
		if( len == 140 && len < 0 )
			tweetBtn.setEnabled( false );
		else
			tweetBtn.setEnabled( true );
	}
	
	private void postTweet()
	{
		if( len > 0 && len < 140 )
		{
			String buf = tweetMsg.getText().toString();
			if( inReplyTo != 0 && !buf.contains( tweetMsgText ) )
				inReplyTo = 0;
			
			Intent intent = getIntent();
			intent.putExtra( Const.TWEET_MSG, buf );
			intent.putExtra( Const.IN_REPLY_TO, inReplyTo );
			
			setResult( Activity.RESULT_OK, intent );
			finish();
		}
	}
	 
	private void shortenUrl()
	{
		new ShortenUrl( this ).execute();
	}
	
	private class ShortenUrl extends AsyncTask< Void, Void, Void >
	{
		private Context context;
		private ProgressDialog progressDialog;
		private String buf = "";
		private int pos = 0;

		public ShortenUrl( Context context )
		{
			this.context = context;
		}
		
		@Override
		protected void onPreExecute()
		{
			progressDialog = new ProgressDialog( context );
			progressDialog.setMessage( getText( R.string.shorteningMsg ) );
			progressDialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
			progressDialog.setCancelable( false );
			progressDialog.show();
			
			buf = tweetMsg.getText().toString();
			pos = tweetMsg.getSelectionStart();
		}
		
		@Override
		protected Void doInBackground( Void... params )
		{
			buf = Const.NICO_LIVE_PATTERN.matcher( buf ).replaceAll( "http://nico.lv/$1$2" );
			buf = Const.NICO_CRUISE_PATTERN.matcher( buf ).replaceAll( "http://nico.lv/cruise" );
			buf = Const.NICO_VIDEO_PATTERN.matcher( buf ).replaceAll( "http://nico.ms/$1" );
			return null;
		}
		
		@Override
		protected void onPostExecute( Void v )
		{
			tweetMsg.setText( buf );
			tweetMsg.setSelection( pos );
			progressDialog.hide();
		}
	}
}
