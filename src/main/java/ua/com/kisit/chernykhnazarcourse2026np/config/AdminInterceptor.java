package ua.com.kisit.chernykhnazarcourse2026np.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        if (session == null) return true;

        User user = (User) session.getAttribute("user");

        // Якщо акаунт заблокований після входу — завершуємо сесію
        if (user != null && !user.getIsActive()) {
            session.invalidate();
            response.sendRedirect("/login?blocked=true");
            return false;
        }

        return true;
    }
}