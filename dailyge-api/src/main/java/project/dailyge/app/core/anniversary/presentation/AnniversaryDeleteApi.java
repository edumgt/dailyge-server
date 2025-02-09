package project.dailyge.app.core.anniversary.presentation;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static project.dailyge.app.codeandmessage.CommonCodeAndMessage.NO_CONTENT;
import project.dailyge.app.common.annotation.LoginUser;
import project.dailyge.app.common.annotation.PresentationLayer;
import project.dailyge.app.common.response.ApiResponse;
import project.dailyge.app.core.anniversary.application.AnniversaryWriteService;
import project.dailyge.app.core.anniversary.presentation.response.AnniversaryCreateResponse;
import project.dailyge.app.core.common.auth.DailygeUser;

@RequestMapping(path = "/api/anniversaries")
@PresentationLayer(value = "AnniversaryDeleteApi")
public class AnniversaryDeleteApi {

    private final AnniversaryWriteService anniversaryWriteService;

    public AnniversaryDeleteApi(final AnniversaryWriteService anniversaryWriteService) {
        this.anniversaryWriteService = anniversaryWriteService;
    }

    @DeleteMapping(path = "/{anniversaryId}")
    public ApiResponse<AnniversaryCreateResponse> createAnniversary(
        @LoginUser final DailygeUser dailygeUser,
        @PathVariable(name = "anniversaryId") final Long anniversaryId
    ) {
        anniversaryWriteService.delete(dailygeUser, anniversaryId);
        return ApiResponse.from(NO_CONTENT);
    }
}
