package com.theglobalmarketaccess.ohlc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Exchange {
    private String name;
    private Iterable<String> symbols;
    private int count;
}
