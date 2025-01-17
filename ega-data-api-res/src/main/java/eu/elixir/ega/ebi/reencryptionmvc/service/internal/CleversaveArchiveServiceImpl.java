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
package eu.elixir.ega.ebi.reencryptionmvc.service.internal;

import eu.elixir.ega.ebi.reencryptionmvc.config.NotFoundException;
import eu.elixir.ega.ebi.reencryptionmvc.config.ServerErrorException;
import eu.elixir.ega.ebi.reencryptionmvc.dto.ArchiveSource;
import eu.elixir.ega.ebi.reencryptionmvc.dto.EgaFile;
import eu.elixir.ega.ebi.reencryptionmvc.service.ArchiveAdapterService;
import eu.elixir.ega.ebi.reencryptionmvc.service.ArchiveService;
import eu.elixir.ega.ebi.reencryptionmvc.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

import static eu.elixir.ega.ebi.shared.Constants.FILEDATABASE_SERVICE;

/**
 * @author asenf
 */
@Service
@Profile("default")
@Primary
@EnableDiscoveryClient
public class CleversaveArchiveServiceImpl implements ArchiveService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KeyService keyService;

    @Autowired
    private ArchiveAdapterService archiveAdapterService;

    @Override
//    @Retryable(maxAttempts = 4, backoff = @Backoff(delay = 2000, multiplier = 2))
    @Cacheable(cacheNames = "archive")
    public ArchiveSource getArchiveFile(String id, HttpServletResponse response) {
        // Get Filename from EgaFile ID - via DATA service (potentially multiple files)
        ResponseEntity<EgaFile[]> forEntity = restTemplate.getForEntity(FILEDATABASE_SERVICE + "/file/{fileId}", EgaFile[].class, id);
        response.setStatus(forEntity.getStatusCodeValue());
        if (forEntity.getStatusCode() != HttpStatus.OK) {
            return null;
        }

        EgaFile[] body = forEntity.getBody();
        String fileName = (body != null && body.length > 0) ? forEntity.getBody()[0].getFileName() : "";
        if ((body == null || body.length == 0)) {
            response.setStatus(forEntity.getStatusCodeValue());
            throw new NotFoundException("Can't obtain File data for ID", id);
        }
        if (fileName.startsWith("/fire")) fileName = fileName.substring(16);
        // Guess Encryption Format from File
        String encryptionFormat = fileName.toLowerCase().endsWith("gpg") ? "symmetricgpg" : "aes256";
        // Get Cleversafe URL from Filename via Fire
        String[] filePath = archiveAdapterService.getPath(fileName);
        if (filePath == null || filePath[0] == null) {
            response.setStatus(530);
            throw new ServerErrorException("Fire Error in obtaining URL for ", fileName);
        }
        String fileUrlString = filePath[0];
        long size = Long.valueOf(filePath[1]);

        // Get EgaFile encryption Key
        String encryptionKey = keyService.getFileKey(id);
        if (encryptionKey == null || encryptionKey.length() == 0) {
            response.setStatus(532);
            throw new ServerErrorException("Error in obtaining Archive Key for ", fileName);
        }

        // Build result object and return it (auth is 'null' --> it is part of the URL now)
        return new ArchiveSource(fileUrlString, size, null, encryptionFormat, encryptionKey, null);
    }

}
