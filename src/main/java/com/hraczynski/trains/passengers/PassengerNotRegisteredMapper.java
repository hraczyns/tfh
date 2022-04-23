package com.hraczynski.trains.passengers;

import com.hraczynski.trains.payment.Discount;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PassengerNotRegisteredMapper {
    private static final int SIZE_MAKING_DISCOUNT_EXIST = 4;

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
                    passengerAsString += "<>";
                    passengerAsString += getDiscount(passengerNotRegistered.getDiscountCode());
                    return passengerAsString;
                })
                .collect(Collectors.joining(","));
    }

    private String getDiscount(String discountCode) {
        Discount discount = Discount.findByCode(discountCode);
        if (discount != null) {
            return discount.name().toLowerCase();
        }
        return "";
    }

    private void verify(PassengerNotRegistered passengerNotRegistered) {
        if (passengerNotRegistered == null || passengerNotRegistered.getName() == null || passengerNotRegistered.getSurname() == null || passengerNotRegistered.getEmail() == null) {
            throw new IllegalStateException("Non registered passengers should have filled name and surname fields!");
        }
    }

    public List<PassengerNotRegistered> deserialize(String passengersNotRegistered) {
        if(passengersNotRegistered == null){
            return Collections.emptyList();
        }
        String[] passengers = passengersNotRegistered.split(",");
        List<PassengerNotRegistered> passengerNotRegistered = new ArrayList<>();
        Arrays.stream(passengers)
                .map(p -> p.split("<>"))
                .forEach(passArray -> {
                    PassengerNotRegistered pass = new PassengerNotRegistered();
                    pass.setName(passArray[0]);
                    pass.setSurname(passArray[1]);
                    pass.setEmail(passArray[2]);

                    if (discountExist(passArray)) {
                        String discount = passArray[3];
                        pass.setDiscountCode(Discount.valueOf(discount.toUpperCase()).getCode());
                    }

                    passengerNotRegistered.add(pass);
                });
        return passengerNotRegistered;

    }

    private boolean discountExist(String[] passArray) {
        return passArray.length == SIZE_MAKING_DISCOUNT_EXIST && !passArray[3].isEmpty();
    }
}
