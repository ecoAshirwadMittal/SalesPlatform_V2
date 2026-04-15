package com.ecoatm.salesplatform.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Smoke test validating the Phase 0 async + event foundation:
 *
 * <ul>
 *   <li>Both executor beans load from {@link AsyncConfig}.</li>
 *   <li>{@code @TransactionalEventListener(AFTER_COMMIT)} fires when the
 *       originating transaction commits.</li>
 *   <li>The same listener is NOT invoked when the transaction rolls back —
 *       a critical guarantee for email/Snowflake side effects.</li>
 * </ul>
 */
@Import({
    AsyncEventFoundationTest.SmokeTestConfig.class,
    AsyncEventFoundationTest.SmokePublisher.class
})
class AsyncEventFoundationTest extends PostgresIntegrationTest {

    @Autowired private SmokePublisher publisher;
    @Autowired private SmokeTestConfig.EventRecorder recorder;

    @Autowired
    @Qualifier(AsyncConfig.EMAIL_EXECUTOR)
    private Executor emailExecutor;

    @Autowired
    @Qualifier(AsyncConfig.SNOWFLAKE_EXECUTOR)
    private Executor snowflakeExecutor;

    @BeforeEach
    void resetRecorder() {
        recorder.received.clear();
    }

    @Test
    @DisplayName("both executor beans load and are ThreadPoolTaskExecutor instances")
    void executors_loaded() {
        assertThat(emailExecutor).isInstanceOf(ThreadPoolTaskExecutor.class);
        assertThat(snowflakeExecutor).isInstanceOf(ThreadPoolTaskExecutor.class);
    }

    @Test
    @DisplayName("AFTER_COMMIT listener fires when transaction commits")
    void listener_firesOnCommit() {
        publisher.publishAndCommit("hello");

        assertThat(recorder.received).containsExactly("hello");
    }

    @Test
    @DisplayName("AFTER_COMMIT listener does NOT fire when transaction rolls back")
    void listener_doesNotFireOnRollback() {
        assertThatThrownBy(() -> publisher.publishAndRollback("should-not-arrive"))
                .isInstanceOf(IllegalStateException.class);

        assertThat(recorder.received).isEmpty();
    }

    /** Minimal event type used only by this smoke test. */
    record SmokeEvent(String payload) {}

    /**
     * Test-scoped publisher. Inner static class so it only enters the context
     * when this test's {@code @Import} is applied.
     */
    @Component
    static class SmokePublisher {
        private final ApplicationEventPublisher events;

        SmokePublisher(ApplicationEventPublisher events) {
            this.events = events;
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void publishAndCommit(String payload) {
            events.publishEvent(new SmokeEvent(payload));
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void publishAndRollback(String payload) {
            events.publishEvent(new SmokeEvent(payload));
            throw new IllegalStateException("forced rollback");
        }
    }

    @TestConfiguration
    static class SmokeTestConfig {

        @Component
        static class EventRecorder {
            final List<String> received = new CopyOnWriteArrayList<>();

            @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
            public void onEvent(SmokeEvent event) {
                received.add(event.payload());
            }
        }
    }
}
