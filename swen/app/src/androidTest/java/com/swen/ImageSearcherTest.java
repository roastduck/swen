package com.swen;

import org.junit.Before;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.*;

public class ImageSearcherTest
{
    ImageSearcher ims;

    @Before
    public void setUp() {
        ims = new ImageSearcher(10);
    }

    @Test
    public void testSearch() throws Exception {
        String keyword = "百态：手机之瘾 孤独之影";
        String url = ims.search(keyword);
        HttpURLConnection conn = (HttpURLConnection)(new URL(url).openConnection());
        assertEquals(200, conn.getResponseCode());
        Map<String, List<String>> map = conn.getHeaderFields();
        assertTrue(map.get("Content-Type").get(0).contains("image"));
    }
}