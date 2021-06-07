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
    private int requestsCompleteWithQueue;
    private int requestsNotCompleteOnFirstTerminal;
    private int requestsNotCompleteOnSecondTerminal;
    private int requestsNotCompleteOnThirdTerminal;
    private int requestsNotCompleteOnGlobalQueue;
    private int cyclesComplete;
    private double computerLoad;
}
