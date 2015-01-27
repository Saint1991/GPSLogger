package geologger.saints.com.geologger.utils;

import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Mizuno on 2015/01/25.
 */
@EBean
public class BaseHttpClient {

    @RootContext
    protected Context mContext;

    public BaseHttpClient() {}

    protected String sendHttpGetRequest(String query) {

        String ret = null;

        //Prepare Http
        HttpGet get = new HttpGet(query);
        DefaultHttpClient client = new DefaultHttpClient();

        HttpResponse response = null;
        try {

            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            ret = EntityUtils.toString(response.getEntity(), "UTF-8");

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    protected String sendHttpPostRequest(String url, String params) {

        String ret = null;

        HttpPost post = new HttpPost(url);
        DefaultHttpClient client = new DefaultHttpClient();

        StringEntity paramEntity;
        HttpResponse response;

        try {

            paramEntity = new StringEntity(params);
            paramEntity.setChunked(false);
            post.setEntity(paramEntity);

            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            ret = EntityUtils.toString(response.getEntity(), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }
}