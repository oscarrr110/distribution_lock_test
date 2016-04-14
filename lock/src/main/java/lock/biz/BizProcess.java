package lock.biz;

/**
 * Created by tuhu on 16/3/31.
 */
public interface BizProcess {

    /**
     * 成功拿到业务锁后的业务处理入口
     */
    public void process();

}
