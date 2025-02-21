package br.com.lucaszarzur.events.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.lucaszarzur.events.model.Event;
import br.com.lucaszarzur.events.repository.EventRepository;

@Service
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;

    public Event addNewEvent(Event event){

        event.setPrettyName(event.getTitle().toLowerCase().replaceAll(" ", "-"));
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents(){
        return (List<Event>) eventRepository.findAll();
    }

    public Event getByPrettyName(String prettyName){
        return eventRepository.findByPrettyName(prettyName);
    }
}
