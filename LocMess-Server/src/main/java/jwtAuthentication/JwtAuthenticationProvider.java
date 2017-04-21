package jwtAuthentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by lads on 21-04-2017.
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Override public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //TODO implement the check to authenticate JWT
        return null;
    }

    @Override public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
