package lock;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * Created by tuhu on 16/3/30.
 */
public class ZkTest {


    ZooKeeper zk;

    static final String LOCK_PATH = "/distribution_lock";

    static final String WRITE_LOCK_PREFIX = "write-";


    public ZkTest(ZooKeeper zookeeper) {
        this.zk = zookeeper;
    }


    public static void main(String[] args)  throws Exception {

        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 3000, null);
        ZkTest zkTools = new ZkTest(zk);
        zkTools.writeLock(true);
    }


    public void writeLock(boolean needWatch) throws Exception {


        String fullpath = new StringBuilder(LOCK_PATH).append("/").append(WRITE_LOCK_PREFIX).toString();
        String currentPath = zk.create(fullpath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        processLock(currentPath);

        List<String> childs = zk.getChildren(LOCK_PATH, false);
        for (String child :  childs) {
            System.out.println(child);
        }
    }


    private void processLock(String currentPath) throws  Exception {

        int intCurrentPath = convertToIntFromPath(currentPath);
        int slowestSequenceNumber = intCurrentPath;
        String slowestSequencePath = currentPath;

        List<String> keys =  zk.getChildren(LOCK_PATH, false);
        for(String key : keys) {
            int ikey = convertToIntFromPath(key);
            if(slowestSequenceNumber > ikey) {
                //找到比当前创建的sequencePath还要小的path,需要记录下来，后面会watch该path
                slowestSequenceNumber = ikey;
                slowestSequencePath = key;
            }
        }

        //get lock success
        if(slowestSequenceNumber == intCurrentPath) {
            //proccess your business
            zk.delete(slowestSequencePath, 0);
        } else {
            Stat stat = zk.exists(slowestSequencePath, true);
            if (stat == null) {
                //节点不存在，sleep 1millonsecond, 然后递归调用
                Thread.sleep(1);
                processLock(currentPath);
            }
        }
    }

    private int convertToIntFromPath(String path) {
        String suffix = path.replace(WRITE_LOCK_PREFIX,"");
        return Integer.parseInt(suffix);
    }

}
