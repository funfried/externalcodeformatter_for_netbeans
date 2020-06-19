Keep this project alive by supporting it: one-time [![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=926F5XBCTK2LQ&source=url) or become a [![Patreon!](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Fshieldsio-patreon.herokuapp.com%2Fuser?u=32336291&style=for-the-badge)](https://www.patreon.com/bePatron?u=32336291)

[![Linux Build Status](https://travis-ci.com/funfried/externalcodeformatter_for_netbeans.svg?branch=master)](https://travis-ci.com/funfried/externalcodeformatter_for_netbeans)
[![Windows Build Status](https://ci.appveyor.com/api/projects/status/kva6pnb5se94t1wu?svg=true)](https://ci.appveyor.com/project/funfried/externalcodeformatter-for-netbeans)
[![Coverage Status](https://coveralls.io/repos/github/funfried/externalcodeformatter_for_netbeans/badge.svg?branch=master)](https://coveralls.io/github/funfried/externalcodeformatter_for_netbeans?branch=master)
[![Code Coverage](https://codecov.io/gh/funfried/externalcodeformatter_for_netbeans/branch/master/graph/badge.svg)](https://codecov.io/gh/funfried/externalcodeformatter_for_netbeans)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7d825731274a4f4783e4203eb7cbf811)](https://www.codacy.com/manual/funfried/externalcodeformatter_for_netbeans?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=funfried/externalcodeformatter_for_netbeans&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.funfried.netbeans.plugins/externalcodeformatter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.funfried.netbeans.plugins/externalcodeformatter/)
![GitHub All Releases](https://img.shields.io/github/downloads/funfried/externalcodeformatter_for_netbeans/total)
![GitHub issues](https://img.shields.io/github/issues/funfried/externalcodeformatter_for_netbeans)
[![Eclipse Public License, Version 2.0](https://img.shields.io/badge/license-EPL%20v2.0-green)](http://funfried.github.io/externalcodeformatter_for_netbeans/licenses.html)
![Follow Me On Twitter](https://img.shields.io/twitter/follow/funfried84?style=social)

External Code Formatters for NetBeans
=====================================
(formerly known as Eclipse Java Code Formatter for NetBeans)

What is External Code Formatters Plugin for NetBeans
----------------------------------------------------
This plugin helps you to apply to a common code style in a team of Eclipse,
IntelliJ and NetBeans IDE users. You can use several external code formatters
inside the NetBeans IDE (see below), depending on whatever your co-workers use.
The original formatting engine of Eclipse, the Google Code Formatter, the Spring
Java Formatter or any other are embedded and allow you to format the source code
the same way as e.g. your co-workers do with their Eclipse IDE.

![Global settings](/src/site/resources/imgs/global.png)
![Project settings](/src/site/resources/imgs/project.png)

### Supported formatters
*   Java
    *   [Google Code Formatter](https://github.com/google/google-java-format)
    *   [Eclipse Java Code Formatter](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Fguide%2Fjdt_api_codeformatter.htm)
    *   [Spring Java Formatter](https://github.com/spring-io/spring-javaformat)

*   Javascript
    *   [Eclipse Javascript Code Formatter](https://help.eclipse.org/2019-12/index.jsp?topic=%2Forg.eclipse.wst.jsdt.doc%2Fstarted%2Foverview.html&cp%3D52_0)

*   XML
    *   [revelc.net XML Code Formatter](https://github.com/revelc/xml-formatter)
    *   [Jsoup XML Code Formatter](https://jsoup.org/)

*   Json
    *   [Jackson Json Code Formatter](https://github.com/FasterXML/jackson)

### Features
*   Global configuration and project specific configuration
*   On save action (Disabled by default)
*   Shows the used formatter in a notification (Disabled by default)
*   Supports profiles (since 1.6)
*   Supports format of selected text (since 1.7)
*   Preserve breakpoints (experimental) (since 1.8)
*   Code templates for @formatter:on/off (since 1.9)
*   Supports [Workspace Mechanic](https://code.google.com/a/eclipselabs.org/p/workspacemechanic/) configuration file (since 1.10)
*   Support configuration from .settings/org.eclipse.jdt.core.prefs (absolute and relative paths) (since 1.10)
*   On save action: Introduced option for formatting only the changed lines (since 1.10)
*   Support configuration of linefeed (since 1.10)
*   Support configuration of source level (since 1.10)
*   Support for macro invocation (since 1.12)
*   Support for guarded documents (documents that are created by the NetBeans GUI builder, guarded blocks are skipped of course, but everything in between can be formatted), only for Eclipse Java Code Formatter (since 1.13)
*   Support for Google code formatter (since 1.13)
*   Support for Spring Java formatter (since 1.14)
*   Support for Eclipse Javascript formatter (since 1.14)
*   Support for revelc.net XML formatter (since 1.14)
*   Support for Jsoup XML formatter (since 1.14)
*   Support for Jackson Json formatter (since 1.14)

Compatibility
-------------
Compatible with NetBeans 8.2+ and JDK8+.

Downloads
---------
You can find the download links [here](http://funfried.github.io/externalcodeformatter_for_netbeans/downloads.html).

Known issues
------------
Please check the open [GitHub Issues](/../../issues) and see [here](http://funfried.github.io/externalcodeformatter_for_netbeans/known_issues.html)

### Note
Please note that this plugin only provides support for formatting. Eclipse users may miss the application of save-actions like "adding @Override annotations" or "member sort order". Such AST-based transformations are not provided by this plugin.

Licensing
---------
This plugin is licensed under the [Eclipse Public License, Version 2.0](http://funfried.github.io/externalcodeformatter_for_netbeans/licenses.html).
This plugin uses third-party libraries, which are needed to provide its functionality, please check their licenses [here](https://funfried.github.io/externalcodeformatter_for_netbeans/dependencies.html).

The history of this project
---------------------------
*   The original idea is based on [this blog entry](http://epochcoder.blogspot.com/2013/08/import-eclipse-formatter-into-netbeans.html) from [Willie Scholtz](https://github.com/epochcoder)
*   [Geertjan Wielenga](https://github.com/geertjanw) made further investigations on that idea and [put everything together](https://blogs.oracle.com/geertjan/entry/eclipse_formatter_for_netbeans_ide) to create a first NetBeans Plugin out of this idea
*   This was then [forked](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/) by [Benno Markiewicz](https://github.com/markiewb)
*   [Benno Markiewicz](https://github.com/markiewb) stopped the development on the plugin and so [Geertjan Wielenga](https://github.com/geertjanw) forked it again and was looking for someone who wanted to take it over
*   I think I used this plugin since I use NetBeans, because in every company I was so far, I had exactly this circumstance that the formatting configuration was always there for Eclipse, but not for NetBeans. And because I prefer NetBeans over Eclipse I had to find a way to use NetBeans but the code style configuration from Eclipse. I'm happy that I can give back something to the community now by continuing the development of this plugin.

Feedback
--------
Provide defects, requests for enhancements or general feedback at the [GitHub issues](/../../issues) page

Changelog
---------
You can find the changelog of all versions [here](http://funfried.github.io/externalcodeformatter_for_netbeans/changes-report.html)

Remember
---------
Keep this project alive by supporting it: one-time [![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=926F5XBCTK2LQ&source=url) or become a [![Patreon!](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Fshieldsio-patreon.herokuapp.com%2Fuser?u=32336291&style=for-the-badge)](https://www.patreon.com/bePatron?u=32336291)
