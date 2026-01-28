package alt.portfolio.builder.application;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.exceptions.EntityNotFoundException;
import alt.portfolio.builder.exceptions.UnauthorizedException;
import alt.portfolio.builder.exceptions.ValidationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.URLDecoder;
import java.nio.charset.Charset;

@ControllerAdvice
public class MainControllerAdvice {

    @ModelAttribute("activeUser")
    public User getActivatedUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return null;
            }
            Object principal = auth.getPrincipal();
            if (principal instanceof User) {
                return (User) principal;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ModelAndView entityNotFound(EntityNotFoundException ex) {
        return  new ModelAndView("/errors/404","message", ex.getMessage());
    }

    @ExceptionHandler(exception = UnauthorizedException.class)
    public ModelAndView unauthorizedHandler(UnauthorizedException ex) {
        return new ModelAndView("/errors/403", "message", ex.getMessage());
    }

    @ExceptionHandler(exception = ValidationException.class)
    public ModelAndView validationExceptionHandler(ValidationException ex) {
        return new ModelAndView("/errors/400", "message", ex.getMessage());
    }

    @ExceptionHandler(exception = NullPointerException.class)
    public ModelAndView nullPointerExceptionHandler() {
        return new ModelAndView("/errors/404","message", "Accès à une instance non créée.");
    }

    @ExceptionHandler(exception = NoHandlerFoundException.class)
    public ModelAndView handle404(NoHandlerFoundException ex) {
        String url = URLDecoder.decode(ex.getRequestURL(), Charset.defaultCharset());
        ModelAndView mv = new ModelAndView("/errors/404","message", "URL non trouvée : " + url);
        User activeUser = getActivatedUser();
        if (activeUser != null) {
            mv.addObject("activeUser", activeUser);
        }
        return mv;
    }
}
