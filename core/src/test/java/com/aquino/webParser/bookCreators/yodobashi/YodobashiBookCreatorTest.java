package com.aquino.webParser.bookCreators.yodobashi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class YodobashiBookCreatorTest {
    private YodobashiBookCreator bc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bc = new YodobashiBookCreator();
    }

    @Test
    public void testFillInDetailsLive() throws IOException {
        var book = bc.createBookFromIsbn("9784865373295");
        bc.fillInAllDetails(book);

        assertThat(book.getOriginalPrice(), is("1650"));
        assertThat(book.getCategory(), is("トップ > 書籍 > 実用書 > 占い･易･おまじない > 超自然･超能力･UFO"));
        assertThat(book.getDescription(), is("知ったら最後、夜眠れなくなる 世界の未解決ミステリー68 の 商品概要\n" + //
                        "要旨（「BOOK」データベースより）\n" + //
                        "\n" + //
                        "なぜ消えた？誰が殺った？蒸発した少女、謎多き怪事件、正体不明の真犯人…未解決ミステリーシリーズ第６弾！\n" + //
                        "目次（「BOOK」データベースより）\n" + //
                        "\n" + //
                        "第１章　人間蒸発　国内編（日光市・佐々木奈保子ちゃん失踪事件；茨城県三和町・石嵜容子さん消失事件　ほか）\n" + //
                        "第２章　人間蒸発　海外編（米・ソダー家児童５人失踪事件；英２歳女児、カトリック・リー失踪事件　ほか）\n" + //
                        "第３章　殺人の迷宮　国内編（島田市一家４人殺人事件；佐賀女性７人連続殺人事件　ほか）\n" + //
                        "第４章　殺人の迷宮　海外編（英１７歳大学生、アン・ノブレット殺害事件；米フロリダ州、ウォーカー一家殺害事件　ほか）\n" + //
                        "第５章　残された謎（ハリウッド女優セルマ・トッド不審死事件；フィンランド・イムヤルビＵＦＯ遭遇事件　ほか）"));
    }
}
