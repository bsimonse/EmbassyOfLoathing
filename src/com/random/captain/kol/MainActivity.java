package com.random.captain.kol;

import android.app.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import com.random.captain.kol.helper.*;
import com.random.captain.kol.parser.login.*;
import java.io.*;
import java.security.*;
import java.util.*;
import org.apache.commons.codec.binary.*;
import org.apache.http.*;
import org.apache.http.message.*;
import org.w3c.tidy.*;public class MainActivity extends Activity
{
	private TextView mainText;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		mainText = (TextView)findViewById(R.id.mainText);
    }
	
	@Override
	public void onResume()
	{
		super.onResume();
		getChallenge();
		toast("Logging in...");
	}
	
	private void toast(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	private void getChallenge()
	{
		ServerRequest.makeRequest("login.php", ServerRequest.RequestType.POST, new ServerRequest.RequestFinishedHandler()
		{
			@Override
			public void requestFinished(String response)
			{
				try
				{
					LoginParser parser = new LoginParser();
					Tidy tidy = new Tidy();
					tidy.setInputEncoding("utf8");
					tidy.setOutputEncoding("utf8");
					//tidy.setXHTML(true);
					//tidy.setPrintBodyOnly(true);
					String stuff = "<main><dude>Hi</dude></main>";
					
					ByteArrayOutputStream xhtmlStream = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(xhtmlStream);
					StringReader htmlReader = new StringReader(stuff);
					Log.i("kol","Going to tidyparse");
					tidy.parse(htmlReader, ps);
					String xhtml = new String(xhtmlStream.toByteArray(), "UTF8");
					//String xhtml = b.toString();
					Log.i("kol","D: "+xhtml);
					String result = parser.parseToBean(xhtml);
					Log.i("kol","Parser result: "+result);
					//secureLogin("Bingo the Wallaby","a1s2d3f4kol", "al");
				}
				catch(Exception e)
				{
					Log.i("kol","Parser oops.");
				}
			}
			
			@Override
			public void requestFailed(String reason)
			{
				
			}
		}
		);
	}
	
	private void secureLogin(String username, String password, String challenge)
	{
		//String challenge = "b726805548e54c62911a402dcec6f105";
		//String challenge = "e1bf7d1991c24e5094e5043b3c1c9eca";
		String hashword = "";
		
		try
		{
			MessageDigest md5er = MessageDigest.getInstance("MD5");
			
			md5er.update(password.getBytes());
			hashword = new String(Hex.encodeHex(md5er.digest()));
			hashword += ":"+challenge;
			md5er.update(hashword.getBytes());
			hashword = new String(Hex.encodeHex(md5er.digest()));
		}
		catch (NoSuchAlgorithmException e)
		{Log.i("kol","Couldn't log in securely.  Not defaulting to insecure.");return;}

		List<NameValuePair> params = new ArrayList<NameValuePair>(5);
		params.add(new BasicNameValuePair("loggingin","Yup."));
		params.add(new BasicNameValuePair("loginname",username));
		params.add(new BasicNameValuePair("password",""));
		params.add(new BasicNameValuePair("secure", "1"));
		params.add(new BasicNameValuePair("challenge",challenge));
		params.add(new BasicNameValuePair("response",hashword));

		ServerRequest.makeRequest("login.php", ServerRequest.RequestType.POST, params, new ServerRequest.RequestFinishedHandler()
			{
				@Override
				public void requestFinished(String response)
				{
					toast("Logged in?");
					//getApi();
					mainText.setText(response);
				}

				@Override
				public void requestFailed(String reason)
				{
					toast("Logon failed.");
				}
			}
		);
	}
	
	private void getApi()
	{
		ServerRequest.makeRequest("api.php?what=status&for=testing+the+api",ServerRequest.RequestType.GET, new ServerRequest.RequestFinishedHandler(){
				@Override
				public void requestFinished(String response)
				{
					toast("Yes!");
					mainText.setText(response);
				}

				@Override
				public void requestFailed(String reason)
				{
					toast("Not actually logged in.");
				}
		}
		);
	}
}
