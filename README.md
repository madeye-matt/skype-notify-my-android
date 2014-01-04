skype-notify-my-android
=======================

This program came about as I found myself continously missing Skype messages.  Having recently been given a Pebble watch, it seemed a natural thing to find a way to get the important messages forwarded to my device.  

## Background

For the moment, setting this up will most likely require Java development knowledge.  If I find the time I may package it in an installer, but for now it is best run from the command line or similar. 

## Dependencies

### Notify My Android

My small program makes use of Notify My Android ( https://www.notifymyandroid.com/ ) to get the Skype notifications to an Android device.  The Notify My Android app needs to be installed on the Android device and signed in.  You will also need to set up an account and obtain an API key - a straightforward process.  A $4.99 premium account is required if you are expecting more than one Skype message an hour. If you are expecting more than 800 you will need to persuade NMA it's a good idea. 

### Notify My Pebble

In order to get the Skype messages to the Pebble watch you need to install Notify My Pebble ( https://play.google.com/store/apps/details?id=net.skumler.notifymypebble&hl=en ) on your Android device. 

### taksan/skype-java-api

I believe these to be an improved version of the now defunct Skype Java API, obtainable from https://github.com/taksan/skype-java-api.  From what I have seen so far, this library is well-structured and very stable. Everything worked as expected.  You will need to download the source and build version 1.6-SNAPSHOT as my program uses features added after the last 1.5 release that can be easily located in Maven repos. 

## Configuration

This application accepts an external properties file, which may contain the following items. Alternatively, these can be set with -D system properties.

* nma.apikey - the NMA apikey to use for Notify My Android REST API operations
* nma.base_url - the base URL for the Notify My Android "notify" REST operation
* nma.application_name - the application name to use for the NMA notification
* nma.priority.Status - the default priority for Skype status change notifications (if less than -2 then notifications are suppressed. Can be modded by user - see below)
* nma.priority.Message - the default priority for Skype messages (if less than -2 then notifications are suppressed. Can be modded by user - see below)
* nma.users.(SkypeUserId) - an integer value to add to nma.priority.Status and nma.priority.Message to affect the priority of notifications from a particular Skype contact.

Please note: SkypeUserId must be the account id for the user and not the display name or full name.  

By default, nma.priority.Status is set to -3 which will cause all status notifications to be suppressed.  To white list status notifications from particular users add a nma.users line in the configuration with a value of at least 1.

## Running

The program will need to be built and run on whichever machines you are running Skype on using the following key:

    java -Dnma.apikey=(apikey obtained from NMA) com.madeye.notify.SkypeNotifyMyAndroid (configuration file)

## Known Issues

+ Total lack of unit tests
+ At the time of writing, this code has only been tested on Fedora Linux

