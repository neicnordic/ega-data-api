package eu.elixir.ega.ebi.shared.service.internal;

import eu.elixir.ega.ebi.dataedge.config.CustomUsernamePasswordAuthenticationToken;
import eu.elixir.ega.ebi.shared.service.AuthenticationService;

import java.util.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

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
    if ( details instanceof OAuth2AuthenticationDetails ){
      OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails)details;

      Map<String, String> decodedDetails = (Map<String, String>) oauthDetails.getDecodedDetails();
      decodedDetails = firstNonNull(decodedDetails, new LinkedHashMap<>());
      for (Map.Entry<String,String> entry : decodedDetails.entrySet())
        System.out.println("Key = " + entry.getKey() +
                ", Value = " + entry.getValue());


      //String sub = decodedDetails.get("sub").toString();
      //System.out.println( "My custom claim value: " + sub );
      System.out.println(oauthDetails.getTokenValue());
      System.out.println(oauthDetails.getTokenType());
      System.out.println(oauthDetails.getRemoteAddress());
      return "sub";

    } else {
      return null;
    }
    /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    OAuth2Authentication auth = (OAuth2Authentication) authentication;
    Authentication details = auth.getUserAuthentication();
    LinkedHashMap<String, String> smth = (LinkedHashMap<String, String>) details.getDetails();
    String sub = smth.get("sub");
    return sub;*/

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

}
