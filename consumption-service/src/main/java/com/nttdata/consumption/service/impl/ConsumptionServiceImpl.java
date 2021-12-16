package com.nttdata.consumption.service.impl;

import com.nttdata.consumption.model.entity.Consumption;
import com.nttdata.consumption.repository.IConsumptionRepository;
import com.nttdata.consumption.service.IConsumptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConsumptionServiceImpl implements IConsumptionService {

    private final IConsumptionRepository repository;

    @Override
    public Mono<Consumption> save(Consumption consumption) {
        return repository.save(consumption);
    }

    @Override
    public Mono<Consumption> findById(String id) {
        return repository.findById(id);
    }
}
