package com.nikitsenka.bankflux;

import com.nikitsenka.bankflux.model.Balance;
import com.nikitsenka.bankflux.model.Client;
import com.nikitsenka.bankflux.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class BankService {

    @Autowired
    private BankPostgresRepository repository;

    public Flux<Integer> newClient(Integer balance) {

        return repository.createClient(new Client(0, "", "", ""));
    }

    public Transaction newTransaction(Transaction transaction) {
        repository.createTransaction(transaction)
                .subscribe(id -> transaction.setId(id));
        return transaction;
    }

    public Balance getBalance(Integer clientId) {
        return repository.getBalance(clientId);
    }
}
