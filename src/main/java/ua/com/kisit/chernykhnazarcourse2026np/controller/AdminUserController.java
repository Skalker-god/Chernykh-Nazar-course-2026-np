package ua.com.kisit.chernykhnazarcourse2026np.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;
import ua.com.kisit.chernykhnazarcourse2026np.entity.UserRole;
import ua.com.kisit.chernykhnazarcourse2026np.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView usersPage() {
        ModelAndView mav = new ModelAndView("admin/users");
        mav.addObject("users", userService.getAllUsers());
        return mav;
    }

    @PostMapping("/role/{id}")
    public String changeRole(@PathVariable Long id,
                             @RequestParam String role,
                             HttpSession session,
                             HttpServletRequest request) {
        User currentUser = (User) session.getAttribute("user");

        // Захист: адмін не може змінити сам собі роль
        if (currentUser != null && currentUser.getId().equals(id)) {
            return "redirect:/admin/users?error=cannot_change_own_role";
        }

        UserRole newRole = UserRole.valueOf(role);
        userService.changeRole(id, newRole);

        // null-safe: якщо currentUser чомусь null — пишемо "unknown"
        String adminPhone = currentUser != null ? currentUser.getPhone() : "unknown";
        log.warn("АДМІН: {} змінив роль користувача {} на {} (IP: {})",
                adminPhone, id, newRole, request.getRemoteAddr());

        return "redirect:/admin/users";
    }

    @PostMapping("/toggle/{id}")
    public String toggleUser(@PathVariable Long id,
                             HttpSession session,
                             HttpServletRequest request) {
        User currentUser = (User) session.getAttribute("user");

        // Захист: адмін не може заблокувати сам себе
        if (currentUser != null && currentUser.getId().equals(id)) {
            return "redirect:/admin/users?error=cannot_block_own_account";
        }

        userService.toggleUser(id);

        // Читаємо оновлений стан після toggle для точного логування
        User target = userService.getUserById(id);
        String adminPhone = currentUser != null ? currentUser.getPhone() : "unknown";
        String action = (target != null && target.getIsActive()) ? "РОЗБЛОКУВАВ" : "ЗАБЛОКУВАВ";

        log.warn("АДМІН: {} {} користувача {} (IP: {})",
                adminPhone, action, id, request.getRemoteAddr());

        return "redirect:/admin/users";
    }
}