package com.hraczynski.trains.services;

import com.hraczynski.trains.city.*;
import com.hraczynski.trains.country.Country;
import com.hraczynski.trains.country.CountryRepository;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.utils.BeanUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.hraczynski.trains.builders.CityTestBuilder.*;
import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("City service tests")
public class CityServiceTest {

    @Mock
    private CityRepository cityRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private ApplicationContext applicationContext;
    @Captor
    private ArgumentCaptor<City> argumentCaptor;
    private CityService service;
    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    public void setup() {
        service = new CityServiceImpl(cityRepository, modelMapper, countryRepository);
    }

    @Nested
    @DisplayName("Save tests")
    class SaveTests {

        @Test
        @DisplayName("Save city")
        void save() {
            //given
            Country country = givenCountry();
            CityRequest cityRequest = givenCityRequest();
            City city = givenCity();
            when(countryRepository.findCountryByName(cityRequest.getCountry())).thenReturn(Optional.of(country));
            when(cityRepository.save(city)).thenReturn(city);

            //when
            City result = service.save(cityRequest);

            //then
            assertThat(city).isEqualTo(result);
            verify(countryRepository).findCountryByName(cityRequest.getCountry());
            verify(cityRepository).save(argumentCaptor.capture());
            City value = argumentCaptor.getValue();
            assertThat(city).isEqualTo(value);
            assertThat(country).isEqualTo(value.getCountry());
        }

        @Test
        @DisplayName("Doesn't save city when request is null")
        void notSaveWhenRequestNull() {
            //when
            //then
            assertThatCode(() -> service.save(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Doesn't save city when country in request is null")
        @SuppressWarnings(value = "all")
        void notSaveWhenCountryNull() {
            //given
            String country = null;
            CityRequest cityRequest = make(a(BasicCityRequest)
                    .but(with(countryRequest, country))); // null cannot be explicit placed here because two methods would match that invocation
            //when
            //then
            assertThatCode(() -> service.save(cityRequest))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Update tests")
    class UpdateTests {
        @Test
        @DisplayName("Update city by city request")
        void update() {
            //given
            Country country = givenCountry();
            CityRequest cityRequest = givenCityRequest();
            City city = givenCity();
            when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));
            when(countryRepository.findCountryByName(cityRequest.getCountry())).thenReturn(Optional.of(country));
            whenBeanUtil();

            //when
            service.update(cityRequest);

            //then
            verify(countryRepository).findCountryByName(cityRequest.getCountry());
            verify(cityRepository).save(argumentCaptor.capture());
            City value = argumentCaptor.getValue();
            assertThat(city).isEqualTo(value);
            assertThat(country).isEqualTo(value.getCountry());
        }

        @Test
        @DisplayName("Doesn't update city when city request is null")
        @SuppressWarnings("all")
        void notUpdateWhenRequestIsNull() {
            //given
            CityRequest cityRequest = null;

            //when
            //then
            assertThatCode(() -> service.update(cityRequest))
                    .isInstanceOf(IllegalArgumentException.class);
            verifyNoInteractions(cityRepository);
        }

        @Test
        @DisplayName("Doesn't update city when id of city request isn't specified")
        void notUpdateWhenRequestIdIsNotSpecified() {
            whenBeanUtil();
            //given
            CityRequest cityRequest = make(a(BasicCityRequest)
                    .but(with(idRequest, -1L)));

            //when
            //then
            assertThatCode(() -> service.update(cityRequest))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(cityRepository).findById(-1L);
        }
    }

    @Nested
    @DisplayName("Update tests")
    class PatchTests {
        @Test
        @DisplayName("Patch city by city request")
        void patch() {
            //given
            Country country = givenCountry();
            CityRequest cityRequest = givenCityRequest();
            City returned = givenCity();
            City city = givenCityForPatchPurpose();
            when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));
            when(countryRepository.findCountryByName(cityRequest.getCountry())).thenReturn(Optional.of(country));
            whenBeanUtil();

            //when
            service.patch(cityRequest);

            //then
            verify(countryRepository).findCountryByName(cityRequest.getCountry());
            verify(cityRepository).save(argumentCaptor.capture());
            City value = argumentCaptor.getValue();
            assertThat(returned)
                    .usingRecursiveComparison()
                    .isEqualTo(value);
            assertThat(country)
                    .usingRecursiveComparison()
                    .isEqualTo(value.getCountry());
        }

        @Test
        @DisplayName("Doesn't patch city when city request is null")
        @SuppressWarnings("all")
        void notPatchWhenRequestIsNull() {
            //given
            CityRequest cityRequest = null;

            //when
            //then
            assertThatCode(() -> service.patch(cityRequest))
                    .isInstanceOf(IllegalArgumentException.class);
            verifyNoInteractions(cityRepository);
        }

        @Test
        @DisplayName("Doesn't patch city when id of city request isn't specified")
        void notPatchWhenRequestIdIsNotSpecified() {
            whenBeanUtil();
            //given
            CityRequest cityRequest = make(a(BasicCityRequest)
                    .but(with(idRequest, -1L)));

            //when
            //then
            assertThatCode(() -> service.patch(cityRequest))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(cityRepository).findById(-1L);
        }
    }

    @Test
    @DisplayName("Delete city by id")
    void deleteById() {
        //given
        City city = givenCity();
        when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));
        whenBeanUtil();

        //when
        City result = service.deleteById(city.getId());

        //then
        assertThat(result).isEqualTo(city);
        verify(cityRepository).findById(city.getId());
        verify(cityRepository).deleteById(city.getId());
    }

    @Test
    @DisplayName("Find by id")
    void findById() {
        //given
        City city = givenCity();
        when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));
        whenBeanUtil();

        //when
        City result = service.findById(city.getId());

        //then
        assertThat(result).isEqualTo(city);
        verify(cityRepository).findById(city.getId());
    }

    @Test
    @DisplayName("Find all")
    void findAll() {
        //given
        Set<City> citySet = givenCitySet();
        when(cityRepository.findAll()).thenReturn(citySet);

        //when
        Set<City> result = service.findAll();

        //then
        assertThat(result).isEqualTo(citySet);
        verify(cityRepository).findAll();
    }

    private City givenCity() {
        return make(a(BasicCityEntity));
    }

    private CityRequest givenCityRequest() {
        return make(a(BasicCityRequest));
    }

    private City givenCityForPatchPurpose() {
        return make(PatchCityEntityMaker);
    }

    private Country givenCountry() {
        return new Country(1L, "Poland");
    }

    private Set<City> givenCitySet() {
        return new HashSet<>(List.of(givenCity()));
    }

    @SuppressWarnings("all")
    private void whenBeanUtil() {
        BeanUtil beanUtil = new BeanUtil();
        beanUtil.setApplicationContext(applicationContext);
        when(BeanUtil.getBean(CityRepository.class)).thenReturn(cityRepository);
    }
}
