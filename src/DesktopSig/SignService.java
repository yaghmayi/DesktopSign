package DesktopSig;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.omg.CORBA.portable.ApplicationException;

/**
 * 
 * SignService is a class for calling server side services.
 */
public class SignService 
{
//	private String serevrUrl = "https://easysign.galactic.network/fake/";
	private String serevrUrl = "https://easysign.galactic.network/api/";
	
	/**
	 * Constructs a new initiate of SignService
	 */
	public SignService()
	{
		
	}
	
	/**
	 * Gets token for a specific user
	 * @param email : the user's email
	 * @param password : the user's password
	 * @return the token value as a string
	 */
	public String getToken(String email, String password)
	{
		JSONObject userPropertiesJs = new JSONObject();
		userPropertiesJs.put("email", email);
		userPropertiesJs.put("password", password);
		
		JSONObject userJs = new JSONObject();
		userJs.put("user", userPropertiesJs);
		
		String token = null;
		
		try
		{
			HttpClient client = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(serevrUrl +  "login");
			StringEntity params =new StringEntity(userJs.toJSONString());
			postRequest.addHeader("content-type",  "application/json");
			postRequest.setEntity(params);
			HttpResponse response = client.execute(postRequest);
			BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
			String answer = rd.readLine();
			
			JSONParser parser = new JSONParser();
			JSONObject answerJs = (JSONObject) parser.parse(answer);
			
			
			if (answerJs.get("token") != null)
				token = answerJs.get("token").toString();
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		
		return token;
	}
	
	/**
	 * Gets the user's signature
	 * @param token : the user's token
	 * @param pdfHashCode : the pdf's hashCode
	 * @return the signature vector
	 */
	public List<List<Point>> getSignature(String token, String pdfHashCode)
	{
		List<List<Point>> vector = new ArrayList<>();
		
		try
		{
			if (!hasSignRequest(token, pdfHashCode))
				requestForSignature(token, pdfHashCode);
			
			vector = waitAndGetSignature(token, pdfHashCode);
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		finally
		{
			return vector;
		}
	}

	private List<List<Point>> waitAndGetSignature(String token, String pdfHashCode)
			throws InterruptedException, IOException, ClientProtocolException, ParseException 
	{
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		List<List<Point>> vector = new ArrayList<>();
		HttpGet getRequest = new HttpGet(serevrUrl +  "request/" + pdfHashCode);
		getRequest.addHeader("content-type",  "application/json");
		getRequest.addHeader("token",  token);
		int t = 0;
		int statusCode;
		do 
		{
			System.out.println("call");
			t += 2;
			Thread.sleep(2000);
			response = client.execute(getRequest);
			statusCode = response.getStatusLine().getStatusCode();
		} 
		while (statusCode != 200 && t < 120);
		
		if (response.getStatusLine().getStatusCode() == 200)
		{
			BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
			String answer = rd.readLine();
			System.out.println(answer);
			
			JSONParser parser = new JSONParser();
			JSONArray signatuteJsArray = (JSONArray) parser.parse(answer);
			
			for (int i=0; i<signatuteJsArray.size(); i++)
			{
				List<Point> list = new ArrayList<>();
				JSONArray innerJsArray = (JSONArray) signatuteJsArray.get(i);
				
				for (int j=0; j< innerJsArray.size(); j++)
				{
					JSONObject pointJs = (JSONObject) innerJsArray.get(j);
					
					double x = Double.parseDouble(pointJs.get("x").toString());
					double y = Double.parseDouble(pointJs.get("y").toString());
					
					int px = (int) x;
					int py = (int) y;
					
					Point p = new Point(px,  py);
					list.add(p);
				}
				
				vector.add(list);
			}
		}
		
		return vector;
	}

	private void requestForSignature(String token, String pdfHashCode)
			throws UnsupportedEncodingException, IOException, ClientProtocolException 
	{
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		HttpPost postRequest = new HttpPost(serevrUrl +  "request");
		JSONObject pdfHashCodeJs = new JSONObject();
		pdfHashCodeJs.put("id", pdfHashCode);
		
		JSONObject documentJs = new JSONObject();
		documentJs.put("document", pdfHashCodeJs);
		StringEntity params =new StringEntity(documentJs.toJSONString());
		postRequest.addHeader("content-type",  "application/json");
		postRequest.addHeader("token",  token);
		postRequest.setEntity(params);
		response = client.execute(postRequest);
	}

	private boolean hasSignRequest(String token, String pdfHashCode)
			throws IOException, ClientProtocolException, ParseException 
	{
		boolean hasRequest = false;
		HttpGet getRequest = new HttpGet(serevrUrl +  "request/");
		getRequest.addHeader("content-type",  "application/json");
		getRequest.addHeader("token",  token);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(getRequest);
		if (response.getStatusLine().getStatusCode() == 200)
		{
			BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
			String answer = rd.readLine();
			
			JSONParser parser = new JSONParser();
			JSONObject answerJs = (JSONObject) parser.parse(answer);
			
			JSONArray requestsJsArray = (JSONArray) answerJs.get("signature");
			for (int i=0; i<requestsJsArray.size(); i++)
			{
				JSONObject idObject = (JSONObject) requestsJsArray.get(i);
				if (idObject.get("id").toString().equals(pdfHashCode))
				{
					hasRequest = true;
					break;
				}
			}
		}
		
		return hasRequest;
	}

}
