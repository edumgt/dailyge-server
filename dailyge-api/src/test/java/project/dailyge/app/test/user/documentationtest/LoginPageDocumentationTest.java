package project.dailyge.app.test.user.documentationtest;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.dailyge.app.common.DatabaseTestBase;
import project.dailyge.app.test.user.documentationtest.snippet.UserSnippet;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static project.dailyge.app.test.user.documentationtest.snippet.UserSnippet.LOGIN_PAGE_RESPONSE_SNIPPET;

@DisplayName("[DocumentationTest] 로그인 페이지 API 문서화 테스트")
class LoginPageDocumentationTest extends DatabaseTestBase {

    @Test
    @DisplayName("로그인 페이지를 요청하면, 200 OK 응답을 받는다")
    void whenGetLoginPageThenStatusCodeShouldBe200_OK() {
        RestAssured.given(this.specification)
            .filter(document(IDENTIFIER,
                LOGIN_PAGE_RESPONSE_SNIPPET
            ))
            .contentType(APPLICATION_JSON_VALUE)
            .when()
            .get("/api/login")
            .then()
            .statusCode(200)
            .log()
            .all();
    }
}
