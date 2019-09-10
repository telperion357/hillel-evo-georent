package com.georent.service;

import com.georent.domain.GeoRentUser;
import com.georent.message.Message;
import com.georent.repository.GeoRentUserRepository;
import com.georent.domain.GeoRentUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GeoRentUserDetailsService implements UserDetailsService {

    private final transient GeoRentUserRepository userRepository;

    @Autowired
    public GeoRentUserDetailsService(final GeoRentUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) {
        GeoRentUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_EMAIL.getDescription() + email));
        return GeoRentUserDetails.create(user);
    }

    public UserDetails loadUserById(final Long userId) {
        GeoRentUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(Message.INVALID_GET_USER_ID.getDescription() + userId));
        return GeoRentUserDetails.create(user);
    }
}
