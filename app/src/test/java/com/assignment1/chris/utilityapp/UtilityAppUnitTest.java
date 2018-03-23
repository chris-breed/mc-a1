package com.assignment1.chris.utilityapp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UtilityAppUnitTest {
    @Test
    public void generateURL() throws Exception {
        String from = "usd";
        String to = "aud";
        String url = "https://free.currencyconverterapi.com/api/v5/convert?q=";

        String result = (url + from.toUpperCase() + "_" + to.toUpperCase());

        assertEquals("https://free.currencyconverterapi.com/api/v5/convert?q=USD_AUD", result);
    }

    @Test
    public void convertValue() throws Exception {
        double origValue = 30.00;
        double conversionMultiplier = 2.835113;
        double convertedValued = origValue * conversionMultiplier;
        double delta = 0.01;

        assertEquals(85.05, convertedValued, delta);
    }
}