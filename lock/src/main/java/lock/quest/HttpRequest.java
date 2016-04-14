package lock.quest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by tuhu on 16/3/31.
 */
public class HttpRequest {


    static final Logger LOG = LoggerFactory.getLogger(HttpRequest.class);

    String url;

    public  HttpRequest(String url) {
        this.url = url;
    }

    public void doEnterRequest() throws Exception {
        doProcess(this.url + "/add");
    }

    public void doLeaveRequest() throws Exception {
        doProcess(this.url + "/minus");
    }


    private void doProcess(String url) throws Exception {

        URL u = new URL(url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(u.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            LOG.info(String.format("url: %s , response: %s", url.toString(), inputLine));
        in.close();
    }


    public static void main(String[] args) throws Exception {

        HttpRequest request = new HttpRequest("http://127.0.0.1:5000");
        request.doEnterRequest();
        request.doLeaveRequest();
    }
}
