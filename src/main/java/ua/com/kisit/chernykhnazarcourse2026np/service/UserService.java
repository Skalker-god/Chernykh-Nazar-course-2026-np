package ua.com.kisit.chernykhnazarcourse2026np.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;
import ua.com.kisit.chernykhnazarcourse2026np.entity.UserRole;
import ua.com.kisit.chernykhnazarcourse2026np.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Отримати всіх користувачів
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Змінити роль користувача
    public void changeRole(Long id, UserRole newRole) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setRole(newRole);
            userRepository.save(user);
        }
    }

    // Заблокувати або розблокувати користувача
    public void toggleUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setIsActive(!user.getIsActive());
            userRepository.save(user);
        }
    }

    // Отримати користувача за ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}