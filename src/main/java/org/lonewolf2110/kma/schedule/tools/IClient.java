package org.lonewolf2110.kma.schedule.tools;

public interface IClient {
    int login(String username, String password);

    int getScheduleAsStream();
}
