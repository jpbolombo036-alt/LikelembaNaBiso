package com.example.demo.Config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapte la variable d'environnement fournie par Railway ({@code DATABASE_URL},
 * au format {@code postgresql://user:pass@host:port/db}) en configuration JDBC
 * utilisable par Spring Boot (préfixe {@code jdbc:} + {@code sslmode=require}).
 *
 * Aucun effet en local (où DATABASE_URL est absent) : la configuration de
 * application.properties est alors utilisée telle quelle.
 */
public class RailwayEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }

        String jdbcUrl = databaseUrl.startsWith("jdbc:")
                ? databaseUrl
                : "jdbc:" + databaseUrl;

        if (!jdbcUrl.contains("sslmode=")) {
            jdbcUrl = jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
        }

        Map<String, Object> overrides = new HashMap<>();
        overrides.put("spring.datasource.url", jdbcUrl);

        // Extraire les identifiants de l'URL pour ne pas utiliser les valeurs par défaut
        try {
            URI uri = new URI(databaseUrl.replaceFirst("^postgresql://", "postgres://"));
            String userInfo = uri.getUserInfo();
            if (userInfo != null && !userInfo.isBlank()) {
                String[] parts = userInfo.split(":", 2);
                overrides.put("spring.datasource.username", parts[0]);
                if (parts.length == 2) {
                    overrides.put("spring.datasource.password", parts[1]);
                }
            }
        } catch (Exception e) {
            // On garde l'URL JDBC telle quelle ; les identifiants peuvent être fournis
            // séparément via DATABASE_USERNAME / DATABASE_PASSWORD si besoin.
        }

        MapPropertySource source = new MapPropertySource("railwayDatabaseOverride", overrides);
        environment.getPropertySources().addFirst(source);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
