package br.com.lucaszarzur.events.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.lucaszarzur.events.dto.ErrorMessage;
import br.com.lucaszarzur.events.dto.SubscriptionResponse;
import br.com.lucaszarzur.events.exception.EventNotFoundException;
import br.com.lucaszarzur.events.exception.SubscriptionConflictException;
import br.com.lucaszarzur.events.exception.UserIndicatorNotFoundException;
import br.com.lucaszarzur.events.model.User;
import br.com.lucaszarzur.events.service.SubscriptionService;

@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping({"/subscription/{prettyName}", "/subscription/{prettyName}/{userId}"})
    public ResponseEntity<?> createSubscription(@PathVariable String prettyName, @RequestBody User subscriber, @PathVariable(required = false) Integer userId){
        try {
            SubscriptionResponse res = subscriptionService.createNewSubscription(prettyName, subscriber, userId);
            if (res != null){
                return ResponseEntity.ok(res);
            }
            
        } catch (EventNotFoundException ex){
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));

        } catch (SubscriptionConflictException ex){
            return ResponseEntity.status(409).body(new ErrorMessage(ex.getMessage()));

        } catch (UserIndicatorNotFoundException ex) {
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/subscription/{prettyName}/ranking")
    public ResponseEntity<?> generateRankingByEvent(@PathVariable String prettyName) {
        try {
            return ResponseEntity.ok(subscriptionService.getCompleteRanking(prettyName).subList(0, 3));
        } catch (EventNotFoundException e){
            return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping("/subscription/{prettyName}/ranking/{userId}")
    public ResponseEntity<?> generateRankingByEventAndUser(@PathVariable String prettyName, @PathVariable Integer userId) {
        try {
            return ResponseEntity.ok(subscriptionService.getRankingByUser(prettyName, userId));
        } catch (Exception ex){
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
    }
}
