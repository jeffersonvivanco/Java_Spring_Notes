package app.services;

import app.model.ErisUser;
import app.repositories.ErisUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ErisUserService {

    private final ErisUserRepository erisUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ErisUserService(ErisUserRepository erisUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.erisUserRepository = erisUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /*
    Sign up
     */
    public void signUp(ErisUser erisUser){
        erisUser.setPassword(bCryptPasswordEncoder.encode(erisUser.getPassword()));
        erisUserRepository.save(erisUser);
    }
}
