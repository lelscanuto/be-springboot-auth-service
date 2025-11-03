package be.school.portal.auth_service.common.handler;

import be.school.portal.auth_service.common.component.JwtTokenComponent;
import be.school.portal.auth_service.common.security.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A security filter that handles JWT (JSON Web Token)–based authentication for incoming HTTP
 * requests.
 *
 * <p>This filter intercepts each request, validates the provided JWT, and sets the authenticated
 * {@link org.springframework.security.core.context.SecurityContext SecurityContext} if the token is
 * valid. It extends {@link org.springframework.web.filter.OncePerRequestFilter} to ensure that each
 * request is processed exactly once per request lifecycle.
 *
 * <p>Responsibilities:
 *
 * <ul>
 *   <li>Extract and validate the <b>Bearer</b> token from the {@code Authorization} header.
 *   <li>Retrieve user details via {@link UserDetailsService} if the token is valid.
 *   <li>Establish authentication in the {@link SecurityContextHolder} for downstream security
 *       checks.
 *   <li>Allow unauthenticated requests (e.g., public endpoints) to proceed without interruption.
 * </ul>
 *
 * <p>This component is typically registered before Spring Security’s {@code
 * UsernamePasswordAuthenticationFilter} in the security filter chain.
 *
 * @see org.springframework.web.filter.OncePerRequestFilter
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 * @see org.springframework.security.core.context.SecurityContextHolder
 * @author Francis Jorell Canuto
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final JwtTokenComponent jwtTokenComponent;
  private final UserDetailsService userDetailsService;

  /**
   * Constructs a new {@code JwtAuthenticationFilter} with the required security dependencies.
   *
   * @param jwtTokenComponent the component responsible for token creation, validation, and parsing
   * @param userDetailsService the service used to load user-specific data from the application’s
   *     authentication context (cache or persistence)
   */
  public JwtAuthenticationFilter(
      JwtTokenComponent jwtTokenComponent, UserDetailsService userDetailsService) {
    this.jwtTokenComponent = jwtTokenComponent;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Indicates whether this filter should be skipped during asynchronous dispatch processing.
   *
   * <p>By returning {@code false}, the filter ensures that JWT validation is applied to
   * asynchronous requests as well (e.g., {@code @Async} or deferred result processing).
   *
   * @return {@code false} to enforce JWT validation on async dispatches
   */
  @Override
  protected boolean shouldNotFilterAsyncDispatch() {
    return false;
  }

  /**
   * Core filtering logic that validates JWT tokens and sets the authenticated user context.
   *
   * <p>Processing steps:
   *
   * <ol>
   *   <li>Extracts the {@code Authorization} header and verifies it starts with {@code Bearer }.
   *   <li>Parses and validates the JWT via {@link JwtTokenComponent#validateToken(String)}.
   *   <li>Extracts the username claim and loads user details through {@link UserDetailsService}.
   *   <li>If valid, constructs an authenticated {@link UsernamePasswordAuthenticationToken} and
   *       stores it in the {@link SecurityContextHolder}.
   *   <li>Delegates the request down the filter chain.
   * </ol>
   *
   * <p>If no valid token is found or validation fails, the filter chain proceeds without setting
   * authentication (e.g., for anonymous or public requests).
   *
   * @param request the incoming {@link HttpServletRequest}
   * @param response the outgoing {@link HttpServletResponse}
   * @param filterChain the Spring Security filter chain
   * @throws ServletException if an error occurs during filtering
   * @throws IOException if an I/O error occurs while reading or writing the request/response
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // Log the full request path for traceability
    String path = request.getRequestURI();
    String queryString = request.getQueryString();
    if (queryString != null) {
      path += "?" + queryString;
    }
    LOGGER.debug("Incoming request path: {}", path);

    // 1️⃣ Extract Authorization header
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2️⃣ Extract and validate token
    final String token = authHeader.substring(7);
    if (!jwtTokenComponent.validateToken(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 3️⃣ Retrieve username from token
    String username = jwtTokenComponent.getUsernameFromToken(token);

    // 4️⃣ Establish authentication if not already set
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (jwtTokenComponent.isTokenValid(token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                new UserPrincipal(userDetails), null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    // 5️⃣ Continue with the filter chain
    filterChain.doFilter(request, response);
  }
}
