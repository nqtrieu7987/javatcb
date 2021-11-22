package com.vnm.erp.cover;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: dbtsai
 * Date: 2/24/13
 * Time: 1:21 PM
 */

public class ResourcePoolClient extends ResourcePool<ClientOB> {

	public ResourcePoolClient(int size, Boolean dynamicCreation) {
        super(size, dynamicCreation);
        createPool();
    }

    @Override
    protected ClientOB createObject() {
        return new ClientOB();
    }

    public void returnPoll(ClientOB client) throws Exception {
    	 recycle(client);
    }
   
}
 