package com.georent;


import com.georent.domain.GeoRentUser;
import com.georent.domain.UserRole;
import com.georent.repository.GeoRentUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
public class GeoRentStarter {

    private final GeoRentUserRepository userRepository;

    @Autowired
    public GeoRentStarter(GeoRentUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(GeoRentStarter.class, args);
    }

    @Bean
    CommandLineRunner runner() {

        return args -> {

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            GeoRentUser user = new GeoRentUser();
            user.setFirstName("ПетручіоqqZ");
            user.setLastName("їєіыюфввrr");
            user.setEmail("email@email.com");
            user.setPhoneNumber("380951111111");
            user.setPassword(encoder.encode("MyPassw0rd"));
            user.setRole(UserRole.ADMIN);

            if (userRepository.existsByEmail(user.getEmail())) {
                return;
            }

            GeoRentUser saved = userRepository.save(user);
        };
    }

}
