package project.dailyge.app.test.user.integrationtest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static project.dailyge.app.codeandmessage.CommonCodeAndMessage.INVALID_USER_ID;
import project.dailyge.app.common.DatabaseTestBase;
import project.dailyge.app.common.exception.CommonException;
import project.dailyge.app.core.user.application.UserReadService;
import project.dailyge.app.core.user.application.UserWriteService;
import static project.dailyge.app.core.user.exception.UserCodeAndMessage.USER_NOT_FOUND;
import project.dailyge.app.core.user.exception.UserTypeException;
import static project.dailyge.app.core.user.exception.UserCodeAndMessage.USER_SERVICE_UNAVAILABLE;
import static project.dailyge.app.test.user.fixture.UserFixture.createUser;
import static project.dailyge.app.test.user.integrationtest.TokenManagerIntegrationTest.DETAIL_MESSAGE;
import project.dailyge.entity.user.UserJpaEntity;

import java.util.Optional;

@DisplayName("[IntegrationTest] 사용자 조회 통합 테스트")
class UserReadIntegrationTest extends DatabaseTestBase {

    private static final String NAME = "dailyges";
    private static final String EMAIL = "dailyges@gmail.com";


    @Autowired
    private UserReadService userReadService;

    @Autowired
    private UserWriteService userWriteService;

    @Test
    @DisplayName("등록된 사용자를 조회하면, Null이 아니다.")
    void whenFindUserThenUserShouldBeNotNull() {
        final UserJpaEntity user = userWriteService.save(createUser(null, NAME, EMAIL));
        final UserJpaEntity findUser = userReadService.findById(user.getId());

        assertNotNull(findUser);
    }

    @Test
    @DisplayName("등록된 사용자가 없다면, UserNotFoundException이 발생한다.")
    void whenFindNonExistentUserThenUserNotFoundExceptionShouldBeHappen() {
        assertThatThrownBy(() -> userReadService.findById(Long.MAX_VALUE))
            .isExactlyInstanceOf(UserTypeException.from(USER_NOT_FOUND).getClass())
            .isInstanceOf(UserTypeException.class)
            .hasMessage(USER_NOT_FOUND.message());
    }

    @Test
    @DisplayName("사용자를 조회하면, Null이 아니다.")
    void whenActiveUserFindThenUserShouldBeNotNull() {
        final UserJpaEntity saveUser = userWriteService.save(createUser(null, NAME, EMAIL));
        final UserJpaEntity findUser = userReadService.findActiveUserById(saveUser.getId());

        assertNotNull(findUser);
    }

    @Test
    @DisplayName("블랙리스트 사용자 조회 시, UserServiceUnAvailableException이 발생한다.")
    void whenFindBlacklistUserThenUserServiceUnAvailableExceptionShouldBeHappen() {
        final UserJpaEntity blacklistUser = new UserJpaEntity(null, "blacklistUser", "blacklistUser@gmail.com", true);
        final UserJpaEntity saveBlacklistUser = userWriteService.save(blacklistUser);

        assertThatThrownBy(() -> userReadService.findActiveUserById(saveBlacklistUser.getId()))
            .isExactlyInstanceOf(UserTypeException.from(USER_SERVICE_UNAVAILABLE).getClass())
            .isInstanceOf(UserTypeException.class)
            .hasMessage(USER_SERVICE_UNAVAILABLE.message());
    }

    @Test
    @DisplayName("사용자 조회 시 없다면, UserNotFoundException이 발생한다.")
    void whenFindNonActiveUserThenUserNotFoundExceptionShouldBeHappen() {
        assertThatThrownBy(() -> userReadService.findById(Long.MAX_VALUE))
            .isExactlyInstanceOf(UserTypeException.from(USER_NOT_FOUND).getClass())
            .isInstanceOf(UserTypeException.class)
            .hasMessage(USER_NOT_FOUND.message());
    }

    @Test
    @DisplayName("사용자 조회 시 없다면, UnAuthorizedException이 발생한다.")
    void whenFindLoggedUserNonExistentThenUnAuthorizedExceptionShouldBeHappen() {
        assertThatThrownBy(() -> userReadService.findAuthorizedUserById(Long.MAX_VALUE))
            .isExactlyInstanceOf(CommonException.from(INVALID_USER_ID).getClass())
            .extracting(DETAIL_MESSAGE)
            .isEqualTo(USER_NOT_FOUND.message());
    }

    @Test
    @DisplayName("이메일로 사용자를 조회 시, 값이 존재한다.")
    void whenFindUserByRegisteredEmailThenResultShouldBeTrue() {
        final UserJpaEntity user = userWriteService.save(createUser(null, NAME, EMAIL));
        final Optional<UserJpaEntity> findUser = userReadService.findActiveUserByEmail(user.getEmail());

        assertTrue(findUser.isPresent());
    }

    @Test
    @DisplayName("존재하지 않은 이메일로 조회 시, 값이 나오지 않는다.")
    void whenFindUserByUnregisteredEmailThenResultShouldBeFalse() {
        final Optional<UserJpaEntity> findUser = userReadService.findActiveUserByEmail("notExist@gmail.com");

        assertFalse(findUser.isPresent());
    }

    @Test
    @DisplayName("동일한 이메일로 재 가입 시, 삭제 되지 않은 정보만 검색된다.")
    void whenFindUserReRegisteredBySameEmailThenActiveUserShouldBeOne() {
        final UserJpaEntity deleteUser = userWriteService.save(createUser(null, NAME, EMAIL));
        userWriteService.delete(deleteUser.getId());

        final UserJpaEntity activeUser = userWriteService.save(createUser(null, NAME, EMAIL));
        final Optional<UserJpaEntity> findUser = userReadService.findActiveUserByEmail(EMAIL);

        assertAll(
            () -> assertTrue(findUser.isPresent()),
            () -> assertEquals(activeUser.getId(), findUser.get().getId()),
            () -> assertNotEquals(deleteUser.getId(), findUser.get().getId())
        );
    }

    @Test
    @DisplayName("사용자가 존재할 경우, true 를 반환한다.")
    void whenUserExistentUserThenResultShouldBeTrue() {
        final UserJpaEntity user = userWriteService.save(createUser(null, NAME, EMAIL));

        assertTrue(userReadService.existsById(user.getId()));
    }

    @Test
    @DisplayName("사용자가 존재하지 않는 경우, false 를 반환한다.")
    void whenUserNonExistentThenResultShouldBeTrue() {
        assertFalse(userReadService.existsById(Long.MAX_VALUE));
    }
}
