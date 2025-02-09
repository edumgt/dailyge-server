package project.dailyge.app.core.coupon.presentation;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static project.dailyge.app.codeandmessage.CommonCodeAndMessage.OK;
import project.dailyge.app.common.annotation.PresentationLayer;
import project.dailyge.app.common.response.ApiResponse;
import project.dailyge.app.core.coupon.application.CouponEventWriteService;
import project.dailyge.app.core.coupon.presentation.request.CouponWinnerRequest;

@PresentationLayer
@RequestMapping(path = "/api/coupons")
public class CouponWinnerApi {

    private final CouponEventWriteService couponEventWriteService;

    public CouponWinnerApi(final CouponEventWriteService couponEventWriteService) {
        this.couponEventWriteService = couponEventWriteService;
    }

    @PostMapping(path = "/winners")
    public ApiResponse<Void> findWinners(@Valid @RequestBody final CouponWinnerRequest request) {
        couponEventWriteService.pickWinners(request.winnerCount(), request.eventId());
        return ApiResponse.from(OK);
    }
}
