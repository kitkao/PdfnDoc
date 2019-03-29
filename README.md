# UI

## Doc rules

<https://github.com/DavidAnson/markdownlint/blob/v0.12.0/doc/Rules.md>

## Design

* A JAVAFX application for merging list of word 2003 doc files into a pdf booklet
  * Doc to PDF: jacob invoke KWPS.application
  * Merge PDF: itextpdf
  * UI beautify: jfoenix  
    * refer to <https://www.youtube.com/watch?v=L72FEmEt-G8>

* Java copyright conclusion
  * Oracle JDK 8 u192 是2019年1月前发布的最新版本，所以只要一直使用 JDK 8 u192 以及更早的版本，就不需付费。

## Notes

* IDE: Intellij IDEA
* Env: Jdk 1.8
* Packages:
  * itextpdf: 5.5
  * jocab: 1.19
  * jphonix: 8.0

* generate jar:
  * JAR
  * define x86/x64 project jdk for platforms
* generate exe:
  * JavaFX applications
  * use x84 IDEA launcher
  * define x86/x64 project jdk for platforms

## Outcome

* JavaFX leverage css is good.
* But JavaFX has some bugs. I would choose electron next time.

## License
