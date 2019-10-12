# No-IP Java DNS Updater
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=T9USZAMJHNBBC&lc=IT&item_name=No-IP%20Java%20DNS%20Updater&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted)
![Travis-CI](https://travis-ci.com/davidecolombo/noip.svg?branch=master)
[![DepShield Badge](https://depshield.sonatype.org/badges/davidecolombo/noip/depshield.svg)](https://depshield.github.io)
[![Known Vulnerabilities](https://snyk.io//test/github/davidecolombo/noip/badge.svg?targetFile=pom.xml)](https://snyk.io//test/github/davidecolombo/noip?targetFile=pom.xml)

This is a Java DNS updater for [No-IP](https://www.noip.com/), an alternative to [DUC](https://www.noip.com/download) (DNS Update Client). This updater is using both [Ipify](https://www.ipify.org/) and No-IP APIs to retrieve your current IP address and update your No-IP hostname. Please take a look at [settings.json](src/test/resources/settings.json) to configure it:

| Property | Description |
| --- | --- |
| _userName_ | your No-IP username |
| _password_ | your No-IP password |
| _hostName_ | the hostname(s) (host.domain.com) or group(s) (group_name) to be updated |
| _userAgent_ | HTTP User-Agent to help No-IP identify your client |

__NOTE__: When making an update it's important to include an HTTP User-Agent to help No-IP identify different clients that access the system. Clients that do not supply a User-Agent risk being blocked from the system.
Your user agent should be in the following format:
```
NameOfUpdateProgram/VersionNumber maintainercontact@domain.com
```
Please also note you've to manually schedule the application execution in order to keep updated your dynamic DNS. The simplest way is probably using [Cron](https://en.wikipedia.org/wiki/Cron). Example:

```
*/30 * * * * sudo DISPLAY=:1 java -cp /mnt/usbstorage/noip/noip-0.0.1-SNAPSHOT-jar-with-dependencies.jar space.davidecolombo.noip.Main -settings /mnt/usbstorage/noip/resources/settings.json > /mnt/usbstorage/noip/log.txt 2>&1
```

Expected output:
```
[main] INFO  s.d.noip.noip.NoIpUpdater - IpifyResponse(ip=XX.XX.XXX.XX)
[main] INFO  s.d.noip.noip.NoIpUpdater - HTTP status code: 200
[main] INFO  s.d.noip.noip.NoIpUpdater - HTTP status message: OK
[main] INFO  s.d.noip.noip.NoIpUpdater - No-IP response: nochg XX.XX.XXX.XX
[main] INFO  space.davidecolombo.noip.Main - Status: 1
```
I used the following components, see them included as dependencies in [pom.xml](pom.xml):

* [SLF4J](http://www.slf4j.org/) an abstraction for various logging frameworks
* [Logback](http://logback.qos.ch/) a Java logging framework, successor to the popular log4j
* [sysout-over-slf4j](https://github.com/Mahoney/sysout-over-slf4j) redirect System.out and System.err to SLF4J
* [Lombok](https://projectlombok.org/) an amazing solution for lazy coders
* [Jackson Data Processor](https://github.com/FasterXML/jackson-databind) a multi-purpose library for processing JSON data
* [args4j](https://github.com/kohsuke/args4j) a library to parse command line arguments
* [Retrofit 2](https://square.github.io/retrofit/) a type-safe HTTP client for Android and Java
* [Commons Lang](https://commons.apache.org/proper/commons-lang/) provides a host of helper utilities for the java.lang API

```
+- org.projectlombok:lombok:jar:1.18.10:provided
+- org.slf4j:slf4j-api:jar:1.7.28:compile
+- ch.qos.logback:logback-classic:jar:1.2.3:compile
|  \- ch.qos.logback:logback-core:jar:1.2.3:compile
+- uk.org.lidalia:sysout-over-slf4j:jar:1.0.2:compile
+- com.fasterxml.jackson.core:jackson-databind:jar:2.10.0.pr3:compile
|  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.10.0.pr3:compile
|  \- com.fasterxml.jackson.core:jackson-core:jar:2.10.0.pr3:compile
+- args4j:args4j:jar:2.33:compile
+- com.squareup.retrofit2:retrofit:jar:2.6.2:compile
|  \- com.squareup.okhttp3:okhttp:jar:3.12.0:compile
|     \- com.squareup.okio:okio:jar:1.15.0:compile
+- com.squareup.retrofit2:converter-scalars:jar:2.6.2:compile
+- org.apache.commons:commons-lang3:jar:3.9:compile
+- org.junit.jupiter:junit-jupiter-api:jar:5.5.2:test
|  +- org.apiguardian:apiguardian-api:jar:1.1.0:test
|  +- org.opentest4j:opentest4j:jar:1.2.0:test
|  \- org.junit.platform:junit-platform-commons:jar:1.5.2:test
\- org.junit.jupiter:junit-jupiter-engine:jar:5.5.2:test
\- org.junit.platform:junit-platform-engine:jar:1.5.2:test
```
