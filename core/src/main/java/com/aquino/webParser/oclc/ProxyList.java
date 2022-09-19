package com.aquino.webParser.oclc;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ProxyList {
    private final Set<String> proxies = new HashSet<>();

    public String getProxy() throws IOException {
        if(proxies.size() <= 0){
            getProxies();
        }

        if(proxies.size() <= 0){
            throw new IOException("Couldn't load proxies");
        }

        return proxies.stream().findFirst().get();
    }

    public void removeProxy(String proxy){
        proxies.remove(proxy);
    }

    private void getProxies() throws IOException {
        var doc = Jsoup.connect("us-proxy.org")
            .get();

        doc.selectFirst(".table.table-striped.table-bordered")
            .selectFirst("tbody")
            .getElementsByTag("tr")
            .stream()
            .filter(e -> e.getElementsByIndexEquals())


    }
}
