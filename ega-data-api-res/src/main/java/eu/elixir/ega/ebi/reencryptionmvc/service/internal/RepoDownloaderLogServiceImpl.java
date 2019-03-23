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
package eu.elixir.ega.ebi.reencryptionmvc.service.internal;

import eu.elixir.ega.ebi.reencryptionmvc.domain.repository.DownloaderLog;
import eu.elixir.ega.ebi.reencryptionmvc.service.DownloaderLogService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile("repo-logger")
@Primary
@Transactional
@Service
public class RepoDownloaderLogServiceImpl implements DownloaderLogService {

  @Autowired
  private DownloaderLog downloaderLogRepository;

  @Autowired
  private HttpServletRequest request;

  @Override
  public String logStart(String stableId, long startCoordinate, long endCoordinate) {
    String clientIp = request.getHeader("X-FORWARDED-FOR");
    String user = request.getHeader("X-USER-NAME");
    return downloaderLogRepository
        .makeRequest(stableId, user, clientIp, startCoordinate, endCoordinate);
  }

  @Override
  public void logCompleted(String requestId, long downloadSize, double speed) {
    downloaderLogRepository.downloadComplete(requestId, downloadSize, speed);
  }

  @Override
  public void logError(String requestId, String code, String cause) {
    String hostname = "unknown";
    try {
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      //Ignore
    }
    downloaderLogRepository.setError(requestId, hostname, code, cause);
  }
}
