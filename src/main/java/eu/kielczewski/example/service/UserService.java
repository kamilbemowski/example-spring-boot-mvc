package eu.kielczewski.example.service;

import eu.kielczewski.example.domain.User;
import io.reactivex.Observer;

import java.util.List;

public interface UserService {

    User save(User user);

    List<User> getList();

    void subscribe(Observer<User> action);
}
