package ru.random.walk.club_service.controller.resolver;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import ru.random.walk.club_service.model.exception.AuthenticationException;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.model.exception.ValidationException;

@Component
@Slf4j
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
            case ValidationException validation -> GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(validation.getMessage())
                    .build();
            default -> {
                log.error(ex.getMessage(), ex);
                yield GraphqlErrorBuilder.newError()
                        .message("Unknown internal error!")
                        .errorType(ErrorType.INTERNAL_ERROR)
                        .build();
            }
        };
    }
}
