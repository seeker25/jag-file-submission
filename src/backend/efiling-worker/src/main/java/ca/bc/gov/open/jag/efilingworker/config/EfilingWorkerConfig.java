package ca.bc.gov.open.jag.efilingworker.config;

import ca.bc.gov.open.jag.efilingsubmissionclient.DemoSubmissionServiceImpl;
import ca.bc.gov.open.jag.efilingsubmissionclient.EfilingSubmissionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EfilingWorkerConfig {
    @Bean
    public EfilingSubmissionService efilingSubmissionService() {
        return new DemoSubmissionServiceImpl();
    }
}
