package com.nttdata.consumption.service;

import com.nttdata.consumption.model.entity.Movement;
import reactor.core.publisher.Mono;

public interface IMovementService {
    public Mono<Movement> save(Movement movement);
}
