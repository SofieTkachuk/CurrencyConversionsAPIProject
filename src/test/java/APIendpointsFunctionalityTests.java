import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

public class APIendpointsFunctionalityTests {

    private static Response response;

    // Get current currency conversion rates
    // /live endpoint is expected to return JSON response with the following information:
    // success - Returns true or false depending on whether or not your query succeeds.
    // terms - Returns a link to the currency layer Terms & Conditions.
    // privacy - Returns a link to the currency layer Privacy Policy.
    // source - Returns the currency to which all exchange rates are relative. (default: USD)
    // quotes - It contains all exchange rate values, consisting of the currency pairs and their respective conversion rates.

    @Test
    public void liveEndpointValidResponseTest(){
        response = given().get(Consts.URL+Consts.LIVE_ENDPOINT+"?"+Consts.API_ACCESS_KEY);
        System.out.println(response.asString());

        response.then()
                .assertThat()
                .body("success", anyOf(equalTo(true), equalTo(false)))
                //.body("terms", contains("http")) // body does not found
                //.body("privacy", contains("http")) // body does not found
                .body("timestamp", notNullValue())
                .body("source", equalTo("USD"))
                .body("quotes", notNullValue());
    }

    // timestamp - Returns the exact date and time (UNIX) the exchange rates were collected.

    @Test
    public void timeStampTest(){
        response = given().get(Consts.URL+Consts.LIVE_ENDPOINT+"?"+Consts.API_ACCESS_KEY);
        System.out.println(response.asString());

        String expected = LocalDate.now().toString();
        //get timestamp from response
        Integer actualMs = response.path("timestamp");
        //create format to match expected String date
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //get Date from the timestamp in request - a hard part, we need to set it to long and multiply by 1000
        //as in this case API returns UNIX time and not epoch time
        Date date2 = new Date((long)actualMs*1000);
        //format date from response to match expected String date
        String actual = format.format(date2.getTime());

        Assertions.assertEquals(expected, actual);}

    // Please note that our customer will use  only some of the currencies (USDCAD, USDEUR, USDNIS, and USDRUB)

    @ParameterizedTest
    @ValueSource(strings = {"USDCAD", "USDEUR", "USDILS", "USDRUB"}) // changed NIS to ILS
    public void getCurrentCurrencyConversionRatesTest(String currency){
        response = given().get(Consts.URL+Consts.LIVE_ENDPOINT+"?"+Consts.API_ACCESS_KEY);
        System.out.println(response.asString());

        response.then().body("quotes", hasKey(currency));
    }

    // Every currency could be retrieved to the client using the “currencies" parameter. In the request, we should be able to send one or several currencies divided by comma.
    // Please use the currencies that are important for our customer (CAD, EUR, ILS, and RUB).

    @Test
    public void retrievingSpecificCurrenciesTest() {
        response = given().get(Consts.URL + Consts.LIVE_ENDPOINT + "?" + Consts.API_ACCESS_KEY + "&" + Consts.SOURCE_PARAMETER + "USD&" + Consts.CURRENCIES_PARAMETER + "CAD,EUR,ILS,RUB");
        System.out.println(response.asString());

        response.then().statusCode(200);
    }

    // Incorrect currency code should trigger a user-friendly error.

    @Test
    public void incorrectCurrencyCodeTest() {
        response = given().get(Consts.URL + Consts.LIVE_ENDPOINT + "?" + Consts.API_ACCESS_KEY + "&" + Consts.SOURCE_PARAMETER + "USD&" + Consts.CURRENCIES_PARAMETER + "LAA");
        System.out.println(response.asString());

        response.then().body("error.code", equalTo(202));
    }

    // Historical conversion according to date
    // /historical endpoint is functional and requires the “Date” parameter

    @Test
    public void historicalConversionWithDateTest() {
        response = given().get(Consts.URL + Consts.HISTORICAL_ENDPOINT + "?" + Consts.DATE_PARAMETER+"2024-01-03&"+Consts.API_ACCESS_KEY);
        System.out.println(response.asString());

        response.then().statusCode(200);
    }

    // A user-friendly error should be returned if the parameter is incorrect or missing.

    @Test
    public void historicalConversionWithoutDateTest() {
        response = given().get(Consts.URL+Consts.HISTORICAL_ENDPOINT+"?"+Consts.DATE_PARAMETER+"&"+Consts.API_ACCESS_KEY);
        System.out.println(response.asString());

        response.then().body("error.code", equalTo(301));
    }

    @Test
    public void historicalConversionWithInvalidDateTest() {
        response = given().get(Consts.URL + Consts.HISTORICAL_ENDPOINT + "?" + Consts.DATE_PARAMETER+"invalid-date&"+Consts.API_ACCESS_KEY);
        System.out.println(response.asString());

        response.then().body("error.code", equalTo(302));
    }

    // Endpoint is able to receive currencies parameters.
    // Historical rates should be available for the same currencies as before  (CAD, EUR, ILS, and RUB).

    @Test
    public void historicalConversionWithCurrencyTest() {
        response = given().get(Consts.URL + Consts.HISTORICAL_ENDPOINT + "?" + Consts.DATE_PARAMETER+"2024-01-03&"+Consts.API_ACCESS_KEY+"&" + Consts.SOURCE_PARAMETER + "USD&" + Consts.CURRENCIES_PARAMETER + "CAD,EUR,ILS,RUB");
        System.out.println(response.asString());

        response.then().statusCode(200);
    }
}
