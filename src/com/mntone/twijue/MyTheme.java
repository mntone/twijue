package com.mntone.twijue;

public class MyTheme
{
	private String theme    = "";
	private String fontSize = "";
	private String option   = "";
	
	public MyTheme( String theme, String fontSize, String option )
	{
		this.theme = theme;
		this.fontSize = fontSize;
		this.option = option;
	}
	
	public int getTheme()
	{
		if( "White".equals( theme ) )
		{
			if( "Large".equals( fontSize ) )
			{
				if( "NoTitleBar".equals( option ) )
					return R.style.White_Large_NoTitleBar;
				
				else if( "HalfTransparent".equals( option ) )
					return R.style.White_Large_HalfTransparent;
				
				else if( "Transparent".equals( option ) )
					return R.style.White_Large_Transparent;
				
				return R.style.White_Large;
			}
			else if( "Middle".equals( fontSize ) )
			{
				if( "NoTitleBar".equals( option ) )
					return R.style.White_Middle_NoTitleBar;

				else if( "HalfTransparent".equals( option ) )
					return R.style.White_Middle_HalfTransparent;

				else if( "Transparent".equals( option ) )
					return R.style.White_Middle_Transparent;
				
				return R.style.White_Middle;
			}
			else if( "Small".equals( fontSize ) )
			{
				if( "NoTitleBar".equals( option ) )
					return R.style.White_Small_NoTitleBar;

				else if( "HalfTransparent".equals( option ) )
					return R.style.White_Small_HalfTransparent;

				else if( "Transparent".equals( option ) )
					return R.style.White_Small_Transparent;
				
				return R.style.White_Small;
			}
			
			return R.style.White;
		}
		
		if( "Large".equals( fontSize ) )
		{
			if( "NoTitleBar".equals( option ) )
				return R.style.Black_Large_NoTitleBar;

			else if( "HalfTransparent".equals( option ) )
				return R.style.Black_Large_HalfTransparent;

			else if( "Transparent".equals( option ) )
				return R.style.Black_Large_Transparent;
			
			return R.style.Black_Large;
		}
		else if( "Middle".equals( fontSize ) )
		{
			if( "NoTitleBar".equals( option ) )
				return R.style.Black_Middle_NoTitleBar;

			else if( "HalfTransparent".equals( option ) )
				return R.style.Black_Middle_HalfTransparent;

			else if( "Transparent".equals( option ) )
				return R.style.Black_Middle_Transparent;
			
			return R.style.Black_Middle;
		}
		else if( "Small".equals( fontSize ) )
		{
			if( "NoTitleBar".equals( option ) )
				return R.style.Black_Small_NoTitleBar;

			else if( "HalfTransparent".equals( option ) )
				return R.style.Black_Small_HalfTransparent;

			else if( "Transparent".equals( option ) )
				return R.style.Black_Small_Transparent;
			
			return R.style.Black_Small;
		}
		
		return R.style.Black;
	}
	
}
