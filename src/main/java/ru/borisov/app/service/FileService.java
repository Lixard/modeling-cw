package ru.borisov.app.service;

import ru.borisov.app.model.SimulateResultModel;

import java.nio.file.Path;

public interface FileService {

    Path saveToFile(SimulateResultModel results, Path pathToSave);
}
