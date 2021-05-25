package ru.borisov.app.startup;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/**
 * @author Maxim Borisov
 */
public class StageReadyEvent extends ApplicationEvent {

    public final Stage stage;

    public StageReadyEvent(Stage stage) {
        super(stage);
        this.stage = stage;
    }
}
