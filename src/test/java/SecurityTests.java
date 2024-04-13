import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class SecurityTests {

    // Critical for all the endpoints below: /live, /historical
    // Unauthorized users should be restricted to get the response from the external API -
    // Expected error: You have not supplied a valid API Access Key.

    private static Response response;
    private static final String ERROR_MESSAGE = "You have not supplied a valid API Access Key.";

    @Test
    public void getErrorMessageNoAccessKeyTestLiveEndpoint(){
        response = given().get(Consts.URL+Consts.LIVE_ENDPOINT);
        System.out.println(response.asString());

        response.then().body("message", equalTo (ERROR_MESSAGE));
        response.then().statusCode(101);
    }

    @Test
    public void getErrorMessageInvalidKeyTestLiveEndpoint(){
        response = given().get(Consts.URL+Consts.LIVE_ENDPOINT+"?"+Consts.INVALID_API_ACCESS_KEY);
        System.out.println(response.asString());

        response.then().body("message", equalTo (ERROR_MESSAGE));
        response.then().statusCode(101);
    }

    @Test
    public void getErrorMessageNoAccessKeyTestHistoricalEndpoint(){
        response = given().get(Consts.URL+Consts.HISTORICAL_ENDPOINT);
        System.out.println(response.asString());

        response.then().body("message", equalTo (ERROR_MESSAGE));
        response.then().statusCode(101);
    }

    @Test
    public void getErrorMessageInvalidKeyTestHistoricalEndpoint(){
        response = given().get(Consts.URL+Consts.HISTORICAL_ENDPOINT+"?"+Consts.DATE_PARAMETER+"2024-01-02&"+Consts.INVALID_API_ACCESS_KEY);
        System.out.println(response.asString());

        response.then().body("message", equalTo (ERROR_MESSAGE));
        response.then().statusCode(101);
    }
}
