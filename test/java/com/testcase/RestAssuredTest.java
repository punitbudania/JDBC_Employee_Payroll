package com.testcase;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class RestAssuredTest
{
    @Before
    public void SetUp()
    {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }

    @Test
    public void givenNewEmployee_OnPost_ShouldReturnAddedEmployee()
    {
        Response response = RestAssured.given()
                                        .contentType(ContentType.JSON)
                                        .accept(ContentType.JSON)
                                        .body("{\"name\": \"Lisa\",\"salary\": \"2000\"}")
                                        .when()
                                        .post("/employees");
        String respAsStr = response.asString();
        JsonObject jsonObject = new Gson().fromJson(respAsStr, JsonObject.class);
        int id = jsonObject.get("id").getAsInt();
        response.then().body("id", Matchers.any(Integer.class));
        response.then().body("name", Matchers.is("Lisa"));
    }

    @Test
    public void givenEmployee_OnUpdate_ShouldReturnUpdatedEmployee()
    {
        Response response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .accept(ContentType.JSON)
                            .body("{\"name\": \"Lisa\", \"salary\": \"8000\"}")
                            .when()
                            .put("/employees/3");
        String respAsStr = response.asString();
        response.then().body("name", Matchers.is("Lisa"));
        response.then().body("salary", Matchers.is("8000"));
    }

    @Test
    public void onCalling_ReturnEmployeeList()
    {
        Response response = RestAssured.get("/employees");
        System.out.println("AT FIRST: " + response.asString());
        response.then().body("id", Matchers.hasItems(1, 2, 3));
        response.then().body("name", Matchers.hasItems("Lisa"));
    }

    @Test
    public  void givenEmployeeId_OnDelete_ShouldReturnSuccessStatus()
    {
        Response response = RestAssured.delete("/employees/1");
        String respAsStr = response.asString();
        int statusCode = response.getStatusCode();
        MatcherAssert.assertThat(statusCode, CoreMatchers.is(200));
        response = RestAssured.get("/employees");
        response.then().body("id", Matchers.not(1));
    }
}
