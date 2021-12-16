package com.nttdata.consumption.service;

import com.nttdata.consumption.model.entity.Consumption;
import reactor.core.publisher.Mono;

public interface IConsumptionService {
    public Mono<Consumption> save(Consumption consumption);
    public Mono<Consumption> findById(String id);
}
