package com.danya.exception;

public class TrainerDoesNotMatchTrainingTypeException extends RuntimeException {
    public TrainerDoesNotMatchTrainingTypeException(String message) {
        super(message);
    }

    public TrainerDoesNotMatchTrainingTypeException() {
        super();
    }
}
