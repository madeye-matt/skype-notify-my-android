package com.madeye.notify;

import com.skype.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkypeNotifyMyAndroid {

    private static final Logger LOG = Logger.getLogger(SkypeNotifyMyAndroid.class.getName());

    private static final String PROP_APIKEY = "nma.apikey";
    private static final String PROP_APPLICATION = "nma.application_name";
    private static final String PROP_BASE_URL = "nma.base_url";

    private String apiKey;
    private String application;
    private URI baseUri;

    private class UserStatusListener extends UserListenerAdapter {
        @Override
        public void statusMonitor(User.Status status, User user) throws SkypeException {
            String displayName = user.getDisplayName();

            displayName = StringUtils.isBlank(displayName) ? user.toString() : displayName;

            String statusStr = status.toString();

            try {
                sendNmaNotification(0, displayName, "Status: " + statusStr);
            } catch (NotificationFailedException e) {
                LOG.log(Level.WARNING, "Failed to Notify My Android", e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final SkypeNotifyMyAndroid nma = new SkypeNotifyMyAndroid();

        Skype.setDaemon(false); // to prevent exiting from this program
        Skype.addChatMessageListener(new ChatMessageAdapter() {
            public void chatMessageReceived(ChatMessage received) throws SkypeException {
                String content = received.getContent();
                String sender = received.getSenderDisplayName();

                System.out.println("From " + sender + ": " + content);

                try {
                    nma.sendNmaNotification(-1, sender, content);
                } catch (NotificationFailedException e) {
                    LOG.log(Level.WARNING, "Failed to Notify My Android", e);
                }
            }
        });
        Skype.addUserListener(nma.createUserStatusListener());
    }

    public SkypeNotifyMyAndroid(){
        InputStream is = null;

        try {
            this.apiKey = System.getProperty(PROP_APIKEY);

            is = SkypeNotifyMyAndroid.class.getResourceAsStream("/notifymyandroid.properties");
            Properties props = new Properties();
            props.load(is);

            String baseUrl = props.getProperty(PROP_BASE_URL);
            this.application = props.getProperty(PROP_APPLICATION);

            this.baseUri = new URI(baseUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration properties");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to parse URI for SkypeNotifyMyAndroid");
        } finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Failed to close properties file after reading", e);
                }
            }
        }
    }

    private UserStatusListener createUserStatusListener(){
        return new UserStatusListener();
    }

    private void sendNmaNotification(int priority, String sender, String content) throws NotificationFailedException {
        URIBuilder builder = new URIBuilder(this.baseUri);

        builder.addParameter("apikey", this.apiKey);
        builder.addParameter("application", this.application);
        builder.addParameter("event", sender);
        builder.addParameter("description", content);
        builder.addParameter("priority", Integer.toString(priority));

        CloseableHttpResponse response = null;

        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();

            URI uri = builder.build();
            HttpGet get = new HttpGet(uri);

            response = httpclient.execute(get);

            StatusLine status = response.getStatusLine();

            int statusCode = status.getStatusCode();

            HttpEntity entity1 = response.getEntity();
            EntityUtils.consume(entity1);

            if (statusCode != 200){
                throw new NotificationFailedException("Failed to call SkypeNotifyMyAndroid REST API - status code: " + statusCode);
            }
        } catch (IOException e) {
            throw new NotificationFailedException("Failed to call SkypeNotifyMyAndroid REST API");
        } catch (URISyntaxException e) {
            throw new NotificationFailedException("Failed to build URI for SkypeNotifyMyAndroid");
        } finally {
            if (response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Failed to close HTTP connection to SkypeNotifyMyAndroid", e);
                }
            }
        }
    }
}
