[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=926F5XBCTK2LQ&source=url)

[![Build Status](https://travis-ci.com/funfried/externalcodeformatter_for_netbeans.svg?branch=master)](https://travis-ci.com/funfried/externalcodeformatter_for_netbeans)

External Java Code Formatters for NetBeans
========================================
(formerly known as Eclipse Java Code Formatter for NetBeans)

## What is External Java Code Formatters Plugin for NetBeans?
This plugin helps you to apply to a common code style in a team of Eclipse JDT, IntelliJ
and NetBeans IDE users. Either you can use the [Google Code Formatter](https://github.com/google/google-java-format)
or the [Eclipse Java Code Formatter](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Fguide%2Fjdt_api_codeformatter.htm)
inside the NetBeans IDE, depending on whatever your co-workers use. The original formatting
engine of Eclipse and the Google Code Formatter are embedded and allow you to format the
source code the same way as e.g. your Eclipse co-workers do.

![Global settings](/src/site/resources/imgs/global.png)
![Project settings](/src/site/resources/imgs/project.png)

### Features
* Global configuration and project specific configuration
* On save action (Disabled by default)
* Shows the used formatter in a notification (Disabled by default)
* Supports profiles (since 1.6)
* Supports format of selected text (since 1.7)
* Preserve breakpoints (experimental) (since 1.8)
* Code templates for @formatter:on/off (since 1.9)
* Supports [Workspace Mechanic](https://code.google.com/a/eclipselabs.org/p/workspacemechanic/) configuration file (since 1.10)
* Support configuration from .settings/org.eclipse.jdt.core.prefs (absolute and relative paths) (since 1.10)
* On save action: Introduced option for formatting only the changed lines (since 1.10)
* Support configuration of linefeed (since 1.10)
* Support configuration of source level (since 1.10)
* Support for macro invocation (since 1.12)
* Support for guarded documents (documents that are created by the NetBeans GUI builder, guarded blocks are skipped of course, but everything in between can be formatted) (since 1.13)
* Support for Google code formatter (since 1.13)

## Compatibility
Compatible with NetBeans 8.2+ and JDK8+.

## Downloads
You can find the download links [here](http://funfried.github.io/ecf4nb/downloads.html).

## Known issues
Please check the open [GitHub Issues](/issues) and see [here](http://funfried.github.io/ecf4nb/known_issues.html)

### Note:
Please note that this plugin only provides support for formatting. Eclipse users may miss the application of save-actions like "adding @Override annotations" or "member sort order". Such AST-based transformations are not provided by this plugin.

## Licensing
This plugin is licensed under the [Eclipse Public License, Version 2.0](http://funfried.github.io/ecf4nb/licenses.html).
This plugin uses third-party libraries, which are needed to provide this plugins functionality, please check their licenses [here](https://funfried.github.io/ecf4nb/dependencies.html).

## The history of this project
* The original idea is based on http://epochcoder.blogspot.com/2013/08/import-eclipse-formatter-into-netbeans.html from [Willie Scholtz](https://github.com/epochcoder)
* [Geertjan Wielenga](https://github.com/geertjanw) made further investigations on that idea and [put everything together](https://blogs.oracle.com/geertjan/entry/eclipse_formatter_for_netbeans_ide) to create a first NetBeans Plugin out of this idea
* This was then [forked](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/) by [Benno Markiewicz](https://github.com/markiewb)
* [Benno Markiewicz](https://github.com/markiewb) stopped the development on the plugin and so [Geertjan Wielenga](https://github.com/geertjanw) forked it again and was looking for someone who wanted to take it over
* I think I used this plugin since I use NetBeans, because in every company I was so far, I had exactly this circumstance that the formatting configuration was always there for Eclipse, but not for NetBeans. And because I prefer NetBeans over Eclipse I had to find a way to use NetBeans but the code style configuration from Eclipse. I'm happy that I can give back something to the community now by continuing the development of this plugin.

## Feedback
Provide defects, request for enhancements and general feedback at the [GitHub issues](/issues) page

## Changelog
### 1.13-SNAPSHOT
* [Feature]: Besides the Eclipse code formatter you can now also configure and use the Google code formatter
* [Feature]: Added editor popup action, so in the context menu of the editor you can also find the "Format with Eclipse formatter" action
* [Feature]: Guarded documents are now supported (e.g. Panels or Dialogs that are created with the NetBeans GUI builder, guarded blocks are skip of course, but every thing in between will be formatted according to the formatter settings)
* [Improvement]: Switched from old Ant based NetBeans plugin project to the new Maven project for NetBeans plugins

### 1.12.2.46 - 4.6 Fork:
* [[Bugfix 93](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/93)]: Fixed: java.lang.NoClassDefFoundError after another version of the plugin has been uninstalled

### 1.12.1.46 - 4.6 Fork:
* [[Feature 84](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/84)]: Provide support for Eclipse Neon 4.6 1a
* [[Bugfix 88](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/90)]: Fixed: Keyboard shortcut does not format non java files

### 1.12.0:
* [[Feature 86](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/86)]: Allow action to be invoked via macro

### 1.11.0:
* [[Feature 78](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/78)]: Support formatting using Eclipse 4.5.2 Mars.2
* [[Bugfix 74](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/74)]: Fixed: Minor UI issues in options dialog
* [[Bugfix 80](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/80)]: Fixed: Prevent "URI is not hierarchical" exception
* [[Bugfix 77](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/77)]: Fixed: Exception, when using third-party Java code formatter in configuation file

### 1.10.2:
* [[Feature 61](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/61)]: Support [Workspace Mechanic](https://code.google.com/a/eclipselabs.org/p/workspacemechanic/) configuration file (*.epf)
* [[Feature 67](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/67)]: Support configuration via <projectdir>/.settings/org.eclipse.jdt.core.prefs or absolute path to org.eclipse.jdt.core.prefs
* [[Feature 68](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/68)]: Reduce download size by 77% by repacking jars with pack200
* [[Feature 70](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/70)]: On save action: Introduced option for formatting only the changed lines
* [[Feature 71](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/71)]: Support configuration of linefeed
* [[Feature 23](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/23)]: Support configuration of source level
* [[Feature 37](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/37)]: Fallback to NB formatter, if file isn't a java file
* [[Bugfix 73](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/73)]: Selection in undocked windows is not respected, when called via keyboard

### 1.9:
* [[Task 63](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/63)]: Update to Eclipse formatter jars from Eclipse 4.5.1 (Mars.1)
* [[Feature 65](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/65)]: Code templates for @formatter:on/off (More details...)(https://github.com/markiewb/eclipsecodeformatter_for_netbeans/wiki/Support-of-@formatter:off)
* [[Task 66](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/66)]: Update requirements to NetBeans 8.0+

### 1.8.0.6:
* [[Bugfix 57](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/57)]: Fixed: java.lang.IllegalArgumentException: bad position and run parts in EDT
* [[Bugfix 4](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/4)]: Fixed: option panel isn't found by searching for "eclipse"

### 1.8.0.5:
* [[Bugfix 56](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/56)]: Fixed: Format on Save - Changed files remain modified after saving them in Java Editor 
* [[Bugfix 55](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/55)]: Fixed: "URI is not hierarchical" error message on calling format - add logging for this error

### 1.8.0.4:
* [[Feature 47](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/47)]: Preserve Class/Method/Field breakpoints (experimental, can be disabled in options)
* [[Bugfix 53](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/47)]: Fixed: Do not remove linebreakpoint, if line is not included in selection
* [[Bugfix 52](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/52)]: Fixed: Cannot assign shortcut for "Format with Eclipse Formatter" action
* [[Task 46](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/46)]: Update to use eclipse formatter libs from eclipse 4.4 
* [[Task 48](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/48)]: Support only NetBeans 7.4 and above
* [[Task 49](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/49)]: Add donation button
* [[Task 50](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/50)]: Add link to github/homepage

### 1.7.1:
* [[Bugfix 41](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/41)]: Fixed: NPE when configuration file not found 

### 1.7:
* [[Feature 38](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/pull/38)]: Format selected part of document (PR by [saadmufti](https://github.com/saadmufti))

### 1.6.1:
* [[Bugfix 34](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/34)]: Fixed: NPE while saving options when no profile is set

### 1.6:
* [[Task 30](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/30)]: Update to Eclipse formatter jars from Eclipse Kepler 4.3
* [[Feature 31](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/31)]: First profile in file is always used - support selection of profile
* [[Feature 21](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/21)]: Project specific options: Show link to global options

### 1.5:
* [[Bugfix 18](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/18)]: Fixed: Formatting with eclipse formatter introduces empty document in undo manager
* [[Task 27](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/27)]: Provide a signed package for the PPUC

### 1.4.1:
* [[Bugfix 25](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues/25)]: Fixed: Configured formatter.xml isn't used

### 1.4:
* major refactorings and fixes - see [milestone 1.4@github](https://github.com/markiewb/eclipsecodeformatter_for_netbeans/issues?milestone=1&page=1&state=closed)

[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=926F5XBCTK2LQ&source=url)
