package com.theglobalmarketaccess.ohlc.config.cache;


import com.theglobalmarketaccess.ohlc.config.YamlPropertySourceFactory;
import com.theglobalmarketaccess.ohlc.utils.AppShutdownHelper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties
@PropertySource(value = "file:config/application.yml", factory = YamlPropertySourceFactory.class)
public class CacheConfiguration extends CachingConfigurerSupport {
    @Autowired
    AppShutdownHelper shutdownHelper;
    static final Logger logger = LoggerFactory.getLogger(CacheConfiguration.class);
    @Value("${spring.cache.server:redis://redis-feed-qa-cache-tns.1eiork.ng.0001.aps1.cache.amazonaws.com:6379}")
    String server;
    @Value("${spring.cache.redis.nettyThreads:32}")
    private int nettyThreads;
    @Value("${spring.cache.redis.connectionPoolSize:64}")
    private int connectionPoolSize;
    @Value("${spring.cache.redis.connectionMinimumIdleSize:24}")
    private int connectionMinimumIdleSize;
    @Value("${spring.cache.redis.timeout:3000}")
    private int timeout;
    @Value("${spring.cache.redis.connectTimeout:1000}")
    private int connectTimeout;
    @Value("${spring.cache.redis.retryAttempts:3}")
    private int retryAttempts;
    @Value("${spring.cache.redis.retryInterval:1500}")
    private int retryInterval;
    @Value("${spring.cache.redis.cacheTTL:300}")
    private long cacheTTL;
    @Value("${spring.cache.redis.cacheMaxSize:1000}")
    private int cacheMaxSize;

//    // Cache name in Redis
//    @Value("${database.gtn-user-data.json-column}")
//    private String jsonColumn;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(value = "spring.cache.type", havingValue = "redis")
    RedissonClient redisson() {

        logger.info("Redis configuration started");

        Config config = new Config();
        config.setNettyThreads(nettyThreads)
                .useSingleServer() //TODO: May need to change depending on AWS Elasticache, Add to application config to switch between cluster and single server
                .setAddress(server)
                .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setConnectionPoolSize(connectionPoolSize)
                .setTimeout(timeout)
                .setConnectTimeout(connectTimeout)
                .setRetryAttempts(retryAttempts)
                .setRetryInterval(retryInterval);

        RedissonClient client = null;
        try {
            client = Redisson.create(config);
            logger.info("Redis configured");
        } catch (Exception e) {
            logger.error("Redis not Available...Application Shutting Down");
            shutdownHelper.initiateShutdown(1);
        }
        return client;

    }

    @Bean
    @ConditionalOnProperty(value = "spring.cache.type", havingValue = "redis")
    public CacheManager redissonCacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<>();
        logger.info("Redis cache manager configuring");
        CacheConfig cacheConfig = new CacheConfig();
        if (cacheTTL>0) cacheConfig.setTTL(cacheTTL * 1000);
        cacheConfig.setMaxSize(cacheMaxSize);
//        config.put(jsonColumn, cacheConfig);
        CacheManager cacheManager = new RedissonSpringCacheManager(redissonClient, config);
        logger.info("redis cache manager configured");
        return cacheManager;
    }

}
