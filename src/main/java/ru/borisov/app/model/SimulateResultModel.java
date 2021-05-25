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
public class SimulateResultModel {

    private int requestsComplete;
    private int requestsCompleteWithoutQueue;
    private int requestsCompleteWithQueue;
    private int requestsDrop;
    private int requestsNotComplete;
    private int cyclesComplete;
    private double failureProbability;
}
