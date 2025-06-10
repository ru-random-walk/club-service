package ru.random.walk.club_service.mockito;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;

public class JsonArgMatcher {
    public static String jsonEq(String expectedJsonPayload) {
        return argThat(actualJsonPayload -> jsonStrEquals(expectedJsonPayload, actualJsonPayload));
    }

    public static String jsonEqAny(List<String> expectedJsonPayloads) {
        return argThat(actualJsonPayload -> {
            for (var expected : expectedJsonPayloads) {
                if (jsonStrEquals(expected, actualJsonPayload)) {
                    return true;
                }
            }
            return false;
        });
    }

    private static boolean jsonStrEquals(String expectedJsonPayload, String actualJsonPayload) {
        try {
            JSONAssert.assertEquals(
                    expectedJsonPayload,
                    actualJsonPayload,
                    JSONCompareMode.STRICT
            );
        } catch (JSONException | Error e) {
            return false;
        }
        return true;
    }
}
