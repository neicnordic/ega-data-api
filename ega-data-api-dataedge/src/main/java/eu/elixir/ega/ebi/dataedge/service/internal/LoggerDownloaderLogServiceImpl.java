package eu.elixir.ega.ebi.dataedge.service.internal;

import eu.elixir.ega.ebi.dataedge.service.DownloaderLogService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("logger-log")
@Primary
@Service
public class LoggerDownloaderLogServiceImpl extends AbstractDownloaderLogService implements DownloaderLogService {

}
