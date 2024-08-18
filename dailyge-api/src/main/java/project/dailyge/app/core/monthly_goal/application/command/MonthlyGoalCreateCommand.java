package project.dailyge.app.core.monthly_goal.application.command;

import project.dailyge.app.common.auth.DailygeUser;
import project.dailyge.entity.monthly_goal.MonthlyGoalJpaEntity;

public record MonthlyGoalCreateCommand(
    String title,
    String content
) {
    public MonthlyGoalJpaEntity toEntity(final DailygeUser dailygeUser) {
        return new MonthlyGoalJpaEntity(title, content, dailygeUser.getId());
    }

    @Override
    public String toString() {
        return String.format(
            "{\"title\":\"%s\",\"content\":\"%s\"}",
            title != null ? title : "",
            content != null ? content : ""
        );
    }
}
