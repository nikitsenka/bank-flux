package com.nikitsenka.bankflux;

import com.nikitsenka.bankflux.model.Client;
import com.nikitsenka.bankflux.model.Transaction;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.jdbc.core.JdbcOperations;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BankPostgresRepositoryTest {

    @RegisterExtension
    static final PostgresqlServerExtension SERVER = new PostgresqlServerExtension();

    private BankPostgresRepository repository = new BankPostgresRepository();

    private ConnectionFactory getConnectionFactory() {
        ConnectionFactory connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "postgresql")
                .option(HOST, SERVER.getHost())
                .option(USER, SERVER.getUsername())
                .option(PASSWORD, SERVER.getPassword())
                .option(PORT, SERVER.getPort())
                .option(DATABASE, SERVER.getDatabase())
                .build());
        return connectionFactory;
    }


    @BeforeEach
    void createTable() {
        repository.setConnectionFactory(getConnectionFactory());
        getJdbcOperations().execute("CREATE TABLE test ( value INTEGER )");
        getJdbcOperations().execute("CREATE TABLE client(id SERIAL PRIMARY KEY NOT NULL, name VARCHAR(20), email VARCHAR(20), phone VARCHAR(20));");
        getJdbcOperations().execute("CREATE TABLE transaction(id SERIAL PRIMARY KEY NOT NULL, from_client_id INTEGER, to_client_id INTEGER, amount INTEGER)");
    }

    @AfterEach
    void dropTable() {
        getJdbcOperations().execute("DROP TABLE test");
        getJdbcOperations().execute("DROP TABLE client");
        getJdbcOperations().execute("DROP TABLE transaction");
    }

    @Test
    void createClient() {

        Integer result = repository.createClient(new Client(0, "", "", "")).block();

        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    void createTransaction() {
        Integer transaction = repository.createTransaction(new Transaction(0, 1, 2, 100)).block();

        assertEquals(Integer.valueOf(1), transaction);
    }

    @Test
    void getBalance() {
        Integer firstClient = repository.createClient(new Client(0, "", "", "")).block();
        Integer secondClient = repository.createClient(new Client(0, "", "", "")).block();
        repository.createTransaction(new Transaction(0, firstClient, secondClient, 100)).block();
        Long firstClientBalance = repository.getBalance(firstClient).block();
        assertEquals(Long.valueOf(-100), firstClientBalance);
        Long secondClientBalance = repository.getBalance(secondClient).block();
        assertEquals(Long.valueOf(100), secondClientBalance);
    }

    public JdbcOperations getJdbcOperations() {
        JdbcOperations jdbcOperations = SERVER.getJdbcOperations();

        if (jdbcOperations == null) {
            throw new IllegalStateException("JdbcOperations not yet initialized");
        }

        return jdbcOperations;
    }


}