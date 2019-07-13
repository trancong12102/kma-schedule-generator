package org.lonewolf2110.utils.interfaces;

public interface IKMAClient {
    int login(String username, String password) ;

    int getScheduleAsStream();
}
