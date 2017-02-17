package eu.kielczewski.example.service;

import eu.kielczewski.example.domain.User;
import eu.kielczewski.example.repository.UserRepository;
import eu.kielczewski.example.service.exception.UserAlreadyExistsException;
import io.reactivex.Observable;
import io.reactivex.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository repository;
    private List<Observer<User>> observers = new ArrayList<>();

    @Inject
    public UserServiceImpl(final UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public User save(final User user) {
        LOGGER.debug("Creating {}", user);
        User existing = repository.findOne(user.getId());
        if (existing != null) {
            throw new UserAlreadyExistsException(
                    String.format("There already exists a user with id=%s", user.getId()));
        }
        User save = repository.save(user);
        Observable<User> userObservable = Observable.just(user);
        observers.forEach(userObservable::subscribe);
        return save;
    }


    @Override
    @Transactional(readOnly = true)
    public List<User> getList() {
        LOGGER.debug("Retrieving the list of all users");
        return repository.findAll();
    }

    @Override
    public void subscribe(Observer<User> action) {
        this.observers.add(action);
    }
}
