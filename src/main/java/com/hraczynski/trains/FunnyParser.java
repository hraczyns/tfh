package com.hraczynski.trains;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hraczynski.trains.city.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.hraczynski.trains.city.CityTemp;
import com.hraczynski.trains.country.Country;
import com.hraczynski.trains.city.CityRepository;
import com.hraczynski.trains.country.CountryRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FunnyParser {

    @Autowired
    CityRepository repository;
    @Autowired
    CountryRepository repository1;

//    @EventListener(ApplicationReadyEvent.class)
    public void parse() throws IOException {

        RestTemplate template = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        template.setMessageConverters(messageConverters);
        ObjectMapper mapper = new ObjectMapper();
        CountryTemp[] countryTemp = mapper.readValue(new File("contries.txt"), CountryTemp[].class);
        List<CountryTemp> collect = Stream.of(countryTemp).collect(Collectors.toList());
        CityTemp[] exchange = template.getForObject("https://raw.githubusercontent.com/lutangar/cities.json/master/cities.json", CityTemp[].class);
        List<CityTemp> collect1 = Stream.of(Objects.requireNonNull(exchange))
                .collect(Collectors.toList());

        Map<String, String> map = new HashMap<>();
        map.put("Berlin", "DE");
        map.put("Warsaw", "PL");
        map.put("Paris", "FR");
        map.put("Madrid", "ES");
        map.put("London", "GB");
        map.put("Radom", "PL");
        map.put("Moscow", "RU");
        map.put("Brighton", "GB");
        map.put("Lisbon", "PT");
        map.put("Stockholm", "SE");
        map.put("Athens", "GR");
        map.put("Lyon", "FR");
        map.put("Vienna", "AT");
        map.put("Amsterdam", "NL");
        map.put("Hamburg", "DE");


        List<CityTemp> collect2 = collect1.stream()
                .filter(s -> map.containsKey(s.getName()) && map.get(s.getName()).equals(s.getCountry()))
                .map(s -> {
                    CityTemp cityTemp = new CityTemp(s);
                    cityTemp.setCountry(collect.stream().filter(k -> s.getCountry().equals(k.getCode())).map(CountryTemp::getName).findFirst().get());
                    return cityTemp;
                })
                .collect(Collectors.toList());

        collect2.forEach(System.out::println);
        Map<String, List<CityTemp>> collect3 = collect2.stream()
                .collect(Collectors.groupingBy(CityTemp::getCountry));
        System.out.println(collect3);
        List<Country> countries = mapCountriesTempToCountries(collect2);
        List<City> cities = mapCitiesTempToCities(collect2, countries);
        for (City city : cities) {
            System.out.println(city.getId());
            System.out.println(city.getLat());
            System.out.println(city.getLon());
            System.out.println(city.getCountry());
            System.out.println(city.getName());
            System.out.println();
        }

        createQueriey(countries, cities);
    }

    private void createQueriey(List<Country> countries, List<City> cities) {
        countries.forEach(s -> {
            System.out.println("insert into countries (id,name) values (" + s.getId() + ",'" + s.getName() + "');");
        });
        cities.forEach(s -> {
            System.out.println("insert into cities (id,name,lon,lat,country_id) values (" + s.getId() + ",'" + s.getName() + "'," + s.getLon() + "," + s.getLat() + "," + s.getCountry().getId() + ");");
        });

    }

    private List<Country> mapCountriesTempToCountries(List<CityTemp> collect2) {
        List<Country> countries = new ArrayList<>();
        AtomicReference<Long> index1 = new AtomicReference<>(1L);
        collect2.forEach(s -> {
            if (countries.stream().noneMatch(k -> k.getName().equals(s.getCountry()))) {
                Country country = new Country();
                country.setName(s.getCountry());
                country.setId(index1.getAndSet(index1.get() + 1));
                countries.add(country);
            }
        });
        return countries;
    }

    private List<City> mapCitiesTempToCities(List<CityTemp> collect2, List<Country> countries) {
        List<City> cities = new ArrayList<>();
        AtomicReference<Long> index = new AtomicReference<>(1L);


        collect2.forEach(s -> {
            City city = new City();
            city.setId(index.getAndSet(index.get() + 1));
            city.setCountry(countries.stream()
                    .filter(k -> k.getName().equals(s.getCountry()))
                    .findFirst()
                    .get());
            city.setName(s.getName());
            city.setLat(Double.parseDouble(s.getLat()));
            city.setLon(Double.parseDouble(s.getLng()));
            cities.add(city);
        });

        return cities;
    }
}
