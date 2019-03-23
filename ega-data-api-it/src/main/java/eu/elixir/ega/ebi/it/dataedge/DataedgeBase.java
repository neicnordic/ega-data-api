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
package eu.elixir.ega.ebi.it.dataedge;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;

public class DataedgeBase {
    RequestSpecification REQUEST;
    RequestSpecification REQUEST_ZERO_DATASETID;

    String fileId;
    String datasetId;
    String unencryptedChecksum;

    public DataedgeBase() {

        if (System.getProperty("dataedge.url") == null) {
            throw new IllegalArgumentException(
                    "Dataedge service host url is null. Pls check configuration in pom.xml.");
        } else if (System.getProperty("fileId") == null) {
            throw new IllegalArgumentException("FileId value is null. Pls check configuration in pom.xml.");
        } else if (System.getProperty("dataedge.port") == null) {
            throw new IllegalArgumentException("Port value is null. Pls check configuration in pom.xml.");
        } else if (System.getProperty("datasetId") == null) {
            throw new IllegalArgumentException("DatasetId value is null. Pls check configuration in pom.xml.");
        } else if (System.getProperty("res.file.checksum") == null) {
            throw new IllegalArgumentException("Checksum value is null. Pls check configuration in pom.xml.");
        } else if (System.getProperty("EGAD00010000919.token") == null) {
            throw new IllegalArgumentException(
                    "EGAD00010000919 token value is null. Pls check configuration in pom.xml.");
        } else if (System.getProperty("EGAD00000000000.token") == null) {
            throw new IllegalArgumentException(
                    "EGAD00000000000 token value is null. Pls check configuration in pom.xml.");
        }

        RestAssured.baseURI = System.getProperty("dataedge.url");
        RestAssured.port = Integer.parseInt(System.getProperty("dataedge.port"));
        unencryptedChecksum = System.getProperty("res.file.checksum");
        fileId = System.getProperty("fileId");
        datasetId = System.getProperty("datasetId");

        REQUEST = RestAssured.given().header("Authorization", "Bearer " + System.getProperty("EGAD00010000919.token"))
                .contentType(ContentType.JSON);

        REQUEST_ZERO_DATASETID = RestAssured.given()
                .header("Authorization", "Bearer " + System.getProperty("EGAD00000000000.token"))
                .contentType(ContentType.JSON);
    }

}
