package br.com.lucaszarzur.events.service;

import java.util.List;
import java.util.stream.IntStream;

import br.com.lucaszarzur.events.repository.EventRepository;
import br.com.lucaszarzur.events.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.lucaszarzur.events.dto.SubscriptionRankingByUser;
import br.com.lucaszarzur.events.dto.SubscriptionRankingItem;
import br.com.lucaszarzur.events.dto.SubscriptionResponse;
import br.com.lucaszarzur.events.exception.EventNotFoundException;
import br.com.lucaszarzur.events.exception.SubscriptionConflictException;
import br.com.lucaszarzur.events.exception.UserIndicatorNotFoundException;
import br.com.lucaszarzur.events.model.Event;
import br.com.lucaszarzur.events.model.Subscription;
import br.com.lucaszarzur.events.model.User;
import br.com.lucaszarzur.events.repository.UserRepository;

@Service
public class SubscriptionService {
    
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId){
        Event evt = eventRepository.findByPrettyName(eventName);
        if (evt == null){
            throw new EventNotFoundException("Evento " + eventName+ " não existe");
        }

        User userRec = userRepository.findByEmail(user.getEmail());
        if (userRec == null) {
            userRec = userRepository.save(user);
        }

        User indicator = null;
        if (userId != null) {
            indicator = userRepository.findById(userId).orElse(null);
            if (indicator == null) {
                throw new UserIndicatorNotFoundException("Usuário " + userId + " indicador não existe");
            }
        }

        Subscription subs = new Subscription();
        subs.setEvent(evt);
        subs.setSubscriber(userRec);
        subs.setIndication(indicator);

        Subscription tmpSub = subscriptionRepository.findByEventAndSubscriber(evt, userRec);
        if (tmpSub != null) {
            throw new SubscriptionConflictException("Já existe incrição para o usuário " + userRec.getName() + " no evento " + evt.getTitle());
        }

        Subscription res = subscriptionRepository.save(subs);
        return new SubscriptionResponse(res.getSubscriptionNumber(), "http://codecraft.com/subscription/"+res.getEvent().getPrettyName()+"/"+res.getSubscriber().getId());
    }

    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName){
        Event evt = eventRepository.findByPrettyName(prettyName);
        if (evt ==null) {
            throw new EventNotFoundException("Ranking do evento " + prettyName + " não existe");
        }
        return subscriptionRepository.generateRanking(evt.getEventId());
    }

    public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId){
        List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);

        SubscriptionRankingItem item = ranking.stream().filter(i-> i.userId().equals(userId)).findFirst().orElse(null);
        if(item == null) {
            throw new UserIndicatorNotFoundException("Não há inscrições com indicação do usuário " + userId);
        }

        Integer posicao = IntStream.range(0, ranking.size())
                          .filter(pos -> ranking.get(pos).userId().equals(userId))
                          .findFirst().getAsInt();

        return new SubscriptionRankingByUser(item, posicao+1);
    }
}
