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

import com.io7m.zeptoblog.core.ZBlogConfiguration;
import com.io7m.zeptoblog.core.ZBlogConfigurations;
import com.io7m.zeptoblog.core.ZBlogPost;
import com.io7m.zeptoblog.core.ZBlogPostGeneratorType;
import com.io7m.zeptoblog.core.ZError;
import javaslang.Tuple2;
import javaslang.collection.Seq;
import javaslang.collection.SortedMap;
import javaslang.control.Validation;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public abstract class ZGlossaryGeneratorContract
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(ZGlossaryGeneratorContract.class);
  }

  protected abstract ZBlogPostGeneratorType createGenerator();

  protected abstract FileSystem createFilesystem();

  @Test
  public final void testMissingSourcePath()
    throws Exception
  {
    try (final FileSystem fs = this.createFilesystem()) {
      final ZBlogPostGeneratorType gen = this.createGenerator();
      final ZBlogConfiguration config = baseConfiguration(fs);
      final Properties props = new Properties();
      final Validation<Seq<ZError>, SortedMap<Path, ZBlogPost>> r =
        gen.generate(config, props);

      dumpResult(r);
      Assert.assertTrue(r.isInvalid());
      Assert.assertThat(
        r.getError().get(0).message(),
        StringContains.containsString("source_dir"));
    }
  }

  @Test
  public final void testMissingOutputPath()
    throws Exception
  {
    try (final FileSystem fs = this.createFilesystem()) {
      final ZBlogPostGeneratorType gen = this.createGenerator();
      final ZBlogConfiguration config = baseConfiguration(fs);
      final Properties props = new Properties();
      props.setProperty("com.io7m.zeptoblog.glossary.source_dir", "/glossary");
      final Validation<Seq<ZError>, SortedMap<Path, ZBlogPost>> r =
        gen.generate(config, props);

      dumpResult(r);
      Assert.assertTrue(r.isInvalid());
      Assert.assertThat(
        r.getError().get(0).message(),
        StringContains.containsString("output_file"));
    }
  }

  @Test
  public final void testEmpty()
    throws Exception
  {
    try (final FileSystem fs = this.createFilesystem()) {
      Files.createDirectories(fs.getPath("/glossary"));

      final ZBlogPostGeneratorType gen = this.createGenerator();
      final ZBlogConfiguration config = baseConfiguration(fs);
      final Properties props = new Properties();
      props.setProperty("com.io7m.zeptoblog.glossary.source_dir", "/glossary");
      props.setProperty("com.io7m.zeptoblog.glossary.output_file", "glossary.zbp");
      final Validation<Seq<ZError>, SortedMap<Path, ZBlogPost>> r =
        gen.generate(config, props);

      dumpResult(r);
      Assert.assertTrue(r.isValid());

      final SortedMap<Path, ZBlogPost> glossary = r.get();
      Assert.assertEquals(1L , (long) glossary.size());
    }
  }

  private static <T> void dumpResult(
    final Validation<Seq<ZError>, T> result)
  {
    if (result.isInvalid()) {
      result.getError().forEach(e -> LOG.error("{}", e));
    }
  }

  private static ZBlogConfiguration baseConfiguration(
    final FileSystem fs)
  {
    final Properties properties = ZBlogConfigurationsTest.baseProperties();


    final Validation<Seq<ZError>, ZBlogConfiguration> result =
      ZBlogConfigurations.fromProperties(
        fs.getPath("/config"), properties);
    return result.get();
  }
}
