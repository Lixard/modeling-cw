package ru.borisov.app.service.impl;

import org.springframework.stereotype.Service;
import ru.borisov.app.model.SimulateResultModel;
import ru.borisov.app.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

/**
 * @author Maxim Borisov
 */
@Service
public class FileServiceImpl implements FileService {

    private static final String MODELING_RESULT_STR = "Результаты моделирования за ";

    @Override
    public Path saveToFile(SimulateResultModel results, Path pathToSave) {
        final var file = createFile(pathToSave);
        return writeToFile(file, generateMessage(results));
    }

    private String generateMessage(SimulateResultModel results) {
        return MODELING_RESULT_STR + Instant.now() + '\n' + results.toString();
    }

    private Path writeToFile(Path filePath, String message) {
        try {
            return Files.writeString(filePath, message);
        } catch (IOException e) {
            throw new IllegalStateException("File must be wrote");
        }
    }

    private Path createFile(Path path) {
        try {
            return Files.createFile(path);
        } catch (IOException e) {
            throw new IllegalStateException("File must be created", e);
        }
    }
}
