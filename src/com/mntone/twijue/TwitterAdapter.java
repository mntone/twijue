package com.mntone.twijue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TwitterAdapter extends ArrayAdapter< MyStatus >
{
	private ArrayList< MyStatus >items;
	private LayoutInflater inflater;
	//private final WebImageView webImageView;
	private Context context;
	private String regexp = "^<a href=\"(.+?)\" rel=\"nofollow\">(.+?)</a>$";
	private ArrayList< String > idList = new ArrayList< String >();

	public TwitterAdapter( Context context, int textViewResourceId, ArrayList< MyStatus > items )
	{
		super( context, textViewResourceId, items );
		this.context = context;
		this.items = items;
		this.inflater = ( LayoutInflater )context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	}
	
	@Override
	public View getView( final int position, View convertView, ViewGroup parent )
	{		
		View view = convertView;
		if( view == null )
			view = inflater.inflate(
				Const.ICON_SIZE.equals( "Large" )
					? R.layout.tweetlarge
					: ( Const.ICON_SIZE.equals( "Small" ) ? R.layout.tweetsmall : R.layout.tweetmiddle ),
				parent,
				false
			);		
		final MyStatus item = ( MyStatus )items.get( position );
		/*final ProfileImage profileImage;
		try
		{
			profileImage = twitter.getProfileImage( item.getUser().getScreenName(), ProfileImage.BIGGER );
			webImageView.setUrl( profileImage.getURL() );
		}
		catch( TwitterException ex )
		{
		}*/
		
		if( item != null && item.getStatusId() != 0 )
		{
			view.setVisibility( View.VISIBLE );
			
			final TextView bar = ( TextView )view.findViewById( R.id.bar );
			final TextView screen_name = ( TextView )view.findViewById( R.id.screen_name );
			//screen_name.setTypeface( Typeface.DEFAULT_BOLD );
			final TextView name   = ( TextView )view.findViewById( R.id.name );
			final TextView text   = ( TextView )view.findViewById( R.id.text );
			final TextView others = ( TextView )view.findViewById( R.id.others );
			final ImageView profile_image = ( ImageView )view.findViewById( R.id.profile_image );
			final LinearLayout retweet = ( LinearLayout )view.findViewById( R.id.retweet_box );
			final ImageView retweet_profile_image = ( ImageView )view.findViewById( R.id.retweet_profile_image );
			final TextView retweet_text = ( TextView )view.findViewById( R.id.retweet_text );
			final ImageView key = ( ImageView )view.findViewById( R.id.key );
			final ImageView fav = ( ImageView )view.findViewById( R.id.fav );
			
			Pattern pattern = Pattern.compile( regexp );
			
			//final long myId;
			final String myScreenName = item.getScreenName();
			final String myName = item.getName();
			final String myProfileImageUrl = "http://img.tweetimag.es/i/" + myScreenName + "_b";
			final String myText = item.getTweet();
			String myClientName = item.getClient();

			// protected flag
			if( item.isProtect() )
				 key.setVisibility( View.VISIBLE );
			else key.setVisibility( View.GONE );
			
			// favorite flag
			if( item.isFav() )
				 fav.setVisibility( View.VISIBLE );
			else fav.setVisibility( View.GONE );
			
			// retweet flag
			if( item.isRt() )
			{
				retweet.setVisibility( View.VISIBLE );
				retweet_profile_image.setTag( myScreenName );
				
				//final long myRetweetId		 = item.getId();
				final String myRetweetScreenName = item.getRetweetedScreenName();
				final String myRetweetName	  	 = item.getRetweetedName();
				if( retweet_text != null )
				{
					if( "White".equals( Const.THEME ) )
						retweet_text.setText( Html.fromHtml( String.format(
							"<font color=\"#000000\"><b>" + myRetweetScreenName + "</b></font> (" + myRetweetName + ") がリツイート"
						) ) );
					else
						retweet_text.setText( Html.fromHtml( String.format(
							"<font color=\"#ffffff\"><b>" + myRetweetScreenName + "</b></font> (" + myRetweetName + ") がリツイート"
						) ) );
				}
				profile_image.setTag( myRetweetScreenName );
				
				if( retweet_profile_image != null )
				{
					//retweet_profile_image.setTag( myRetweetId );
					fetchPhoto(
						context,
						myRetweetScreenName,
						retweet_profile_image,
						//item.getUser().getProfileImageURL().toString()
						"http://img.tweetimag.es/i/" + myRetweetScreenName + "_b"
					);
				}
			}
			else 
			{
				retweet.setVisibility( View.GONE );
				profile_image.setTag( myScreenName );
			}
			Matcher matcher = pattern.matcher( myClientName );
			while( matcher.find() ) { myClientName = matcher.group( 2 ); }
			
			// (ツイート処理を入れるならここ)
			
			// getContext().getResources().getColor( R.color.BlackTextColorPrimary )
			if( item.isRt() )
				bar.setBackgroundColor( Color.rgb( 0x00, 0xcc, 0x44 ) );
			else if( myText.contains( "<u>@" + Const.MAIN_MY_SCREEN_NAME + "</u>" ) )
				bar.setBackgroundColor( Color.rgb( 0xe6, 0x00, 0x11 ) );
			else if( myScreenName.equals( Const.MAIN_MY_SCREEN_NAME ) || myScreenName.equals( Const.SUB_MY_SCREEN_NAME ) )
				bar.setBackgroundColor( Color.rgb( 0xee, 0x82, 0xee ) );
			//else if( item.isFav() )
			//	bar.setBackgroundColor( Color.rgb( 0xff, 0xd9, 0x00 ) );
			else if( !item.isFollow() )
				//bar.setBackgroundColor( Color.rgb( 0x00, 0x00, 0xff ) );
				bar.setBackgroundColor( Color.rgb( 0x90, 0xd7, 0xec ) );
			else if( Const.THEME.equals( "White" ) )
				bar.setBackgroundColor( Color.rgb( 0xd5, 0xd5, 0xd5 ) );
			else
				bar.setBackgroundColor( Color.rgb( 0x2a, 0x2a, 0x2a ) );
			
			if( screen_name != null ) screen_name.setText( myScreenName );
			if( name != null )		  name.setText( myName );
			if( text != null )		  text.setText( Html.fromHtml( myText ) );
			if( others != null )
			{
				String buf = ( Date2String( item.getDate() ) ) + " " + myClientName + "から";
				String inReplyToScreenName = item.getReplyToScreenName();
				if( inReplyToScreenName != null )
					buf += " " + inReplyToScreenName + "宛";
				others.setText( buf );
			}
			if( profile_image != null )
			{
				//profile_image.setTag( myId );
				fetchPhoto( context, myScreenName, profile_image, myProfileImageUrl );
			}
		}
		else
		{
			view.setVisibility( View.GONE );
		}
		return view;
	}
	
	private String Date2String( Date date )
	{
		String str = "";
		Calendar cal = Calendar.getInstance();
		int delta = Math.round( ( int )( ( cal.getTimeInMillis() - date.getTime() ) / 10000 ) );
		
		if( delta == 0 ) str = "約0秒前";
		else if( delta < 6 ) str = "約" + delta + "0秒前";
		else
		{
			delta /= 6;
			if( delta < 60 ) str = "約" + delta + "分前";
			else
			{
				delta /= 60;
				if( delta < 24 )
					str = "約" + delta + "時間前";
				else
					str = date.toLocaleString();
			}
		}
		
		return str;
	}
	
	private void fetchPhoto( Context context, String screen_name, ImageView imageView, String url )
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;
		boolean flg = false;
		
		try
		{
			//long time = Calendar.getInstance().getTimeInMillis() / 1000;
			
			CacheSqlHelper helper = new CacheSqlHelper( context );
			db = helper.getReadableDatabase();
			String sql = String.format(
					"select %s from `%s` where %s=?",// AND %s - %s < %s",
					Const.CACHE_PROFILE_IMAGE,
					Const.CACHE_TABLE_NAME,
					Const.CACHE_SCREEN_NAME//,
					//"" + time,
					//Const.CACHE_TIME,
					//"" + ( 1 * 60 * 60 ) // 1 時間
			);
			
			cursor = db.rawQuery( sql, new String[]{ screen_name } );
			if( cursor.moveToFirst() )
			{
				flg = true;
				byte[] blob = cursor.getBlob( 0 );
				//ImageView myIV = ( ImageView )myLV.findViewWithTag( myId );
				//if( myIV != null ) myIV.setImageBitmap( BitmapFactory.decodeByteArray( blob, 0, blob.length ) );
				imageView.setImageBitmap( BitmapFactory.decodeByteArray( blob, 0, blob.length ) );
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
		
		if( !flg )
		{
			InputStream is = ( InputStream )context.getResources().openRawResource( R.raw.loading );
			//ImageView myIV = ( ImageView )myLV.findViewWithTag( myId );
			//if( myIV != null ) myIV.setImageBitmap( ( ( BitmapDrawable )Drawable.createFromStream( is, "src name" ) ).getBitmap() );
			imageView.setImageBitmap( ( ( BitmapDrawable )Drawable.createFromStream( is, "src name" ) ).getBitmap() );
			
			/*ByteArrayOutputStream os = new ByteArrayOutputStream();
			int size = 0;
			byte[] byteArray = new byte[1024];
			try
			{
				while( ( size = is.read( byteArray ) ) != -1 )
				{   
					os.write( byteArray, 0, size );   
				}
			}
			catch( Exception ex ){}
			byte[] result = os.toByteArray();
			
			imageView.setImageBitmap( BitmapFactory.decodeByteArray( result, 0, result.length ) );*/
			
			if( !idList.contains( screen_name ) )
			{
				idList.add( screen_name );
				new GetImageTask( context, screen_name, imageView ).execute( url );
			}
		}
	}
}
