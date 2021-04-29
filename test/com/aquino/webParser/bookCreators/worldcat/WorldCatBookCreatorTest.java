package com.aquino.webParser.bookCreators.worldcat;

import com.aquino.webParser.bookCreators.amazon.AmazonJapanBookCreator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.Assert.*;

public class WorldCatBookCreatorTest {

    private WorldCatBookCreator bc;

    @Before
    public void setUp() throws Exception {
        bc = new WorldCatBookCreator();
    }

    @Test
    public void createBookFromIsbn1() throws IOException {
        var book = bc.createBookFromIsbn("9784822289607");
        assertEquals("Hans Rosling,Ola Rosling,Anna Rosling Rönnlund,Shūsaku Uesugi", book.getAuthor());
        assertEquals("Factfulness = Fakutofurunesu : jū no omoikomi o norikoe dēta o moto ni sekai o tadashiku miru shūkan", book.getTitle());
        assertEquals("Nikkei BP Sha", book.getPublisher());
    }
    @Test
    public void createBookFromIsbn2() throws IOException {
        var book = bc.createBookFromIsbn("9784163907956");
        assertEquals("maiko Seo", book.getAuthor());
        assertEquals("Soshite baton wa watasareta.", book.getTitle());
        assertEquals(WorldCatBookCreator.NOT_FOUND, book.getPublisher());
    }
    @Test
    public void createBookFromIsbn3() throws IOException {
        var book = bc.createBookFromIsbn("9784000222945");
        assertEquals("Atsushi Okada", book.getAuthor());
        assertEquals("Eiga wa kaiga no yō ni : seishi, undō, jikan", book.getTitle());
        assertEquals("Iwanami Shoten", book.getPublisher());
    }

}