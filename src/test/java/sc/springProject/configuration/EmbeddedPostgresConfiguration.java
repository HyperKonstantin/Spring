package sc.springProject.configuration;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import sc.springProject.entities.User;
import sc.springProject.repositories.UserRepo;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Locale;

@Configuration
@EnableJpaRepositories(basePackageClasses = UserRepo.class)
@EntityScan(basePackageClasses = User.class)
public class EmbeddedPostgresConfiguration {
    private static EmbeddedPostgres embeddedPostgres;

    @Primary
    @Bean
    public DataSource dataSource() throws IOException {
        embeddedPostgres = EmbeddedPostgres.builder()
                .start();
        new JdbcTemplate(embeddedPostgres.getPostgresDatabase()).execute(
                "CREATE USER testuser SUPERUSER PASSWORD '12345';");
        return embeddedPostgres.getPostgresDatabase();
    }

    public static class EmbeddedPostgresExtension implements AfterAllCallback {
        @Override
        public void afterAll(ExtensionContext context) throws Exception {
            if (embeddedPostgres == null) {
                return;
            }
            embeddedPostgres.close();
        }
    }
}
