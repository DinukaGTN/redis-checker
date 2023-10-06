package com.theglobalmarketaccess.ohlc;

import com.theglobalmarketaccess.ohlc.config.cache.CacheConfiguration;
import com.theglobalmarketaccess.ohlc.model.Exchange;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisChecker {
    private final CacheConfiguration cacheConfig;

    RedissonClient redissonClient;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    public RedisChecker(CacheConfiguration cacheConfig, RedissonClient redissonClient) {
        this.cacheConfig = cacheConfig;
        this.redissonClient = redissonClient;
    }

    public RScoredSortedSet<String> getKeys(String key) {
        return redissonClient.getScoredSortedSet(key);
    }

    public Exchange getExchange(String exchange) {
        Exchange exchangeObj = new Exchange();
        exchangeObj.setName(exchange);

        RKeys keys = redissonClient.getKeys();
        String keyPattern = exchange + "~*";

        // get the keys conforming to a pattern
        exchangeObj.setSymbols(keys.getKeysByPattern(keyPattern));

        int count = 0;
        for (String s : exchangeObj.getSymbols()) count++;

        exchangeObj.setCount(count);

        return exchangeObj;
    }

    public String getAllKeysSpecificExchange(Exchange exchange) {
        List<RScoredSortedSet<String>> symbolList = new ArrayList<>();
        for (int i = 0; i < exchange.getCount(); i++) {
            for (String symbol : exchange.getSymbols()) {
                symbolList.add(getKeys(symbol));
            }
        }
        return "read all keys in " + exchange.getName();
    }

    public int getNumberOfKeys() {
        RKeys keys = redissonClient.getKeys();
        return (int)keys.count();
    }

//    public String getSpecificSymbolByExchange(String symbol) {
//        return scoredSortedData;
//    }

}
