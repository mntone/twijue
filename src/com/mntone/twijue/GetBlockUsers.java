package com.mntone.twijue;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class GetBlockUsers
{
	private long[] _blocks;
	public GetBlockUsers()
	{
		Configuration conf = new ConfigurationBuilder()
			.setOAuthAccessToken( Const.MAIN_AOUTH_KEY_TOKEN )
			.setOAuthAccessTokenSecret( Const.MAIN_AOUTH_KEY_SECRET )
			.build()
		;
		Twitter twitter  = new TwitterFactory( conf ).getInstance();
		try
		{
			_blocks = twitter.getBlockingUsersIDs().getIDs();
		}
		catch( TwitterException ex ){}
	}
	
	public long[] getList()
	{
		return _blocks;
	}
}
