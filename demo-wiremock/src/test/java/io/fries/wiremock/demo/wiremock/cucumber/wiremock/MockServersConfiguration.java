package io.fries.wiremock.demo.wiremock.cucumber.wiremock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
@Profile("mock")
public class MockServersConfiguration {

    @Autowired
    private MockServers mockServers;

    @PostConstruct
    public void setUp() {
        mockServers.start();
    }

    @PreDestroy
    public void tearDown() {
        mockServers.stop();
    }
}
