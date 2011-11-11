package com.mntone.twijue;

import java.util.regex.Pattern;

public class Const
{
	// 全般
	static final String BACK_UP_LIST = "BACK_UP_LIST";
	static final int RETWEET = 1;
	static final int FAVORITE = 2;
	static final int UNFAVORITE = 3;
	static final int DELETE = 4;
	
	// グローバル変数
	static String MAIN_MY_SCREEN_NAME	= "";
	static String MAIN_AOUTH_KEY_SECRET	= "";
	static String MAIN_AOUTH_KEY_TOKEN	= "";
	static String SUB_MY_SCREEN_NAME	= "";
	static String SUB_AOUTH_KEY_SECRET	= "";
	static String SUB_AOUTH_KEY_TOKEN	= "";
	static boolean ENTER_POST			= true;
	static String QUOTE_TWEET			= "unofficialRetweet";
	static String THEME					= "Black";
	static String FONT_SIZE				= "Middle";
	static String ICON_SIZE				= "Middle";
	static boolean SKELETON_POST_SCREEN = false;
	static boolean BACK_LIGHT			= true;
	static String ORIENTATION			= "default";
	
	static long[] FOLLOWER_LIST = { 0 };
	static long[] BLOCK_LIST = { 0 };

	// Preference
	static String PREFERENCE_NAME = "TWITTERforTWIJUE";
	static final String PREF_MAIN_MY_SCREEN_NAME	= "PREF_MAIN_MY_SCREEN_NAME";
	static final String PREF_MAIN_AOUTH_KEY_SECRET	= "PREF_MAIN_AOUTH_KEY_SECRET";
	static final String PREF_MAIN_AOUTH_KEY_TOKEN	= "PREF_MAIN_AOUTH_KEY_TOKEN";
	static final String PREF_SUB_MY_SCREEN_NAME		= "PREF_SUB_MY_SCREEN_NAME";
	static final String PREF_SUB_AOUTH_KEY_SECRET	= "PREF_SUB_AOUTH_KEY_SECRET";
	static final String PREF_SUB_AOUTH_KEY_TOKEN	= "PREF_SUB_AOUTH_KEY_TOKEN";
	static final String PREF_ENTER_POST				= "PREF_ENTER_POST";
	static final String PREF_QUOTE_TWEET			= "PREF_QUOTE_TWEET";
	static final String PREF_THEME					= "PREF_THEME";
	static final String PREF_FONT_SIZE				= "PREF_FONT_SIZE";
	static final String PREF_ICON_SIZE				= "PREF_ICON_SIZE";
	static final String PREF_SKELETON_POST_SCREEN	= "PREF_SKELETON_POST_SCREEN";
	static final String PREF_BACK_LIGHT				= "PREF_BACKLIGHT";
	static final String PREF_ORIENTATION			= "PREF_ORIENTATION";
	
	// ツイート正規表現
	static final Pattern ID_MATCH_PATTERN		= Pattern.compile( "(?:@|＠)([A-Za-z0-9_]+?)( |　|:|$)" );
	//static final Pattern URL_MATCH_PATTERN		= Pattern.compile( "(https?://)(.+?)( |　|$)", Pattern.CASE_INSENSITIVE );
	static final Pattern NICO_LIVE_PATTERN		= Pattern.compile( "http://live.nicovideo.jp/(?:watch|gate)/(lv|co|ch)([0-9]+?)" );
	static final Pattern NICO_CRUISE_PATTERN	= Pattern.compile( "http://live.nicovideo.jp/(?:watch|gate)/cruise" );
	static final Pattern NICO_VIDEO_PATTERN		= Pattern.compile( "http://(?:www.nicovideo.jp/watch|co.nicovideo.jp/community|com.nicovideo.jp/community|ch.nicovideo.jp/channel|nicovideo.jp/watch|seiga.nicovideo.jp/bbs|www.niconicommons.jp/material|niconicommons.jp/material|news.nicovideo.jp/watch)/([a-z0-9]+?)" );
	
	// データベース関連 cache
	static final String CACHE_TABLE_NAME	= "cache_table_name";
	static final String CACHE_SCREEN_NAME	= "cache_screen_name";
	static final String CACHE_PROFILE_IMAGE	= "cache_profile_image";
	static final String CACHE_TIME			= "cache_time";
	
	// intentId 0: oAuth 関連
	static final int INTENT_OAUTH_CODE = 0;
	//static final String CONSUMER_KEY = "nEMKImKIeXyJR57vxVd5xw";
	//static final String CONSUMER_SECRET = "6mRmZr2IZS4STszBO7GcIRoPPhyHSMYRJlxAowWj3cE";
	static final String CALLBACK_URL = "myapp://oauth";
	static final String IEXTRA_AUTH_URL = "auth_url";
	static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
	static final String IEXTRA_OAUTH_TOKEN = "oauth_token";
	
	// intentId 1: ツイート関連
	static final int INTENT_POST_CODE = 1;
	static final String TWEET_MSG		= "TWEET_MSG";
	static final String TWEET_MSG_POS	= "TWEET_MSG_POS";
	static final String IN_REPLY_TO		= "IN_REPLY_TO";
	static final String STATUS_ID		= "STATUS_ID";
	
	// intentId 2: setttings 関連
	static final int INTENT_SETTINGS_CODE = 2;
}