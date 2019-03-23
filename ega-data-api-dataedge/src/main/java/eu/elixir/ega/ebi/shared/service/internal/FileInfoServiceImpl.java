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
package eu.elixir.ega.ebi.shared.service.internal;

import static eu.elixir.ega.ebi.shared.Constants.FILEDATABASE_SERVICE;
import static eu.elixir.ega.ebi.shared.Constants.RES_SERVICE;

import eu.elixir.ega.ebi.shared.config.NotFoundException;
import eu.elixir.ega.ebi.shared.service.FileInfoService;
import eu.elixir.ega.ebi.shared.service.PermissionsService;
import eu.elixir.ega.ebi.shared.dto.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FileInfoServiceImpl implements FileInfoService{

  @Autowired
  PermissionsService permissionService;

  @Autowired
  private RestTemplate restTemplate;

  @Override
  @Cacheable(cacheNames = "reqFile", key="T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication() + #p0")
  public File getFileInfo(String fileId) {

    //check User has permissions for this file
    String datasetId = permissionService.getFilePermissionsEntity(fileId);

    File reqFile = null;
    ResponseEntity<File[]> forEntity = restTemplate
        .getForEntity(FILEDATABASE_SERVICE + "/file/{fileId}", File[].class, fileId);
    File[] body = forEntity.getBody();
    if (body != null) {
      reqFile = body[0];
      reqFile.setDatasetId(datasetId);
      // If there's no file size in the database, obtain it from RES
      if (reqFile.getFileSize() == 0) {
        ResponseEntity<Long> forSize = restTemplate
            .getForEntity(RES_SERVICE + "/file/archive/{fileId}/size", Long.class, fileId);
        reqFile.setFileSize(forSize.getBody());
      }
    } else { // 404 File Not Found
      throw new NotFoundException(fileId, "4");
    }
    return reqFile;
  }

}
