package project.dailyge.app.core.user.exception;

import project.dailyge.app.codeandmessage.CodeAndMessage;
import project.dailyge.app.common.exception.BusinessException;
import java.util.HashMap;
import java.util.Map;
import static project.dailyge.app.core.user.exception.UserCodeAndMessage.DUPLICATED_EMAIL;
import static project.dailyge.app.core.user.exception.UserCodeAndMessage.USER_NOT_FOUND;
import static project.dailyge.app.core.user.exception.UserCodeAndMessage.USER_SERVICE_UNAVAILABLE;

public sealed class UserTypeException extends BusinessException {

    private static final Map<UserCodeAndMessage, UserTypeException> exceptionMap = new HashMap<>();

    private UserTypeException(final CodeAndMessage codeAndMessage) {
        super(codeAndMessage);
    }

    static {
        exceptionMap.put(USER_NOT_FOUND, new UserNotFoundException(USER_NOT_FOUND));
        exceptionMap.put(USER_SERVICE_UNAVAILABLE, new UserServiceUnAvailableException(USER_SERVICE_UNAVAILABLE));
        exceptionMap.put(DUPLICATED_EMAIL, new DuplicatedEmailException(DUPLICATED_EMAIL));
    }

    public static UserTypeException from(final UserCodeAndMessage codeAndMessage) {
        final UserTypeException userTypeException = exceptionMap.get(codeAndMessage);
        if (userTypeException == null) {
            return new UserUnResolvedException(codeAndMessage);
        }
        return userTypeException;
    }

    private static final class UserNotFoundException extends UserTypeException {
        public UserNotFoundException(final UserCodeAndMessage codeAndMessage) {
            super(codeAndMessage);
        }
    }

    private static final class UserServiceUnAvailableException extends UserTypeException {
        public UserServiceUnAvailableException(final UserCodeAndMessage codeAndMessage) {
            super(codeAndMessage);
        }
    }

    private static final class DuplicatedEmailException extends UserTypeException {
        public DuplicatedEmailException(final UserCodeAndMessage codeAndMessage) {
            super(codeAndMessage);
        }
    }

    private static final class UserUnResolvedException extends UserTypeException {
        public UserUnResolvedException(final UserCodeAndMessage codeAndMessage) {
            super(codeAndMessage);
        }
    }
}
