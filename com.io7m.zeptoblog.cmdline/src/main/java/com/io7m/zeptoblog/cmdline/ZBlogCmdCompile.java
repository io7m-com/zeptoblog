/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.zeptoblog.cmdline;

import com.io7m.jproperties.JProperties;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QCommandType;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType;
import com.io7m.quarrel.ext.logback.QLogback;
import com.io7m.zeptoblog.core.ZBlog;
import com.io7m.zeptoblog.core.ZBlogConfiguration;
import com.io7m.zeptoblog.core.ZBlogConfigurations;
import com.io7m.zeptoblog.core.ZBlogParserProvider;
import com.io7m.zeptoblog.core.ZBlogParserProviderType;
import com.io7m.zeptoblog.core.ZBlogParserType;
import com.io7m.zeptoblog.core.ZBlogPostGeneratorExecutor;
import com.io7m.zeptoblog.core.ZBlogPostGeneratorExecutorType;
import com.io7m.zeptoblog.core.ZBlogRendererProvider;
import com.io7m.zeptoblog.core.ZBlogRendererProviderType;
import com.io7m.zeptoblog.core.ZBlogRendererType;
import com.io7m.zeptoblog.core.ZError;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Compile a blog.
 */

public final class ZBlogCmdCompile implements QCommandType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(ZBlogCmdCompile.class);

  private final QCommandMetadata metadata;

  private static final QParameterNamed1<Path> FILE =
    new QParameterNamed1<>(
      "--file",
      List.of(),
      new QStringType.QConstant("The configuration file."),
      Optional.empty(),
      Path.class
    );

  /**
   * Compile a blog.
   */

  public ZBlogCmdCompile()
  {
    this.metadata = new QCommandMetadata(
      "compile",
      new QStringType.QConstant("Compile a blog."),
      Optional.empty()
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return QLogback.plusParameters(List.of(FILE));
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final File configFile =
      context.parameterValue(FILE).toFile();
    final Properties configProps =
      JProperties.fromFile(configFile);
    final Validation<Seq<ZError>, ZBlogConfiguration> cr =
      ZBlogConfigurations.fromProperties(configFile.toPath(), configProps);

    if (!cr.isValid()) {
      cr.getError().forEach(ZBlogCmdCompile::show);
      return QCommandStatus.FAILURE;
    }

    final ZBlogConfiguration config = cr.get();
    final ZBlogPostGeneratorExecutorType exec = new ZBlogPostGeneratorExecutor();
    final Validation<Seq<ZError>, Void> er = exec.executeAll(config);
    if (!er.isValid()) {
      er.getError().forEach(ZBlogCmdCompile::show);
      return QCommandStatus.FAILURE;
    }

    final ZBlogParserProviderType blogProvider = new ZBlogParserProvider();
    final ZBlogParserType blogParser = blogProvider.createParser(config);
    final Validation<Seq<ZError>, ZBlog> br = blogParser.parse();
    if (!br.isValid()) {
      br.getError().forEach(ZBlogCmdCompile::show);
      return QCommandStatus.FAILURE;
    }

    final ZBlogRendererProviderType blogWriterProvider =
      new ZBlogRendererProvider();
    final ZBlogRendererType blogWriter =
      blogWriterProvider.createRenderer(config);

    final ZBlog blog = br.get();
    final Validation<Seq<ZError>, Void> wr = blogWriter.render(blog);
    if (!wr.isValid()) {
      wr.getError().forEach(ZBlogCmdCompile::show);
      return QCommandStatus.FAILURE;
    }

    LOG.debug("done");
    return QCommandStatus.SUCCESS;
  }

  private static void show(
    final ZError error)
  {
    LOG.error(error.show());
    error.error().ifPresent(ex -> LOG.error("exception: ", ex));
  }

  @Override
  public QCommandMetadata metadata()
  {
    return this.metadata;
  }
}
