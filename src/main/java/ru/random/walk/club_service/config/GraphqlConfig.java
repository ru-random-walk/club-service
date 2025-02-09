package ru.random.walk.club_service.config;

import graphql.validation.rules.OnValidationErrorStrategy;
import graphql.validation.rules.ValidationRule;
import graphql.validation.rules.ValidationRules;
import graphql.validation.schemawiring.ValidationSchemaWiring;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.util.List;

@Configuration
@AllArgsConstructor
public class GraphqlConfig {
    private final List<ValidationRule> validationRules;

    @Bean
    RuntimeWiringConfigurer runtimeWiringConfigurer() {
        var rulesBuilder = ValidationRules.newValidationRules()
                .onValidationErrorStrategy(OnValidationErrorStrategy.RETURN_NULL);
        for (var rule : validationRules) {
            rulesBuilder.addRule(rule);
        }
        var validationSchema = new ValidationSchemaWiring(rulesBuilder.build());
        return configBuilder -> configBuilder
                .directiveWiring(validationSchema)
                .build();
    }
}
