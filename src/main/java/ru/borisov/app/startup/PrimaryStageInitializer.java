package ru.borisov.app.startup;

import javafx.scene.Scene;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.borisov.app.controller.MainSceneController;

/**
 * @author Maxim Borisov
 */
@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;

    @Autowired
    public PrimaryStageInitializer(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        final var scene = new Scene(fxWeaver.loadView(MainSceneController.class));
        event.stage.setScene(scene);
        event.stage.show();
    }
}
