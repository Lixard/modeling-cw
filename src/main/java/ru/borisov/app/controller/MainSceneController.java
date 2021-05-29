package ru.borisov.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.borisov.app.model.SimulateInputDataModel;
import ru.borisov.app.model.SimulateResultModel;
import ru.borisov.app.service.impl.SimulateServiceImpl;

import java.util.function.UnaryOperator;

@Component
@FxmlView("main-scene.fxml")
public class MainSceneController {

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
    private TextField requestsCompleteWithoutQueueField;

    @FXML
    private TextField requestsCompleteWithQueueField;

    @FXML
    private TextField requestsDropField;

    @FXML
    private TextField requestsNotCompleteField;

    @FXML
    private TextField cyclesCompleteField;

    @FXML
    private TextField failureProbabilityField;

    @FXML
    public void initialize() {
        validateInputFieldsNotNull();
        validateFieldsOnlyNumber(dataComputeSpeed, requestSizeField, requestSizeDeltaField, requestIntervalField,
                requestIntervalDeltaField, terminalProcessingTimeField, globalModelingTimeField);
        startModelingButton.setOnAction(event -> simulate());
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
        setResults(results);
    }

    private void setResults(SimulateResultModel results) {
        requestsCompleteField.setText(String.valueOf(results.getRequestsComplete()));
        requestsCompleteWithoutQueueField.setText(String.valueOf(results.getRequestsCompleteWithoutQueue()));
        requestsCompleteWithQueueField.setText(String.valueOf(results.getRequestsCompleteWithQueue()));
        requestsDropField.setText(String.valueOf(results.getRequestsDrop()));
        requestsNotCompleteField.setText(String.valueOf(results.getRequestsNotComplete()));
        cyclesCompleteField.setText(String.valueOf(results.getCyclesComplete()));
        failureProbabilityField.setText(computePercentageOf(results.getFailureProbability()));
    }

    private String computePercentageOf(double value) {
        return String.format("%.3f", value * 100);
    }

    private void validateFieldsOnlyNumber(TextField... textFields) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("\\A[0-9]+\\Z")) {
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
