package com.nikitsenka.bankflux;

import com.nikitsenka.bankflux.model.Client;
import com.nikitsenka.bankflux.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BankService {

    @Autowired
    private BankPostgresRepository repository;

    public Mono<Integer> newClient(Integer balance) {

        return repository.createClient(new Client(0, "", "", ""));
    }

    public Mono<Integer> newTransaction(Transaction transaction) { ;
        return repository.createTransaction(transaction);
    }

    public Mono<Long> getBalance(Integer clientId) {
        return repository.getBalance(clientId);
    }
}
