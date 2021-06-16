package ru.borisov.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.stage.FileChooser;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ru.borisov.app.model.SimulateInputDataModel;
import ru.borisov.app.model.SimulateResultModel;
import ru.borisov.app.service.FileService;
import ru.borisov.app.service.impl.SimulateServiceImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.function.UnaryOperator;

@Component
@FxmlView("main-scene.fxml")
public class MainSceneController {

    private final FileService fileService;

    @FXML
    private TextField dataComputeSpeed;

    @FXML
    private TextField requestSizeField;

    @FXML
    private TextField requestSizeDeltaField;

    @FXML
    private TextField requestIntervalField;

    @FXML
    private TextField requestIntervalDeltaField;

    @FXML
    private TextField terminalProcessingTimeField;

    @FXML
    private TextField globalModelingTimeField;

    @FXML
    private Button startModelingButton;

    @FXML
    private TextField requestsCompleteField;

    @FXML
    private TextField requestsCompleteWithQueueField;

    @FXML
    private TextField queueOnFirstTerminalField;

    @FXML
    private TextField queueOnSecondTerminalField;

    @FXML
    private TextField queueOnThirdTerminalField;

    @FXML
    private TextField queueOnGlobalField;

    @FXML
    private TextField cyclesCompleteField;

    @FXML
    private TextField computerLoadField;

    @FXML
    private TextField eomTimeField;

    @FXML
    private AnchorPane schemaPane;

    @FXML
    private Label queueOnFirstTerminalSchemaCounter;

    @FXML
    private Label queueOnSecondTerminalSchemaCounter;

    @FXML
    private Label queueOnThirdTerminalSchemaCounter;

    @FXML
    private Label queueOnGlobalQueueSchemaCounter;

    @FXML
    private Button exportToFileButton;

    private SimulateResultModel lastResult;

    @Autowired
    public MainSceneController(FileService fileService) {
        this.fileService = fileService;
    }

    @FXML
    public void initialize() {
        validateInputFieldsNotNull();
        validateFieldsOnlyNumber(dataComputeSpeed, requestSizeField, requestSizeDeltaField, requestIntervalField,
                requestIntervalDeltaField, terminalProcessingTimeField, globalModelingTimeField);
        setupSchemaBackground();
        startModelingButton.setOnAction(event -> {
            simulate();
            exportToFileButton.setDisable(false);
        });
        exportToFileButton.setOnAction(event -> exportToFile());
        initValues();
    }

    private void initValues() {
        dataComputeSpeed.setText("10");
        requestSizeField.setText("300");
        requestSizeDeltaField.setText("50");
        requestIntervalField.setText("30");
        requestIntervalDeltaField.setText("5");
        terminalProcessingTimeField.setText("30");
        globalModelingTimeField.setText("18000");
    }

    private void exportToFile() {
        final var fileChooser = new FileChooser().showSaveDialog(schemaPane.getScene().getWindow());
        final var filePath = Path.of(fileChooser.getAbsolutePath());
        fileService.saveToFile(lastResult, filePath).toFile();
    }

    private void setupSchemaBackground() {
        final var image = catchImageFromResources();
        final var backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)
        );
        final var background = new Background(backgroundImage);
        schemaPane.setBackground(background);
    }

    private Image catchImageFromResources() {
        try {
            return new Image(new ClassPathResource("schema.png").getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("That cannot happen", e);
        }
    }

    private void simulate() {
        final var results = new SimulateServiceImpl(
                SimulateInputDataModel.builder()
                        .dataComputeSpeed(Integer.parseInt(dataComputeSpeed.getText()))
                        .requestSize(Integer.parseInt(requestSizeField.getText()))
                        .requestSizeDelta(Integer.parseInt(requestSizeDeltaField.getText()))
                        .requestInterval(Integer.parseInt(requestIntervalField.getText()))
                        .requestIntervalDelta(Integer.parseInt(requestIntervalDeltaField.getText()))
                        .terminalProcessingTime(Integer.parseInt(terminalProcessingTimeField.getText()))
                        .globalModelingTime(Long.parseLong(globalModelingTimeField.getText()))
                        .build()
        ).simulate();
        this.lastResult = results;
        setResults(results);
    }

    private void setResults(SimulateResultModel results) {
        requestsCompleteField.setText(String.valueOf(results.getRequestsComplete()));
        requestsCompleteWithQueueField.setText(String.valueOf(results.getRequestsCompleteWithQueue()));
        queueOnFirstTerminalField.setText(String.valueOf(results.getRequestsNotCompleteOnFirstTerminal()));
        queueOnSecondTerminalField.setText(String.valueOf(results.getRequestsNotCompleteOnSecondTerminal()));
        queueOnThirdTerminalField.setText(String.valueOf(results.getRequestsNotCompleteOnThirdTerminal()));
        queueOnGlobalField.setText(String.valueOf(results.getRequestsNotCompleteOnGlobalQueue()));
        cyclesCompleteField.setText(String.valueOf(results.getCyclesComplete()));
        computerLoadField.setText(computePercentageOf(results.getComputerLoad()));
        eomTimeField.setText(String.valueOf(results.getEomTime()));

        queueOnFirstTerminalSchemaCounter.setText(String.valueOf(results.getRequestsNotCompleteOnFirstTerminal()));
        queueOnSecondTerminalSchemaCounter.setText(String.valueOf(results.getRequestsNotCompleteOnSecondTerminal()));
        queueOnThirdTerminalSchemaCounter.setText(String.valueOf(results.getRequestsNotCompleteOnThirdTerminal()));
        queueOnGlobalQueueSchemaCounter.setText(String.valueOf(results.getRequestsNotCompleteOnGlobalQueue()));
    }

    private String computePercentageOf(double value) {
        return new DecimalFormat("#%").format(value);
    }

    private void validateFieldsOnlyNumber(TextField... textFields) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("\\A[0-9]+\\Z") || Objects.equals(text, "")) {
                return change;
            }
            return null;
        };
        for (final var textField : textFields) {
            textField.setTextFormatter(new TextFormatter<>(filter));
        }
    }

    private void validateInputFieldsNotNull() {
        final var notNullBinding = dataComputeSpeed.textProperty().isEmpty()
                .or(requestSizeField.textProperty().isEmpty())
                .or(requestSizeDeltaField.textProperty().isEmpty())
                .or(requestIntervalField.textProperty().isEmpty())
                .or(requestIntervalDeltaField.textProperty().isEmpty())
                .or(terminalProcessingTimeField.textProperty().isEmpty())
                .or(globalModelingTimeField.textProperty().isEmpty());

        startModelingButton.disableProperty().bind(notNullBinding);
    }

}
