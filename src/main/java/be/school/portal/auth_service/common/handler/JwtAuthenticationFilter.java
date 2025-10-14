package be.school.portal.auth_service.common.handler;

import be.school.portal.auth_service.common.component.JwtTokenComponent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenComponent jwtTokenComponent;
  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(
      JwtTokenComponent jwtTokenComponent, UserDetailsService userDetailsService) {
    this.jwtTokenComponent = jwtTokenComponent;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 1️⃣ Get Authorization header
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2️⃣ Extract token
    final String token = authHeader.substring(7);

    // 3️⃣ Validate token
    if (!jwtTokenComponent.validateToken(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 4️⃣ Extract username from token
    String username = jwtTokenComponent.getUsernameFromToken(token);

    // 5️⃣ If not already authenticated, load user details and set authentication
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (jwtTokenComponent.isTokenValid(token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    // Continue the filter chain
    filterChain.doFilter(request, response);
  }
}
