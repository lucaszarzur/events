package br.com.lucaszarzur.events.repository;

import br.com.lucaszarzur.events.model.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    public User findByEmail(String email);
}
