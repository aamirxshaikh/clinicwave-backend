package com.clinicwave.usermanagementservice.security.service;

import com.clinicwave.usermanagementservice.model.entity.Role;
import com.clinicwave.usermanagementservice.model.entity.User;
import com.clinicwave.usermanagementservice.model.enums.ERole;
import com.clinicwave.usermanagementservice.repository.RoleRepository;
import com.clinicwave.usermanagementservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubOAuth2UserService extends DefaultOAuth2UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    Map<String, Object> attributes = oAuth2User.getAttributes();

    String email = (String) attributes.get("email");
    String username = (String) attributes.get("login");
    String name = (String) attributes.get("name");

    // Check if user exists
    User user = userRepository.findByEmail(email)
            .orElseGet(() -> {
              // Create new user if not exists
              User newUser = User.builder()
                      .username(username)
                      .email(email)
                      .firstName(name != null ? name.split(" ")[0] : username)
                      .lastName(name != null && name.split(" ").length > 1 ? name.split(" ")[1] : "")
                      .enabled(true)
                      .emailVerified(true)
                      .build();

              // Set default role
              Set<Role> roles = new HashSet<>();
              Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                      .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
              roles.add(userRole);
              newUser.setRoles(roles);

              return userRepository.save(newUser);
            });

    // Create UserDetails
    UserDetailsImpl userDetails = UserDetailsImpl.build(user);

    // Create authentication
    Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    return oAuth2User;
  }
}