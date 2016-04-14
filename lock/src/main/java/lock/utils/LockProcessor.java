package lock.utils;

import lock.biz.BizProcess;

/**
 * Created by tuhu on 16/4/6.
 */
public interface LockProcessor {

    /**
     * 同步获取当前路径的锁
     *
     * @param currentPath 根据当前路径获得同步锁
     * @return 返回对应的sequence路径
     * @throws Exception
     */
    String writeLock(String currentPath) throws Exception;


    /**
     * 异步获取当前路径的锁
     *
     * 异步获取路径锁以后，waiter处理回调业务，处理完成后会调用unlock操作，用户无需主动调用unlock,这一点与同步锁的使用方法不同，
     * 同步锁需要主动调用writeUnlock操作
     *
     * @param currentPath 根据当前路径获得锁资源
     * @throws Exception
     */
    void asyWriteLock(String currentPath) throws Exception;

    /**
     * 异步锁拿到锁以后，处理回调业务的实现入口
     *
     * @param currentPath
     * @throws Exception
     */
    void processLockCallback(String currentPath) throws Exception;



    void writeUnlock(String currentPath) throws Exception;

}
