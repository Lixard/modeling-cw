package ru.borisov.app.startup;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ru.borisov.app.controller.MainSceneController;

import java.io.IOException;

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
        event.stage.setMinHeight(490);
        event.stage.setMinWidth(600);
        event.stage.setTitle("Моделирование работы ЭВМ с круговым циклическим алгоритмом");
        event.stage.setResizable(false);
        setAppIcon(event.stage);
        event.stage.show();
    }

    private void setAppIcon(final Stage stage) {
        try {
            stage.getIcons().add(new Image(new ClassPathResource("icon.jpg").getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
