package com.vnm.erp.cover;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable{

private BlockingQueue<Message> queue;
    int thredid;
    public Consumer(BlockingQueue<Message> q,int thredid){
        this.queue=q;
        this.thredid=thredid;
    }

    @Override
    public void run() {
        try{
            Message msg;
            //consuming messages until exit message is received
           while(GlobalVariables.boolean1.get()){
            Thread.sleep(100);
            msg = queue.take();
            if(msg.getMsg().equals("done"))GlobalVariables.boolean1.set(false);
            System.out.println("Consumed "+thredid+"--"+msg.getMsg());
            }
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}