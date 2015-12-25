package com.rick.archi.soa.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public abstract class AbstractZookeeper implements Watcher {
		
	//缓存时间  
    private static final int SESSION_TIME = 2000;     
    protected ZooKeeper zooKeeper;  
    protected CountDownLatch countDownLatch = new CountDownLatch(1);  
 
    public void connect(String hosts) throws IOException, InterruptedException{     
           zooKeeper = new ZooKeeper(hosts, SESSION_TIME, this);     
           countDownLatch.await();     
     }     

	@Override
	public void process(WatchedEvent event) {
		if(event.getState() == KeeperState.SyncConnected){  
            countDownLatch.countDown();  
        }  
	}
	
	public void close() throws InterruptedException{     
        zooKeeper.close();     
    } 

}
