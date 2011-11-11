package com.mntone.twijue;

import java.util.Date;

public class MyStatus
{
	private boolean _fav = false;
	private boolean _rt = false;
	private boolean _protect = false;
	private boolean _follow = true;
	private boolean _block = false;
	private String _name = "";
	private String _sname = "";
	private long _userId = 0;
	private String _rname = "";
	private String _rsname = "";
	private long _r2statusId = 0;
	private String _r2name = "";
	private String _tweet = "";
	private String _tweetRaw = "";
	private String _client = "";
	private long _statusId = 0;
	private Date _date = new Date();
	private twitter4j.UserMentionEntity[] _mentions;
	private twitter4j.URLEntity[] _urls;
	private twitter4j.HashtagEntity[] _hashtags;
	
	public MyStatus()
	{
	}
	
	public MyStatus( twitter4j.Status stat )
	{
		_fav = stat.isFavorited();
		_rt = stat.isRetweet();
		if( _rt )
		{
			_protect = false;
			_name = stat.getRetweetedStatus().getUser().getName();
			_sname = stat.getRetweetedStatus().getUser().getScreenName();
			_userId = stat.getRetweetedStatus().getUser().getId();
			_rname = stat.getUser().getName();
			_rsname = stat.getUser().getScreenName();
			_tweetRaw = _tweet = stat.getRetweetedStatus().getText();
			//_tweetRaw = _tweet = _tweet.substring( ( "RT @" + _sname + ": " ).length(), _tweet.length() );
			_client = stat.getRetweetedStatus().getSource();
			_statusId = stat.getRetweetedStatus().getId();
			_date = stat.getRetweetedStatus().getCreatedAt();
		}
		else
		{
			_protect = stat.getUser().isProtected();
			_name = stat.getUser().getName();
			_sname = stat.getUser().getScreenName();
			_userId = stat.getUser().getId();
			_tweetRaw = _tweet = stat.getText();
			_client = stat.getSource();
			_statusId = stat.getId();
			_date = stat.getCreatedAt();
		}
		_r2statusId = stat.getInReplyToStatusId();
		_r2name = stat.getInReplyToScreenName();
		
		// ツイート対策
		_tweet = _tweet.replace( "<", "&lt;" );
		_tweet = _tweet.replace( ">", "&gt;" );

		// ブロックユーザーかどうか
		for( long blockUserId : Const.BLOCK_LIST )
		{
			if( blockUserId == _userId )
			{
				_block = true;
				break;
			}
		}
		
		// 以後、ブロックでなければ処理
		if( !_block )
		{
		
		// フォロワーかどうか
		_follow = false;
		for( long followerId : Const.FOLLOWER_LIST )
		{
			if( followerId == _userId )
			{
				_follow = true;
				break;
			}
		}
		
		// ID処理
		//_tweet = Const.ID_MATCH_PATTERN.matcher( _tweet ).replaceAll( "<u>@$1</u>$2" );
		_mentions = stat.getUserMentionEntities();
		for( twitter4j.UserMentionEntity mention : _mentions )
		{
			try
			{
				_tweet = _tweet.replace( "@" + mention.getScreenName(), "<u>@" + mention.getScreenName() + "</u>" );
				_tweet = _tweet.replace( "＠" + mention.getScreenName(), "<u>＠" + mention.getScreenName() + "</u>" );
			}
			catch( NullPointerException ex ){}
		}
		
		// URL処理
		_urls = stat.getURLEntities();
		for( twitter4j.URLEntity url : _urls )
		{
			try
			{
				_tweet = _tweet.replace( url.getURL().toString(), "<u>" + url.getExpandedURL().toString() + "</u>" );
			}
			catch( NullPointerException ex )
			{
				_tweet = _tweet.replace( url.getURL().toString(), "<u>" + url.toString() + "</u>" );
			}
			//Log.d( "url debug", url.getURL().toString() );
		}
		
		// ハッシュタグ処理
		_hashtags = stat.getHashtagEntities();
		for( twitter4j.HashtagEntity hashtag : _hashtags )
		{
			try
			{
				_tweet = _tweet.replace( "#" + hashtag.getText(), "<u>#" + hashtag.getText() + "</u>" );
				_tweet = _tweet.replace( "＃" + hashtag.getText(), "<u>＃" + hashtag.getText() + "</u>" );
			}
			catch( NullPointerException ex ){}
			//Log.d( "hashtag debug", hashtag.getText() );
		}
		
		} // if( !_block )
	}
	
	public void setFav( boolean fav )
	{
		this._fav = fav;
	}
	
	public boolean isFav()
	{
		return _fav;
	}
	
	public boolean isRt()
	{
		return _rt;
	}
	
	public boolean isProtect()
	{
		return _protect;
	}
	
	public boolean isFollow()
	{
		return _follow;
	}
	
	public boolean isBlock()
	{
		return _block;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getScreenName()
	{
		return _sname;
	}
	
	public long getUserId()
	{
		return _userId;
	}
	
	public String getRetweetedName()
	{
		return _rname;
	}
	
	public String getRetweetedScreenName()
	{
		return _rsname;
	}
	
	public long getReplyToStatusId()
	{
		return _r2statusId;
	}
	
	public String getReplyToScreenName()
	{
		return _r2name;
	}
	
	public String getTweet()
	{
		return _tweet;
	}
	
	public String getTweetRaw()
	{
		return _tweetRaw;
	}
	
	public String getClient()
	{
		return _client;
	}
	
	public long getStatusId()
	{
		return _statusId;
	}
	
	public Date getDate()
	{
		return _date;
	}
	
	public twitter4j.UserMentionEntity[] getUserMentionEntities()
	{
		return _mentions;
	}
	
	public twitter4j.URLEntity[] getURLEntities()
	{
		return _urls;
	}
	
	public twitter4j.HashtagEntity[] getHashtagEntities()
	{
		return _hashtags;
	}
}
