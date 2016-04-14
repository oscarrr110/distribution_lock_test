package lock.utils;

import lock.biz.BizProcess;
import lock.quest.HttpRequest;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by tuhu on 16/3/30.
 */
public class ZkLockWatcher implements Watcher {

    static Logger LOG = LoggerFactory.getLogger(ZkLockWatcher.class);

    ZooKeeper zk;

    BizProcess biz;

    public ZkLockWatcher(String url, int timeout, BizProcess biz) throws Exception{
        this.zk = new ZooKeeper(url, timeout, this);
        this.biz = biz;
    }


    public void process(WatchedEvent event) {

        String path = event.getPath();
        LOG.info("watch, currentPath: " + path);
        if (event.getType() == Event.EventType.None) {
            // We are are being told that the state of the
            // connection has changed
            switch (event.getState()) {
                case SyncConnected:
                    break;
                case Expired:
                    break;
            }
        } else {

            if (path != null) {
                try {
                    new ZkLockProcessor(zk, biz).processLockCallback(null);
                } catch (Exception e) {
                   LOG.error("process error while watching, currentPath: " + path, e);
                }
            }
//            if (path != null && path.equals(znode)) {
//                // Something has changed on the node, let's find out
//                zk.exists(znode, true, this, null);
//            }
        }

    }


    public ZooKeeper getZookeeper() {
        return this.zk;
    }

}
