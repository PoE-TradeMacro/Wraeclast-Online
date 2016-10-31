/*
 *     This file is part of wraelclast-online.
 *
 *     wraelclast-online is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     wraelclast-online is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with wraelclast-online.  If not, see <http://www.gnu.org/licenses/>.
 */

package wo.trade;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

@Log4j2
public class BackendClient {
	
    private HttpClient client = HttpClientBuilder.create().build();
    
    int timeout = 15;
	int CONNECTION_TIMEOUT = timeout  * 1000; // timeout in millis
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(CONNECTION_TIMEOUT)
        .setConnectTimeout(CONNECTION_TIMEOUT)
        .setSocketTimeout(CONNECTION_TIMEOUT)
        .build();
    
    public final String userAgent;

    public BackendClient() {
    	userAgent = userAgents[RandomUtils.nextInt(0, userAgents.length)];
	}
    public String post(String payload)
            throws Exception {
    	return post("http://poe.trade/search", payload);
    }
    
    public String post(String url, String payload)
    			throws Exception {
    	log.info("post() payload: " + payload);
    	log.info("post() url: " + url);
    	
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);

        // add header
        post.setHeader("Host", "poe.trade");
        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.setHeader("Accept-Language", "en-US,en;q=0.5");
        post.setHeader("Accept-Encoding", "gzip, deflate");
        post.setHeader("Referer", "http://poe.trade/");
    	post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.setHeader("Connection", "keep-alive");

        post.setEntity(new StringEntity(payload));

        log.info("Sending 'POST' request to URL : " + url);
        // bombs away!
        HttpResponse response = postWithRetry(post);

        int responseCode = response.getStatusLine().getStatusCode();

        log.info("Response Code : " + responseCode);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        
        rd.close();
        
        String location = null;
        
        final Header[] allHeaders = response.getAllHeaders();
        for (Header header : allHeaders) {
            if (header.getName().equalsIgnoreCase("Location")) {
                location = header.getValue();
            }
        }
        
//        boolean captchaDetected = containsCaptchaKeyword(result.toString());
//        if (captchaDetected) {
//			throw new CaptchaDetectedException(location);
//		}
        
        return location;
    }
//	private boolean containsCaptchaKeyword(String line) {
//		return line.contains("recaptcha/api.js");
//	}
    
    public String postXMLHttpRequest(String url, String payload)
    		throws Exception {
    	log.info("postXMLHttpRequest() payload: " + payload);
    	log.info("postXMLHttpRequest() url: " + url);
    	StringEntity entity = new StringEntity(payload);
    	
    	HttpPost post = new HttpPost(url);
    	post.setConfig(requestConfig);
    	
    	// add header
    	post.setHeader("Accept", "*/*");
    	post.setHeader("Accept-Encoding", "gzip, deflate");
    	post.setHeader("Accept-Language", "en-US,en;q=0.5");
    	post.setHeader("Cache-Control", "no-cache");
    	post.setHeader("Connection", "keep-alive");
    	post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    	post.setHeader("Host", "poe.trade");
    	post.setHeader("Pragma", "no-cache");
    	post.setHeader("Referer", url);
    	post.setHeader("User-Agent", USER_AGENT);
    	post.setHeader("X-Requested-With", "XMLHttpRequest");
    	
		post.setEntity(entity);
    	
    	log.info("Sending 'POST' request to URL : " + url);
    	// bombs away!
    	HttpResponse response = postWithRetry(post);
    	
    	int responseCode = response.getStatusLine().getStatusCode();
    	
    	log.info("Response Code : " + responseCode);
    	
    	BufferedReader rd = new BufferedReader(
    			new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
    	
    	StringBuilder result = new StringBuilder();
    	String line = "";
    	while ((line = rd.readLine()) != null) {
    		result.append(line);
    	}
    	
    	rd.close();
    	
//    	String location = null;
    	
//    	final Header[] allHeaders = response.getAllHeaders();
//    	for (Header header : allHeaders) {
//    		if (header.getName().equalsIgnoreCase("Location")) {
//    			location = header.getValue();
//    		}
//    	}
    	
//    	boolean captchaDetected = containsCaptchaKeyword(result.toString());
//    	if (captchaDetected) {
//    		throw new CaptchaDetectedException(url);
//    	}
    	
    	return result.toString();
    }
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0";

    public String get(String url) throws Exception {

        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig);
        
        get.setHeader("Host", "poe.trade");
        get.setHeader("User-Agent", USER_AGENT);
        get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        get.setHeader("Accept-Language", "en-US,en;q=0.5");
        get.setHeader("Accept-Encoding", "gzip, deflate");
        get.setHeader("Referer", "http://poe.trade/");
        get.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        
        HttpResponse response = client.execute(get);
        int responseCode = response.getStatusLine().getStatusCode();

        log.info("Sending 'GET' request to URL : " + url);
        log.info("Response Code : " + responseCode);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        
        rd.close();

        return result.toString();
    }

    public static final String userAgents[] = new String[] {
    		"Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13",
    	    "Mozilla/5.0 (Windows; U; Windows NT 6.0; de) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13",
    	    "Mozilla/5.0 (Windows; U; Windows NT 5.2; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13",
    	    "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.13(KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13",
    	    "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13",
    	    "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13",
    	    "Mozilla/5.0 (Linux; U; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13",
    		
    		"Mozilla/5.0 (Macintosh; U; Mac OS X 10_6_1; en-US) AppleWebKit/530.5 (KHTML, like Gecko) Chrome/ Safari/530.5",
    	    "Mozilla/5.0 (Macintosh; U; Mac OS X 10_5_7; en-US) AppleWebKit/530.5 (KHTML, like Gecko) Chrome/ Safari/530.5",
    	    "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_6; en-US) AppleWebKit/530.9 (KHTML, like Gecko) Chrome/ Safari/530.9",
    	    "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_6; en-US) AppleWebKit/530.6 (KHTML, like Gecko) Chrome/ Safari/530.6",
    	    "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_6; en-US) AppleWebKit/530.5 (KHTML, like Gecko) Chrome/ Safari/530.5",
    		
    	    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1",
    	    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0",
    	    "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0",
    	    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0",
    	    "Mozilla/5.0 (X11; Linux i586; rv:31.0) Gecko/20100101 Firefox/31.0",
    	    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20130401 Firefox/31.0",
    	    "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0",
    	    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20120101 Firefox/29.0",
    	    "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/29.0",
    	    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0"
    };
    
	private HttpResponse postWithRetry(HttpPost post) throws IOException, ClientProtocolException {
		int count = 0;
		int maxTries = 10;
		while(true) {
		    try {
		    	return client.execute(post);
		    } catch (Exception e) {
		    	++count;
		    	log.info(format("Http post failed, gonna try again. Number of retries so far: %d", count));
		        if (count == maxTries) {
		        	String msg = format("Http post failed after %d max retries.", maxTries);
					log.info(msg);
		        	throw new RuntimeException(msg, e);
		        }
		    }
		}
		
	}

}