package com.hraczynski.trains.passengers;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PassengerNotRegisteredMapper {
    public String serialize(List<PassengerNotRegistered> passengerNotRegisteredSet) {
        if (passengerNotRegisteredSet == null || passengerNotRegisteredSet.isEmpty()) {
            return null;
        }
        return passengerNotRegisteredSet
                .stream()
                .map(passengerNotRegistered -> {
                    verify(passengerNotRegistered);
                    String passengerAsString = "";
                    passengerAsString += passengerNotRegistered.getName();
                    passengerAsString += "<>";
                    passengerAsString += passengerNotRegistered.getSurname();
                    passengerAsString += "<>";
                    passengerAsString += passengerNotRegistered.getEmail();
                    return passengerAsString;
                })
                .collect(Collectors.joining(","));
    }

    private void verify(PassengerNotRegistered passengerNotRegistered) {
        if (passengerNotRegistered == null || passengerNotRegistered.getName() == null || passengerNotRegistered.getSurname() == null || passengerNotRegistered.getEmail() == null) {
            throw new IllegalStateException("Non registered passengers should have filled name and surname fields!");
        }
    }
}
