package com.aquino.webParser.oclc;


import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class OclcServiceImplTest {

    @Test
    public void findOclcTest() {
        var oclcService = new OclcServiceImpl();
        var oclc = oclcService.findOclc("9791168340442");
        assertThat("Must be greater than 0", oclc, Matchers.greaterThan(-1L));
    }

    @Test
    public void findOclcTest2() {
        var oclcService = new OclcServiceImpl();
        var oclc = oclcService.findOclc("9788932474427");
        assertThat("Must be greater than 0", oclc, Matchers.greaterThan(-1L));
    }
}