package eu.elixir.ega.ebi.shared.service;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public interface AuthenticationService {

  /**
   * Returns the authentication information from the current security context.
   *
   * @return The current authentication or null.
   */
  Authentication getAuthentication();

  /**
   * Returns the login name from the current security context.
   *
   * @return The login name or null.
   */
  String getName();

  String getSubjectIdentifier();

  /**
   * Returns the authentication authorities of the current security context.
   *
   * @return The collection of authorities, or null.
   */
  Collection<? extends GrantedAuthority> getAuthorities();

  /**
   * Returns a map of datasets and included files for the current authentication
   * context.
   *
   * @return A map of datasets and included files, or null.
   */
  Map<String, List<String>> getDatasetFileMapping();
}
