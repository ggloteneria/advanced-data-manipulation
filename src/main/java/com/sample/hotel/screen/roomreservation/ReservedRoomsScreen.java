package com.sample.hotel.screen.roomreservation;

import com.sample.hotel.entity.Client;
import com.sample.hotel.entity.RoomReservation;
import io.jmix.ui.Dialogs;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("ReservedRooms")
@UiDescriptor("reserved-rooms.xml")
@LookupComponent("roomReservationsTable")
public class ReservedRoomsScreen extends StandardLookup<RoomReservation> {
    @Autowired
    private GroupTable<RoomReservation> roomReservationsTable;
    @Autowired
    private Dialogs dialogs;

    @Subscribe("roomReservationsTable.viewClientEmail")
    public void onRoomReservationsTableViewClientEmail(Action.ActionPerformedEvent event) {
        RoomReservation reservation = roomReservationsTable.getSingleSelected();
        if (reservation == null) {
            return;
        }
        Client client = reservation.getBooking().getClient();

        dialogs.createMessageDialog()
                .withCaption("Client email")
                .withMessage(client.getEmail())
                .show();
    }
}