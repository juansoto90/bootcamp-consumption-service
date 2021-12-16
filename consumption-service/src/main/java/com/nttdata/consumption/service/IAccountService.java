package com.nttdata.consumption.service;

import com.nttdata.consumption.model.entity.Account;
import reactor.core.publisher.Mono;

public interface IAccountService {
    public Mono<Account> updateConsumptionAccount(Account account);
    public Mono<Account> findByAccountNumber(String accountNumber);
}
