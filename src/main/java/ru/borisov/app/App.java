package ru.borisov.app;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.borisov.app.startup.JavaFxApplication;

/**
 * @author Maxim Borisov
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}
