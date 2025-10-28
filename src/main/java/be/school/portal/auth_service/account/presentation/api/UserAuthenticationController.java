package be.school.portal.auth_service.account.presentation.api;

import be.school.portal.auth_service.account.application.dto.LoginRequest;
import be.school.portal.auth_service.account.application.dto.LoginResponse;
import be.school.portal.auth_service.account.application.dto.TokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.CompletableFuture;

@Tag(
    name = "User Authentication",
    description = "Endpoints for user authentication and token management")
public interface UserAuthenticationController {

  @Operation(
      summary = "Authenticate user and generate tokens",
      description =
          "Authenticates a user using their credentials and returns an access token and a refresh token. "
              + "The access token is used for authorization, while the refresh token can be used to obtain new access tokens.",
      requestBody =
          @RequestBody(
              required = true,
              description = "User credentials (username and password)",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = LoginRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated. Access and refresh tokens are returned.",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "401", description = "Invalid username or password")
      })
  CompletableFuture<LoginResponse> login(LoginRequest loginRequest);

  @Operation(
      summary = "Revoke refresh token (logout)",
      security = {@SecurityRequirement(name = "bearerAuth")},
      description =
          "Revokes the provided refresh token, effectively logging out the user. "
              + "Once revoked, the token can no longer be used to obtain new access tokens.",
      requestBody =
          @RequestBody(
              required = true,
              description = "The refresh token to be revoked",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = TokenRequest.class))),
      responses = {
        @ApiResponse(responseCode = "204", description = "Successfully revoked token"),
        @ApiResponse(responseCode = "400", description = "Invalid token format or missing token"),
        @ApiResponse(responseCode = "401", description = "Unauthorized or invalid token")
      })
  CompletableFuture<Void> logout(TokenRequest tokenRequest);

  @Operation(
      summary = "Refresh access token",
      description =
          "Generates a new access token using a valid refresh token. "
              + "This endpoint allows a user to continue their session without re-entering credentials.",
      requestBody =
          @RequestBody(
              required = true,
              description = "The refresh token used to obtain a new access token",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = TokenRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully refreshed tokens",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid or expired refresh token",
            content = @Content)
      })
  CompletableFuture<LoginResponse> refresh(TokenRequest tokenRequest);
}
