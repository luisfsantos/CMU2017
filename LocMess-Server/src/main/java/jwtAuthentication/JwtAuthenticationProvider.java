package jwtAuthentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * Created by lads on 21-04-2017.
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Override public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jwtToken = (String) authentication.getCredentials();
        DecodedJWT jwt;
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build(); //Reusable verifier instance
            System.out.println(jwtToken);
            jwt = verifier.verify(jwtToken);
            String username = jwt.getSubject();
            UserContext context = new UserContext(username, null);
            return new JwtAuthenticationToken(context, context.getAuthorities());
        } catch (UnsupportedEncodingException exception){
            throw new BadCredentialsException("The given encoding is not supported");
        } catch (JWTVerificationException exception){
            throw new BadCredentialsException("Invalid Signature or Claim");
        }
    }

    @Override public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
