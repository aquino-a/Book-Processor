/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.aquino.webParser.chatgpt;

import com.aquino.webParser.ProcessorFactoryImpl;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import com.aquino.webParser.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author alex
 */
@RunWith(Parameterized.class)
public class ChatGptServiceImplTest {

    private final AbstractAiService aiService;

    public ChatGptServiceImplTest(AbstractAiService aiService) {
        this.aiService = aiService;
    }

    @Parameters
    public static Collection<Object[]> data() throws IOException {
        var summaryRepository = mock(SummaryRepository.class);
        var factory = new ProcessorFactoryImpl();
        var objectMapper = new ObjectMapper();

        var chatKey = factory.getOpenAiApiKey();
        var chat = new ChatGptServiceImpl(objectMapper, chatKey, summaryRepository);
        chat.setCategories(factory.getCategories());

        var grokKey = factory.getGrokApiKey();
        var grok = new GrokService(objectMapper, grokKey, summaryRepository);
        grok.setCategories(factory.getCategories());

        return Arrays.asList(new Object[][] {
                { chat },
                { grok }
        });
    }

    private static final String TEST_DESCRIPTION = "「週刊文春ミステリーベスト10」&「MRC大賞2022」堂々ダブル受賞!\n"
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
            "ダ・ヴィンチ BOOK OF THE YEAR 2022 小説部門(KADOKAWA) 第7位";

    @Test
    public void testGetSummary() {
        var book = new Book();
        book.setIsbn(12345L);
        book.setDescription(TEST_DESCRIPTION);
        var summary = aiService.getSummary(book);

        assertThat(summary, is(notNullValue()));
    }

    @Test
    public void testGetTitle() {
        var book = new Book();
        book.setIsbn(12345L);
        book.setTitle("混ぜるだけサラダとさっと煮るだけスープ");
        var title = aiService.getTitle(book);

        assertThat(title, is(notNullValue()));
    }

    @Test
    public void testSetCategory() {
        var book = new Book();
        book.setIsbn(12345L);
        book.setDescription(TEST_DESCRIPTION);
        aiService.setCategory(book);

        assertThat(book.getCategory(), is(notNullValue()));
        assertThat(book.getCategory2(), is(notNullValue()));
        assertThat(book.getCategory3(), is(notNullValue()));
    }

    @Test
    public void testGetKoreanDescription() {
        var book = new Book();
        book.setIsbn(12345L);
        var description = String.join("\n",
                "高２で絵を描くことの楽しさに目覚め。猛烈な努力の末に東京藝大に合格した矢口八虎。",
                "藝大２年目を迎え、これまでの課題や講評で芽生えた自分の才能や大学への疑問や不安に美術への情熱を曇らせ、道に迷う八虎に、学外のアート集団ノーマークスと主宰の不二桐緒は新しい視点を与えた。",
                "「新入生」の時期は終わり、大人へのステップが始まる。新しい出会い、新しい課題、美術との関わり方、八虎の人生も新しい局面へ。",
                "夏休みのある日、金も予定も目標もなくむなしく時を過ごす八虎に、高校からのライバル・世田介に「公募展」なるものがあることを教えられ賞金や展示など授業や課題とは一線を画す作品作りの世界を意識する。公募展に挑むか久々にわくわくする八虎を、年上の同級生・八雲と鉢呂が、彼等の故郷。広島へ誘う。広いアトリエで思う存分作品を作っては？という誘いだった。八虎と世田介は鉢呂、訳も、柿ノ木坂桃代とともに、車で広島へ向かう！",
                "アートの歴史や可能性を詳細に活写、美大に進学した青年たちの情熱や奮闘を描く、今までになかった美術系青春漫画、早くも最新刊登場！！");

        book.setDescription(description);
        var koreanDescription = aiService.getKoreanDescription(book);

        assertThat(koreanDescription, is(notNullValue()));
    }
}
