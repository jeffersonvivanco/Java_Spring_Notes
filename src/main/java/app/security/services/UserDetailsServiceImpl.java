package app.security.services;

import app.models.entities.ErisUserEntity;
import app.repositories.ErisUserRepository;
import app.security.ErisUserAuth;
import app.security.models.Privilege;
import app.security.models.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
Spring Security doesn't come with a UserDetailsService that we could use with our in-memory db.
Therefore we extend it and tune it for our needs.
 */
@Service("UserDetailsServiceImpl")
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ErisUserRepository erisUserRepository;

    public UserDetailsServiceImpl(ErisUserRepository erisUserRepository) {
        this.erisUserRepository = erisUserRepository;
    }

    /*
    When a user tries to authenticate, this method receives the username, searches the db for a record containing it,
    and if found returns an instance of User. The properties of this instance (username and password) are then checked
    against the credentials passed by the user in the login request. This last process is executed outside this class
    by the spring security framework.
     */
    @Override
    public ErisUserAuth loadUserByUsername(String username){
        ErisUserEntity erisUser = erisUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        // Privileges and Roles are mapped to GrantedAuthority entities
        return new ErisUserAuth(erisUser.getUsername(), erisUser.getPassword(),
                getAuthorities(erisUser.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles){
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(Collection<Role> roles){
        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();

        for (Role role : roles){
            collection.addAll(role.getPrivileges());
        }

        for (Privilege privilege : collection){
            privileges.add(privilege.getName());
        }

        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges){
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges){
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }
}
