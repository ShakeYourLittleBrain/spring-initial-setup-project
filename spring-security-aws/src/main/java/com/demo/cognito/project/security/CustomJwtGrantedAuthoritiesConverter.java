package com.demo.cognito.project.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // First, retrieve the default authorities (like scope-based authorities) from the token
        Collection<GrantedAuthority> authorities = defaultGrantedAuthoritiesConverter.convert(jwt);

        // Extract custom claim "cognito:groups" from the JWT
        List<String> groups = jwt.getClaimAsStringList("cognito:groups");

        if (groups != null) {
            // Map each group to a GrantedAuthority (Spring Security authority)
            List<GrantedAuthority> groupAuthorities = groups.stream()
                    .map(group -> new SimpleGrantedAuthority("ROLE_" + group))  // Convert group to ROLE_
                    .collect(Collectors.toList());

            // Add the group authorities to the existing authorities
            authorities.addAll(groupAuthorities);
        }

        return authorities;
    }
}
