package ru.borisov.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Queue;

/**
 * @author Maxim Borisov
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Terminal {

    public int genCycleTime;
    public int requestGenInterval;
    public Queue<Task> queue;
}
