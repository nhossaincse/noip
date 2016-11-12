# noip
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=T9USZAMJHNBBC&lc=IT&item_name=Davide%20Colombo&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted)

This is a Java DNS updater for [No-IP](https://www.noip.com/), an alternative to [DUC](https://www.noip.com/download) (DNS Update Client). This updater is using both No-IP and [Ipify](https://www.ipify.org/) API. I used the following components, see them included as dependencies in [pom.xml](pom.xml):

* [SLF4J](http://www.slf4j.org/) an abstraction for various logging frameworks.
* [Logback](http://logback.qos.ch/) a Java logging framework, successor to the popular log4j.
* [sysout-over-slf4j](https://github.com/Mahoney/sysout-over-slf4j) redirect System.out and System.err to SLF4J.
* [Lombok](https://projectlombok.org/) an amazing solution for lazy coders.
* [Jackson Data Processor](https://github.com/FasterXML/jackson-databind) a multi-purpose library for processing JSON data. 
* [args4j](https://github.com/kohsuke/args4j) a library to parse command line arguments.

Please note you've to manually schedule the application in order to keep updated your dynamic DNS. The simplest way is probably using [Cron](https://en.wikipedia.org/wiki/Cron).
