package example.micronaut;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.*;
import java.sql.DriverManager;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({OtelDriverExtension.class})
@Inherited
@interface JDbcOtelDriveWithTestContainers {
}
class OtelDriverExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        DriverManager.registerDriver(new org.testcontainers.jdbc.ContainerDatabaseDriver());
        //OpenTelemetryDriver.addDriverCandidate(new org.testcontainers.jdbc.ContainerDatabaseDriver());
    }
}
