package com.howtodoinjava.rest;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vnm.erp.cover.GlobalVariables;

@Service
public class GitHubLookupService {

	private static final Logger logger = LoggerFactory
			.getLogger(GitHubLookupService.class);

	private final RestTemplate restTemplate;

	public GitHubLookupService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}
	@Async("threadPoolTaskExecutor")
	public CompletableFuture<String> findUser(String body )
			throws InterruptedException { 
		Thread.sleep(5000);
		System.out.println("done xxxxx");
        return CompletableFuture.completedFuture("");

	}
	 
	@Async("threadPoolTaskExecutor")
	public void findUser(String body, String url, Map<String, String> header)
			throws InterruptedException {

		StringEntity xmlEntity;
		HttpResponse response;
		String result = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		HttpPost post = new HttpPost(url);
		String SOAPAction="";
        for (Map.Entry<String,String> entry : header.entrySet())  
        {
        	 System.out.println(entry.getKey()+"--"+entry.getValue());
        	if(entry.getKey().equals("soapaction"))
        	{SOAPAction=entry.getValue();}
        } 
        System.out.print(SOAPAction);
		post.setHeader("Content-Type", "text/xml;charset=UTF-8");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("SOAPAction", SOAPAction);
		try {
			 
			xmlEntity = new StringEntity(body);
			GlobalVariables.logger.info(body);
			post.setEntity(xmlEntity);
			response = httpClient.execute(post);
			result = EntityUtils.toString(response.getEntity());
			System.out.print(result);
			post.releaseConnection();

		} catch (Exception ex) {
			GlobalVariables.logger.error(body );
			ex.printStackTrace();
		}
	}
}