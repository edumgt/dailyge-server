package project.dailyge.app.core.weeklygoal.presentation;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static project.dailyge.app.codeandmessage.CommonCodeAndMessage.NO_CONTENT;
import project.dailyge.app.common.annotation.LoginUser;
import project.dailyge.app.common.annotation.PresentationLayer;
import project.dailyge.app.common.response.ApiResponse;
import project.dailyge.app.core.common.auth.DailygeUser;
import project.dailyge.app.core.weeklygoal.application.WeeklyGoalWriteService;

@RequestMapping(path = "/api/weekly-goals")
@PresentationLayer(value = "WeeklyGoalDeleteApi")
public class WeeklyGoalDeleteApi {

    private final WeeklyGoalWriteService weeklyGoalWriteService;

    public WeeklyGoalDeleteApi(final WeeklyGoalWriteService weeklyGoalWriteService) {
        this.weeklyGoalWriteService = weeklyGoalWriteService;
    }

    @DeleteMapping(path = {"/{weeklyGoalId}"})
    public ApiResponse<Void> deleteWeeklyGoalById(
        @LoginUser final DailygeUser dailygeUser,
        @PathVariable(name = "weeklyGoalId") final Long weeklyGoalId
    ) {
        weeklyGoalWriteService.delete(dailygeUser, weeklyGoalId);
        return ApiResponse.from(NO_CONTENT);
    }
}
