package lock.utils;

import lock.biz.BizProcess;
import lock.biz.DiskService;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.common.PathUtils;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by tuhu on 16/4/6.
 */
public class ZkLockProcessor implements LockProcessor{

    static final String LOCK_PATH = "/distribution_lock";

    static final String WRITE_LOCK_PREFIX = "write-";

    static final String MUTEX_LOCK_KEY = "mutex";

    static Logger LOG = LoggerFactory.getLogger(ZkLockWatcher.class);

    private ZooKeeper zk;

    private BizProcess bizProcessor;


    public ZkLockProcessor(ZooKeeper zk, BizProcess bizProcess)
    {
        this.zk = zk;
        this.bizProcessor = bizProcess;
    }

    public String writeLock(String currentPath) throws Exception {

        PathUtils.validatePath(currentPath);

        acquireMutexLock(currentPath);

        String lock_full_path = new StringBuilder(LOCK_PATH).append("/").append(WRITE_LOCK_PREFIX).toString();
        try {

            byte[] ts = String.valueOf(System.currentTimeMillis()).getBytes();
            String path = this.zk.create(lock_full_path, ts, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);

            return path;
        }
        finally {
            releaseMutexLock(currentPath);
        }
    }

    public void asyWriteLock(String currentPath) throws Exception {


        PathUtils.validatePath(currentPath);

        acquireMutexLock(currentPath);
        String createPath = null;

        try {
            String fullPath = new StringBuilder(LOCK_PATH).append("/").append(WRITE_LOCK_PREFIX).toString();
            byte[] ts = String.valueOf(System.currentTimeMillis()).getBytes();
            createPath = this.zk.create(fullPath, ts, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
            processLockCallback(createPath);
        }
        finally {

            releaseMutexLock(currentPath);
            if(createPath != null) {
                writeUnlock(createPath);
            }
        }
    }

    public void processLockCallback(String currentPath) throws Exception {

        int intCurrentPath = -1;
        int slowestSequenceNumber = -1;
        String slowestSequencePath = null;

        /**
         * 当传入的currentPath为Null时, intCurrentPath , slowestSequenceNumber 和 slowestSequencePath 被设置成初始值，
         * 下面的getChildren会拿到值最小的path, 然后处理。
         *
         */

        if(!StringUtils.isEmpty(currentPath)) {

            PathUtils.validatePath(currentPath);
            intCurrentPath = convertToIntFromPath(currentPath);
            slowestSequenceNumber = intCurrentPath;
            slowestSequencePath = currentPath;
        }


        List<String> keys =  this.zk.getChildren(LOCK_PATH, false);
        for(String key : keys) {
            int ikey = convertToIntFromPath(key);
            if(slowestSequenceNumber > ikey) {
                //找到比当前创建的sequencePath还要小的path,需要记录下来，后面会watch该path
                slowestSequenceNumber = ikey;
                slowestSequencePath = new StringBuilder(LOCK_PATH).append("/").append(key).toString();
            }
        }

        //get lock success
        if(slowestSequenceNumber == intCurrentPath) {
            LOG.info(String.format("lock success, currentPath :  %s is the slowest one ", currentPath));
            this.bizProcessor.process();
            writeUnlock(currentPath);
        } else {
            Stat stat = zk.exists(slowestSequencePath, true);
            if (stat == null) {
                //节点不存在，sleep 1millonsecond, 然后递归调用
                Thread.sleep(1);
                processLockCallback(currentPath);
            }
        }
    }

    public void writeUnlock(String currentPath) throws Exception {

        zk.delete(currentPath, 0);
        LOG.info("unlock success, currentPath : " + currentPath);
    }

    private String acquireMutexLock(String path) throws Exception {

        /**
         * 互斥锁，这个锁保证分布式环境下，该逻辑的互斥处理
         */
        String mutex_full_path = new StringBuilder(this.LOCK_PATH).append("/").append(path).append("/").append(this.MUTEX_LOCK_KEY).toString();

        byte[] ts = String.valueOf(System.currentTimeMillis()).getBytes();
        String mutexPath = this.zk.create(mutex_full_path, ts, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        return mutexPath;
    }

    private void releaseMutexLock(String path) throws Exception {

        /**
         * 互斥锁，这个锁保证分布式环境下，该逻辑的互斥处理
         */
        String mutex_full_path = new StringBuilder(this.LOCK_PATH).append("/").append(path).append("/").append(this.MUTEX_LOCK_KEY).toString();
        this.zk.delete(mutex_full_path, -1);
    }

    private int convertToIntFromPath(String path) {

        String needStripValue = null;
        if(path.startsWith(LOCK_PATH)) {
            needStripValue = new StringBuilder(LOCK_PATH).append("/").append(WRITE_LOCK_PREFIX).toString();
        } else {
            needStripValue = new StringBuilder(WRITE_LOCK_PREFIX).toString();
        }
        String suffix = path.replace(needStripValue, "");
        return Integer.parseInt(suffix);
    }



}
