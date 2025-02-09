package project.dailyge.app.core.task.presentation.requesst;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import project.dailyge.app.core.task.application.command.TaskRecurrenceUpdateCommand;
import project.dailyge.entity.task.TaskColor;

public record TaskRecurrenceUpdateRequest(
    @Length(min = 1, max = 150)
    @NotNull(message = "제목을 입력해 주세요.")
    @NotBlank(message = "제목은 공백일 수 없습니다.")
    String title,

    @Length(min = 1, max = 2500)
    @NotNull(message = "내용을 입력해 주세요.")
    @NotBlank(message = "내용은 공백일 수 없습니다.")
    String content,

    @NotNull(message = "색상을 입력해주세요.")
    TaskColor color
) {

    public TaskRecurrenceUpdateCommand toCommand() {
        return new TaskRecurrenceUpdateCommand(title, content, color);
    }

    @Override
    public String toString() {
        return String.format(
            "{\"title\":\"%s\",\"content\":\"%s\",\"color\":\"%s\"}",
            title, content, color
        );
    }
}
