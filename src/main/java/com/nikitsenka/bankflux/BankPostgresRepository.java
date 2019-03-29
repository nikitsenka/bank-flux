package com.nikitsenka.bankflux;

import com.nikitsenka.bankflux.model.Client;
import com.nikitsenka.bankflux.model.Transaction;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BankPostgresRepository {

    @Autowired
    private ConnectionFactory connectionFactory;

    /**
     * Creates new client.
     *
     * @param client
     * @return client id
     */
    public Mono<Integer> createClient(Client client) {
        return DatabaseClient.create(connectionFactory)
                .execute()
                .sql("INSERT INTO client(name, email, phone) VALUES ($1, $2, $3) RETURNING id")
                .bind("$1", client.getName())
                .bind("$2", client.getEmail())
                .bind("$3", client.getPhone())
                .map((row, rowMetadata) -> row.get("id", Integer.class))
                .first();
    }

    /**
     * Creates new transaction
     *
     * @param transaction
     * @return transaction id
     */
    public Mono<Integer> createTransaction(Transaction transaction) {
        return DatabaseClient.create(connectionFactory)
                .execute()
                .sql("INSERT INTO transaction(from_client_id, to_client_id, amount) VALUES ($1, $2, $3) RETURNING id")
                .bind("$1", transaction.getFromClientId())
                .bind("$2", transaction.getToClientId())
                .bind("$3", transaction.getAmount())
                .map((row, rowMetadata) -> row.get("id", Integer.class))
                .first();
    }


    /**
     * Returns balance for the client
     *
     * @param clientId
     * @return balance
     */
    public Mono<Long> getBalance(Integer clientId) {
        return DatabaseClient.create(connectionFactory).execute()
                .sql(balanceSql(clientId))
                .map((row, rowMetadata) -> row.get("balance", Long.class))
                .first();
    }

    private String balanceSql(final Integer clientId) {
        return String.format("SELECT debit - credit as balance FROM " +
                        "(SELECT COALESCE(sum(amount), 0) AS debit FROM transaction WHERE to_client_id = %s ) a, " +
                        "(SELECT COALESCE(sum(amount), 0) AS credit FROM transaction WHERE from_client_id = %s ) b;",
                clientId, clientId);
    }

    public void setConnectionFactory(final ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
