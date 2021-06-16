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
    private double eomTime;

    @Override
    public String toString() {
        return "================================================\n" +
                "Заявок обслужено - " + requestsComplete + '\n' +
                "Заявок обслужено из общей очереди - " + requestsCompleteWithQueue + '\n' +
                "Неоконченные заявки на первом терминале - " + requestsNotCompleteOnFirstTerminal + '\n' +
                "Неоконченные заявки на втором терминале - " + requestsNotCompleteOnSecondTerminal + '\n' +
                "Неоконченные заявки на третьем терминале - " + requestsNotCompleteOnThirdTerminal + '\n' +
                "Неоконченные заявки в общей очереди - " + requestsNotCompleteOnGlobalQueue + '\n' +
                "Циклов пройдено - " + cyclesComplete + '\n' +
                "загрузка ЭВМ - " + computerLoad + '\n' +
                "===============================================\n";
    }
}
