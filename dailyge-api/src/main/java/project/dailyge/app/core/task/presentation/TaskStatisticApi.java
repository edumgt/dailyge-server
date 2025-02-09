package project.dailyge.app.core.task.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import static project.dailyge.app.codeandmessage.CommonCodeAndMessage.OK;
import project.dailyge.app.common.annotation.LoginUser;
import project.dailyge.app.common.annotation.PresentationLayer;
import project.dailyge.app.common.response.ApiResponse;
import project.dailyge.app.core.common.auth.DailygeUser;
import project.dailyge.app.core.task.application.TaskReadService;
import project.dailyge.app.core.task.presentation.response.MonthlyTasksStatisticResponse;
import project.dailyge.app.core.task.presentation.response.MonthlyWeekTasksStatisticResponse;
import project.dailyge.app.core.task.presentation.response.WeeklyTasksStatisticResponse;
import project.dailyge.app.core.task.presentation.validator.TaskClientValidator;
import project.dailyge.entity.task.Tasks;

import java.time.LocalDate;

@RequestMapping(path = {"/api/tasks/statistic"})
@PresentationLayer(value = "TaskStatisticApi")
public class TaskStatisticApi {

    private final TaskClientValidator validator;
    private final TaskReadService taskReadService;

    public TaskStatisticApi(
        final TaskClientValidator validator,
        final TaskReadService taskReadService
    ) {
        this.validator = validator;
        this.taskReadService = taskReadService;
    }

    @GetMapping(path = {"/weekly"})
    public ApiResponse<WeeklyTasksStatisticResponse> findWeeklyTasksStatisticByUserIdAndDate(
        @LoginUser final DailygeUser dailygeUser,
        @RequestParam(value = "startDate") final LocalDate startDate,
        @RequestParam(value = "endDate") final LocalDate endDate
    ) {
        validator.validateFromStartDateToEndDate(startDate, endDate);
        final Tasks weeklyTasks = taskReadService.findTasksStatisticByUserIdAndDate(dailygeUser, startDate, endDate);
        final WeeklyTasksStatisticResponse payload = new WeeklyTasksStatisticResponse(startDate, endDate, weeklyTasks);
        return ApiResponse.from(OK, payload);
    }

    @GetMapping(path = {"/monthly"})
    public ApiResponse<MonthlyTasksStatisticResponse> findMonthlyTasksStatisticByUserIdAndDate(
        @LoginUser final DailygeUser dailygeUser,
        @RequestParam(value = "startDate") final LocalDate startDate,
        @RequestParam(value = "endDate") final LocalDate endDate
    ) {
        validator.validateOneMonthDifference(startDate, endDate);
        final Tasks monthlyTasks = taskReadService.findTasksStatisticByUserIdAndDate(dailygeUser, startDate, endDate);
        final MonthlyTasksStatisticResponse payload = new MonthlyTasksStatisticResponse(startDate, endDate, monthlyTasks);
        return ApiResponse.from(OK, payload);
    }

    @GetMapping(path = {"/monthly-weeks"})
    public ApiResponse<MonthlyWeekTasksStatisticResponse> findMonthlyWeekTasksStatisticByUserIdAndDate(
        @LoginUser final DailygeUser dailygeUser,
        @RequestParam(value = "startDate") final LocalDate startDate,
        @RequestParam(value = "endDate") final LocalDate endDate
    ) {
        validator.validateOneMonthDifference(startDate, endDate);
        final Tasks monthlyTasks = taskReadService.findTasksStatisticByUserIdAndDate(dailygeUser, startDate, endDate);
        final MonthlyWeekTasksStatisticResponse payload = new MonthlyWeekTasksStatisticResponse(startDate, endDate, monthlyTasks);
        return ApiResponse.from(OK, payload);
    }
}
