package com.sample.hotel.app;

import com.sample.hotel.entity.Booking;
import com.sample.hotel.entity.Room;
import com.sample.hotel.entity.RoomReservation;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlans;
import io.jmix.data.PersistenceHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class BookingService {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private FetchPlans fetchPlans;

//    public BookingService(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }


    /**
     * Check if given room is suitable for the booking.
     * 1) Check that sleeping places is enough to fit numberOfGuests.
     * 2) Check that there are no reservations for this room at the same range of dates.
     * Use javax.persistence.EntityManager and JPQL query for querying database.
     *
     * @param booking booking
     * @param room    room
     * @return true if checks are passed successfully
     */

    @Transactional
    public boolean isSuitable(Booking booking, Room room) {
        //todo implement me!

        if (room.getSleepingPlaces() >= booking.getNumberOfGuests()) {
            try {
                RoomReservation roomReservation = entityManager.createQuery("select r from RoomReservation r WHERE r.room = ?1", RoomReservation.class)
                        .setParameter(1, room)
                        .getSingleResult();

                LocalDate roomReservationArrivalDate = roomReservation.getBooking().getArrivalDate();
                LocalDate roomReservationDepartureDate = roomReservation.getBooking().getDepartureDate();

                if (!((roomReservationArrivalDate.isAfter(booking.getArrivalDate()) && roomReservationArrivalDate.isBefore(booking.getDepartureDate())) ||
                        (roomReservationDepartureDate.isBefore(booking.getDepartureDate()) && roomReservationDepartureDate.isAfter(booking.getArrivalDate())))) {
                    return true;
                }

            } catch (NoResultException e) {
                return true;
            }
        }
        return false;
    }


    /**
     * Check that room is suitable for the booking, and create a reservation for this room.
     *
     * @param room    room to reserve
     * @param booking hotel booking
     *                Wrap operation into a transaction (declarative or manual).
     * @return created reservation object, or null if room is not suitable
     */
    @Transactional
    public RoomReservation reserveRoom(Booking booking, Room room) {
        //todo implement me!
        if (isSuitable(booking, room)) {

            RoomReservation roomReservation = dataManager.create(RoomReservation.class);
            roomReservation.setRoom(room);
            roomReservation.setBooking(booking);

            dataManager.save(roomReservation);

            return roomReservation;
        }
        return null;
    }
}