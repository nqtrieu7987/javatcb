package com.howtodoinjava.rest;

import java.io.IOException;
import java.security.Security;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.vnm.erp.cover.GlobalVariables;

@SpringBootApplication 
@EnableAsync
public class SpringBootDemoApplication  {
	@Autowired
	private static GitHubLookupService gitHubLookupService;
	@Bean("threadPoolTaskExecutor")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(1000);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("Async-");
		return executor;
	}
    public static void main(String[] args) {
    	Security.setProperty("crypto.policy", "unlimited");
    	 
		try {
			GlobalVariables.LoadAllParameter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		//GlobalVariables.pool = new ResourcePoolClient(10, true);
        SpringApplication.run(SpringBootDemoApplication.class, args);
        
    
	 
		

    }
   
}
