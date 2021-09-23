package no.nav.bidrag.aktoerregister.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mq")
public class MQProperties {

  private String host;
  private int port;
  private String channel;
  private String queueManager;
  private String username;
  private String password;
  private String tssRequestQueue;
  private String tpsRequestQueue;
  private String tpsEventQueue;
  private int timeout;
  private String applicationName;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getQueueManager() {
    return queueManager;
  }

  public void setQueueManager(String queueManager) {
    this.queueManager = queueManager;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getTssRequestQueue() {
    return tssRequestQueue;
  }

  public void setTssRequestQueue(String tssRequestQueue) {
    this.tssRequestQueue = tssRequestQueue;
  }

  public String getTpsRequestQueue() {
    return tpsRequestQueue;
  }

  public void setTpsRequestQueue(String tpsRequestQueue) {
    this.tpsRequestQueue = tpsRequestQueue;
  }

  public String getTpsEventQueue() {
    return tpsEventQueue;
  }

  public void setTpsEventQueue(String tpsEventQueue) {
    this.tpsEventQueue = tpsEventQueue;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }


  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }
}
