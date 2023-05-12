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
        book.setIsbn(12345L);
        book.setDescription("「週刊文春ミステリーベスト10」&「MRC大賞2022」堂々ダブル受賞!\n"
                + "\n"
                + "9人のうち、死んでもいいのは、ーー死ぬべきなのは誰か?\n"
                + "\n"
                + "大学時代の友達と従兄と一緒に山奥の地下建築を訪れた柊一は、偶然出会った三人家族とともに地下建築の中で夜を越すことになった。\n"
                + "翌日の明け方、地震が発生し、扉が岩でふさがれた。さらに地盤に異変が起き、水が流入しはじめた。いずれ地下建築は水没する。\n"
                + "そんな矢先に殺人が起こった。\n"
                + "だれか一人を犠牲にすれば脱出できる。生贄には、その犯人がなるべきだ。ーー犯人以外の全員が、そう思った。\n"
                + "\n"
                + "タイムリミットまでおよそ1週間。それまでに、僕らは殺人犯を見つけなければならない。\n"
                + "\n"
                + "その他ミステリーランキングにも続々ランクイン!\n"
                + "本格ミステリ・ベスト10 2023 国内ランキング(原書房) 第2位\n"
                + "このミステリーがすごい! 2023年版 国内編(宝島社) 第4位\n"
                + "ミステリが読みたい! 2023年版 国内篇(早川書房) 第6位\n" +
                "ダ・ヴィンチ BOOK OF THE YEAR 2022 小説部門(KADOKAWA) 第7位");
        var summary = chat.getSummary(book);

        assertThat(summary, is(notNullValue()));
    }
    
    @Test
    public void testGetTitle() {
        var book = new Book();
        book.setIsbn(12345L);
        book.setTitle("混ぜるだけサラダとさっと煮るだけスープ");
        var title = chat.getTitle(book);

        assertThat(title, is(notNullValue()));
    }
}
