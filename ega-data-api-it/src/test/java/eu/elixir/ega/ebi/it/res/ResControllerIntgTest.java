/*
 * Copyright 2019 ELIXIR EGA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.elixir.ega.ebi.it.res;

import static eu.elixir.ega.ebi.it.common.Common.getMd5DigestFromResponse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jayway.restassured.response.Response;

/**
 * Class to do the integration test for FileController in ega-data-api-key.
 * 
 * @author amohan
 *
 */
public class ResControllerIntgTest extends ResBase {

    private final static String FILE_PATH = "/file";
    private final static String FILE_ARCHIVE_PATH = "/file/archive/";

    /**
     * Verify the api call /file/archive/{id} and check status is
     * {@link org.apache.http.HttpStatus#SC_OK}. Also checks the response body
     * should not be null.
     * 
     * @throws Exception
     */
    @Test
    public void testGetArchiveFile() throws Exception {
        final Response response = REQUEST.get(FILE_ARCHIVE_PATH + fileId + "?destinationFormat=plain");
        assertTrue(getMd5DigestFromResponse(response).equalsIgnoreCase(unencryptedChecksum));
    }

    /**
     * Verify the api call /file and compares the response body md5.
     * 
     * @throws Exception
     */
    @Test
    public void testGetFile() throws Exception {
        final Response response = REQUEST.get(FILE_PATH +"?sourceKey="+sourceKey+"&sourceIV="+sourceIV+"&filePath="+filePath+"&destinationFormat=plain");
        assertTrue(getMd5DigestFromResponse(response).equalsIgnoreCase(unencryptedChecksum));
    }
}
