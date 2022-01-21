# No-IP Java DNS Updater
[![Donate](https://img.shields.io/badge/PayPal-00457C?style=flat&logo=paypal&logoColor=white)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=T9USZAMJHNBBC&lc=IT&item_name=No-IP%20Java%20DNS%20Updater&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted)
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=java&logoColor=white)
[![CircleCI](https://circleci.com/gh/davidecolombo/noip/tree/master.svg?style=shield)](https://circleci.com/gh/davidecolombo/noip/tree/master)
![Travis-CI](https://travis-ci.com/davidecolombo/noip.svg?branch=master)
[![DepShield Badge](https://depshield.sonatype.org/badges/davidecolombo/noip/depshield.svg)](https://depshield.github.io)
[![Known Vulnerabilities](https://snyk.io//test/github/davidecolombo/noip/badge.svg?targetFile=pom.xml)](https://snyk.io//test/github/davidecolombo/noip?targetFile=pom.xml)
![Code Size](https://img.shields.io/github/languages/code-size/davidecolombo/noip)
![License](https://img.shields.io/github/license/davidecolombo/noip)

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
*/30 * * * * sudo DISPLAY=:1 java -cp /home/user/noip/noip-1.0.0.jar space.davidecolombo.noip.App -settings /home/user/noip/settings.json > /home/user/noip/log.txt 2>&1
```

Expected output:
```
[main] INFO  s.d.noip.noip.NoIpUpdater - IpifyResponse(ip=XX.XX.XXX.XX)
[main] INFO  s.d.noip.noip.NoIpUpdater - HTTP status code: 200
[main] INFO  s.d.noip.noip.NoIpUpdater - HTTP status message: OK
[main] INFO  s.d.noip.noip.NoIpUpdater - No-IP response: nochg XX.XX.XXX.XX
[main] INFO  space.davidecolombo.noip.App - Status: 1
```
