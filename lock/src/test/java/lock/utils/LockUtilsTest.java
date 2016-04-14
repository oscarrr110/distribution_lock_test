package lock.utils;

import lock.biz.BizProcess;
import lock.biz.DiskService;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

/**
 * Created by tuhu on 16/3/31.
 */
public class LockUtilsTest  {


    @Test
    public void writeLockTest() throws Exception {

        String url = "127.0.0.1:2181";

        int timeout = 30000;
        BizProcess biz = new DiskService();

        ZkLockWatcher w = new ZkLockWatcher(url, timeout, biz);
        BizProcess processor = new DiskService();
        LockProcessor p = new ZkLockProcessor(w.getZookeeper());

        p.writeLock("abc", processor);

        Thread.sleep(20000);
    }


//    public static void main(String[] args) throws Exception {
//
//        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 30000, null);
//        Stat s = zk.exists("/sync-service/aa", false);
//        if(s == null) {
//            System.out.println(s);
//            zk.create("/sync-service/aa", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        }
//
////        String path = zk.create("/sync-service/a/a", null , null, CreateMode.PERSISTENT);
//    }

}
