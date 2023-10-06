package com.theglobalmarketaccess.ohlc.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AppShutdownHelper {
    @Autowired
    private ApplicationContext applicationContext;

    public void initiateShutdown(int returnCode) {
        SpringApplication.exit(applicationContext, () -> returnCode);
        System.exit(returnCode);
    }
}
