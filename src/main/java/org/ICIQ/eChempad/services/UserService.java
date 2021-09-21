package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public long count()
    {
        return this.userRepository.count();
    }
}
