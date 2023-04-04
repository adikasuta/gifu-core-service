package com.gifu.coreservice.utils;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.exception.InvalidRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class SessionUtils {

    public static String getClientIP(HttpServletRequest request){
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ipAddress)) {
            String[] parts = ipAddress.split(",");
            if (parts.length > 0) {
                ipAddress = parts[0].trim();
            }
        } else {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
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
