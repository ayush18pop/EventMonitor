package org.example.config;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

//import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class PrometheusConfig {

    @PostConstruct
    public void init() throws IOException {
        DefaultExports.initialize();
        HTTPServer server = new HTTPServer(8082);  // Ensure this matches the port Prometheus is scraping
    }
}