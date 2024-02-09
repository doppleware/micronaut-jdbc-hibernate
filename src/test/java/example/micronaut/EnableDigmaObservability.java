package example.micronaut;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.*;
import java.util.Dictionary;
import java.util.Hashtable;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({EnableDigmaObservability.class})
@Inherited
@interface EnableOtelTests {
}
class EnableDigmaObservability implements BeforeEachCallback, AfterEachCallback {

    private final Dictionary<String, SpanInfo> spanDictionary;

    public EnableDigmaObservability() {
        spanDictionary = new Hashtable<>();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {

        String testFullName = getTestFullName(extensionContext);
        Span span = GlobalOpenTelemetry
                .getTracerProvider().get("com.digma.junit")
                .spanBuilder(testFullName).startSpan();
        Scope scope = span.makeCurrent();
        span.setAttribute("code.function", extensionContext.getTestMethod().get().getName());
        span.setAttribute("code.namespace", extensionContext.getTestClass().get().getName());
        span.setAttribute("testing.framework", "junit");
        spanDictionary.put(testFullName, new SpanInfo(span, scope));
    }

    @NotNull
    private static String getTestFullName(ExtensionContext extensionContext) {
        var className = extensionContext.getTestClass().get().getSimpleName();
        var methodName = extensionContext.getTestMethod().get().getName();
        return className + "." + methodName;
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        String testFullName = getTestFullName(extensionContext);
        var info = spanDictionary.get(testFullName);

        var failure = extensionContext.getExecutionException().isPresent();
        if (failure) {
            info.getSpan().recordException(extensionContext.getExecutionException().get());
            info.getSpan().setAttribute("testing.result", "failure");
        } else {
            info.getSpan().setAttribute("testing.result", "success");

        }
        info.getSpan().end();
        info.getScope().close();
    }

    static class SpanInfo {
        private final Span span;
        private final Scope scope;

        public SpanInfo(Span span, Scope scope) {
            this.span = span;
            this.scope = scope;
        }

        public Span getSpan() {
            return span;
        }

        public Scope getScope() {
            return scope;
        }
    }
}
