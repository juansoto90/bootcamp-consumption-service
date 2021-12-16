package com.nttdata.consumption.config;

import com.nttdata.consumption.handler.ConsumptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> routes(ConsumptionHandler handler){
        return route(GET("/consumption/{id}"), handler::findById)
                .andRoute(POST("/consumption"), handler::create)
                .andRoute(POST("/consumption/add"), handler::consumptionAdd);
    }
}
