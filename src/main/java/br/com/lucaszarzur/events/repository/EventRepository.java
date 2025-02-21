package br.com.lucaszarzur.events.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.lucaszarzur.events.model.Event;

public interface EventRepository extends CrudRepository<Event, Integer> {
    public Event findByPrettyName(String prettyName);
    
}
