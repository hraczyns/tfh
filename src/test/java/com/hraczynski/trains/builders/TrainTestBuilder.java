package com.hraczynski.trains.builders;

import com.hraczynski.trains.reservations.Reservation;
import com.hraczynski.trains.train.Train;
import com.hraczynski.trains.train.TrainDTO;
import com.hraczynski.trains.train.TrainRequest;
import com.hraczynski.trains.train.TrainType;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;

import java.util.HashSet;
import java.util.Set;

public class TrainTestBuilder {

    private static final Long ID = 21L;
    private static final int NUMBER_OF_SEATS = 200;
    private static final Set<Reservation> RESERVATIONS = new HashSet<>();
    private static final TrainType TRAIN_TYPE = TrainType.NORMAL;
    private static final String NAME = "RANDOM";
    private static final String REPRESENTATION_UNIQUE = "ABCDEFGH";
    private static final boolean USED = true;

    public static final Property<Train, Long> idEntity = Property.newProperty();
    public static final Property<Train, Integer> numOfSeatsEntity = Property.newProperty();
    public static final Property<Train, Set<Reservation>> setReservationsEntity = Property.newProperty();
    public static final Property<Train, TrainType> trainTypeEntity = Property.newProperty();
    public static final Property<Train, String> nameEntity = Property.newProperty();
    public static final Property<Train, String> repUniqueEntity = Property.newProperty();

    public static final Property<TrainRequest, Long> idRequest = Property.newProperty();
    public static final Property<TrainRequest, Integer> numOfSeatsReqest = Property.newProperty();
    public static final Property<TrainRequest, TrainType> trainTypeRequest = Property.newProperty();
    public static final Property<TrainRequest, String> nameRequest = Property.newProperty();
    public static final Property<TrainRequest, String> repUniqueRequest = Property.newProperty();

    public static final Property<TrainDTO, Long> idDto = Property.newProperty();
    public static final Property<TrainDTO, Integer> numOfSeatsDto = Property.newProperty();
    public static final Property<TrainDTO, TrainType> trainTypeDto = Property.newProperty();
    public static final Property<TrainDTO, String> nameDto = Property.newProperty();
    public static final Property<TrainDTO, String> repUniqueDto = Property.newProperty();
    public static final Property<TrainDTO, Boolean> usedDto = Property.newProperty();

    public static final Instantiator<TrainDTO> BasicTrainDto = propertyLookUp -> {
        TrainDTO trainDTO = new TrainDTO();
        trainDTO.setId(propertyLookUp.valueOf(idDto, ID));
        trainDTO.setUsed(propertyLookUp.valueOf(usedDto, USED));
        trainDTO.setName(propertyLookUp.valueOf(nameDto, NAME));
        trainDTO.setModel(propertyLookUp.valueOf(trainTypeDto, TRAIN_TYPE));
        trainDTO.setNumberOfSeats(propertyLookUp.valueOf(numOfSeatsDto, NUMBER_OF_SEATS));
        trainDTO.setRepresentationUnique(propertyLookUp.valueOf(repUniqueDto, REPRESENTATION_UNIQUE));
        return trainDTO;
    };

    public static final Instantiator<Train> BasicTrainEntity = propertyLookup -> {
        Train train = new Train();
        train.setId(propertyLookup.valueOf(idEntity, ID));
        train.setModel(propertyLookup.valueOf(trainTypeEntity, TRAIN_TYPE));
        train.setName(propertyLookup.valueOf(nameEntity, NAME));
        train.setRepresentationUnique(propertyLookup.valueOf(repUniqueEntity, REPRESENTATION_UNIQUE));
        train.setNumberOfSeats(propertyLookup.valueOf(numOfSeatsEntity, NUMBER_OF_SEATS));
        train.setReservations(propertyLookup.valueOf(setReservationsEntity, RESERVATIONS));
        return train;
    };

    public static final Instantiator<TrainRequest> BasicTrainRequest = propertyLookup -> {
        TrainRequest trainRequest = new TrainRequest();
        trainRequest.setId(propertyLookup.valueOf(idRequest, ID));
        trainRequest.setModel(propertyLookup.valueOf(trainTypeRequest, TRAIN_TYPE));
        trainRequest.setName(propertyLookup.valueOf(nameRequest, NAME));
        trainRequest.setRepresentationUnique(propertyLookup.valueOf(repUniqueRequest, REPRESENTATION_UNIQUE));
        trainRequest.setNumberOfSeats(propertyLookup.valueOf(numOfSeatsReqest, NUMBER_OF_SEATS));
        return trainRequest;
    };


}
