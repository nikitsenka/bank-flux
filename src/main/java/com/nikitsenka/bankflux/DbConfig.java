package com.nikitsenka.bankflux;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

@Configuration
public class DbConfig {

    @Value("${POSTGRES_HOST:localhost}")
    private String host;

    @Value("${postgres.db.user:postgres}")
    private String user;

    @Value("${postgres.db.password:test1234}")
    private String password;

    @Value("${postgres.db.name:postgres}")
    private String name;

    @Bean
    public ConnectionFactory connectionFactory() {

        ConnectionFactory connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "postgresql")
                .option(HOST, host)
                .option(USER, user)
                .option(PASSWORD, password)
                .option(PORT, 5432)
                .option(DATABASE, name)
                .build());

        return connectionFactory;
    }
}
