package com.nikitsenka.bankflux;

import com.nikitsenka.bankflux.model.Balance;
import com.nikitsenka.bankflux.model.Client;
import com.nikitsenka.bankflux.model.Transaction;
import io.r2dbc.client.R2dbc;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;

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

    private R2dbc r2dbc;

    @PostConstruct
    public void init() {
        r2dbc = getR2dbc();
    }


    /**
     * Creates new client.
     *
     * @param client
     * @return client id
     */
    public Flux<Integer> createClient(Client client) {
        return r2dbc.inTransaction(handle ->
                handle.execute("INSERT INTO client(name, email, phone) VALUES (?, ?, ?)",
                        client.getName(), client.getEmail(), client.getPhone()))
                .thenMany(r2dbc.inTransaction(handle ->
                        handle.select("SELECT id FROM client")
                                .mapResult(result -> result.map((row, rowMetadata) -> row.get("id", Integer.class)))));
    }

    /**
     * Creates new transaction
     *
     * @param transaction
     * @return transaction id
     */
    public Flux<Integer> createTransaction(Transaction transaction) {
        return r2dbc.inTransaction(handle ->
                handle.execute("INSERT INTO transaction(from_client_id, to_client_id, amount) VALUES (?, ?, ?)",
                        transaction.getFromClientId(), transaction.getToClientId(), transaction.getAmount()))
                .thenMany(r2dbc.inTransaction(handle ->
                        handle.select("SELECT id FROM transaction")
                                .mapResult(result -> result.map((row, rowMetadata) -> row.get("id", Integer.class)))));
    }


    /**
     * Returns balance for the client
     *
     * @param clientId
     * @return balance
     */
    public Balance getBalance(Integer clientId) {
        Balance balance = new Balance();
        r2dbc.inTransaction(handle -> handle.select("SELECT debit - credit FROM (SELECT COALESCE(sum(amount), 0) AS debit FROM transaction WHERE to_client_id = ? ) a, ( SELECT COALESCE(sum(amount), 0) AS credit FROM transaction WHERE from_client_id = ? ) b;",
                clientId).mapResult(result -> result.map((row, rowMetadata) -> row.get("debit", Integer.class))))
                .subscribe(debit -> balance.setBalance(debit));
        return balance;
    }

    private R2dbc getR2dbc() {
        PostgresqlConnectionConfiguration configuration = PostgresqlConnectionConfiguration.builder()
                .host(host)
                .database(name)
                .username(user)
                .password(password)
                .build();
        return new R2dbc(new PostgresqlConnectionFactory(configuration));
    }


}
