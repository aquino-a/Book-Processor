package com.aquino.webParser.oclc;


import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

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

    @Test
    public void createBookTest() throws IOException {
        var oclcService = new OclcServiceImpl();
        var book = oclcService.createBookFromIsbn("1344342536");

        assertThat("Has matching author", book.getAuthor(), Matchers.is("Franziska Biermann"));
        assertThat("Must not have second author", book.getAuthor2(), Matchers.emptyOrNullString());
    }

    @Test
    public void createBookTest2() throws IOException {
        var oclcService = new OclcServiceImpl();
        var book = oclcService.createBookFromIsbn("1344301242");

        assertThat("Has matching author", book.getAuthor(), Matchers.is("Angela Ackerman"));
        assertThat("Has matching author2", book.getAuthor2(), Matchers.is("Becca Puglisi"));
    }


}