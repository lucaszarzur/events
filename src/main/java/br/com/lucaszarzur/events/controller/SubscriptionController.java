package br.com.lucaszarzur.events.controller;

import br.com.lucaszarzur.events.dto.ErrorMessage;
import br.com.lucaszarzur.events.dto.SubscriptionResponse;
import br.com.lucaszarzur.events.exception.EventNotFoundException;
import br.com.lucaszarzur.events.exception.SubscriptionConflictException;
import br.com.lucaszarzur.events.exception.UserIndicatorNotFoundException;
import br.com.lucaszarzur.events.model.User;
import br.com.lucaszarzur.events.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionService service;

    @PostMapping({"/subscription/{prettyName}", "/subscription/{prettyName}/{userId}"})
    public ResponseEntity<?> createSubscription(@PathVariable String prettyName, @RequestBody User subscriber, @PathVariable(required = false) Integer userId){
        try {
            SubscriptionResponse res = service.createNewSubscription(prettyName, subscriber, userId);
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
}
