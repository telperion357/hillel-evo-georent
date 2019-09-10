package com.georent.test_utils;

import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.GeoRentUser;
import com.georent.domain.Lot;
import com.georent.domain.RentOrder;
import com.georent.domain.RentOrderStatus;
import com.georent.repository.GeoRentUserRepository;
import com.georent.repository.LotRepository;
import com.georent.repository.RentOrderRepository;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

public class DBPopulationUtils {

    /**
     * Encoded password 'pass5678910'.
     */
    private static final String PASSWORD = "$2a$10$Oa2Wby6Y/FRnf.Y4sdzBne8qoxWjDvyxhYpXnzScWdoKR5p8eOl6.";

    private GeoRentUserRepository userRepository;

    private LotRepository lotRepository;

    private RentOrderRepository orderRepository;

    /**
     * Initializes populator with provided context.
     * Get repositories from context.
     */
    public DBPopulationUtils(ApplicationContext context) {
        userRepository = context.getBean("geoRentUserRepository", GeoRentUserRepository.class);
        lotRepository = context.getBean("lotRepository", LotRepository.class);
        orderRepository = context.getBean("rentOrderRepository", RentOrderRepository.class);
    }

    /**
     * Creates and saves to database n typical users with email template userN@gmail.com,
     * where N is from 1 to n inclusively.
     * The password is a constant encoded from pass5678910
     * @param n - Number of users to produce and save to the database.
     */
    public void createUsers(int n) {
        for (int i = 1; i <= n; i++) {

            GeoRentUser user = new GeoRentUser();
            user.setEmail(String.format("user%d@gmail.com", i));
            user.setPassword(PASSWORD);
            user.setFirstName(String.format("Ivan%d", i));
            user.setLastName(String.format("Petrov%d", i));
            user.setPhoneNumber("0353572233");

            userRepository.save(user);
        }
    }

    /**
     * Creates and saves to the database n typical lots for each user.
     * @param n - the number of lots to create.
     */
    public void createLots(int n) {

        List<GeoRentUser> users = userRepository.findAll();

        for (GeoRentUser user : users) {
            for (int i = 0; i < n; i++) {
                Lot lot = new Lot();

                Coordinates coordKyiv = new Coordinates();
                coordKyiv.setLot(lot);
                coordKyiv.setLatitude(50.45466f);
                coordKyiv.setLongitude(30.5238f);

                Description description = new Description();
                description.setLot(lot);
                description.setLotName(String.format(
                        "User %s lot number %d", user.getEmail(), i));
                description.setLotDescription(String.format("User %s %s %s lot number %d",
                                user.getFirstName(), user.getLastName(), user.getEmail(), i));

                lot.setGeoRentUser(user);
                lot.setCoordinates(coordKyiv);
                lot.setDescription(description);
                lot.setPrice(i*100L);

                lotRepository.save(lot);
            }
        }
    }

    /**
     * Creates and saves to database n orders from each user to each lot,
     * except for lots of this user.
     * @param n - number of orders to generate from each user to each lot.
     */
    public void createRentOrders(int n) {

        userRepository.findAll().stream().forEach( geoRentUser -> {
            lotRepository.findAll().stream()
                    .filter(lot -> lot.getGeoRentUser().getId() != geoRentUser.getId())
                    .forEach(lot -> {
                IntStream.range(0, n).forEach( i -> {

                    RentOrder order = new RentOrder();
                    order.setRentee(geoRentUser);
                    order.setLot(lot);

                    order.setStatus(RentOrderStatus.PENDING);
                    order.setStartTime(LocalDateTime.now());
                    order.setEndTime(order.getStartTime().plusDays(1));

                    orderRepository.save(order);
                });
            });
        });
    }
}
