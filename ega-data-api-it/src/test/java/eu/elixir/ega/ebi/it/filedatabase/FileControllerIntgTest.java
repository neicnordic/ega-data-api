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
package eu.elixir.ega.ebi.it.filedatabase;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.jayway.restassured.response.Response;

/**
 * Class to do the integration test for FileController in ega-data-api-key.
 * 
 * @author amohan
 *
 */
public class FileControllerIntgTest extends FileDatabaseBase {

    private final static String FILE_CONTROLLER = "/file/";    
    
    /**
     * Verify the api call /file/{fileId} and check status is {@link org.apache.http.HttpStatus#SC_OK}.
     * Also checks the response fileId.
     */
    @Test
    public void testGetFile() {
        final Response response = REQUEST.get(FILE_CONTROLLER + fileId);
        JSONArray jsonArray = new JSONArray(response.body().asString()); 
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        
        response.then().assertThat().statusCode(SC_OK);
        assertThat(jsonArray.length(), equalTo(1));
        assertThat(jsonObject.getString("fileId"), equalTo(fileId));
    }
    
    /**
     * Verify the api call /file/{fileId} and check status is {@link org.apache.http.HttpStatus#SC_OK}.
     * Also passing wrong Id 000 and expecting the response body to be empty array i.e, []
     */
    @Test
    public void testGetFileForZeroId() {
        final Response response = REQUEST.get(FILE_CONTROLLER + ID_ZERO);
        JSONArray jsonArray = new JSONArray(response.body().asString()); 
        
        response.then().assertThat().statusCode(SC_OK);
        assertThat(jsonArray.length(), equalTo(0));
    }
    
    
    /**
     * Verify the api call /file/{fileId}/datasets and check status is {@link org.apache.http.HttpStatus#SC_OK}.
     * Also checks the response fileId & datasetId.
     */
    @Test
    public void testGetFileDataset() {
        final Response response = REQUEST.get(FILE_CONTROLLER + fileId + "/datasets");
        JSONArray jsonArray = new JSONArray(response.body().asString()); 
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        
        response.then().assertThat().statusCode(SC_OK);
        assertThat(jsonArray.length(), equalTo(1));
        assertThat(jsonObject.getString("fileId"), equalTo(fileId));
        assertThat(jsonObject.getString("datasetId"), equalTo(datasetId));
    }
    
    /**
     * Verify the api call /file/{fileId}/datasets and check status is {@link org.apache.http.HttpStatus#SC_OK}.
     * Also passing wrong Id 000 and expecting the response body to be empty array i.e, []
     */
    @Test
    public void testGetFileDatasetForZeroId() {
        final Response response = REQUEST.get(FILE_CONTROLLER + ID_ZERO + "/datasets");
        JSONArray jsonArray = new JSONArray(response.body().asString()); 
        
        response.then().assertThat().statusCode(SC_OK);
        assertThat(jsonArray.length(), equalTo(0));
    }
    
}
