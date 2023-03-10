package com.sample.hotel.listener;

import com.sample.hotel.entity.Booking;
import com.sample.hotel.entity.BookingStatus;
import io.jmix.core.DataManager;
import io.jmix.core.event.EntityChangedEvent;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

@Component
public class BookingEventListener {
    @Autowired
    private DataManager dataManager;

    @EventListener
    public void onBookingSaving(EntitySavingEvent<Booking> event) {
        LocalDate arrivalDate = event.getEntity().getArrivalDate();
        Integer nightsOfStay = event.getEntity().getNightsOfStay();

        event.getEntity().setDepartureDate(calcDepartureDate(arrivalDate, nightsOfStay));
    }

//    @TransactionalEventListener
//    public void onBookingChangedAfterCommit(EntityChangedEvent<Booking> event) {
//        LocalDate arrivalDate = event.getChanges().getArrivalDate();
//        Integer nightsOfStay = event.getEntity().getNightsOfStay();
//
//        event.getEntity().setDepartureDate(calcDepartureDate(arrivalDate, nightsOfStay));
//    }

    @Transactional
    @EventListener
    public void onBookingChangedBeforeCommit(EntityChangedEvent<Booking> event) {
        Booking booking = dataManager.load(event.getEntityId()).one();

        if (event.getType() == EntityChangedEvent.Type.UPDATED &&
                (event.getChanges().isChanged("arrivalDate") ||
                        event.getChanges().isChanged("nightsOfStay"))) {
            booking.setDepartureDate(calcDepartureDate(booking.getArrivalDate(), booking.getNightsOfStay()));
        }

        if (event.getType() == EntityChangedEvent.Type.UPDATED && event.getChanges().isChanged("status") &&
                booking.getRoomReservation() != null) {
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                dataManager.remove(booking.getRoomReservation());
            }
        }
    }

    private LocalDate calcDepartureDate(LocalDate arrivalDate, Integer nightsOfStay) {
        return arrivalDate.plusDays(nightsOfStay);
    }
}