package com.testcase;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
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

    public Response getEmployeeList()
    {
        Response response = RestAssured.get("/employees/list");
        return response;
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

    }
}
