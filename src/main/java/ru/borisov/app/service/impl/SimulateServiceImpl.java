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

    private int cycleCounter = 0;

    private int requestsComplete = 0;
    private int requestsCompleteWithGlobalQueue = 0;
    private int cyclesComplete = 0;
    private double computerLoad = 0;

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

            terminals.stream()
                    .map(this::incTerminalCycleTime)
                    .filter(terminal -> terminal.genCycleTime >= terminal.requestGenInterval)
                    .forEach(terminal -> {
                        terminal.queue.add(new Task(computeWithDelta(requestSize, requestSizeDelta)));
                        terminal.genCycleTime = 0;
                        terminal.requestGenInterval = computeWithDelta(requestInterval, requestIntervalDelta);
                    });

            if (computeTime == 0) {
                computeTime++;
                selectedTerminal = getNextTerminal();
            } else if (computeTime < terminalProcessingTime) {
                computeTime++;
                var computedTask = selectedTerminal.queue.peek();
                if (computedTask == null) {
                    final var taskFromGlobalQueue = globalQueue.poll();
                    if (taskFromGlobalQueue != null) {
                        selectedTerminal.queue.add(taskFromGlobalQueue);
                        computedTask = selectedTerminal.queue.element();
                        computedTask.isFromGlobalQueue = true;
                    }
                }
                if (computedTask != null) {
                    computedTask.estimatedSymbols -= dataComputeSpeed;
                    if (computedTask.estimatedSymbols <= 0) {
                        computeTime = 0;
                        final var taskToRemove = selectedTerminal.queue.remove();
                        if (taskToRemove.isFromGlobalQueue) requestsCompleteWithGlobalQueue++;
                        requestsComplete++;
                    }
                }
            } else {
                computeTime = 0;
                final var estimatedTask = selectedTerminal.queue.remove();
                globalQueue.add(estimatedTask);
            }
        }


        return SimulateResultModel.builder()
                .requestsComplete(requestsComplete)
                .requestsCompleteWithQueue(requestsCompleteWithGlobalQueue)
                .requestsNotCompleteOnFirstTerminal(findTerminalQueueSizeById(1))
                .requestsNotCompleteOnSecondTerminal(findTerminalQueueSizeById(2))
                .requestsNotCompleteOnThirdTerminal(findTerminalQueueSizeById(3))
                .requestsNotCompleteOnGlobalQueue(globalQueue.size())
                .cyclesComplete(cyclesComplete)
                .computerLoad(1.00)
                .build();
    }

    private int findTerminalQueueSizeById(int id) {
        return terminals.stream()
                .filter(terminal -> terminal.id == id)
                .map(terminal -> terminal.queue.size())
                .findAny()
                .orElseThrow();
    }

    private void preparation() {
        terminals.add(
                Terminal.builder()
                        .id(1)
                        .requestGenInterval(computeWithDelta(requestInterval, requestIntervalDelta))
                        .queue(new ArrayDeque<>())
                        .build()
        );
        terminals.add(
                Terminal.builder()
                        .id(2)
                        .requestGenInterval(computeWithDelta(requestInterval, requestIntervalDelta))
                        .queue(new ArrayDeque<>())
                        .build()
        );
        terminals.add(
                Terminal.builder()
                        .id(3)
                        .requestGenInterval(computeWithDelta(requestInterval, requestIntervalDelta))
                        .queue(new ArrayDeque<>())
                        .build()
        );
        terminals.forEach(terminal -> terminal.queue.add(new Task(computeWithDelta(requestSize, requestSizeDelta))));
    }

    private int computeWithDelta(int value, int delta) {
        return ThreadLocalRandom.current().nextInt(value - delta, value + delta + 1);
    }

    private Terminal incTerminalCycleTime(Terminal terminal) {
        terminal.genCycleTime++;
        return terminal;
    }

    private Terminal getNextTerminal() {
        if (++cycleCounter == terminals.size()) {
            cycleCounter = 0;
            cyclesComplete++;
        }
        final var terminal = terminals.remove();
        terminals.add(terminal);
        return terminal;
    }
}
