package project.dailyge.app.core.anniversary.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import static project.dailyge.app.codeandmessage.CommonCodeAndMessage.OK;
import project.dailyge.app.common.annotation.LoginUser;
import project.dailyge.app.common.annotation.PresentationLayer;
import project.dailyge.app.common.response.ApiResponse;
import project.dailyge.app.core.anniversary.application.AnniversaryReadService;
import project.dailyge.app.core.anniversary.presentation.response.AnniversaryResponse;
import project.dailyge.app.core.anniversary.presentation.validator.AnniversaryClientValidator;
import project.dailyge.app.core.common.auth.DailygeUser;

import java.time.LocalDate;
import java.util.List;

@RequestMapping(path = "/api/anniversaries")
@PresentationLayer(value = "AnniversaryReadApi")
public class AnniversaryReadApi {

    private final AnniversaryClientValidator validator;
    private final AnniversaryReadService anniversaryReadService;

    public AnniversaryReadApi(
        final AnniversaryClientValidator validator,
        final AnniversaryReadService anniversaryReadService
    ) {
        this.validator = validator;
        this.anniversaryReadService = anniversaryReadService;
    }

    @GetMapping
    public ApiResponse<List<AnniversaryResponse>> searchAnniversaries(
        @LoginUser final DailygeUser dailygeUser,
        @RequestParam(name = "startDate", required = false) final LocalDate startDate,
        @RequestParam(name = "endDate", required = false) final LocalDate endDate
    ) {
        validator.validateFromStartDateToEndDate(startDate, endDate);
        final List<AnniversaryResponse> findAnniversaries =
            anniversaryReadService.findByDates(dailygeUser, startDate, endDate).stream()
                .map(AnniversaryResponse::new)
                .toList();
        return ApiResponse.from(OK, findAnniversaries);
    }
}
