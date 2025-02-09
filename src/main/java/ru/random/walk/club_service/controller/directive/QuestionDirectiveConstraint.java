package ru.random.walk.club_service.controller.directive;

import graphql.GraphQLError;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.validation.constraints.AbstractDirectiveConstraint;
import graphql.validation.constraints.Documentation;
import graphql.validation.rules.ValidationEnvironment;
import org.springframework.stereotype.Component;
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.model.graphql.types.AnswerType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class QuestionDirectiveConstraint extends AbstractDirectiveConstraint {
    private static final String NAME = "ValidQuestion";

    public QuestionDirectiveConstraint() {
        super(NAME);
    }

    @Override
    protected boolean appliesToType(GraphQLInputType type) {
        return type instanceof GraphQLInputObjectType inputObject && inputObject.getName().equals("QuestionInput");
    }

    @Override
    protected List<GraphQLError> runConstraint(ValidationEnvironment env) {
        try {
            //noinspection unchecked
            var fieldMap = (Map<String, Object>) env.getValidatedValue();
            List<?> answerOptions = Optional.ofNullable(fieldMap.get("answerOptions"))
                    .map(obj -> (List<?>) obj)
                    .orElseThrow(() -> new ValidationException("QuestionInput must contains field answerOptions: [String!]!"));
            AnswerType answerType = Optional.ofNullable(fieldMap.get("answerType"))
                    .map(obj -> (String) obj)
                    .map(AnswerType::valueOf)
                    .orElseThrow(() -> new ValidationException("QuestionInput must contains field answerType: AnswerType!"));
            //noinspection unchecked
            List<Integer> correctOptionNumbers = Optional.ofNullable(fieldMap.get("correctOptionNumbers"))
                    .map(obj -> (List<Integer>) obj)
                    .orElseThrow(() -> new ValidationException("QuestionInput must contains field correctOptionNumbers: [Int!]!"));
            checkAnswerTypeMatchWithAnswer(answerType, correctOptionNumbers);
            checkAnswerIndices(answerOptions, correctOptionNumbers);
        } catch (ClassCastException ignored) {
            throw new ValidationException("""
                    QuestionInput must contains fields
                    answerOptions: [String!]!, answerType: AnswerType!
                    and correctOptionNumbers: [Int!]!""");
        }
        return Collections.emptyList();
    }

    private void checkAnswerIndices(List<?> answerOptions, List<Integer> correctOptionNumbers) {
        for (var index : correctOptionNumbers) {
            if (index < 0 || index >= answerOptions.size()) {
                throw new ValidationException("correctOptionNumbers must contains indices of answerOptions");
            }
        }
    }

    private void checkAnswerTypeMatchWithAnswer(AnswerType answerType, List<Integer> correctOptionNumbers) {
        if (answerType == AnswerType.SINGLE && correctOptionNumbers.size() != 1) {
            throw new ValidationException("correctOptionNumbers with SINGLE answer type must contain exactly one option");
        }
    }

    @Override
    protected boolean appliesToListElements() {
        return false;
    }

    @Override
    public Documentation getDocumentation() {
        return Documentation.newDocumentation()
                .description("""
                        # Valid question:
                        # - Has fields answerOptions: [String!]!, answerType: AnswerType! and correctOptionNumbers: [Int!]!
                        # - Answer type matches with correct option numbers size
                        # - Answer options matches with correct option numbers indices
                        """)
                .build();
    }
}
