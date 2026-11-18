package hk.edu.polyu.comp.comp2021.crobs.model;

public class CrobsException extends RuntimeException {
    public CrobsException(String message) {
        super(message);
    }

    public CrobsException(String message, Throwable cause) {
        super(message, cause);
    }

    /** A command is malformed, unknown, or given the wrong arguments. */
    public static class InvalidCommandException extends CrobsException {
        public InvalidCommandException(String message) {
            super(message);
        }
    }

    /** The current user is not allowed to perform an operation. */
    public static class PermissionDeniedException extends CrobsException {
        public PermissionDeniedException(String message) {
            super(message);
        }
    }

    /** A referenced user does not exist. */
    public static class NoSuchUserException extends CrobsException {
        public NoSuchUserException(String message) {
            super(message);
        }
    }

    /** A referenced room does not exist. */
    public static class NoSuchRoomException extends CrobsException {
        public NoSuchRoomException(String message) {
            super(message);
        }
    }

    /** A referenced booking does not exist. */
    public static class NoSuchBookingException extends CrobsException {
        public NoSuchBookingException(String message) {
            super(message);
        }
    }

    /** A booking would overlap another booking of the same room and date. */
    public static class BookingConflictException extends CrobsException {
        public BookingConflictException(String message) {
            super(message);
        }
    }

    /** A value (date, time, capacity, role, feature, ...) is malformed or invalid. */
    public static class InvalidValueException extends CrobsException {
        public InvalidValueException(String message) {
            super(message);
        }
    }
}
