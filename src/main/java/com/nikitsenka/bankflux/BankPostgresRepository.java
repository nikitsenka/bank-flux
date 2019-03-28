package com.nikitsenka.bankflux;

import com.nikitsenka.bankflux.model.Balance;
import com.nikitsenka.bankflux.model.Client;
import com.nikitsenka.bankflux.model.Transaction;
import io.r2dbc.client.R2dbc;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

@Repository
public class BankPostgresRepository {

    @Value("${POSTGRES_HOST:localhost}")
    private String host;

    @Value("${postgres.db.user:postgres}")
    private String user;

    @Value("${postgres.db.password:test1234}")
    private String password;

    @Value("${postgres.db.name:postgres}")
    private String name;

    /**
     * Creates new client.
     *
     * @param client
     * @return client id
     */
    public Flux<Integer> createClient(Client client) {
        R2dbc r2dbc = new R2dbc(getConnectionFactory());
        r2dbc.inTransaction(handle ->
                handle.createQuery("INSERT INTO client(name, email, phone) VALUES ($1, $2, $3)")
                        .bind("$1", client.getName())
                        .bind("$2", client.getEmail())
                        .bind("$3", client.getPhone())
                        .mapResult(Result::getRowsUpdated))
                        .next();

        return r2dbc
                .inTransaction(handle -> handle.select(
                        "SELECT id FROM client")
                        .mapRow((row, rowMetadata) -> row.get("id", Integer.class)));
    }

    /**
     * Creates new transaction
     *
     * @param transaction
     * @return transaction id
     */
    public Flux<Integer> createTransaction(Transaction transaction) {
        return null;
    }


    /**
     * Returns balance for the client
     *
     * @param clientId
     * @return balance
     */
    public Balance getBalance(Integer clientId) {
        return null;
    }

    private ConnectionFactory getConnectionFactory() {

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
