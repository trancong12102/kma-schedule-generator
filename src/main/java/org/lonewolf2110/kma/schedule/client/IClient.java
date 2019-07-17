package org.lonewolf2110.kma.schedule.client;

import java.io.InputStream;

public interface IClient {
    int login(String username, String password);

    int getScheduleAsStream();

    InputStream getInputStream();
}
