package br.com.lucaszarzur.events.repository;

import br.com.lucaszarzur.events.model.Event;
import br.com.lucaszarzur.events.model.Subscription;
import br.com.lucaszarzur.events.model.User;
import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepository extends CrudRepository<Subscription, Integer> {
    public Subscription findByEventAndSubscriber(Event evt, User user);

}
