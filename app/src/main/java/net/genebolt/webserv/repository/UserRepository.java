package net.genebolt.webserv.repository;

import java.util.List;

import net.genebolt.webserv.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    public List<User> findByFirstName(String firstName);
}