import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.util.Locale;

import static io.restassured.RestAssured.given;
//Создание уникального пользователя
public class ApiUser {
    public static Response postCreateNewUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
    }
//логин под существующим пользователем
    public static Response checkRequestAuthLogin(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/login");
    }

    //удаление пользователя
    public Response deleteUser(String accessToken){
        return given()
                .header("Authorization",accessToken)
                .when()
                .delete("/api/auth/user");
    }
    //Ответ сервера на неуспешную регистрацию пользователя
    public static void failedResponseRegister(Response response) {
        response.then().log().all()
                .assertThat().statusCode(403).and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("Email, password and name are required fields"));
    }
    // авторизация с неверным логином и паролем
    public void failedResponseAuthLogin(Response response) {
        response.then().log().all()
                .assertThat().statusCode(401).and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("email or password are incorrect"));
    }
    //Изменение данных пользователя без авторизации
    public Response sendPatchRequestWithoutAuthorizationApiAuthUser(User user) {
        return given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .patch("/api/auth/user");
    }
    //Изменение данных пользователя с авторизацией
    public Response sendPatchRequestWithAuthorizationApiAuthUser(User user, String token) {
        return given()
                .log()
                .all()
                .headers("Content-Type", "application/json", "Authorization", token)
                .body(user)
                .when()
                .patch("/api/auth/user");
    }

    //Успешный ответ сервера на изменение данных пользователя
    public void checkSuccessResponseAuthUser(Response response, String email, String name) {
        response.then().log().all()
                .assertThat()
                .statusCode(200)
                .body("success", Matchers.is(true))
                .and().body("user.email", Matchers.is(email.toLowerCase(Locale.ROOT)))
                .and().body("user.name", Matchers.is(name));
    }
}
