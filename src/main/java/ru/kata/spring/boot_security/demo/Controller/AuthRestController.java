//package ru.kata.spring.boot_security.demo.Controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthRestController {
//
//    @PostMapping("/logout")
//    public ResponseEntity<?> performLogout(HttpServletRequest request,
//                                           HttpServletResponse response) {
//        try {
//            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
//            logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
//
//            return ResponseEntity.ok().body(Map.of(
//                    "message", "Logout successful",
//                    "redirectUrl", "/login?logout"
//            ));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Logout failed"));
//        }
//    }
//}
