package hk.edu.polyu.comp.comp2021.crobs.model;

public class CrobsException extends RuntimeException {
    public CrobsException(String message) {
        super(message);
    }
    public static class InvalidValueException extends CrobsException {
        public InvalidValueException(String message) {
            super(message);
        }
    }
    public static class NotFoundException extends CrobsException {
        public NotFoundException(String message) {
            super(message);
        }
    }
    public static class PermissionException extends CrobsException {
        public PermissionException(String message) {
            super(message);
        }
    }
    public static class ConflictException extends CrobsException {
        public ConflictException(String message) {
            super(message);
        }
    }
}