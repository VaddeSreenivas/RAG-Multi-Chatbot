package com.sreenu.RagPinecoe.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebScraper {

    public static String fetchText(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (ChatBot)")
                    .timeout(15000)
                    .get();
            return doc.text();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch URL: " + url, e);
        }
    }

}
