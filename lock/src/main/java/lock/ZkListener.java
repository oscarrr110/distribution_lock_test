package lock;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * Created by tuhu on 16/3/30.
 */
public class ZkListener  implements Watcher {



    public void process(WatchedEvent event) {

        String path = event.getPath();
        System.out.println("watch path: " + path);
        if (event.getType() == Event.EventType.None) {
            // We are are being told that the state of the
            // connection has changed
            switch (event.getState()) {
                case SyncConnected:
                    // In this particular example we don't need to do anything
                    // here - watches are automatically re-registered with
                    // server and any watches triggered while the client was
                    // disconnected will be delivered (in order of course)
                    break;
                case Expired:
                    // It's all over
//                    dead = true;
//                    listener.closing(KeeperException.Code.SessionExpired);
                    break;
            }
        } else {
//            if (path != null && path.equals(znode)) {
//                // Something has changed on the node, let's find out
//                zk.exists(znode, true, this, null);
//            }
        }
    }

}
