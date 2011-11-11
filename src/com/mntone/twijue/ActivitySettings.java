package com.mntone.twijue;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class ActivitySettings extends PreferenceActivity
{
	private Context context;
	private Twitter twitter;
	private RequestToken requestToken;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
    	// テーマの設定
    	setTheme( new MyTheme( Const.THEME, "", "" ).getTheme() );
		setTitle( R.string.setttingsTitle );
		
		super.onCreate( savedInstanceState );
		getPreferenceManager().setSharedPreferencesName( Const.PREFERENCE_NAME );
		this.addPreferencesFromResource( R.xml.settings );
		
		context = this;
		
		PreferenceScreen mainAccountReset = ( PreferenceScreen )findPreference( getText( R.string.PREF_MAIN_ACCOUNT_RESET ) );
		mainAccountReset.setSummary(
			String.format( "" + getText( R.string.settingsMainAccountResetSummary ), Const.MAIN_MY_SCREEN_NAME )
		);
		mainAccountReset.setOnPreferenceClickListener( new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick( Preference arg0 )
			{
				new AlertDialog.Builder( context )
					.setTitle( getText( R.string.settingsMainAccountResetTitle ) )
					.setMessage( getText( R.string.settingsMainAccountResetMsg ) )
					.setPositiveButton( getText( R.string.yesText ), new DialogInterface.OnClickListener() {
						@Override
						public void onClick( DialogInterface dialog, int which )
						{
							SharedPreferences pref = getSharedPreferences( Const.PREFERENCE_NAME, MODE_PRIVATE );
			    			SharedPreferences.Editor editor = pref.edit();
			    			editor.putString( Const.PREF_MAIN_MY_SCREEN_NAME  , "" );
			    			editor.putString( Const.PREF_MAIN_AOUTH_KEY_TOKEN,  "" );
			    			editor.putString( Const.PREF_MAIN_AOUTH_KEY_SECRET, "" );
			    			editor.commit();
						}
					})
					.setNegativeButton( getText( R.string.noText ), null )
					.show()
				;
				return false;
			}
		});
		
		PreferenceScreen subAccountAuth = ( PreferenceScreen )findPreference( getText( R.string.PREF_SUB_ACCOUNT_AUTH ) );
		if( !Const.SUB_MY_SCREEN_NAME.equals( "" ) ) subAccountAuth.setEnabled( false );
		subAccountAuth.setOnPreferenceClickListener( new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick( Preference arg0 )
			{
				twitter = new TwitterFactory().getInstance();
				twitter.setOAuthAccessToken( null );
				
				try
			    {
			    	requestToken = twitter.getOAuthRequestToken( Const.CALLBACK_URL );
			    	
			    	Intent intent = new Intent( context, ActivityLogin.class );
			    	intent.putExtra( Const.IEXTRA_AUTH_URL, requestToken.getAuthorizationURL() );
			    	
			    	startActivityForResult( intent, Const.INTENT_OAUTH_CODE );
			    }
			    catch( TwitterException ex )
			    {
			    	// -1: インターネットに接続できないなど  それ以外は oAuth 関係エラー
			    	Toast.makeText( context, "" + ex.getStatusCode(), Toast.LENGTH_SHORT ).show();
			    }
				
				return false;
			}
		});
		
		PreferenceScreen subAccountReset = ( PreferenceScreen )findPreference( getText( R.string.PREF_SUB_ACCOUNT_RESET ) );
		if( Const.SUB_MY_SCREEN_NAME.equals( "" ) )
			subAccountReset.setEnabled( false );
		else
			subAccountReset.setSummary(
				String.format( "" + getText( R.string.settingsSubAccountResetSummary ), Const.SUB_MY_SCREEN_NAME )
			);
		subAccountReset.setOnPreferenceClickListener( new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick( Preference arg0 )
			{
				new AlertDialog.Builder( context )
					.setTitle( getText( R.string.settingsSubAccountResetTitle ) )
					.setMessage( getText( R.string.settingsSubAccountResetMsg ) )
					.setPositiveButton( getText( R.string.yesText ), new DialogInterface.OnClickListener() {
						@Override
						public void onClick( DialogInterface dialog, int which )
						{
							SharedPreferences pref = getSharedPreferences( Const.PREFERENCE_NAME, MODE_PRIVATE );
			    			SharedPreferences.Editor editor = pref.edit();
			    			editor.putString( Const.PREF_SUB_MY_SCREEN_NAME  , "" );
			    			editor.putString( Const.PREF_SUB_AOUTH_KEY_TOKEN,  "" );
			    			editor.putString( Const.PREF_SUB_AOUTH_KEY_SECRET, "" );
			    			editor.commit();
						}
					})
					.setNegativeButton( getText( R.string.noText ), null )
					.show()
				;
				return false;
			}
		});
		
		CheckBoxPreference enterPost = ( CheckBoxPreference )findPreference( getText( R.string.PREF_ENTER_POST ) );
		enterPost.setOnPreferenceChangeListener( new OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange( Preference pref, Object value )
			{
				Const.ENTER_POST = ( Boolean )value;
				return true;
			}
			
		});
		
		ListPreference quoteTweet = ( ListPreference )findPreference( getText( R.string.PREF_QUOTE_TWEET ) );
		quoteTweet.setOnPreferenceChangeListener( new OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange( Preference pref, Object value )
			{
				Const.QUOTE_TWEET = ( String )value;
				return true;
			}
			
		});
		
		ListPreference theme = ( ListPreference )findPreference( getText( R.string.PREF_THEME ) );
		theme.setOnPreferenceChangeListener( new OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange( Preference pref, Object value )
			{
				Const.THEME = ( String )value;
				return true;
			}
			
		});
		
		ListPreference fontSize = ( ListPreference )findPreference( getText( R.string.PREF_FONT_SIZE ) );
		fontSize.setOnPreferenceChangeListener( new OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange( Preference pref, Object value )
			{
				Const.FONT_SIZE = ( String )value;
				return true;
			}
			
		});
		
		CheckBoxPreference backLight = ( CheckBoxPreference )findPreference( getText( R.string.PREF_BACK_LIGHT ) );
		backLight.setOnPreferenceChangeListener( new OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange( Preference pref, Object value )
			{
				Const.BACK_LIGHT = ( Boolean )value;
				return true;
			}
			
		});
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		Intent intent = getIntent();
		setResult( Activity.RESULT_OK, intent );
		finish();
	}
	
    protected void onActivityResult( int requestCode, int resultCode, Intent intent )
    {
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
    			
        		try
        		{
        			Const.SUB_MY_SCREEN_NAME = twitter.getScreenName();
    			} catch( TwitterException ex ) {}
    			
    	    	Const.SUB_AOUTH_KEY_TOKEN  = accessToken.getToken();
    	    	Const.SUB_AOUTH_KEY_SECRET = accessToken.getTokenSecret();
    			
    			SharedPreferences pref = getSharedPreferences( Const.PREFERENCE_NAME, MODE_PRIVATE );
    			SharedPreferences.Editor editor = pref.edit();
    			editor.putString( Const.PREF_SUB_MY_SCREEN_NAME  , Const.SUB_MY_SCREEN_NAME );
    			editor.putString( Const.PREF_SUB_AOUTH_KEY_TOKEN,  Const.SUB_AOUTH_KEY_TOKEN );
    			editor.putString( Const.PREF_SUB_AOUTH_KEY_SECRET, Const.SUB_AOUTH_KEY_SECRET );
    			editor.commit();
    			
    			Toast.makeText(
    				context,
    				String.format( "" + getText( R.string.verifiedMsg ), Const.SUB_MY_SCREEN_NAME ),
    				Toast.LENGTH_SHORT
    			).show();
    		}
    		catch( TwitterException ex )
    		{
    			ex.printStackTrace();
    		}
    	}
    }
	
}
