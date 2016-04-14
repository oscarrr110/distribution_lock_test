package lock.biz;

import lock.quest.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tuhu on 16/3/31.
 */
public class DiskService implements BizProcess {

    static final Logger LOG = LoggerFactory.getLogger(DiskService.class);
    static HttpRequest request = new HttpRequest("http://127.0.0.1:5000");

    public void process() {

        try {
            request.doEnterRequest();
            LOG.info("process complete");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
