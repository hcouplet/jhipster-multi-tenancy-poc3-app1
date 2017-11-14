package eu.creativeone.tenancy.config;

import eu.creativeone.tenancy.client.UaaClient;
import io.github.jhipster.config.JHipsterConstants;
import liquibase.integration.spring.MultiTenantSpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;

@Configuration
//@EnableJpaRepositories("eu.creativeone.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class TenantDatabaseConfiguration
{
    private final Logger log = LoggerFactory.getLogger(TenantDatabaseConfiguration.class);

    private final Environment env;

    @Autowired
    private UaaClient uaaClient;

    public TenantDatabaseConfiguration(Environment env) {
        this.env = env;
    }

    @Bean
    public MultiTenantSpringLiquibase liquibaseMt(DataSource dataSource) throws SQLException
    {
        MultiTenantSpringLiquibase multiTenantSpringLiquibase = new MultiTenantSpringLiquibase();
        multiTenantSpringLiquibase.setDataSource(dataSource);

        //get all existing tenantsID
        List<String> tenantsSchemas = uaaClient.getAllTenants();

        //prefix with current serviceName for creating schemas
        String serviceNamePrefix = env.getProperty("spring.application.name") + "_";
        for (final ListIterator<String> i = tenantsSchemas.listIterator(); i.hasNext();)
        {
            String schemaName = serviceNamePrefix + i.next();
            i.set(schemaName);
            dataSource.getConnection().createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS `" + schemaName + '`');
        }

        multiTenantSpringLiquibase.setSchemas(tenantsSchemas);
        multiTenantSpringLiquibase.setChangeLog("classpath:config/liquibase/mt_master.xml");
        multiTenantSpringLiquibase.setContexts("development, production");
        if (env.acceptsProfiles(JHipsterConstants.SPRING_PROFILE_NO_LIQUIBASE)) {
            multiTenantSpringLiquibase.setShouldRun(false);
        } else {
            multiTenantSpringLiquibase.setShouldRun(true);
            log.debug("Configuring Liquibase");
        }

        return multiTenantSpringLiquibase;
    }
}
