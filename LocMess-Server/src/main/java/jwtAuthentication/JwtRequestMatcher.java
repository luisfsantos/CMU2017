package jwtAuthentication;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lads on 21-04-2017.
 */
public class JwtRequestMatcher implements RequestMatcher {

    private OrRequestMatcher skipMatcher;
    private RequestMatcher toProcessMatcher;

    public JwtRequestMatcher(String toProcess, List<String> skippingPath) {
        List<RequestMatcher> m = skippingPath.stream().map(path -> new AntPathRequestMatcher(path)).collect(Collectors.toList());
        skipMatcher = new OrRequestMatcher(m);
        toProcessMatcher = new AntPathRequestMatcher(toProcess);
    }

    @Override public boolean matches(HttpServletRequest request) {
        if (skipMatcher.matches(request)) {
            return false;
        }
        return toProcessMatcher.matches(request) ? true : false;
    }
}
