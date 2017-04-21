package jwtAuthentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by lads on 21-04-2017.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private String token;
    private UserContext userContext;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.setAuthenticated(false);
    }

    public JwtAuthenticationToken(UserContext userContext, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.eraseCredentials();
        this.userContext = userContext;
        super.setAuthenticated(true);
    }

    @Override public Object getCredentials() {
        return token;
    }

    @Override public Object getPrincipal() {
        return userContext;
    }

    @Override public void eraseCredentials() {
        super.eraseCredentials();
        token = null;
    }
}
