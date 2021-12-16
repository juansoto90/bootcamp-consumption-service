package com.nttdata.consumption.handler;

import com.nttdata.consumption.exception.ConsumptionException;
import com.nttdata.consumption.exception.messageException;
import com.nttdata.consumption.model.dto.ConsumptionDto;
import com.nttdata.consumption.model.entity.Account;
import com.nttdata.consumption.model.entity.Association;
import com.nttdata.consumption.model.entity.Consumption;
import com.nttdata.consumption.model.entity.Movement;
import com.nttdata.consumption.service.IAccountService;
import com.nttdata.consumption.service.IAssociationService;
import com.nttdata.consumption.service.IConsumptionService;
import com.nttdata.consumption.service.IMovementService;
import com.nttdata.consumption.util.Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class ConsumptionHandler {

    private final IConsumptionService service;
    private final IAssociationService iAssociationService;
    private final IAccountService iAccountService;
    private final IMovementService iMovementService;

    public Mono<ServerResponse> create(ServerRequest request){
        Mono<Consumption> consumptionMono = request.bodyToMono(Consumption.class);
        return consumptionMono
                .flatMap(service::save)
                .flatMap(c -> ServerResponse.created(URI.create("/consumption/".concat(c.getId())))
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(c)
                );
    }

    public Mono<ServerResponse> findById(ServerRequest request){
        String id = request.pathVariable("id");
        return service.findById(id)
                .flatMap(c -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(c)
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> consumptionAdd(ServerRequest request){
        Mono<ConsumptionDto> consumptionDtoMono = request.bodyToMono(ConsumptionDto.class);
        Consumption consumption = new Consumption();
        Association association = new Association();
        Movement movement = new Movement();
        return consumptionDtoMono
                .map(dto -> {
                    consumption.setAmount(dto.getAmount());
                    movement.setConcept(dto.getConcept());
                    return dto;
                })
                .flatMap(dto -> iAssociationService.findByCardNumberAndStatus(dto.getCardNumber(), "ASSOCIATED")
                        .collectList()
                        .flatMap(list -> {
                            if (list.size() == 0){
                                return Mono.error(new WebClientResponseException(
                                        400,messageException.cardNotAssociated(),null,null,null)
                                );
                            } else if (list.size() > 1){
                                return Mono.error(new WebClientResponseException(
                                        400,messageException.cardWithManyAssociatedAccounts(), null, null, null));
                            } else {
                                association.setCardNumber(list.get(0).getCardNumber());
                                association.setCardType(list.get(0).getCardType());
                                association.setAccountNumber(list.get(0).getAccountNumber());
                                return Mono.just(dto);
                            }
                        })
                )
                .flatMap(dto -> iAccountService.findByAccountNumber(association.getAccountNumber()))
                .flatMap(acc -> {
                    double availableCredit = acc.getCreditLine() - acc.getConsumption();
                    if (consumption.getAmount() > availableCredit){
                        return Mono.error(new WebClientResponseException(
                                400,messageException.noCreditAvailable(),null,null,null));
                    }
                    double consumptionNew = acc.getConsumption() + consumption.getAmount();
                    Account account = new Account();
                    account.setAccountNumber(acc.getAccountNumber());
                    account.setConsumption(consumptionNew);
                    return iAccountService.updateConsumptionAccount(account)
                            .flatMap(a -> {
                                Consumption c = new Consumption();
                                c.setOperationNumber(Generator.generateOperationNumber());
                                c.setAmount(consumption.getAmount());
                                c.setAccount(acc);
                                c.setStatus("PROCESSED");
                                return service.save(c)
                                        .flatMap(cons -> {
                                            Movement m = new Movement();
                                            m.setOperationNumber(cons.getOperationNumber());
                                            m.setAccountNumber(acc.getAccountNumber());
                                            m.setCardNumber(association.getCardNumber());
                                            m.setMovementType("CONSUMPTION");
                                            m.setAccountType(acc.getAccountType());
                                            m.setCardType(association.getCardType());
                                            m.setDocumentNumber(acc.getCustomer().getDocumentNumber());
                                            m.setAmount(consumption.getAmount());
                                            m.setConcept(movement.getConcept());
                                            m.setStatus("PROCESSED");
                                            return iMovementService.save(m)
                                                    .flatMap(mo -> Flux.just(a, cons, mo).collectList());
                                        }) ;
                            });
                })
                //.flatMap(c -> ServerResponse.created(URI.create("/consumption/".concat(c.getId())))
                .flatMap(c -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(c)
                )
                .onErrorResume(ConsumptionException::errorHandler);
    }

}
