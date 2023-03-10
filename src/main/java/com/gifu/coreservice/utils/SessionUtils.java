package com.gifu.coreservice.utils;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.exception.InvalidRequestException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SessionUtils {
    public static User getUserContext() throws InvalidRequestException {
       Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       if(principal instanceof User){
          return (User) principal;
       }
        User anon = new User();
       anon.setEmail("anonymous user");
       return anon;
//       throw new InvalidRequestException("User have not login yet", null);
    }
}
