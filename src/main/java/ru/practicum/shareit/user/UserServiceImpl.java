package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Optional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User getUser(Long userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        return user.get();
    }

    @Override
    public List<User> getUsers() {
        return repository.findAll();
    }

    @Override
    public User createUser(User user) {
        validationCheck(user);
        return repository.save(user);
    }

    @Override
    public User patchUser(User user, Long userId) {
        validationCheckPatch(user, userId);
        Optional<User> userOld = repository.findById(userId);
        if (userOld.isPresent()) {
            user.setId(userId);
            if (user.toString().contains("email=null")) {
                user.setEmail(userOld.get().getEmail());
            }
            if (user.toString().contains("name=null")) {
                user.setName(userOld.get().getName());
            }
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        return repository.save(user);
    }

    @Override
    public HttpStatus deleteUser(Long userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isPresent()) {
            repository.deleteById(userId);
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        return HttpStatus.OK;
    }

    private void validationCheck(User user) {
        if (user.toString().contains("email=null") || !user.getEmail().contains("@")) {
            throw new ValidationException("Почта должна содержать символ '@' или быть не пустой.");
        }
    }

    private void validationCheckPatch(User user, Long userId) {
        List<User> usersDuplicateEmail = repository.findByEmailContainingIgnoreCaseAndIdNot(user.getEmail(), userId);
        if (!usersDuplicateEmail.isEmpty()) {
            throw new DuplicateException("Пользователь с почтой " + user.getEmail() + " уже существует.");
        }
    }
}