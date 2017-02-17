package eu.kielczewski.example.controller;

import eu.kielczewski.example.domain.User;
import eu.kielczewski.example.service.UserService;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import javax.annotation.PostConstruct;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class GreetingController implements ApplicationListener<SessionConnectedEvent> {

    @Autowired
    private UserService userService;

    private SimpMessagingTemplate template;

    @Autowired
    public GreetingController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @PostConstruct
    public void init() {
        userService.subscribe(new Observer<User>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(User user) {
                newUsers(user);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @MessageMapping("/hello")
    public void greeting(User user) throws Exception {
        userService.save(user);
    }

    @RequestMapping(path = "/topic/greetings", method = POST)
    public void newUsers(User greeting) {
        this.template.convertAndSend("/topic/greetings", greeting);
    }

    @SubscribeMapping("/topic/greetings")
    public void list() {
        userService.getList().forEach(u -> template.convertAndSend("/topic/greetings", u));
    }

    @Override
    public void onApplicationEvent(SessionConnectedEvent sessionConnectedEvent) {
        this.template.convertAndSend("/topic/greetings", userService.getList());
    }
}