package be.school.portal.auth_service.infrastructure.api;

import be.school.portal.auth_service.application.dto.LoginRequest;
import be.school.portal.auth_service.application.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Authentication", description = "Endpoints for login")
public interface AuthenticationController {

  @Operation(
      summary = "Login user and receive access and refresh tokens",
      requestBody =
          @RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = LoginRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully logged in",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
      })
  CompletableFuture<LoginResponse> login(LoginRequest loginRequest);
}
