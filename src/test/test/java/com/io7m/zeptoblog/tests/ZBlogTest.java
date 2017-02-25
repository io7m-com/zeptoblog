/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.zeptoblog.tests;

import com.io7m.zeptoblog.ZBlog;
import com.io7m.zeptoblog.ZBlogPost;
import javaslang.Tuple2;
import javaslang.collection.Seq;
import javaslang.collection.TreeMap;
import javaslang.collection.TreeSet;
import javaslang.collection.Vector;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

public final class ZBlogTest
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(ZBlogTest.class);
  }

  @Test
  public void testEmpty()
  {
    final ZBlog blog = ZBlog.of("title", TreeMap.empty());
    Assert.assertEquals("title", blog.title());
    Assert.assertTrue(blog.posts().isEmpty());
    Assert.assertTrue(blog.postsByDate().isEmpty());
    Assert.assertTrue(blog.postsByPage(10).isEmpty());
  }

  @Test
  public void testOneHundredPosts()
  {
    final Random random = new Random();

    TreeMap<String, ZBlogPost> posts = TreeMap.empty();
    for (int index = 0; index < 100; ++index) {
      final byte[] data = new byte[32];
      random.nextBytes(data);
      final String title = Hex.encodeHexString(data);
      random.nextBytes(data);
      final String body = Hex.encodeHexString(data);
      final ZonedDateTime date =
        ZonedDateTime.of(
          random.nextInt(2020),
          random.nextInt(12) + 1,
          random.nextInt(20) + 1,
          random.nextInt(23) + 1,
          random.nextInt(59) + 1,
          random.nextInt(59) + 1,
          random.nextInt(100),
          ZoneId.of("UTC"));

      final ZBlogPost post =
        ZBlogPost.of(title, date, TreeSet.empty(), body, Paths.get("/"));
      posts = posts.put(post.id(), post);
    }

    final ZBlog blog = ZBlog.of("title", posts);
    Assert.assertEquals("title", blog.title());
    Assert.assertEquals(posts, blog.posts());
    Assert.assertEquals(100L, (long) blog.posts().size());

    for (final Tuple2<String, ZBlogPost> pair : blog.posts()) {
      Assert.assertEquals(pair._2, blog.postsByDate().get(pair._2.date()).get());
    }

    Vector<ZBlogPost> all = Vector.empty();
    for (final Tuple2<Integer, Seq<ZBlogPost>> pair : blog.postsByPage(10)) {
      final Seq<ZBlogPost> page = pair._2;
      LOG.debug("page[{}] {}", pair._1, Integer.valueOf(page.size()));

      Assert.assertEquals(10L, (long) page.size());
      all = all.appendAll(page);
    }

    for (final Tuple2<String, ZBlogPost> pair : blog.posts()) {
      Assert.assertTrue(all.contains(pair._2));
    }
  }
}
