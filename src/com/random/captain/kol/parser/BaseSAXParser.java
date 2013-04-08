package com.random.captain.kol.parser;

import android.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public abstract class BaseSAXParser<T> extends DefaultHandler
{
	private StringBuilder content;
	protected T bean;
	
	public T parseToBean(String xml)
	{
		return parseToBean(new InputSource(new StringReader(xml)));
	}
	
	public T parseToBean(InputStream xml)
	{
		return parseToBean(new InputSource(xml));
	}
	
	public T parseToBean(InputSource xml)
	{
		try
		{
			Log.i("kol","Parsing to bean!");
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			reader.setContentHandler(this);
			Log.i("kol","Start to parse");
			reader.parse(xml);
			return bean;
		}
		catch(Exception e)
		{
			Log.i("kol","Parse to bean oops. "+e.getLocalizedMessage());
			return null;
		}
	}
	
	@Override
	public final void startElement(String uri, String localName, String qName, Attributes attributes)
	{
		content = new StringBuilder(50);
		startElement(localName, attributes);
	}
	
	public abstract void startElement(String tagName, Attributes attributes);

	@Override
	public final void endElement(String uri, String localName, String qName)
	{
		endElement(content.toString(), localName);
	}
	
	public abstract void endElement(String content, String tagName);
	
	@Override
	public final void characters(char[] ch, int start, int length)
	{
		content.append(ch, start, length);
	}
}
