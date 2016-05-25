package com.theironyard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class CalendarSpringController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home(Model model, HttpSession session) {
        String username = (String) session.getAttribute("userName");
        if(username != null){
            User user = userRepository.findFirstByName(username);
            model.addAttribute("user", user);
        }

        List<Event> events = eventRepository.findAllByOrderByDateTimeDesc();
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        model.addAttribute("events", events);
        model.addAttribute("now", now);

        return "home";
    }

    @RequestMapping(path = "/create-event", method = RequestMethod.POST)
    public String createEvent(HttpSession session, String description, String dateTime){
        String userName = (String) session.getAttribute("userName");
        if (userName != null) {
            Event event = new Event(
                    description,
                    LocalDateTime.parse(dateTime),
                    userRepository.findFirstByName(userName)
            );
            eventRepository.save(event);
        }
        return "redirect:/";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String userName, HttpSession session){

        User user = userRepository.findFirstByName(userName);

        if(user == null){
            user = new User(userName);
            userRepository.save(user);
        }

        session.setAttribute("userName", userName);

        return "redirect:/";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session){
        session.invalidate();

        return "redirect:/";
    }

}
