package com.gifu.coreservice.utils;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.exception.InvalidRequestException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SessionUtils {
    public User getUserContext() throws InvalidRequestException {
       Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       if(principal instanceof User){
          return (User) principal;
       }
       throw new InvalidRequestException("User have not login yet", null);
    }
}
