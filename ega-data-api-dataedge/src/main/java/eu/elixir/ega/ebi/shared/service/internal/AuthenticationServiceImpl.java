package eu.elixir.ega.ebi.shared.service.internal;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.elixir.ega.ebi.dataedge.config.CustomUsernamePasswordAuthenticationToken;
import eu.elixir.ega.ebi.shared.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private static final String SUB = "sub";

  /**
   * Returns the authentication information from the current security context.
   *
   * @return The current authentication or null.
   */
  @Override
  public Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }


  /**
   * Returns the login name from the current security context.
   *
   * @return The login name or null.
   */
  @Override
  public String getName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      return authentication.getName();
    }
    return null;
  }

    public String getSubjectIdentifier() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object details = authentication.getDetails();
        if (details instanceof OAuth2AuthenticationDetails) {
            OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails) details;
            return decodeClaimFromToken(oauthDetails.getTokenValue(), SUB);
        } else {
            return null;
        }
    }
  /**
   * Returns the authentication authorities of the current security context.
   *
   * @return The collection of authorities, or null.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      return authentication.getAuthorities();
    }
    return null;
  }

  /**
   * Returns a map of datasets and included files for the current authentication
   * context.
   *
   * @return A map of datasets and included files, or null.
   */
  @Override
  public Map<String, List<String>> getDatasetFileMapping() {
    if (getAuthentication() instanceof OAuth2Authentication) {
      OAuth2Authentication authentication = (OAuth2Authentication) getAuthentication();
      if (authentication != null
          && authentication.getUserAuthentication() instanceof CustomUsernamePasswordAuthenticationToken) {
        CustomUsernamePasswordAuthenticationToken authenticationToken = (CustomUsernamePasswordAuthenticationToken) authentication
            .getUserAuthentication();
        return authenticationToken.getDatasetFileMapping();
      }
    }
    return null;
  }
  
  private String decodeClaimFromToken(String accessToken, String claimKey) {
    if (accessToken == null || accessToken.length() == 0)
      return null;

    Jwt decodedToken = JwtHelper.decode(accessToken);
    String claims = decodedToken.getClaims();
    boolean contains = claims.contains(claimKey);
    if (contains) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        Map<String, Object> tokenMap =
            mapper.readValue(claims, new TypeReference<Map<String, Object>>() {});
        return tokenMap.get(claimKey).toString();
      } catch (Exception e) {
        log.error(e.getMessage());
      }

    }
    return null;
  }
}
