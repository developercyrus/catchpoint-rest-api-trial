package web.performance.monitoring.catchpoint.extract;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import web.performance.monitoring.catchpoint.entity.Result;


/*
 * reference:
 * http://blog.catchpoint.com/2015/06/30/api-google-docs/
 */
public class ApiEvaluationByHttpClient {
	private static Logger logger = Logger.getLogger(ApiEvaluationByHttpClient.class);
	private static final ApiEvaluationByHttpClient instance = new ApiEvaluationByHttpClient();
	
	private String requestAccessTtokenUrl;
	private String resourceUrl;
	private String grant_type;
	private String clientId;
	private String clientSecret;
	private String accessToken;
	private String responseBody;
	
	
	/**
	 * Private constructor
	 */
	private ApiEvaluationByHttpClient() {
	}
	
	/**
	 * @return		static instance 
	 */
	public static ApiEvaluationByHttpClient getInstance() {
		return instance;
	}
	
	public static void main(String[] args) {
		ApiEvaluationByHttpClient.getInstance().init();
		ApiEvaluationByHttpClient.getInstance().loadAccessToken();
		
		/*
		 * this is equivalnet to 
		 * https://portal.catchpoint.com/ui/Content/Charts/Performance.aspx?fav=88927
		 */
		ApiEvaluationByHttpClient.getInstance().getResult("performance/favoriteCharts/88927/data?startTime=03-08-2017+00%3A00&endTime=03-08-2017+23%3A59");
	}

	
	/**
	 * Initialization by loading properties file
	 */
	public void init() {
		try {
			Properties prop = new Properties();
			prop.load(ApiEvaluation.class.getClassLoader().getResourceAsStream("config.properties"));
			
			this.requestAccessTtokenUrl = prop.getProperty("request_access_token_url");
			this.grant_type = prop.getProperty("grant_type");
			this.resourceUrl = prop.getProperty("request_resource_url"); 	
			this.clientId = prop.getProperty("client_id");
			this.clientSecret = prop.getProperty("client_secret");	
			
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}		
	}
	
	/**
	 * Load access_token by http client
	 */
	public void loadAccessToken() {
		
        try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost postRequest = new HttpPost(requestAccessTtokenUrl);

			postRequest.addHeader(HttpHeaders.ACCEPT, "*/*");

			List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
			data.add(new BasicNameValuePair("client_id", clientId));
			data.add(new BasicNameValuePair("client_secret", clientSecret));
			data.add(new BasicNameValuePair("grant_type", grant_type));
			postRequest.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpClient.execute(postRequest, responseHandler);

			JSONObject obj = new JSONObject(responseBody);
			accessToken = obj.getString("access_token");
			logger.debug(accessToken);
			accessToken = new String(Base64.encodeBase64(accessToken.getBytes()));
			logger.debug(accessToken);
			
			
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		} 
	}
	
	/**
	 * @param resource	A REST resource path
	 * @return			Unindented json response
	 */
	public String getRawResult(String resource) {
		resource = resourceUrl + "/" + resource;
		logger.debug(resource);
		
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();	        
			
			HttpGet getRequest = new HttpGet(resource);
			getRequest.addHeader(HttpHeaders.ACCEPT, "*/*");	
			getRequest.addHeader("Authorization", "Bearer " + accessToken);			
	        		
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpClient.execute(getRequest, responseHandler);
			
			JSONObject root = new JSONObject(responseBody);
			logger.debug(root.toString(4));
	        
		} catch (ClientProtocolException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}
		
		return responseBody;
	}
	
	
	/**
	 * @param resource	A REST resource path
	 * @return			A list of Result entity
	 */
	public List<Result> getResult(String resource) {
		this.getRawResult(resource);
		
		JSONObject root = new JSONObject(responseBody);
		Iterator<Object> items = root.getJSONObject("summary").getJSONArray("items").iterator();

		List<Result> results = new ArrayList<Result>();
		while(items.hasNext()) {
			JSONObject item =  ((JSONObject)items.next());
			String country = item.getJSONObject("breakdown_1").getString("name");
			String city = item.getJSONObject("breakdown_2").getString("name");
			
			Iterator<Object> metrics = item.getJSONArray("synthetic_metrics").iterator();
			double responseTime = (double) metrics.next();
			double availability = (double) metrics.next();
			
			Result result = new Result(country, city, availability, responseTime);
			logger.debug(result);
			results.add(result);
		}
		
		return results;
	}
	
}
