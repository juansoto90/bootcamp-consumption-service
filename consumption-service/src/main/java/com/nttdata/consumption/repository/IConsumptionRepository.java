package com.nttdata.consumption.repository;

import com.nttdata.consumption.model.entity.Consumption;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IConsumptionRepository extends ReactiveMongoRepository<Consumption, String> {
}
