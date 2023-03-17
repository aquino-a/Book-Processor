/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.aquino.webParser.chatgpt;

import com.aquino.webParser.ProcessorFactoryImpl;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import com.aquino.webParser.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author alex
 */
public class ChatGptServiceImplTest {

    private ChatGptServiceImpl chat;

    @Mock
    SummaryRepository summaryRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        var processorFact = new ProcessorFactoryImpl();
        var key = processorFact.getOpenAiApiKey();

        chat = new ChatGptServiceImpl(new ObjectMapper(), key, summaryRepository);
    }

    @Test
    public void testGetSummary() {
        var book = new Book();
        book.setIsbn(1234L);
        book.setDescription("Testing no tokens plz.");
        var summary = chat.getSummary(book);

        assertThat(summary, is(notNullValue()));
    }
}
