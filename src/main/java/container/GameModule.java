package container;

import com.google.inject.AbstractModule;
import io.dropwizard.setup.Environment;
import services.GameService;
import services.PlayService;
import services.impl.GameServiceImpl;
import services.impl.PlayServiceImpl;


/**
 *
 * Module class for bindins across dropwizard
 */
public class GameModule extends AbstractModule {

    private AppConfig configuration;
    private Environment environment;

    public GameModule(AppConfig configuration, Environment environment) {

        this.configuration = configuration;
        this.environment = environment;
    }

    public GameModule() {

    }

    @Override
    protected void configure() {

        bind(GameService.class).to(GameServiceImpl.class);
        bind(PlayService.class).to(PlayServiceImpl.class);
    }
}
