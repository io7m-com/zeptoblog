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

import com.io7m.quarrel.core.QApplication;
import com.io7m.quarrel.core.QApplicationMetadata;
import com.io7m.quarrel.core.QApplicationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The main entry point.
 */

public final class ZBlogMain implements Runnable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(ZBlogMain.class);

  private final List<String> args;
  private final QApplicationType application;
  private int exitCode;

  /**
   * The main entry point.
   *
   * @param inArgs Command-line arguments
   */

  public ZBlogMain(
    final String[] inArgs)
  {
    this.args =
      Objects.requireNonNull(List.of(inArgs), "Command line arguments");

    final var metadata =
      new QApplicationMetadata(
        "zeptoblog",
        "com.io7m.zeptoblog",
        version(),
        build(),
        "The looseleaf server.",
        Optional.of(URI.create("https://www.io7m.com/software/zeptoblog/"))
      );

    final var builder = QApplication.builder(metadata);
    builder.allowAtSyntax(true);
    builder.addCommand(new ZBlogCmdCompile());
    builder.addCommand(new ZBlogCmdFormats());
    builder.addCommand(new ZBlogCmdGenerators());

    this.application = builder.build();
    this.exitCode = 0;
  }

  private static String build()
  {
    // CHECKSTYLE:OFF
    try (var st = ZBlogMain.class.getResourceAsStream(
      "/com/io7m/zeptoblog/cmdline/build.txt")) {
      return new String(st.readAllBytes(), StandardCharsets.UTF_8).trim();
    } catch (final Exception e) {
      return "UNKNOWN";
    }
    // CHECKSTYLE:ON
  }

  private static String version()
  {
    // CHECKSTYLE:OFF
    try (var st = ZBlogMain.class.getResourceAsStream(
      "/com/io7m/zeptoblog/cmdline/version.txt")) {
      return new String(st.readAllBytes(), StandardCharsets.UTF_8).trim();
    } catch (final Exception e) {
      return "0.0.0";
    }
    // CHECKSTYLE:ON
  }

  /**
   * The main entry point.
   *
   * @param args Command line arguments
   */

  public static void main(
    final String[] args)
  {
    System.exit(mainExitless(args));
  }

  /**
   * The main (exitless) entry point.
   *
   * @param args Command line arguments
   *
   * @return The exit code
   */

  public static int mainExitless(
    final String[] args)
  {
    final ZBlogMain cm = new ZBlogMain(args);
    cm.run();
    return cm.exitCode();
  }

  /**
   * @return The program exit code
   */

  public int exitCode()
  {
    return this.exitCode;
  }

  @Override
  public void run()
  {
    this.exitCode = this.application.run(LOG, this.args).exitCode();
  }

  @Override
  public String toString()
  {
    return String.format(
      "[ZBlogMain 0x%s]",
      Long.toUnsignedString(System.identityHashCode(this), 16)
    );
  }
}
