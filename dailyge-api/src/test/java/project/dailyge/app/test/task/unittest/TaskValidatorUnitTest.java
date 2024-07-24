package project.dailyge.app.test.task.unittest;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import project.dailyge.app.common.auth.DailygeUser;
import static project.dailyge.app.common.codeandmessage.CommonCodeAndMessage.UN_AUTHORIZED;
import project.dailyge.app.common.exception.UnAuthorizedException;
import project.dailyge.app.core.task.application.service.TaskValidator;
import static project.dailyge.app.core.task.exception.TaskCodeAndMessage.MONTHLY_TASK_EXISTS;
import static project.dailyge.app.core.task.exception.TaskCodeAndMessage.MONTHLY_TASK_NOT_FOUND;
import static project.dailyge.app.core.task.exception.TaskCodeAndMessage.TOO_MANY_TASKS;
import project.dailyge.app.core.task.exception.TaskTypeException;
import project.dailyge.document.task.MonthlyTaskDocument;
import static project.dailyge.document.task.MonthlyTaskDocument.createMonthlyDocument;
import project.dailyge.document.task.TaskDocumentReadRepository;
import project.dailyge.entity.task.TaskJpaEntity;
import static project.dailyge.entity.task.TaskStatus.TODO;
import static project.dailyge.entity.user.Role.ADMIN;
import static project.dailyge.entity.user.Role.NORMAL;

import java.time.LocalDate;
import java.util.Optional;

@DisplayName("[UnitTest] 할 일 검증 단위 테스트")
class TaskValidatorUnitTest {

    private TaskValidator validator;
    private TaskDocumentReadRepository taskReadRepository;

    @BeforeEach
    void setUp() {
        taskReadRepository = mock(TaskDocumentReadRepository.class);
        validator = new TaskValidator(taskReadRepository);
    }

    @Test
    @DisplayName("관리자면 권한 예외가 발생하지 않는다.")
    void whenAdminUserThenAuthExceptionShouldNotBeHappen() {
        final DailygeUser dailygeUser = new DailygeUser(1L, ADMIN);
        final TaskJpaEntity newTask = new TaskJpaEntity("독서", "Kafka 완벽가이드 1~30p 읽기", now(), TODO, 300L);

        assertDoesNotThrow(() -> validator.validateAuth(dailygeUser, newTask));
    }

    @Test
    @DisplayName("일반 사용자일 때, 자신이 작성한 할 일이 아니라면, UnAuthorizedException이 발생한다.")
    void whenNormalUserIsNotTaskOwnerThenAuthExceptionShouldBeHappen() {
        final DailygeUser dailygeUser = new DailygeUser(1L, NORMAL);
        final TaskJpaEntity newTask = new TaskJpaEntity("독서", "Kafka 완벽가이드 1~30p 읽기", now(), TODO, 300L);

        assertThatThrownBy(() -> validator.validateAuth(dailygeUser, newTask))
            .isInstanceOf(UnAuthorizedException.class)
            .hasMessage(UN_AUTHORIZED.message());
    }

    @Test
    @DisplayName("일반 사용자일 때, 자신이 작성한 할 일이라면, 예외가 발생하지 않는다.")
    void whenNormalUserIsTaskOwnerThenAuthExceptionShouldNotBeHappen() {
        final DailygeUser dailygeUser = new DailygeUser(1L, NORMAL);
        final TaskJpaEntity newTask = new TaskJpaEntity("독서", "Kafka 완벽가이드 1~30p 읽기", now(), TODO, dailygeUser.getUserId());

        assertDoesNotThrow(() -> validator.validateAuth(dailygeUser, newTask));
    }

