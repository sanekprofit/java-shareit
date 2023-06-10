package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserServiceImpl implements UserService {

    Map<Long, User> users = new HashMap<>();

    private long generatorId = 0;

    @Override
    public User getUser(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        validationCheck(user);
        generatorId++;
        user.setId(generatorId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User patchUser(User user, Long userId) {
        validationCheckPatch(user, userId);
        User userOld = users.get(userId);
        user.setId(userId);
        if (user.toString().contains("email=null")) {
            user.setEmail(userOld.getEmail());
        }
        if (user.toString().contains("name=null")) {
            user.setName(userOld.getName());
        }
        users.put(userId, user);
        return users.get(user.getId());
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    private void validationCheck(User user) {
        if (user.toString().contains("email=null") || !user.getEmail().contains("@")) {
            throw new ValidationException("Почта должна содержать символ '@' или быть не пустой.");
        }
        for (User user1 : users.values()) {
            if (user.getEmail().equals(user1.getEmail())) {
                throw new DuplicateException("На почту с названием " + user.getEmail() + " " +
                        "уже зарегестрирован пользователь.");
            }
        }
    }

    private void validationCheckPatch(User user, Long userId) {
        if (user.getEmail() != null) {
            boolean duplicateExists = users.values()
                    .stream()
                    .anyMatch(u -> u.getEmail() != null && u.getEmail().equals(user.getEmail()) && u.getId() != userId);

            if (duplicateExists) {
                throw new DuplicateException("На почту с названием " + user.getEmail() + " " +
                        "уже зарегистрирован пользователь.");
            }
        }
    }
}
