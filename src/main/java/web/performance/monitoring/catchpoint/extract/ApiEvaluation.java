package web.performance.monitoring.catchpoint.extract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.json.JSONObject;

import web.performance.monitoring.catchpoint.entity.Result;

/*
 * reference:
 * https://cwiki.apache.org/confluence/display/OLTU/OAuth+2.0+Client+Quickstart
 */

public class ApiEvaluation {
	private static Logger logger = Logger.getLogger(ApiEvaluation.class);
	private static final ApiEvaluation instance = new ApiEvaluation();
	
	private String requestAccessTtokenUrl;
	private String resourceUrl;
	private String clientId;
	private String clientSecret;
	private String accessToken;
	private String responseBody;
	
	/**
	 * Private constructor
	 */
	private ApiEvaluation() {
	}
	
	/**
	 * @return		static instance 
	 */
	public static ApiEvaluation getInstance() {
		return instance;
	}

	/**
	 * @param args	
	 */
	public static void main(String[] args) {
		ApiEvaluation.getInstance().init();
		ApiEvaluation.getInstance().loadAccessToken();
		
		//ApiEvaluation.getInstance().getRawResult("tests");
		//ApiEvaluation.getInstance().getRawResult("performance/raw/204219?startTime=03-08-2017+00%3A00&endTime=03-08-2017+23%3A59");
		//ApiEvaluation.getInstance().getRawResult("performance/favoriteCharts");
		//ApiEvaluation.getInstance().getRawResult("performance/favoriteCharts/88927/data?startTime=03-08-2017+00%3A00&endTime=03-08-2017+23%3A59");
		//ApiEvaluation.getInstance().getRawResult("nodes");
		//ApiEvaluation.getInstance().getRawResult("glimpse/favoriteCharts");
		
		
		/*
		 * this is equivalnet to 
		 * https://portal.catchpoint.com/ui/Content/Charts/Performance.aspx?fav=88927
		 */
		ApiEvaluation.getInstance().getResult("performance/favoriteCharts/88927/data?startTime=03-08-2017+00%3A00&endTime=03-08-2017+23%3A59");
	}
	
	/**
	 * Initialization by loading properties file
	 */
	public void init() {
		try {
			Properties prop = new Properties();
			prop.load(ApiEvaluation.class.getClassLoader().getResourceAsStream("config.properties"));
			
			this.requestAccessTtokenUrl = prop.getProperty("request_access_token_url");
			this.resourceUrl = prop.getProperty("request_resource_url"); 	
			this.clientId = prop.getProperty("client_id");
			this.clientSecret = prop.getProperty("client_secret");	
			
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Load access_token by Apache Oltu library
	 */
	public void loadAccessToken() {
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			
			OAuthClientRequest request = OAuthClientRequest
                                            .tokenLocation(requestAccessTtokenUrl)
                                            .setGrantType(GrantType.CLIENT_CREDENTIALS)
                                            .setClientId(clientId)
                                            .setClientSecret(clientSecret)
                                            // .buildQueryMessage();
                                            .buildBodyMessage();
			request.setHeader("Accept", "*/*");
			

			accessToken = oAuthClient.accessToken(request, OAuth.HttpMethod.POST, OAuthJSONAccessTokenResponse.class).getAccessToken();
			// encode to base64
			accessToken = new String(Base64.encodeBase64(accessToken.getBytes()));
			logger.debug(accessToken);
			
		} catch (OAuthSystemException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (OAuthProblemException e) {
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
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			
			OAuthClientRequest request = new OAuthBearerClientRequest(resource)
											.setAccessToken(accessToken)											
											//.buildQueryMessage();
											.buildHeaderMessage();
											
			request.setHeader("Accept", "*/*");
			
			OAuthResourceResponse resourceResponse = oAuthClient.resource(request, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
			responseBody = resourceResponse.getBody();						
			
			JSONObject root = new JSONObject(responseBody);
			logger.debug(root.toString(4));
			
		} catch (OAuthSystemException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (OAuthProblemException e) {
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
