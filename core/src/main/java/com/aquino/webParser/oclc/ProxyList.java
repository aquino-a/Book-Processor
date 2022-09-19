package com.aquino.webParser.oclc;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ProxyList {
    private static final Predicate<String> TIME_REGEX
        = Pattern.compile("(\\d{1,2} secs?|1 min) ago").asPredicate();
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
            .filter(e -> TIME_REGEX.test(e.child(7).ownText()))
            .map(e -> String.format("%s:%s", e.child(0), e.child(1)))
            .forEach(s -> proxies.add(s));
    }
}
