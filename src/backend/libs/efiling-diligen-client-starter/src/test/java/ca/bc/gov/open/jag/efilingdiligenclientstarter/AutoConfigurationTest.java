package ca.bc.gov.open.jag.efilingdiligenclientstarter;

import ca.bc.gov.open.efilingdiligenclient.diligen.DiligenAuthService;
import ca.bc.gov.open.efilingdiligenclient.diligen.DiligenProperties;
import ca.bc.gov.open.efilingdiligenclient.diligen.DiligenService;
import ca.bc.gov.open.jag.efilingdiligenclient.api.DocumentsApi;
import ca.bc.gov.open.jag.efilingdiligenclient.api.HealthCheckApi;
import ca.bc.gov.open.jag.efilingdiligenclient.api.handler.ApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AutoConfigurationTest {

    private ApplicationContextRunner context;


    @Test
    @DisplayName("ok: with valid configuration should produce beans")
    public void validConfigurationShouldProduceBeans() {

        context = new ApplicationContextRunner()
                .withUserConfiguration(AutoConfiguration.class)
                .withPropertyValues(
                        "jag.efiling.diligen.basePath=http://test.com " +
                        "jag.efiling.diligen.usename=test " +
                        "jag.efiling.diligen.password=test " +
                        "jag.efiling.diligen.projectIdentifier=2")
                .withUserConfiguration(DiligenProperties.class);

        context.run(it -> {
            assertThat(it).hasSingleBean(ApiClient.class);
            assertThat(it).hasSingleBean(HealthCheckApi.class);
            assertThat(it).doesNotHaveBean(DiligenHealthIndicator.class);
            assertThat(it).hasSingleBean(RestTemplate.class);
            assertThat(it).hasSingleBean(DiligenAuthService.class);
            assertThat(it).hasSingleBean(DiligenService.class);
            assertThat(it).hasSingleBean(DocumentsApi.class);
        });

    }

    @Test
    @DisplayName("ok: with valid configuration and healthchecks enabled should produce beans")
    public void validConfigurationPlusHealthCheckShouldProduceBeans() {

        context = new ApplicationContextRunner()
                .withUserConfiguration(AutoConfiguration.class)
                .withPropertyValues(
                        "jag.efiling.diligen.basePath=http://test.com",
                        "jag.efiling.diligen.health.enabled=true")
                .withUserConfiguration(DiligenProperties.class);

        context.run(it -> {
            assertThat(it).hasSingleBean(ApiClient.class);
            assertThat(it).hasSingleBean(HealthCheckApi.class);
            assertThat(it).hasSingleBean(DiligenHealthIndicator.class);
        });

    }

    @Test
    @DisplayName("error: with invalid configuration should throw configuration exceptions")
    public void invalidConfigurationShouldThrowDiligenConfigurationException() {

        context = new ApplicationContextRunner()
                .withUserConfiguration(AutoConfiguration.class)
                .withUserConfiguration(DiligenProperties.class);


        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> {

            context.run(it -> {

                it.getBean(HealthCheckApi.class);

            });

        });


    }


}
