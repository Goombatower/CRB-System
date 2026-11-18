package hk.edu.polyu.comp.comp2021.crobs.model;

/**
 * special exception cases for our command. parent class:Runtime Exceptions
 */
public class CrobsException extends RuntimeException {
    /**
     * @param message basic exception case
     */
    public CrobsException(String message) {
        super(message);
    }

    /**
     * @param message the exception for the log
     * @param cause the cause of the exception
     */
    public CrobsException(String message, Throwable cause) {
        super(message, cause);
    }

    /** A command is malformed, unknown, or given the wrong arguments. */
    public static class InvalidCommandException extends CrobsException {
        /**
         * @param message returns the command that is invalid
         */
        public InvalidCommandException(String message) {
            super(message);
        }
    }

    /** The current user is not allowed to perform an operation. */
    public static class PermissionDeniedException extends CrobsException {
        /**
         * @param message return that you do not have the permissions for a given action
         */
        public PermissionDeniedException(String message) {
            super(message);
        }
    }

    /** A referenced user does not exist. */
    public static class NoSuchUserException extends CrobsException {
        /**
         * @param message returns that the user you are trying to find does not exist
         */
        public NoSuchUserException(String message) {
            super(message);
        }
    }

    /** A referenced room does not exist. */
    public static class NoSuchRoomException extends CrobsException {
        /**
         * @param message returns that the room you are trying to find does not exist
         */
        public NoSuchRoomException(String message) {
            super(message);
        }
    }

    /** A referenced booking does not exist. */
    public static class NoSuchBookingException extends CrobsException {
        /**
         * @param message returns that the booking you are trying to find does not exist
         */
        public NoSuchBookingException(String message) {
            super(message);
        }
    }

    /** A booking would overlap another booking of the same room and date. */
    public static class BookingConflictException extends CrobsException {
        /**
         * @param message returns that the booking you are trying to create conflicts with an already existing booking
         */
        public BookingConflictException(String message) {
            super(message);
        }
    }

    /** A value (date, time, capacity, role, feature, ...) is malformed or invalid. */
    public static class InvalidValueException extends CrobsException {
        /**
         * @param message returns that you are trying to enter an invalid exception
         */
        public InvalidValueException(String message) {
            super(message);
        }
    }
}
