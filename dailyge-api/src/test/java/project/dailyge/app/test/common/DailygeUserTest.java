package project.dailyge.app.test.common;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.dailyge.app.common.auth.DailygeUser;
import project.dailyge.app.common.exception.UnAuthorizedException;
import static project.dailyge.app.common.exception.UnAuthorizedException.USER_NOT_MATCH_MESSAGE;
import project.dailyge.entity.user.Role;

@DisplayName("[UnitTest] DailygeUser 단위 테스트")
class DailygeUserTest {

    @Test
    @DisplayName("권한 체크 시 관리자일 경우, True를 반환한다.")
    void whenUserRoleIsAdminThenResultShouldBeTrue() {
        final DailygeUser dailygeUser = new DailygeUser(1L, Role.ADMIN);

        assertTrue(dailygeUser.isAdmin());
    }

    @Test
    @DisplayName("권한 체크 시 관리자가 아닐 경우, False를 반환한다. ")
    void whenUserRoleIsNotAdminThenReturnShouldBeFalse() {
        final DailygeUser dailygeUser = new DailygeUser(1L, Role.NORMAL);

        assertFalse(dailygeUser.isAdmin());
    }

    @Test
    @DisplayName("사용자 ID를 통해 권한 검사를 할 수 있다.")
    void whenCheckAuthThenCanKnowTheResult() {
        final DailygeUser dailygeUser = new DailygeUser(1L, Role.NORMAL);

        assertAll(
            () -> assertTrue(dailygeUser.isValid(1L)),
            () -> assertFalse(dailygeUser.isValid(2L))
        );
    }

    @Test
    @DisplayName("로그인 정보와 동일한 사용자 ID를 비교할 경우, 에러가 발생하지 않는다.")
    void whenUserIdSameThenShouldBePassed() {
        final DailygeUser dailygeUser = new DailygeUser(1L, Role.NORMAL);

        assertDoesNotThrow(() -> dailygeUser.validateAuth(1L));
    }

    @Test
    @DisplayName("로그인 정보와 다른 사용자 ID를 비교할 경우, UnAuthorizedException이 발생한다.")
    void whenUserIdDifferentThenUnAuthorizedExceptionShouldBeHappen() {
        final DailygeUser dailygeUser = new DailygeUser(1L, Role.NORMAL);

        assertThatThrownBy(() -> dailygeUser.validateAuth(2L))
            .isExactlyInstanceOf(UnAuthorizedException.class)
            .isInstanceOf(RuntimeException.class)
            .hasMessage(USER_NOT_MATCH_MESSAGE);
    }
}
