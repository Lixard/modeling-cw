package ru.borisov.app.service.impl;

import ru.borisov.app.model.SimulateInputDataModel;
import ru.borisov.app.model.SimulateResultModel;
import ru.borisov.app.model.Task;
import ru.borisov.app.model.Terminal;
import ru.borisov.app.service.SimulateService;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Maxim Borisov
 */
public class SimulateServiceImpl implements SimulateService {

    final Queue<Task> globalQueue = new ArrayDeque<>();
    final Queue<Terminal> terminals = new ArrayDeque<>();

    private final int dataComputeSpeed;
    private final int requestSize;
    private final int requestSizeDelta;
    private final int requestInterval;
    private final int requestIntervalDelta;
    private final int terminalProcessingTime;
    private long globalModelingTime;

    public SimulateServiceImpl(SimulateInputDataModel model) {
        dataComputeSpeed = model.getDataComputeSpeed();
        requestSize = model.getRequestSize();
        requestSizeDelta = model.getRequestSizeDelta();
        requestInterval = model.getRequestInterval();
        requestIntervalDelta = model.getRequestIntervalDelta();
        terminalProcessingTime = model.getTerminalProcessingTime();
        globalModelingTime = model.getGlobalModelingTime();
    }

    @Override
    public SimulateResultModel simulate() {
        preparation();
        Terminal selectedTerminal = null;
        var computeTime = 0;

        while (--globalModelingTime > 0) {

            terminals.forEach(terminal -> terminal.genCycleTime++);

            terminals.stream()
                    .filter(terminal -> terminal.requestGenInterval >= terminal.genCycleTime)
                    .forEach(terminal -> {
                        terminal.queue.add(new Task(computeWithDelta(requestSize, requestSizeDelta)));
                        terminal.genCycleTime = 0;
                    });

            if (computeTime == 0) {
                computeTime++;
                selectedTerminal = getNextTerminal();
            } else if (computeTime < terminalProcessingTime) {
                computeTime++;
                var computedTask = selectedTerminal.queue.peek();
                if (computedTask == null) {
                    selectedTerminal.queue.add(globalQueue.remove());
                    computedTask = selectedTerminal.queue.peek();
                }
                if (computedTask != null) {
                    computedTask.estimatedSymbols -= dataComputeSpeed;
                    if (computedTask.estimatedSymbols <= 0) {
                        selectedTerminal.queue.remove();
                        computeTime = 0;
                    }
                }
            } else {
                computeTime = 0;
                final var estimatedTask = selectedTerminal.queue.remove();
                globalQueue.add(estimatedTask);
            }
        }
        return null;
    }

    private void preparation() {
        terminals.add(
                Terminal.builder()
                        .requestGenInterval(computeWithDelta(requestInterval, requestIntervalDelta))
                        .queue(new ArrayDeque<>())
                        .build()
        );
        terminals.add(
                Terminal.builder()
                        .requestGenInterval(computeWithDelta(requestInterval, requestIntervalDelta))
                        .queue(new ArrayDeque<>())
                        .build()
        );
        terminals.add(
                Terminal.builder()
                        .requestGenInterval(computeWithDelta(requestInterval, requestIntervalDelta))
                        .queue(new ArrayDeque<>())
                        .build()
        );
        terminals.forEach(terminal -> terminal.queue.add(new Task(computeWithDelta(requestSize, requestSizeDelta))));
    }

    private int computeWithDelta(int value, int delta) {
        return ThreadLocalRandom.current().nextInt(value - delta, value + delta + 1);
    }

    private Terminal getNextTerminal() {
        final var terminal = terminals.remove();
        terminals.add(terminal);
        return terminal;
    }
}
