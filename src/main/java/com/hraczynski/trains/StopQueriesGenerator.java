package com.hraczynski.trains;


import com.hraczynski.trains.city.City;
import com.hraczynski.trains.train.TrainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import com.hraczynski.trains.city.CityRepository;
import com.hraczynski.trains.utils.DistanceCalculator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Component
public class StopQueriesGenerator {

    private final CityRepository cityRepository;
    private final DistanceCalculator distanceCalculator;

    @Autowired
    public StopQueriesGenerator(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
        this.distanceCalculator = new DistanceCalculator();
    }

    public void generate() throws IOException {
        List<StopToDb> queriesStopsTemp = new ArrayList<>();
        List<String> queriesStops = new ArrayList<>();
        List<String> queriesTrip = new ArrayList<>();
        List<String> queriesTripStops = new ArrayList<>();
        List<String> trainQueries = new ArrayList<>();
        List<String> fileContent = Files.readAllLines(Path.of("przejazdy.txt"));
        int id = 1;
        int tripId = 1;
        int perDay = 4;
        int days = 25;
        int trainIndex = 0;
        int nextId = 0;
        for (int i1 = 0, fileContentSize = fileContent.size(); i1 < fileContentSize; i1++) {
            String line = fileContent.get(i1);
            List<TrainToDb> trainTempList = new ArrayList<>();
            queriesStopsTemp.clear();
            queriesStopsTemp = parse(line);

            int idTemp = queriesStopsTemp.stream()
                    .map(StopToDb::id)
                    .max(Comparator.naturalOrder())
                    .orElse(-1);
            List<StopToDb> copy = new ArrayList<>(queriesStopsTemp);
            List<StopToDb> copy2 = new ArrayList<>();
            for (int i = 0; i < copy.size(); i++) {
                StopToDb stopToDb = queriesStopsTemp.get(i);
                StopToDb stopNew = new StopToDb(idTemp++, queriesStopsTemp.get(queriesStopsTemp.size() - i - 1).idCity(), stopToDb.dep, stopToDb.arr);
                copy2.add(stopNew);
            }
            queriesStopsTemp.addAll(copy2);

            for (int k = 0; k < days; k++) {
                for (int i = 0; i < perDay; i++) {
                    int tripIdBinding = nextId + k * perDay + i + 1 + i1 * days * perDay;
                    int tripIdBindingMinus1 = tripIdBinding;
                    tripIdBinding++;

                    for (int j = 0, queriesStopsTempSize = queriesStopsTemp.size(); j < queriesStopsTempSize; j++) {
                        StopToDb stopToDb = queriesStopsTemp.get(j);
                        LocalDateTime dateTimeDep = stopToDb.dep();
                        LocalDateTime dateTimeArr = stopToDb.arr();
                        int plusHours = new Random().nextInt(6) + 1;
                        if (!dateTimeDep.equals(LocalDateTime.MIN)) {
//                            dateTimeDep = dateTimeDep.plusHours(2L * i).plusDays(k);
                            dateTimeDep = dateTimeDep.plusHours(plusHours * i).plusDays(k);
                        }
                        if (!dateTimeArr.equals(LocalDateTime.MIN)) {
//                            dateTimeArr = dateTimeArr.plusHours(2L * i).plusDays(k);
                            dateTimeArr = dateTimeArr.plusHours(plusHours * i).plusDays(k);
                        }

                        if (j == queriesStopsTempSize / 2) {
                            nextId++;
                        }

                        int chosenId;
                        if (j < queriesStopsTempSize / 2) {
                            chosenId = tripIdBindingMinus1;
                        } else {
                            chosenId = tripIdBinding;
                        }

                        String departure = dateTimeDep.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                        String arrival = dateTimeArr.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                        String stopQ = "insert into stops (id,arrival_time,departure_time,id_city,trip_id) values " +
                                "(" + (id++) + ",'" + departure + "','" + arrival + "'," + stopToDb.idCity() + "," + chosenId + ");";
                        queriesStops.add(stopQ);
                    }
                }
            }

            id -= perDay * queriesStopsTemp.size() * days;

            String[] trainNames = {"Hubert", "Mexico", "Generator", "Enter", "Parse", "Song", "Guitar"};
            String randomName = trainNames[trainIndex];
            int randomNumber = -1;
            for (int i = 1; i <= 2 * perDay; i++) {
                randomNumber++;
                if (randomNumber == TrainType.values().length) {
                    randomNumber = 0;
                }

                int finalRandomNumber = randomNumber; // yeah, ugly
                TrainType type = Stream.of(TrainType.values())
                        .filter(s -> s.ordinal() == finalRandomNumber)
                        .findFirst()
                        .orElse(TrainType.NORMAL);
                String name = randomName + "-" + "I".repeat(Math.max(0, i));
                trainQueries.add("insert into trains (id,number_of_seats,model,name,representation_unique) values (" + (i + (trainIndex * 2 * perDay)) + ",'" + type.getNumberOfSeats() + "','" + type.name() + "','" + name
                        + "','" + (type.name().substring(0, 1) + (i + (trainIndex * 2 * perDay))) + "'); ");
                TrainToDb train = new TrainToDb((i + (trainIndex * 2 * perDay)),
                        type.getNumberOfSeats(),
                        type,
                        name,
                        ((type.name()).substring(0, 1) + (i + (trainIndex * 2 * perDay))));
                trainTempList.add(train);
            }
            int currentIndex = 0;
            for (int i = 0; i < perDay * days; i++) {
                if (i != 0 && i % perDay == 0) {
                    currentIndex = 0;
                }
                TrainToDb current = trainTempList.get(currentIndex);
                TrainToDb currentReturn = trainTempList.get(trainTempList.size() / 2 + currentIndex);
                currentIndex++;

                String queryTrip = "insert into trips (id,price,train_id,distance) values (" + tripId + "," + estimatePriceAndDistance(line).getSecond() + "," + current.id() + "," + estimatePriceAndDistance(line).getFirst() +
                        ");";
                String queryTrip2 = "insert into trips (id,price,train_id,distance) values (" + (tripId + 1) + "," + estimatePriceAndDistance(line).getSecond() + "," + currentReturn.id() + "," + estimatePriceAndDistance(line).getFirst() +
                        ");";
                queriesTrip.add(queryTrip);
                queriesTrip.add(queryTrip2);
                for (int j = 0; j < queriesStopsTemp.size(); j++) {
                    if (j == queriesStopsTemp.size() / 2) {
                        tripId++;
                    }
                    id++;
                }
                tripId++;
            }
            trainIndex++;

        }
        trainQueries.forEach(System.out::println);
        queriesTrip.forEach(System.out::println);
        queriesStops.forEach(System.out::println);
    }

