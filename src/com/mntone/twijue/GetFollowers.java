package com.mntone.twijue;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class GetFollowers
{
	private long[] _follower;
	public GetFollowers()
	{
		Configuration conf = new ConfigurationBuilder()
			.setOAuthAccessToken( Const.MAIN_AOUTH_KEY_TOKEN )
			.setOAuthAccessTokenSecret( Const.MAIN_AOUTH_KEY_SECRET )
			.build()
		;
		Twitter twitter  = new TwitterFactory( conf ).getInstance();
		try
		{
			_follower = twitter.getFollowersIDs( -1 ).getIDs();
		}
		catch( TwitterException ex ){}
	}
	
	public long[] getList()
	{
		return _follower;
	}
}
