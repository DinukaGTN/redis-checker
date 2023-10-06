package com.theglobalmarketaccess.ohlc;

import com.theglobalmarketaccess.ohlc.config.YamlPropertySourceFactory;
import com.theglobalmarketaccess.ohlc.model.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checker/v1")
@PropertySource(value = "file:config/application.yml", factory = YamlPropertySourceFactory.class)
public class RedisCheckerAPI {

    @Autowired
    private RedisChecker redisChecker;

    private Exchange exchange;

    @GetMapping("/count/{exchange}")
    public int countAllSymbols(@PathVariable String exchange){
        this.exchange = redisChecker.getExchange(exchange);
        return this.exchange.getCount();
    }

//    @GetMapping("/count/{exchange}/all")
//    public void countAlKeysInsideEachSymbol(@PathVariable String exchange){
//        this.exchange = redisChecker.getExchange(exchange);
//    }

}