    @Test
    @DisplayName("월간 일정을 생성할 때, 월 별 일정이 존재하지 않는다면, 일정이 생성된다.")
    void whenMonthlyPlanDoesNotExistsThenAuthValidateShouldBeSuccess() {
        final DailygeUser dailygeUser = new DailygeUser(1L, NORMAL);
        final LocalDate date = LocalDate.now();
        when(taskReadRepository.existsMonthlyPlanByUserIdAndDate(dailygeUser.getId(), date))
            .thenReturn(false);

        final TaskJpaEntity newTask = new TaskJpaEntity("독서", "Kafka 완벽가이드 1~30p 읽기", now(), TODO, dailygeUser.getUserId());

        assertDoesNotThrow(() -> validator.validateAuth(dailygeUser, newTask));
    }

    @Test
    @DisplayName("월간 일정을 생성할 때, 월 별 일정이 이미 존재 한다면, TaskTypeException이 발생한다.")
    void whenCreateMonthlyTasksButMonthlyPlanAlreadyExistsThenResultShouldBeFailed() {
        final DailygeUser dailygeUser = new DailygeUser(1L, NORMAL);
        final LocalDate date = LocalDate.now();
        when(taskReadRepository.existsMonthlyPlanByUserIdAndDate(dailygeUser.getId(), date))
            .thenReturn(true);

        assertThatThrownBy(() -> validator.validateMonthlyPlan(dailygeUser.getId()))
            .isInstanceOf(RuntimeException.class)
            .isExactlyInstanceOf(TaskTypeException.from(MONTHLY_TASK_EXISTS).getClass())
            .hasMessage(MONTHLY_TASK_EXISTS.message());
    }

    @Test
    @DisplayName("일일 일정이 10개를 넘어가면, TaskTypeException이 발생한다.")
    void whenMonthlyPlansOver10ThenAuthValidateShouldBeSuccess() {
        final DailygeUser dailygeUser = new DailygeUser(1L, NORMAL);
        final LocalDate date = LocalDate.now();
        when(taskReadRepository.countTodayTask(dailygeUser.getId(), date))
            .thenReturn(10L);

        assertThatThrownBy(() -> validator.validateTaskCreation(dailygeUser.getId(), date))
            .isInstanceOf(RuntimeException.class)
            .isExactlyInstanceOf(TaskTypeException.from(TOO_MANY_TASKS).getClass())
            .hasMessage(TOO_MANY_TASKS.message());
    }

    @Test
    @DisplayName("월간 일정이 없다면 Task를 생성할 수 없다.")
    void whenMonthlyPlanDoesNotExistsThenTaskCreatedShouldBeFailed() {
        final DailygeUser dailygeUser = new DailygeUser(1L, NORMAL);
        final LocalDate now = LocalDate.now();
        when(taskReadRepository.countTodayTask(dailygeUser.getId(), now))
            .thenReturn(9L);
        when(taskReadRepository.findMonthlyDocumentByUserIdAndDate(dailygeUser.getId(), now))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> validator.validateTaskCreation(dailygeUser.getId(), now))
            .isInstanceOf(RuntimeException.class)
            .isExactlyInstanceOf(TaskTypeException.from(MONTHLY_TASK_NOT_FOUND).getClass())
            .hasMessage(MONTHLY_TASK_NOT_FOUND.message());
    }

    @Test
    @DisplayName("월간 일정이 존재하고, 일일 일정이 10개 미만이라면 Task가 생성된다.")
    void whenTodayTasksUnder10AndExistsMonthlyPlanThenMonthlyPlanShouldBeCreated() {
        final DailygeUser dailygeUser = new DailygeUser(1L, NORMAL);
        final LocalDate now = LocalDate.now();
        final Optional<MonthlyTaskDocument> monthlyTask = Optional.of(
            createMonthlyDocument(dailygeUser.getId(), now.getYear(), now.getMonthValue()
            )
        );
        when(taskReadRepository.countTodayTask(dailygeUser.getId(), now))
            .thenReturn(9L);
        when(taskReadRepository.findMonthlyDocumentByUserIdAndDate(dailygeUser.getId(), now))
            .thenReturn(monthlyTask);

        assertDoesNotThrow(() -> validator.validateTaskCreation(dailygeUser.getId(), now));
    }
}
