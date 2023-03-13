package com.sample.hotel.screen.roomreservation;

import com.sample.hotel.entity.Client;
import com.sample.hotel.entity.RoomReservation;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlans;
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
    @Autowired
    private FetchPlans fetchPlans;
    @Autowired
    private DataManager dataManager;

    @Subscribe("roomReservationsTable.viewClientEmail")
    public void onRoomReservationsTableViewClientEmail(Action.ActionPerformedEvent event) {
        RoomReservation reservation = roomReservationsTable.getSingleSelected();
        if (reservation == null) {
            return;
        }

        FetchPlan fetchPlan = fetchPlans.builder(RoomReservation.class)
                .addFetchPlan(FetchPlan.INSTANCE_NAME)
                .add("booking", fetchPlanBuilder -> fetchPlanBuilder
                        .add("client", fetchPlanBuilder1 -> fetchPlanBuilder1.add("email")))
                .build();


        RoomReservation one = dataManager.load(RoomReservation.class)
                .id(reservation.getId())
                .fetchPlan(fetchPlan)
                .one();

//        Client client = reservation.getBooking().getClient();

        dialogs.createMessageDialog()
                .withCaption("Client email")
                .withMessage(one.getBooking().getClient().getEmail())
                .show();
    }
}