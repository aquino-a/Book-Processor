package com.aquino.webParser.bookCreators;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class DocumentCreator {

    private final Class aClass;

    public DocumentCreator(Class aClass) {
        this.aClass = aClass;
    }

    /**
     * Creates a {@link Document} for testing.
     * The file should be located in the same package as the test class.
     *
     * @param fileName the name of the file to test.
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public Document createDocument(String fileName) throws URISyntaxException, IOException {
        var url = aClass.getResource(fileName);
        if(url == null){
            throw new IllegalArgumentException(
                String.format("File doesn't exist or path is wrong: %s", fileName));
        }
        File f = new File(url.toURI());
        return Jsoup.parse(f, "UTF-8");
    }
}
