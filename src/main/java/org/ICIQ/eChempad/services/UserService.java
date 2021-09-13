package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.models.User;
import org.ICIQ.eChempad.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll()
    {
        return this.userRepository.findAll();
    }
}
