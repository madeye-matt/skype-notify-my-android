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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkypeNotifyMyAndroid {

    private enum NotificationType { Status, Message };

    private static final Logger LOG = Logger.getLogger(SkypeNotifyMyAndroid.class.getName());

    private static final String PROP_APIKEY = "nma.apikey";
    private static final String PROP_APPLICATION = "nma.application_name";
    private static final String PROP_BASE_URL = "nma.base_url";
    private static final String PROP_PRIORITY_BASE = "nma.priority.";
    private static final String PROP_USER_BASE = "nma.users.";

    private static final int DEFAULT_PRIORITY = -100;

    private String apiKey;
    private String application;
    private URI baseUri;

    private Map<NotificationType, Integer> priorityMap = new HashMap<NotificationType, Integer>();
    private Map<String, Integer> userPriorityBoost = new HashMap<String, Integer>();

    private class UserStatusListener extends UserListenerAdapter {
        @Override
        public void statusMonitor(User.Status status, User user) throws SkypeException {
            String userId = user.toString();
            String displayName = user.getFullName();

            displayName = StringUtils.isBlank(displayName) ? userId : displayName;

            String statusStr = status.toString();

            try {
                sendNmaNotification(NotificationType.Status, userId, displayName, "Status: " + statusStr);
            } catch (NotificationFailedException e) {
                LOG.log(Level.WARNING, "Failed to Notify My Android", e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final SkypeNotifyMyAndroid nma;

        if (args.length > 0){
            nma = new SkypeNotifyMyAndroid(args[0]);
        } else {
            nma = new SkypeNotifyMyAndroid();
        }

        Skype.setDaemon(false); // to prevent exiting from this program
        Skype.addChatMessageListener(new ChatMessageAdapter() {
            public void chatMessageReceived(ChatMessage received) throws SkypeException {
                String content = received.getContent();
                String sender = received.getSenderDisplayName();
                String userId = received.getId();

                System.out.println("From " + sender + ": " + content);

                try {
                    nma.sendNmaNotification(NotificationType.Message, userId, sender, content);
                } catch (NotificationFailedException e) {
                    LOG.log(Level.WARNING, "Failed to Notify My Android", e);
                }
            }
        });
        Skype.addUserListener(nma.createUserStatusListener());
    }

    public SkypeNotifyMyAndroid(){
        this(null);
    }

    public SkypeNotifyMyAndroid(String userBoostFile){
        InputStream is = null;

        try {
            this.apiKey = System.getProperty(PROP_APIKEY);

            is = SkypeNotifyMyAndroid.class.getResourceAsStream("/notifymyandroid.properties");
            Properties props = new Properties();
            props.load(is);

            String baseUrl = props.getProperty(PROP_BASE_URL);
            this.application = props.getProperty(PROP_APPLICATION);

            for (NotificationType ntype : NotificationType.values()){
                String propName = PROP_PRIORITY_BASE + ntype.name();
                String propValueStr = props.getProperty(propName);
                int propValue;

                propValue = StringUtils.isBlank(propValueStr) ? DEFAULT_PRIORITY : Integer.parseInt(propValueStr);

                this.priorityMap.put(ntype, propValue);
            }

            if (StringUtils.isBlank(userBoostFile) == false){
                this.userPriorityBoost = loadUserPriorityBoostFile(userBoostFile);
            }

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

    private Map<String, Integer> loadUserPriorityBoostFile(String filename) throws IOException {
        Properties props = new Properties();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        props.load(reader);

        Map<String, Integer> boostMap = new HashMap<String, Integer>();

        for (String propertyName : props.stringPropertyNames()){
            if (propertyName.startsWith(PROP_USER_BASE)){
                String name = propertyName.substring(PROP_USER_BASE.length());
                String valueStr = props.getProperty(propertyName);

                if (StringUtils.isBlank(valueStr) == false){
                    int value = Integer.parseInt(valueStr);

                    boostMap.put(name, value);
                }
            }
        }
        reader.close();

        return boostMap;
    }

    private UserStatusListener createUserStatusListener(){
        return new UserStatusListener();
    }

    private int getPriority(NotificationType type, String userId){
        Integer priority = this.priorityMap.get(type);

        if (priority == null){
            priority = DEFAULT_PRIORITY;
        }

        Integer userBoost = this.userPriorityBoost.get(userId);

        if (userBoost != null){
            priority += userBoost;
        }

        return priority;
    }

    private void sendNmaNotification(NotificationType type, String userId, String name, String content) throws NotificationFailedException {
URIBuilder builder = new URIBuilder(this.baseUri);

        int priority = getPriority(type, userId);

        if (priority >= -2){
            if (priority > 2){
                priority = 2;
            }

            builder.addParameter("apikey", this.apiKey);
            builder.addParameter("application", this.application);
            builder.addParameter("event", name);
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
}
