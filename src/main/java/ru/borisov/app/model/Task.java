package ru.borisov.app.model;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Maxim Borisov
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class Task {

    @NonNull
    public int estimatedSymbols;
    public boolean isFromGlobalQueue;
}
