package com.nikitsenka.bankflux;

import com.nikitsenka.bankflux.model.Balance;
import com.nikitsenka.bankflux.model.Client;
import com.nikitsenka.bankflux.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BankService {

    @Autowired
    private BankPostgresRepository repository;

    public Mono<Client> newClient(Integer balance) {
        Client client = new Client(0, "", "", "");
        return repository.createClient(client)
                .flatMap(clientId -> {
                    client.setId(clientId);
                    return repository.createTransaction(new Transaction(0, 0, clientId, balance));
                })
                .map(transactionId -> client);
    }

    public Mono<Transaction> newTransaction(Transaction transaction) { ;
        return repository.createTransaction(transaction)
                .map(transId -> {
                    transaction.setId(transId);
                    return transaction;
                });
    }

    public Mono<Balance> getBalance(Integer clientId) {
        return repository.getBalance(clientId)
                .map(balance -> new Balance(balance));
    }
}
