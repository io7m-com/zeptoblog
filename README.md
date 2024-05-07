zeptoblog
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.zeptoblog/com.io7m.zeptoblog.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.zeptoblog%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.zeptoblog/com.io7m.zeptoblog?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/zeptoblog/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/zeptoblog.svg?style=flat-square)](https://codecov.io/gh/io7m-com/zeptoblog)
![Java Version](https://img.shields.io/badge/21-java?label=java&color=007fff)

![com.io7m.zeptoblog](./src/site/resources/zeptoblog.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/zeptoblog/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/zeptoblog/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/zeptoblog/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/zeptoblog/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/zeptoblog/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/zeptoblog/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/zeptoblog/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/zeptoblog/actions?query=workflow%3Amain.windows.temurin.lts)|

## Usage

Create a configuration file (in [Java properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) format):

```
# The blog title
com.io7m.zeptoblog.title = Example Blog

# The blog source tree
com.io7m.zeptoblog.source_root = /home/someone/blog-src

# The output directory tree
com.io7m.zeptoblog.output_root = /tmp/blog-out

# The number of posts per page
com.io7m.zeptoblog.posts_per_page = 30

# The site URI
com.io7m.zeptoblog.site_uri = http://blog.io7m.com/

# The author information that will appear in Atom feeds
com.io7m.zeptoblog.author = blog@io7m.com

# The default format of blog posts (CommonMark, here)
com.io7m.zeptoblog.format_default = com.io7m.zeptoblog.commonmark
```

Create posts by creating files in `com.io7m.zeptoblog.source_root` with names ending in `.zbp`.

Posts must consist of a series of commands that specify the date
(in [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) format) and
title of the post, followed by an empty line, followed by the body
of the post in [CommonMark](http://commonmark.org/) format.

```
$ cat /home/someone/blog-src/2017/02/24/post.zbp
title An example post
date 2017-02-24T19:37:48+0000

Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur
efficitur sed nisi ac volutpat.

![Ladybug](/2017/02/24/ladybug.jpg)
```

Files can appear anywhere in `com.io7m.zeptoblog.source_root`,
including any subdirectory, and directory names do not carry any
specific meaning. Organizing posts by `year/month/day` is merely a
useful convention. Any files with names not ending in `.zbp` will
be copied unmodified to the output directory.

Compile the blog:

```
$ java -jar com.io7m.zeptoblog.cmdline-0.2.0-main.jar compile -config blog.conf
```

Sign pages with `gpg`:

```
$ find /tmp/blog-out -name '*.xhtml' -type f -exec gpg -a --detach-sign -u 'my key id' {} \;
```

Use [rsync](https://rsync.samba.org/) to copy `/tmp/blog-out` to a site.

## Real-world Examples

https://blog.io7m.com

