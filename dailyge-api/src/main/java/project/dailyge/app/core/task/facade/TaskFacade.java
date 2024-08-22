package project.dailyge.app.core.task.facade;

import lombok.RequiredArgsConstructor;
import project.dailyge.app.common.annotation.FacadeLayer;
import project.dailyge.app.common.auth.DailygeUser;
import project.dailyge.app.core.task.application.TaskWriteUseCase;
import project.dailyge.app.core.task.application.command.TaskCreateCommand;
import static project.dailyge.app.core.task.exception.TaskCodeAndMessage.MONTHLY_TASK_EXISTS;
import static project.dailyge.app.core.task.exception.TaskCodeAndMessage.TASK_UN_RESOLVED_EXCEPTION;
import project.dailyge.app.core.task.exception.TaskTypeException;
import project.dailyge.lock.Lock;
import project.dailyge.lock.LockUseCase;

import java.time.LocalDate;

@FacadeLayer
@RequiredArgsConstructor
public class TaskFacade {

    private final LockUseCase lockUseCase;
    private final TaskWriteUseCase taskWriteUseCase;

    public void createMonthlyTasks(
        final DailygeUser dailygeUser,
        final LocalDate date
    ) {
        final Lock lock = lockUseCase.getLock(dailygeUser.getUserId());
        try {
            if (!lock.tryLock(0, 4)) {
                throw TaskTypeException.from(MONTHLY_TASK_EXISTS);
            }
            taskWriteUseCase.saveAll(dailygeUser, date);
        } catch (InterruptedException ex) {
            throw TaskTypeException.from(ex.getMessage(), TASK_UN_RESOLVED_EXCEPTION);
        } finally {
            lockUseCase.releaseLock(lock);
        }
    }
}
