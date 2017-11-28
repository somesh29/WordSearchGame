package container;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import resources.GameResource;

public class App extends Application<AppConfig> {

    @Override
    public void run(AppConfig appConfig, Environment environment) throws Exception {

        Injector injector =
                Guice.createInjector(new GameModule(appConfig, environment));
        environment.jersey().register(injector.getInstance(GameResource.class));
    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<AppConfig>() {
            @Override
            public SwaggerBundleConfiguration getSwaggerBundleConfiguration(AppConfig configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });

    }

    public static void main(String args[]) throws Exception {
        new App().run(args);
    }
}


