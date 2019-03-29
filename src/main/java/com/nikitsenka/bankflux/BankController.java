package com.nikitsenka.bankflux;

import com.nikitsenka.bankflux.model.Balance;
import com.nikitsenka.bankflux.model.Client;
import com.nikitsenka.bankflux.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class BankController {

    @Autowired
    private BankService service;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public String healthCheck(){
        return "{\"status\":\"Ok\"}";
    }

    @PostMapping("/client/new/{balance}")
    public Mono<Client> newClient(@PathVariable Integer balance){
        return service.newClient(balance);
    }

    @PostMapping("/transaction")
    public Mono<Transaction> newTransaction(@RequestBody Transaction transaction){
        return service.newTransaction(transaction);
    }

    @GetMapping("/client/{id}/balance")
    public Mono<Balance> getBalance(@PathVariable Integer id){
        return service.getBalance(id);
    }

}
