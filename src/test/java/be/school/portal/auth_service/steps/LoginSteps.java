package be.school.portal.auth_service.steps;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.school.portal.auth_service.common.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

public class LoginSteps {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  private ResultActions result;

  @Autowired
  public LoginSteps(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  @Given("the system has a predefined admin account")
  public void systemHasPredefinedAdminAccount() {
    // No setup needed — admin account already exists in DB or seeded via Flyway.
  }

  @When("the admin logs in using username {string} and password {string}")
  public void adminLogsIn(String username, String password) throws Exception {
    LoginRequest loginRequest = new LoginRequest(username, password);

    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(request().asyncStarted())
            .andReturn();

    result = mockMvc.perform(asyncDispatch(mvcResult));
  }

  @Then("the login should be successful")
  public void loginShouldBeSuccessful() throws Exception {
    result.andExpect(status().isOk()).andDo(document("login-successful"));
  }

  @Then("the login should fail")
  public void loginShouldFail() throws Exception {
    result.andExpect(status().isUnauthorized()).andDo(document("login-failed"));
  }
}
