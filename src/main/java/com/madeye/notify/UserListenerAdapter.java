package com.madeye.notify;

import com.skype.SkypeException;
import com.skype.User;
import com.skype.UserListener;

class UserListenerAdapter implements UserListener {

    @Override
    public void statusMonitor(User.Status status, User user) throws SkypeException {
    }

    @Override
    public void moodTextMonitor(String moodText, User user) throws SkypeException {
    }

    @Override
    public void fullNameMonitor(String fullName, User user) throws SkypeException {
    }

    @Override
    public void phoneMobileMonitor(String phoneMobile, User user) throws SkypeException {
    }

    @Override
    public void phoneHomeMonitor(String phoneHome, User user) throws SkypeException {
    }

    @Override
    public void phoneOfficeMonitor(String phoneOffice, User user) throws SkypeException {
    }

    @Override
    public void displayNameMonitor(String displayName, User user) throws SkypeException {
    }

    @Override
    public void countryMonitor(String country, User user) throws SkypeException {
    }

    @Override
    public void provinceMonitor(String province, User user) throws SkypeException {
    }

    @Override
    public void cityMonitor(String city, User user) throws SkypeException {
    }

    @Override
    public void timeZoneMonitor(String timeZone, User user) throws SkypeException {
    }

    @Override
    public void sexMonitor(User.Sex sex, User user) throws SkypeException {
    }

    @Override
    public void homePageMonitor(String homePage, User user) throws SkypeException {
    }

    @Override
    public void birthdayMonitor(String birthday, User user) throws SkypeException {
    }

    @Override
    public void languageMonitor(String language, User user) throws SkypeException {
    }

    @Override
    public void aboutMonitor(String about, User user) throws SkypeException {
    }

    @Override
    public void isBlockedMonitor(boolean isBlocked, User user) throws SkypeException {
    }

    @Override
    public void isAuthorizedMonitor(boolean isAuthorized, User user) throws SkypeException {
    }
}
