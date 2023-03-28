package no.nav.bidrag.aktoerregister.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
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
  private long backOffInitialInterval;
  private long backOffMaxInterval;
}
