package com.random.captain.kol.parser.login;

import android.util.*;
import com.random.captain.kol.parser.*;
import org.xml.sax.*;

public class LoginParser extends BaseSAXParser<String>
{
	@Override
	public void startDocument()
	{
		bean = "I did it!";
	}
	
	@Override
	public void startElement(String tagName, Attributes attributes)
	{
		Log.i("kol","ParseStart: "+tagName);
	}
	
	@Override
	public void endElement(String content, String tagName)
	{
		
	}
}
