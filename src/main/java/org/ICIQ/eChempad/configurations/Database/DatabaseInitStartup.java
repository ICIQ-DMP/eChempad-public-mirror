/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.Database;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.logging.Logger;

/**
 * This class contains the method
 * public void onApplicationEvent(final @NotNull ApplicationReadyEvent event)
 * which is executed after the application is "ready", which usually is after all the initializations, but before
 * accepting traffic. In here we can implement ways to load data to the database at the start when we are in dev
 * profile.
 */
@Component
public class DatabaseInitStartup implements ApplicationListener<ApplicationReadyEvent> {

    public DatabaseInitStartup() {}

    @Override
    public void onApplicationEvent(final @NotNull ApplicationReadyEvent event) {
        this.initializeDB();
        //OffsetDateTime dateTime = OffsetDateTime.parse("2020-04-19T18:58:41.966Z");
    }

    private void initializeDB()
    {


    }

}
