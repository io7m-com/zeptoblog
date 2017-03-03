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

package com.io7m.zeptoblog.core;

import com.io7m.jnull.NullCheck;
import javaslang.collection.Seq;
import javaslang.control.Validation;
import org.osgi.service.component.annotations.Component;

import java.time.format.DateTimeFormatter;

/**
 * The default implementation of the {@link ZBlogPostSerializerType} interface.
 */

@Component(service = ZBlogPostSerializerType.class)
public final class ZBlogPostSerializer implements ZBlogPostSerializerType
{
  private final DateTimeFormatter formatter;

  /**
   * Construct a serializer.
   */

  public ZBlogPostSerializer()
  {
    this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
  }

  @Override
  public Validation<Seq<ZError>, String> serialize(
    final ZBlogPost post)
  {
    NullCheck.notNull(post, "post");

    final StringBuilder sb = new StringBuilder(128);
    sb.append("title ");
    sb.append(post.title());
    sb.append(System.lineSeparator());

    post.date().ifPresent(date -> {
      sb.append("date ");
      sb.append(date.format(this.formatter));
      sb.append(System.lineSeparator());
    });

    sb.append("format ");
    sb.append(post.body().format());
    sb.append(System.lineSeparator());

    sb.append(System.lineSeparator());
    sb.append(post.body().text());

    return Validation.valid(sb.toString());
  }
}
