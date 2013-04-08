package com.random.captain.kol.helper;

import android.os.*;
import android.util.*;
import java.io.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

class AsyncRequestBean
{
	private String url;
	private ServerRequest.RequestType requestType;
	private List<NameValuePair> params;
	private ServerRequest.RequestFinishedHandler handler;

	public AsyncRequestBean(String urlParam, ServerRequest.RequestType requestTypeParam, List<NameValuePair> paramsParam, ServerRequest.RequestFinishedHandler handlerParam)
	{
		url = urlParam;
		requestType = requestTypeParam;
		params = paramsParam;
		handler = handlerParam;
	}

	public String getURL(){return url;}
	public ServerRequest.RequestType getRequestType(){return requestType;}
	public List<NameValuePair> getParams(){return params;}
	public ServerRequest.RequestFinishedHandler getRequestFinishedHandler(){return handler;}
}

class AsyncRequest extends AsyncTask<AsyncRequestBean, Void, String>
{
	ServerRequest.RequestFinishedHandler handler;
	
	protected String doInBackground(AsyncRequestBean... beans)
	{
		try
		{
			Log.i("kol","starting request");
			AsyncRequestBean bean = beans[0];
			String url = bean.getURL();
			ServerRequest.RequestType requestType = bean.getRequestType();
			List<NameValuePair> params = bean.getParams();
			handler = bean.getRequestFinishedHandler();
			
			HttpRequestBase request;
			if(requestType == ServerRequest.RequestType.POST)
			{
				Log.i("kol","POST request");
				request = new HttpPost(url);

				if(params != null)
				{((HttpPost)request).setEntity(new UrlEncodedFormEntity(params));Log.i("kol","With params");}
			}
			else if(requestType == ServerRequest.RequestType.GET)
			{request = new HttpGet(url);Log.i("kol","GET request");}
			else
			{handler.requestFailed("Unknown RequestType; was not POST or GET");return null;}
			
			Log.i("kol","Request starting");
			HttpResponse respo = ServerRequest.getClient().execute(request);
			Log.i("kol","Request finished");
			if(respo != null)
			{
				Log.i("kol","Got response");
				String response = ServerRequest.stringFromInputStream(respo.getEntity().getContent());
				if(response != null)
				{return response;}
				else
				{handler.requestFailed("Response content was null. or unreadable");}
			}
			else
			{
				Log.i("kol", "No response...");
				handler.requestFailed("Response not received");
				return null;
			}
		}
		catch (IOException e)
		{
			Log.i("kol","Couldn't do first login request: "+e.getLocalizedMessage());
			return null;
		}
		
		return null;
	}

	protected void onPostExecute(String result)
	{
		if(handler != null && result != null)
		{handler.requestFinished(result);}
		else
		{
			handler.requestFailed("Boop.");
		}
	}
}
	
public class ServerRequest
{
	private static DefaultHttpClient httpClient = new DefaultHttpClient();
	
	public static abstract class RequestFinishedHandler
	{
		public abstract void requestFinished(String response);
		public abstract void requestFailed(String reason);
	}
	
	public enum RequestType
	{
		GET, POST;
	}
	
	public static DefaultHttpClient getClient()
	{
		return httpClient;
	}
	
	public static void makeRequest(String resourceName, RequestType requestType, RequestFinishedHandler handler)
	{
		makeRequest(resourceName, requestType, null, handler);
	}
	public static void makeRequest(String resourceName, RequestType requestType, List<NameValuePair> params, RequestFinishedHandler handler)
	{
		AsyncRequestBean bean = new AsyncRequestBean("http://www.kingdomofloathing.com/"+resourceName, requestType, params, handler);
		new AsyncRequest().execute(bean);
	}
	
	public static String stringFromInputStream(InputStream is)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		
		try
		{
			while((line = reader.readLine()) != null)
			{
				sb.append(line);
			}
			
			is.close();
			return sb.toString();
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
