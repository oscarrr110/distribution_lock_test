package lock.schedule;

import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tuhu on 16/4/6.
 */
public class LockTimeoutTimer {


    public static final String LOCK_ROOT_PATH = "/distribution_lock";

    private ZooKeeper zk;

    private long delay;

    private long period;

    private LockTimeoutTimer(ZooKeeper zk, long delay, long period) {
        this.zk = zk;
        this.delay = delay;
        this.period = period;
    }

    public static void main(String[] args) {

        ZooKeeper zk = null;
        LockTimeoutTimer lockSchedule = new LockTimeoutTimer(zk, 0, 1000);
        Timer timer = new Timer();
        ZkTimer zkTimer = new ZkTimer(zk);
        timer.schedule(zkTimer, lockSchedule.delay, lockSchedule.period);
    }
}

class ZkTimer extends TimerTask {

    ZooKeeper zk;

    ZkTimer(ZooKeeper zk) {
        this.zk = zk;
    }

    @Override
    public void run() {

        try {
            List<String> allResources = zk.getChildren(LockTimeoutTimer.LOCK_ROOT_PATH, false);
            for(String resourceName : allResources) {

                List<String> resourceInstances = zk.getChildren(resourceName, false);
                for (String resourceInstance :  resourceInstances) {

                    String path = LockTimeoutTimer.LOCK_ROOT_PATH + "/" + resourceName + "/" + resourceInstance;
                    byte[] time = zk.getData(path, false, null);
                    long dataTimeout = Long.parseLong(new String(time));

                    //如果超时时间已过,说明该节点已经超时,删除之
                    if(System.currentTimeMillis() > dataTimeout) {
                        zk.delete(path, 0);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("do timer");
    }

}
