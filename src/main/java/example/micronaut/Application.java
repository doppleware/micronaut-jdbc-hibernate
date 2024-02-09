package example.micronaut;

import io.micronaut.configuration.jdbc.hikari.DatasourceFactory;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.context.event.BeanInitializedEventListener;
import io.micronaut.context.event.BeanInitializingEvent;
import io.micronaut.core.annotation.Order;
import io.micronaut.jdbc.DataSourceResolver;
import io.micronaut.runtime.Micronaut;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.jdbc.OpenTelemetryDriver;
import io.opentelemetry.instrumentation.jdbc.datasource.OpenTelemetryDataSource;
import jakarta.inject.Singleton;
import io.opentelemetry.sdk.OpenTelemetrySdkBuilder;
import javax.sql.DataSource;

@Factory
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }


        @Singleton
        @Context
        @Order(-1)
        OtelMarkupCofig otel(OpenTelemetry telemetry) {
            GlobalOpenTelemetry.set(telemetry);
            return new OtelMarkupCofig();
        }

        class OtelMarkupCofig{

        }


}