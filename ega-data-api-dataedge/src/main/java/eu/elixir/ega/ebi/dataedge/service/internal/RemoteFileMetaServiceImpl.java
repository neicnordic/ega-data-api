/*
 * Copyright 2016 ELIXIR EGA
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
package eu.elixir.ega.ebi.dataedge.service.internal;

import eu.elixir.ega.ebi.shared.dto.File;
import eu.elixir.ega.ebi.shared.dto.FileDataset;
import eu.elixir.ega.ebi.dataedge.service.FileMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import static eu.elixir.ega.ebi.shared.Constants.FILEDATABASE_SERVICE;

/**
 * @author asenf
 */
@Service
@EnableDiscoveryClient
public class RemoteFileMetaServiceImpl implements FileMetaService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Returns a {@link File} descriptor for the requested file, if the correct
     * permissions are available in @{code auth}.
     *
     * @param auth An authentication token for the file.
     * @param fileId The stable ID of the file to request.
     * @return The requested file descriptor, otherwise an empty {@link File}
     *     object.
     */
    @Override
    @Cacheable(cacheNames = "fileFile")
    public File getFile(Authentication auth, String fileId) {
        ResponseEntity<FileDataset[]> forEntityDataset = restTemplate.getForEntity(FILEDATABASE_SERVICE + "/file/{fileId}/datasets", FileDataset[].class, fileId);
        FileDataset[] bodyDataset = forEntityDataset.getBody();

        // Obtain all Authorised Datasets
        HashSet<String> permissions = new HashSet<>();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        while (iterator.hasNext()) {
            GrantedAuthority next = iterator.next();
            permissions.add(next.getAuthority());
        }

        // Is this File in at least one Authorised Dataset?
        ResponseEntity<File[]> forEntity = restTemplate.getForEntity(FILEDATABASE_SERVICE + "/file/{fileId}", File[].class, fileId);
        File[] body = forEntity.getBody();
        if (body != null && bodyDataset != null) {
            for (FileDataset f : bodyDataset) {
                String datasetId = f.getDatasetId();
                if (permissions.contains(datasetId) && body.length >= 1) {
                    File ff = body[0];
                    ff.setDatasetId(datasetId);
                    return ff;
                }
            }
        }

        return (new File());
    }

    /**
     * Returns the list of files for a given dataset from the file database
     * service.
     *
     * @param datasetId Stable ID of the dataset to request
     * @return List of files for the given dataset
     */
    @Override
    @Cacheable(cacheNames = "fileDatasetFile")
    public Iterable<File> getDatasetFiles(String datasetId) {
        File[] response = restTemplate.getForObject(FILEDATABASE_SERVICE + "/datasets/{datasetId}/files", File[].class, datasetId);
        return Arrays.asList(response);
    }

}
