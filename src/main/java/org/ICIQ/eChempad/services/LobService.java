/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Blob;

public interface LobService {

    Blob createBlob(InputStream content, long size);

    InputStream readBlob(Blob blob);
}
