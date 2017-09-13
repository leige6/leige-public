package com.leige.lockTest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 张亚磊
 * @Description: zookeeperLock 分布式锁练习
 * @date 2017/9/13  10:09
 */
public class ZookeeperLockTest {

    /*
       ExponentialBackoffRetry(超时时间,重连次数)；
     */
    final static CuratorFramework client= CuratorFrameworkFactory.builder().connectString("192.168.1.193:2181").retryPolicy(new ExponentialBackoffRetry(100,1)).build();

    public static void main(String[] args) throws InterruptedException {
        client.start();
        ExecutorService executorService=Executors.newCachedThreadPool();
        final  CountDownLatch latch=new CountDownLatch(1);
        final InterProcessMutex lock=new InterProcessMutex(client,"/tl");
        for (int i = 0; i < 10; i++) {
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        latch.await();
                        lock.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+"订单号:"+getOrderId());
                    try {
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        latch.countDown();
        executorService.shutdown();
    }

    public static String getOrderId(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return  simpleDateFormat.format(new Date());
    }


}
