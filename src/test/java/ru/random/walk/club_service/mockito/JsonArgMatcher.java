package ru.random.walk.club_service.mockito;

import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.mockito.ArgumentMatcher;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.mockito.ArgumentMatchers.argThat;

@AllArgsConstructor
public class JsonArgMatcher implements ArgumentMatcher<String> {
    private final String expectedJsonPayload;

    public static String jsonEq(String expectedJsonPayload) {
        return argThat(new JsonArgMatcher(expectedJsonPayload));
    }

    @Override
    public boolean matches(String actualJsonPayload) {
        try {
            JSONAssert.assertEquals(
                    expectedJsonPayload,
                    actualJsonPayload,
                    JSONCompareMode.STRICT
            );
        } catch (JSONException e) {
            return false;
        }
        return true;
    }
}
