package org.ICIQ.eChempad.services;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Blob;

public interface LobService {

    Blob createBlob(InputStream content, long size);

    InputStream readBlob(Blob blob);
}
