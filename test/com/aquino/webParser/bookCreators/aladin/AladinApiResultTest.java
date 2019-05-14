package com.aquino.webParser.bookCreators.aladin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class AladinApiResultTest {

    private final String json = "{\n" +
            "  \"version\": \"20070901\",\n" +
            "  \"title\": \"\\uc54c\\ub77c\\ub518 \\uc0c1\\ud488\\uc815\\ubcf4 - \\uc544\\uc8fc \\uc791\\uc740 \\uc2b5\\uad00\\uc758 \\ud798\",\n" +
            "  \"link\": \"d=182285146\",\n" +
            "  \"pubDate\": \"Tue, 07 May 2019 02:32:34 GMT\",\n" +
            "  \"imageUrl\": \"ht\\/logo.jpg\",\n" +
            "  \"totalResults\": 1,\n" +
            "  \"startIndex\": 1,\n" +
            "  \"itemsPerPage\": 1,\n" +
            "  \"query\": \"itemid=182285146\",\n" +
            "  \"searchCategoryId\": 0,\n" +
            "  \"searchCategoryName\": \"\",\n" +
            "  \"item\": [\n" +
            "    {\n" +
            "      \"title\": \"\\uc544\\uc8fc \\uc791\\uc740 \\uc2b5\\uad00\\uc758 \\ud798 - \\ucd5c\\uace0\\uc758 \\ubcc0\\ud654\\ub294 \\uc5b4\\ub5bb\\uac8c \\ub9cc\\ub4e4\\uc5b4\\uc9c0\\ub294\\uac00\",\n" +
            "      \"link\": \"api\",\n" +
            "      \"author\": \"\\uc81c\\uc784\\uc2a4 \\ud074\\ub9ac\\uc5b4 \\uc9c0\\uc74c, \\uc774\\ud55c\\uc774 \\uc62e\\uae40\",\n" +
            "      \"pubDate\": \"2019-02-25\",\n" +
            "      \"description\": \"\\uc800\\uc790\\uc758 \\uc0dd\\uc0dd\\ud55c \\uacbd\\ud5d8\\uacfc \\uc0dd\\ubb3c\\ud559, \\ub1cc\\uacfc\\ud559, \\uc2ec\\ub9ac\\ud559\\uc758 \\ucd5c\\uc2e0 \\uc5f0\\uad6c \\uacb0\\uacfc\\ub97c \\uc9d1\\uc57d\\ud574\\uc11c \\uc2b5\\uad00 \\ud558\\ub098\\ub85c \\uc778\\uc0dd\\uc744 \\ubcc0\\ud654\\uc2dc\\ud0ac \\uc218 \\uc788\\ub294 \\ub178\\ud558\\uc6b0\\ub97c \\uc81c\\uc2dc\\ud55c\\ub2e4. \\uc5b4\\ub5a4 \\uc2b5\\uad00\\uc744 \\uc790\\uc2e0\\uc758 \\ubb34\\uae30\\ub85c \\ub9cc\\ub4e4\\uae30 \\uc704\\ud574\\uc11c\\ub294 \\ub531 \\ub124 \\uac00\\uc9c0\\ub9cc \\uae30\\uc5b5\\ud558\\uba74 \\ub41c\\ub2e4. \\ubc14\\ub85c \\u2018\\ud589\\ub3d9 \\ubcc0\\ud654\\uc758 4\\uac00\\uc9c0 \\ubc95\\uce59\\u2019\\uc778\\ub370, \\ubaa8\\ub4e0 \\uc2b5\\uad00\\uc740 \\ubd84\\uba85\\ud558\\uace0(\\uc81c1\\ubc95\\uce59), \\ub9e4\\ub825\\uc801\\uc774\\uace0(\\uc81c2\\ubc95\\uce59), \\uc27d\\uace0(\\uc81c3\\ubc95\\uce59), \\ub9cc\\uc871\\uc2a4\\ub7ec\\uc6cc\\uc57c(\\uc81c4\\ubc95\\uce59) \\ud55c\\ub2e4\\ub294 \\uac83\\uc774\\ub2e4.\",\n" +
            "      \"creator\": \"an\",\n" +
            "      \"isbn\": \"K832534441\",\n" +
            "      \"isbn13\": \"9791162540640\",\n" +
            "      \"itemId\": 182285146,\n" +
            "      \"priceSales\": 14400,\n" +
            "      \"priceStandard\": 16000,\n" +
            "      \"stockStatus\": \"\",\n" +
            "      \"mileage\": 800,\n" +
            "      \"cover\": \"http1_1.jpg\",\n" +
            "      \"categoryId\": 70216,\n" +
            "      \"categoryName\": \"\\uad6d\\ub0b4\\ub3c4\\uc11c>\\uc790\\uae30\\uacc4\\ubc1c>\\uc131\\uacf5>\\uc131\\uacf5\\ud559\",\n" +
            "      \"publisher\": \"\\ube44\\uc988\\ub2c8\\uc2a4\\ubd81\\uc2a4\",\n" +
            "      \"customerReviewRank\": 9,\n" +
            "      \"bookinfo\": {\n" +
            "        \"subTitle\": \"\\ucd5c\\uace0\\uc758 \\ubcc0\\ud654\\ub294 \\uc5b4\\ub5bb\\uac8c \\ub9cc\\ub4e4\\uc5b4\\uc9c0\\ub294\\uac00\",\n" +
            "        \"originalTitle\": \"Atomic Habits (2018\\ub144)\",\n" +
            "        \"itemPage\": 360,\n" +
            "        \"toc\": \"<p><B>Prologue \\uc0c8\\ub85c\\uc6b4 \\uc0b6\\uc758 \\uc2dc\\uc791<\\/B><BR>\\n\\uc778\\uc0dd\\uc758 \\ub098\\ub77d\\uc5d0\\uc11c \\ube60\\uc838\\ub098\\uc624\\ub2e4 \\uff5c \\ub180\\ub78d\\uace0\\ub3c4 \\uc5c4\\uccad\\ub09c \\ubcc0\\ud654\\ub4e4 <BR>\\n<BR>\\n<B>Part 1. \\uc544\\uc8fc \\uc791\\uc740 \\uc2b5\\uad00\\uc774 \\ub9cc\\ub4dc\\ub294 \\uadf9\\uc801\\uc778 \\ubcc0\\ud654<\\/B><BR>\\nChapter 01. \\ud3c9\\ubc94\\ud588\\ub358 \\uc120\\uc218\\ub4e4\\uc740 \\uc5b4\\ub5bb\\uac8c \\uc138\\uacc4 \\ucd5c\\uace0\\uac00 \\ub418\\uc5c8\\uc744\\uae4c <BR>\\n\\ub9e4\\uc77c 1\\ud37c\\uc13c\\ud2b8\\uc529 \\ub2ec\\ub77c\\uc84c\\uc744 \\ubfd0\\uc778\\ub370 \\uff5c \\ub099\\ub2f4\\uc758 \\uace8\\uc9dc\\uae30\\ub97c \\uacac\\ub38c\\ub77c \\uff5c \\ubaa9\\ud45c \\ub530\\uc708 \\uc4f0\\ub808\\uae30\\ud1b5\\uc5d0 \\ub358\\uc838\\ubc84\\ub9ac\\uae30 \\uff5c \\ubc14\\ubcf4\\uc57c, \\ubb38\\uc81c\\ub294 \\uc2dc\\uc2a4\\ud15c\\uc774\\uc57c<BR>\\n<BR>\\nChapter 02. \\uc815\\uccb4\\uc131, \\uc0ac\\ub78c\\uc744 \\uc6c0\\uc9c1\\uc774\\ub294 \\uac00\\uc7a5 \\ud070 \\ube44\\ubc00 <BR>\\n\\uc778\\uc0dd\\uc744 \\ubc14\\uafb8\\ub294 \\ub450 \\uac00\\uc9c0 \\uc9c8\\ubb38 \\uff5c \\uc2b5\\uad00\\uc740 \\uc790\\uc874\\uac10\\uc774\\ub2e4<BR>\\n<BR>\\nChapter 03. \\ubb34\\uc5c7\\uc774\\ub4e0 \\uc27d\\uac8c, \\uc7ac\\ubc0c\\uac8c, \\ub2e8\\uc21c\\ud558\\uac8c <BR>\\n\\uc778\\uc0dd\\uc758 \\ud6a8\\uc728\\uc131\\uc744 \\uacb0\\uc815\\ud558\\ub294 \\uac83 \\uff5c \\ub3c4\\ub11b \\ud558\\ub098 \\uc0ac \\uba39\\uc744 \\ub54c \\ubc8c\\uc5b4\\uc9c0\\ub294 \\uc77c \\uff5c \\ud589\\ub3d9 \\ubcc0\\ud654\\uc758 \\ub124 \\uac00\\uc9c0 \\ubc95\\uce59<BR>\\n<BR>\\n<B>Part 2. \\uccab \\ubc88\\uc9f8 \\ubc95\\uce59, \\ubd84\\uba85\\ud574\\uc57c \\ub2ec\\ub77c\\uc9c4\\ub2e4<\\/B><BR>\\nChapter 04. \\uc778\\uc0dd\\uc740 \\uc0dd\\uac01\\ud558\\ub294 \\ub9cc\\ud07c \\ubc14\\ub010\\ub2e4 <BR>\\n\\uc88b\\uc740 \\uc2b5\\uad00 vs. \\ub098\\uc05c \\uc2b5\\uad00<BR>\\n<BR>\\nChapter 05. \\uc544\\uc8fc \\uad6c\\uccb4\\uc801\\uc73c\\ub85c \\ucabc\\uac1c\\uace0 \\ubd99\\uc5ec\\ub77c <BR>\\n\\uc2b5\\uad00\\uc774 \\uc2dc\\uac04\\uacfc \\uc7a5\\uc18c\\ub97c \\ub9cc\\ub0ac\\uc744 \\ub54c <BR>\\n<BR>\\nChapter 06. \\ud658\\uacbd\\uc774 \\ud589\\ub3d9\\uc744 \\uacb0\\uc815\\ud55c\\ub2e4  <BR>\\n\\uc544\\uce68\\ub9c8\\ub2e4 \\uc0ac\\uacfc\\ub97c \\uba39\\uac8c \\ub41c \\ube44\\ubc00 \\uff5c \\uc65c \\uc9d1\\ubcf4\\ub2e4 \\uc2a4\\ud0c0\\ubc85\\uc2a4\\uc5d0\\uc11c \\uacf5\\ubd80\\uac00 \\ub354 \\uc798 \\ub420\\uae4c<BR>\\n<BR>\\nChapter 07. \\ub098\\uc05c \\uc2b5\\uad00 \\ud53c\\ud558\\uae30 \\uae30\\uc220 <BR>\\n<BR>\\n<B>Part 3. \\ub450 \\ubc88\\uc9f8 \\ubc95\\uce59, \\ub9e4\\ub825\\uc801\\uc774\\uc5b4\\uc57c \\ub2ec\\ub77c\\uc9c4\\ub2e4<\\/B><BR>\\nChapter 08. \\uc65c \\uc5b4\\ub5a4 \\uc2b5\\uad00\\uc740 \\ub354 \\ud558\\uace0 \\uc2f6\\uc744\\uae4c <BR>\\n\\u2018\\uc88b\\uc544\\ud558\\ub294 \\uac83\\u2019\\ubcf4\\ub2e4 \\u2018\\uc6d0\\ud558\\ub294 \\uac83\\u2019\\uc5d0 \\ub04c\\ub9b0\\ub2e4 \\uff5c \\uc77c\\uc0c1\\uc5d0 \\uc0c8\\ub85c\\uc6b4 \\uc2b5\\uad00 \\ub367\\ubd99\\uc774\\uae30<BR>\\n<BR>\\nChapter 09. \\uc65c \\uc8fc\\uc704 \\uc0ac\\ub78c\\uc5d0 \\ub530\\ub77c \\ub0b4 \\uc2b5\\uad00\\uc774 \\ubcc0\\ud560\\uae4c <BR>\\n\\uc6b0\\ub9ac\\uc758 \\ud589\\ub3d9\\uc744 \\uacb0\\uc815\\uc9d3\\ub294 \\uc138 \\uc9d1\\ub2e8<BR>\\n<BR>\\nChapter 10. \\ub098\\uc05c \\uc2b5\\uad00\\ub3c4 \\uc990\\uac81\\uac8c \\uace0\\uce60 \\uc218 \\uc788\\uc744\\uae4c <BR>\\n\\ub2f9\\uc2e0\\uc774 \\uac8c\\uc784\\uc744 \\ud558\\ub294 \\uc9c4\\uc9dc \\uc774\\uc720 \\uff5c \\uae0d\\uc815\\uc801\\uc778 \\ub290\\ub08c\\uc744 \\ub9cc\\ub4e4\\uace0 \\uc2f6\\ub2e4\\uba74<BR>\\n<BR>\\n<B>Part 4. \\uc138 \\ubc88\\uc9f8 \\ubc95\\uce59, \\uc26c\\uc6cc\\uc57c \\ub2ec\\ub77c\\uc9c4\\ub2e4<\\/B><BR>\\nChapter 11. 1\\ub9cc \\uc2dc\\uac04\\uc758 \\ubc95\\uce59\\uc740 \\ud2c0\\ub838\\ub2e4 <BR>\\n\\ub9ce\\uc774 \\ud574\\uc57c \\ud560\\uae4c, \\uc624\\ub798 \\ud574\\uc57c \\ud560\\uae4c<BR>\\n<BR>\\nChapter 12. \\uc6ec\\ub9cc\\ud558\\uba74 \\uc27d\\uac8c \\uac11\\uc2dc\\ub2e4 <BR>\\n\\uad6c\\uae00\\uacfc \\uc544\\ub9c8\\uc874\\uc5d0\\uc11c \\ud30c\\ub294 \\uac83 \\uff5c \\ub178\\ub825\\uc740 \\ucd5c\\uc18c\\ub85c, \\uc131\\uacfc\\ub294 \\ucd5c\\ub300\\ub85c<BR>\\n<BR>\\nChapter 13. \\ubcc0\\ud654\\ub97c \\uc704\\ud55c \\ucd5c\\uc18c\\ud55c\\uc758 \\uc2dc\\uac04<BR>\\n\\ud130\\ubb34\\ub2c8\\uc5c6\\uc744 \\ub9cc\\ud07c \\uc0ac\\uc18c\\ud560 \\uac83<BR>\\n<BR>\\nChapter 14. \\uadf8\\ub4e4\\uc740 \\uc5b4\\ub5bb\\uac8c \\ub098\\uc05c \\uc2b5\\uad00\\uc744 \\ubc84\\ub9ac\\ub294\\uac00<BR>\\n\\ub611\\ub611\\ud55c \\uc0ac\\ub78c\\ub4e4\\uc758 \\uc2b5\\uad00 \\uad00\\ub9ac\\ubc95<BR>\\n<BR>\\n<B>Part 5. \\ub124 \\ubc88\\uc9f8 \\ubc95\\uce59, \\ub9cc\\uc871\\uc2a4\\ub7ec\\uc6cc\\uc57c \\ub2ec\\ub77c\\uc9c4\\ub2e4<\\/B><BR>\\nChapter 15. \\uc7ac\\ubbf8\\uc640 \\ubcf4\\uc0c1 \\ub450 \\ub9c8\\ub9ac \\ud1a0\\ub07c\\ub97c \\uc7a1\\ub294 \\ubc95 <BR>\\n\\ub208\\uc55e\\uc758 \\ub9cc\\uc871\\uc774 \\uc6b0\\uc120\\uc778 \\uc0ac\\ub78c\\ub4e4 \\uff5c \\uc544\\uc8fc \\uc791\\uc740 \\ubcf4\\uc0c1\\uc758 \\ud798 <BR>\\n<BR>\\nChapter 16. \\uc5b4\\ub5bb\\uac8c \\ub9e4\\uc77c \\ubc18\\ubcf5\\ud560 \\uac83\\uc778\\uac00 <BR>\\n\\ubca4\\uc800\\ubbfc \\ud504\\ub7ad\\ud074\\ub9b0\\uc758 \\ub9c8\\uc9c0\\ub9c9 \\uc120\\ubb3c \\uff5c \\uc2b5\\uad00\\uc740 \\ub450 \\ubc88\\uc9f8 \\uc2e4\\uc218\\uc5d0\\uc11c \\ubb34\\ub108\\uc9c4\\ub2e4<BR>\\n<BR>\\nChapter 17. \\ub204\\uad70\\uac00 \\ub2f9\\uc2e0\\uc744 \\uc9c0\\ucf1c\\ubcf4\\uace0 \\uc788\\ub2e4 <BR>\\n\\uc5b4\\ub5bb\\uac8c \\uc548\\uc804\\ubca8\\ud2b8\\ub294 \\uc138\\uacc4\\uc778\\uc758 \\uc2b5\\uad00\\uc774 \\ub410\\uc744\\uae4c<BR>\\n<BR>\\n<B>Part 6. \\ucd5c\\uace0\\uc758 \\uc2b5\\uad00\\uc740 \\uc5b4\\ub5bb\\uac8c \\ub9cc\\ub4e4\\uc5b4\\uc9c0\\ub294\\uac00<\\/B><BR>\\nChapter 18. \\uc2b5\\uad00\\uc5d0\\ub3c4 \\uc801\\uc131\\uc774 \\uc788\\ub2e4 <BR>\\n\\ub098\\uc5d0\\uac8c \\ub531 \\ub9de\\ub294 \\uc2b5\\uad00\\uc740 \\ub530\\ub85c \\uc788\\ub2e4 \\uff5c \\uc798\\ud558\\ub294 \\uc77c\\uacfc \\uc88b\\uc544\\ud558\\ub294 \\uc77c \\uc0ac\\uc774 \\uff5c \\uc720\\uc804\\uc790, \\ubc14\\uafc0 \\uc218 \\uc5c6\\ub2e4\\uba74 \\uc774\\uc6a9\\ud558\\ub77c<BR>\\n<BR>\\nChapter 19. \\uacc4\\uc18d \\ud574\\ub0b4\\ub294 \\ud798\\uc740 \\uc5b4\\ub514\\uc11c \\uc624\\ub294\\uac00 <BR>\\n\\uc804\\ubb38\\uac00\\uc640 \\uc544\\ub9c8\\ucd94\\uc5b4\\uc758 \\ucc28\\uc774<BR>\\n<BR>\\nChapter 20. \\uc2b5\\uad00\\uc758 \\ubc18\\uaca9 <BR>\\n\\uc5ed\\uc0ac\\uc0c1 \\ucd5c\\uace0\\uc758 \\ud300\\uc774 \\uc2e4\\ud328 \\ub05d\\uc5d0 \\uc5bb\\uc740 \\uad50\\ud6c8 \\uff5c \\ub2e4\\ub978 \\uc0b6\\uc5d0\\ub3c4 \\uae38\\uc740 \\uc788\\ub2e4<BR>\\n<BR>\\n<B>Epilogue 100\\ubc88\\ub9cc \\ubc18\\ubcf5\\ud558\\uba74 \\uadf8\\uac8c \\ub2f9\\uc2e0\\uc758 \\ubb34\\uae30\\uac00 \\ub41c\\ub2e4<\\/B> <BR>\\n\\ubd80\\ub85d 1 \\uc0ac\\ub78c\\ub4e4\\uc758 \\ud589\\ub3d9\\uc5d0 \\uad00\\ud55c 18\\uac00\\uc9c0 \\uc9c4\\uc2e4 <BR>\\n\\ubd80\\ub85d 2 \\uc774 \\ucc45\\uc744 \\uc990\\uac81\\uac8c \\uc77d\\uc5c8\\ub2e4\\uba74 <BR>\\n\\uc8fc\\uc11d<\\/p>\",\n" +
            "        \"letslookimg\": [\n" +
            "          \".jpg\",\n" +
            "          \"bs.jpg\",\n" +
            "          \"t1s.jpg\"\n" +
            "        ],\n" +
            "        \"authors\": [\n" +
            "          {\n" +
            "            \"authorType\": \"author\",\n" +
            "            \"authorid\": 6259995,\n" +
            "            \"desc\": \"\\uc9c0\\uc740\\uc774\",\n" +
            "            \"name\": \"\\uc81c\\uc784\\uc2a4 \\ud074\\ub9ac\\uc5b4\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"authorType\": \"translator\",\n" +
            "            \"authorid\": 1606787,\n" +
            "            \"desc\": \"\\uc62e\\uae34\\uc774\",\n" +
            "            \"name\": \"\\uc774\\ud55c\\uc774\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"ebookList\": [\n" +
            "          {\n" +
            "            \"itemId\": 183908185,\n" +
            "            \"isbn\": \"EK83253444\",\n" +
            "            \"priceSales\": 11200,\n" +
            "            \"link\": \"http:?ItemId=183908185\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void getResults() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AladinApiResult a =  mapper.readValue(json, AladinApiResult.class);
        AladinBookDTO bookDTO = a.getResult();
        assertEquals(bookDTO.getTitle(),"\uc544\uc8fc \uc791\uc740 \uc2b5\uad00\uc758 \ud798");
        assertEquals(bookDTO.getIsbn(),9791162540640l);
        assertEquals(bookDTO.getPubDate(),"02/25/2019");
        assertEquals(bookDTO.getPrice(), 16000);

    }
}