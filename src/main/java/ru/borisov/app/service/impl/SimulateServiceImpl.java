package ru.borisov.app.service.impl;

import ru.borisov.app.model.SimulateInputDataModel;
import ru.borisov.app.model.SimulateResultModel;
import ru.borisov.app.model.Task;
import ru.borisov.app.model.Terminal;
import ru.borisov.app.service.SimulateService;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
    private int requestsCompleteWithoutGlobalQueue = 0;
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
                        else requestsCompleteWithoutGlobalQueue++;
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
                .requestsCompleteWithoutQueue(requestsCompleteWithoutGlobalQueue)
                .requestsCompleteWithQueue(requestsCompleteWithGlobalQueue)
                .requestsNotComplete(countNotCompletedTasks())
                .cyclesComplete(cyclesComplete)
                .computerLoad(1.00)
                .build();
    }

    private int countNotCompletedTasks() {
        final var collected = terminals.stream()
                .map(terminal -> terminal.queue.size())
                .collect(Collectors.toList());

        collected.add(globalQueue.size());

        return collected.stream()
                .reduce(Integer::sum)
                .orElse(0);
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
