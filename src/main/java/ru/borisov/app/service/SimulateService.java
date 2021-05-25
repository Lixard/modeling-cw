package ru.borisov.app.service;

import ru.borisov.app.model.SimulateInputDataModel;
import ru.borisov.app.model.SimulateResultModel;

public interface SimulateService {

    SimulateResultModel simulate(SimulateInputDataModel inputData);

}
