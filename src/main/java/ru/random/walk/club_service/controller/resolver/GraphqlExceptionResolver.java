package ru.random.walk.club_service.controller.resolver;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import ru.random.walk.club_service.model.exception.AuthenticationException;
import ru.random.walk.club_service.model.exception.NotFoundException;

@Component
public class GraphqlExceptionResolver extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, @NotNull DataFetchingEnvironment env) {
        return switch (ex) {
            case NotFoundException notFound -> GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(notFound.getMessage())
                    .build();
            case AuthenticationException authentication -> GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.UNAUTHORIZED)
                    .message(authentication.getMessage())
                    .build();
            default -> null;
        };
    }
}
