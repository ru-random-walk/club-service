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
            Map<String, Object> fieldMap = getFieldMap(env);
            List<?> answerOptions = getAnswerOptions(fieldMap);
            AnswerType answerType = getAnswerType(fieldMap);
            List<Integer> correctOptionNumbers = getCorrectOptionNumbers(fieldMap);
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

    private static List<Integer> getCorrectOptionNumbers(Map<String, Object> fieldMap) {
        //noinspection unchecked
        return Optional.ofNullable(fieldMap.get("correctOptionNumbers"))
                .map(obj -> (List<Integer>) obj)
                .orElseThrow(() -> new ValidationException("QuestionInput must contains field correctOptionNumbers: [Int!]!"));
    }

    private static AnswerType getAnswerType(Map<String, Object> fieldMap) {
        return Optional.ofNullable(fieldMap.get("answerType"))
                .map(obj -> (String) obj)
                .map(AnswerType::valueOf)
                .orElseThrow(() -> new ValidationException("QuestionInput must contains field answerType: AnswerType!"));
    }

    private static List<?> getAnswerOptions(Map<String, Object> fieldMap) {
        return Optional.ofNullable(fieldMap.get("answerOptions"))
                .map(obj -> (List<?>) obj)
                .orElseThrow(() -> new ValidationException("QuestionInput must contains field answerOptions: [String!]!"));
    }

    private static Map<String, Object> getFieldMap(ValidationEnvironment env) {
        //noinspection unchecked
        return (Map<String, Object>) env.getValidatedValue();
    }

    private void checkAnswerIndices(List<?> answerOptions, List<Integer> correctOptionNumbers) {
        for (var index : correctOptionNumbers) {
            if (index < 0 || index >= answerOptions.size()) {
                throw new ValidationException("correctOptionNumbers must contains indices of answerOptions");
            }
        }
    }

    private void checkAnswerTypeMatchWithAnswer(AnswerType answerType, List<Integer> correctOptionNumbers) {
        if (answerType == AnswerType.SINGLE && correctOptionNumbers.size() > 1) {
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
