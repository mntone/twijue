package com.mntone.twijue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ActivityLogin extends Activity
{
	@Override
	protected void onCreate( Bundle bundle )
	{
		super.onCreate( bundle );
		setContentView( R.layout.login );
		
		WebView webView = ( WebView )findViewById( R.id.loginTwitter );
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled( true );
		webView.setWebViewClient( new WebViewClient()
		{
			
			public void onPageFinished( WebView view, String url )
			{
				super.onPageFinished( view, url );
				
				if( url != null && url.startsWith( Const.CALLBACK_URL ) )
				{
					String[] urlParameters = url.split( "\\?" )[1].split( "&" );
					
					String oAuthToken = "";
					String oAuthVerifier = "";
					
					if( urlParameters[0].startsWith( "oauth_token" ) )
						oAuthToken = urlParameters[0].split( "=" )[1];
					else if( urlParameters[1].startsWith( "oauth_token" ) )
						oAuthToken = urlParameters[1].split( "=" )[1];
					
					if( urlParameters[0].startsWith( "oauth_verifier" ) )
						oAuthVerifier = urlParameters[0].split( "=" )[1];
					else if( urlParameters[1].startsWith( "oauth_verifier" ) )
						oAuthVerifier = urlParameters[1].split( "=" )[1];
					
					Intent intent = getIntent();
					intent.putExtra( Const.IEXTRA_OAUTH_TOKEN, oAuthToken );
					intent.putExtra( Const.IEXTRA_OAUTH_VERIFIER, oAuthVerifier );

					setResult( Activity.RESULT_OK, intent );
					finish();
				}
			}
			
		});
		
		webView.loadUrl( this.getIntent().getExtras().getString( "auth_url" ) );
	}
}
