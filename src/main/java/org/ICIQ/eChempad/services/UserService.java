package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.repositories.UserRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepositoryInterface userRepositoryInterface;

    public long count()
    {
        return this.userRepositoryInterface.count();
    }
}
