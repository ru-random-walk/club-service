package ru.random.walk.club_service.controller.directive;

import graphql.GraphQLError;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.validation.constraints.AbstractDirectiveConstraint;
import graphql.validation.constraints.Documentation;
import graphql.validation.rules.ValidationEnvironment;
import org.springframework.stereotype.Component;
import ru.random.walk.club_service.model.exception.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MembersConfirmDirectiveConstraint extends AbstractDirectiveConstraint {
    private static final String NAME = "ValidMembersConfirm";

    public MembersConfirmDirectiveConstraint() {
        super(NAME);
    }

    @Override
    protected boolean appliesToType(GraphQLInputType type) {
        return type instanceof GraphQLInputObjectType inputType &&
                inputType.getName().equals("MembersConfirmInput");
    }

    @Override
    protected List<GraphQLError> runConstraint(ValidationEnvironment env) {
        try {
            Map<String, Object> fieldMap = getFieldMap(env);
            Integer requiredConfirmationNumber = getRequiredConfirmationNumber(fieldMap);
            Optional<Integer> approversToNotifyCount = getApproversToNotifyCount(fieldMap);
            if (
                    approversToNotifyCount.isPresent() &&
                            approversToNotifyCount.get() < requiredConfirmationNumber
            ) {
                throw new ValidationException(
                        "approversToNotifyCount must be bigger or equals than requiredConfirmationNumber"
                );
            }
        } catch (ClassCastException ignored) {
            throw new ValidationException("""
                    MembersConfirmInput must contains fields
                    requiredConfirmationNumber: Int! and approversToNotifyCount: Int
                    """);
        }
        return Collections.emptyList();
    }

    private static Map<String, Object> getFieldMap(ValidationEnvironment env) {
        //noinspection unchecked
        return (Map<String, Object>) env.getValidatedValue();
    }

    private static Integer getRequiredConfirmationNumber(Map<String, Object> fieldMap) {
        return Optional.ofNullable(fieldMap.get("requiredConfirmationNumber"))
                .map(obj -> (Integer) obj)
                .orElseThrow(() -> new ValidationException(
                        "MembersConfirmInput must contains field requiredConfirmationNumber: Int!"
                ));
    }

    private static Optional<Integer> getApproversToNotifyCount(Map<String, Object> fieldMap) {
        return Optional.ofNullable(fieldMap.get("approversToNotifyCount"))
                .map(obj -> (Integer) obj);
    }

    @Override
    protected boolean appliesToListElements() {
        return false;
    }

    @Override
    public Documentation getDocumentation() {
        return Documentation.newDocumentation()
                .description("""
                        # Valid Members confirm input
                        # - approversToNotifyCount must be bigger or equals than requiredConfirmationNumber
                        """)
                .build();
    }
}
