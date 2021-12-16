package com.nttdata.consumption.service;

import com.nttdata.consumption.model.entity.Association;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAssociationService {
    public Flux<Association> findByCardNumberAndStatus(String cardNumber, String status);
}
