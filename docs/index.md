---
layout: default
title: Introduction
---

Thymeleaf Layout Dialect
========================

[![Build Status](https://travis-ci.org/ultraq/thymeleaf-layout-dialect.svg?branch=master)](https://travis-ci.org/ultraq/thymeleaf-layout-dialect)
[![GitHub Release](https://img.shields.io/github/release/ultraq/thymeleaf-layout-dialect.svg?maxAge=3600)](https://github.com/ultraq/thymeleaf-layout-dialect/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/nz.net.ultraq.thymeleaf/thymeleaf-layout-dialect.svg?maxAge=3600)](http://search.maven.org/#search|ga|1|g%3A%22nz.net.ultraq.thymeleaf%22%20AND%20a%3A%22thymeleaf-layout-dialect%22)
[![License](https://img.shields.io/github/license/ultraq/thymeleaf-layout-dialect.svg?maxAge=2592000)](https://github.com/ultraq/thymeleaf-layout-dialect/blob/master/LICENSE.txt)

A dialect for Thymeleaf that lets you build layouts and reusable templates in
order to improve code reuse.

If you've ever used SiteMesh for your JSPs, or Facelets with JSFs, then the
concepts of this library will be very familiar to you.


Motivation
----------

After coming off a project that used JSFs and having a long hard look at my
personal website written in JSPs, I thought it was high time for a site redesign
that used a view technology that didn't make me want me to ╯°□°）╯︵ ┻━┻

I came across an article on this new thing called [Thymeleaf](http://www.thymeleaf.org/)
and how it's a fully-fledged JSP replacement.  I gave it a look, and in seconds
it restored my faith in Java web development; using the language of HTML to
create HTML, rather than trying to throw Java code into a place it doesn't
belong.

One problem though: I had been using SiteMesh to create reusable layouts, but
Thymeleaf offered no such alternative.  Some people had tried to shoehorn
SiteMesh into Thymeleaf, but with mixed results.  Seeing how extensible
Thymeleaf could be, I embarked on writing my own layout alternative for
Thymeleaf and, as soon as I got that working on my website, I shared the result
with the community in this project here.

My website still runs on Thymeleaf and the layout dialect, and uses every
feature mentioned in these docs.  In eating my own dogfood, I hope that the
layout dialect is robust enough for your own projects, from personal websites
like my own, to larger high-traffic web apps.  Anywhere Thymeleaf is suitable, I
hope the layout dialect can be suitable too :)
