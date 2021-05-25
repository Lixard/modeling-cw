package ru.borisov.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Maxim Borisov
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulateInputDataModel {

    private int dataComputeSpeed;
    private int requestSize;
    private int requestSizeDelta;
    private int requestInterval;
    private int requestIntervalDelta;
    private int terminalProcessingTime;
    private long globalModelingTime;
}
