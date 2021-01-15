package io.lana.library.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfiguration {
    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
            .baselineVersion("0")
            .baselineOnMigrate(true)
            .dataSource(dataSource)
            .load();
    }
}