    private Pair<String, BigDecimal> estimatePriceAndDistance(String line) {
        String[] route = findRoute(line);
        BigDecimal price = new BigDecimal("0");
        double distance = 0;
        for (int i = 1; i < route.length; i++) {
            Optional<City> byId = cityRepository.findById(Long.parseLong(route[i]));
            Optional<City> byId2 = cityRepository.findById(Long.parseLong(route[i - 1]));
            if (byId.isPresent() && byId2.isPresent()) {
                City city = byId.get();
                City city1 = byId2.get();
                double dist = distanceCalculator.calculate(city, city1);
                distance += dist;
                int pr = ((int) dist) / 10;
                price = price.add(new BigDecimal(String.valueOf(pr))); //todo enhance

            } else {
                throw new IllegalStateException("No city found: id = " + route[i] + ", id = " + route[i - 1]);
            }
        }
        return Pair.of(String.format("%.2f", distance).replace(",", "."), price.setScale(2, RoundingMode.HALF_UP));
    }

    private String[] findRoute(String line) {
        String[] splitted = line.split("->");
        String route = splitted[0];
        return route.split("-");
    }

    private List<StopToDb> parse(String line) {
        int id = 1;
        String[] splitted = line.split("->");
        String[] routeArr = findRoute(line);
        String times = splitted[1];
        String[] timeSplitted = times.split("\\|");

        List<StopToDb> stopToDbList = new ArrayList<>();
        for (int i = 0; i < routeArr.length; i++) {
            String dep = timeSplitted[i].split(",")[0];
            String arr = timeSplitted[i].split(",")[1];
            LocalDateTime departureTime;
            LocalDateTime arrivalTime;
            if ("null".equals(dep)) {
                departureTime = LocalDateTime.MIN;
            } else {
                departureTime = LocalDateTime.parse("2021-03-15T" + dep, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            }

            if ("null".equals(arr)) {
                arrivalTime = LocalDateTime.MIN;
            } else {
                arrivalTime = LocalDateTime.parse("2021-03-15T" + arr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            }
            StopToDb stop = new StopToDb(id++, Integer.parseInt(routeArr[i]), departureTime, arrivalTime);
            stopToDbList.add(stop);
        }
        return stopToDbList;
    }

    record StopToDb(int id, int idCity, LocalDateTime dep, LocalDateTime arr) {
    }

    record TrainToDb(int id, int numberOfSeats, TrainType type, String name, String representationNameUnique) {
    }

}
