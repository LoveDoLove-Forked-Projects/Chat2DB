package ai.chat2db.community.jcef.utils;

import org.cef.callback.CefQueryCallback;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CancellableCefQueryCallbackTest {

    @Test
    void cancellationSuppressesLateCompletion() {
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();
        CancellableCefQueryCallback callback = new CancellableCefQueryCallback(callback(successCount, failureCount));

        callback.cancel();
        callback.success("late");
        callback.failure(500, "late");

        assertEquals(0, successCount.get());
        assertEquals(0, failureCount.get());
    }

    @Test
    void onlyTheFirstCompletionReachesTheNativeCallback() {
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();
        AtomicReference<String> response = new AtomicReference<>();
        CancellableCefQueryCallback callback = new CancellableCefQueryCallback(new CefQueryCallback() {
            @Override
            public void success(String value) {
                successCount.incrementAndGet();
                response.set(value);
            }

            @Override
            public void failure(int errorCode, String errorMessage) {
                failureCount.incrementAndGet();
            }
        });

        callback.success("first");
        callback.success("second");
        callback.failure(500, "late");

        assertEquals(1, successCount.get());
        assertEquals("first", response.get());
        assertEquals(0, failureCount.get());
    }

    private CefQueryCallback callback(AtomicInteger successCount, AtomicInteger failureCount) {
        return new CefQueryCallback() {
            @Override
            public void success(String response) {
                successCount.incrementAndGet();
            }

            @Override
            public void failure(int errorCode, String errorMessage) {
                failureCount.incrementAndGet();
            }
        };
    }
}
