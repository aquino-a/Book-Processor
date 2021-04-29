package com.aquino.webParser.oclc;

import com.aquino.webParser.utilities.Connect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OclcServiceImpl implements OclcService {

    private static final Pattern oclcPattern = Pattern.compile("oclc/(\\d+)$");

    @Override
    public long findOclc(String isbn) {
        String location = Connect.readLocationHeader(isbn);
        if(location.equals("-1"))
            return -1;
        else
            return parseOCLC(location);
    }

    private long parseOCLC(String location) {
        Matcher m = oclcPattern.matcher(location);
        if(m.find())
            return Long.parseLong(m.group(1));
        else return -1;

    }
}
