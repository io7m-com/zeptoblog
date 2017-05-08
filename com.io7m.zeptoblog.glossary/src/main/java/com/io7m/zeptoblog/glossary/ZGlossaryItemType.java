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

package com.io7m.zeptoblog.glossary;

import com.io7m.jnull.NullCheck;
import com.io7m.zeptoblog.core.ZImmutableStyleType;
import javaslang.collection.Set;
import org.immutables.javaslang.encodings.JavaslangEncodingEnabled;
import org.immutables.value.Value;

import java.nio.file.Path;

/**
 * The type of glossary items.
 */

@ZImmutableStyleType
@JavaslangEncodingEnabled
@Value.Immutable
public interface ZGlossaryItemType extends Comparable<ZGlossaryItemType>
{
  /**
   * @return The glossary term
   */

  @Value.Parameter
  String term();

  /**
   * @return The terms related to this term.
   */

  @Value.Parameter
  Set<String> seeAlso();

  /**
   * @return The body of the glossary term.
   */

  @Value.Parameter
  ZGlossaryItemBody body();

  /**
   * @return The file containing the definition
   */

  @Value.Parameter
  Path path();

  @Override
  default int compareTo(
    final ZGlossaryItemType o)
  {
    return this.term().compareTo(NullCheck.notNull(o, "Other").term());
  }

  /**
   * @return A unique ID for the term
   */

  default String targetID()
  {
    return this.term().toLowerCase().replaceAll("\\s+", "_");
  }
}
