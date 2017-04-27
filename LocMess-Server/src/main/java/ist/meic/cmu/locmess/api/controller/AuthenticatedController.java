package ist.meic.cmu.locmess.api.controller;

import jwtAuthentication.UserContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by lads on 27/04/2017.
 */
public class AuthenticatedController {

    protected String getUser() {
        return ((UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }
}
