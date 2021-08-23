//package pl.hub.flights;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
//import org.junit.jupiter.api.Test;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import pl.hub.flights.passengers.PassengerController;
//import pl.hub.flights.passengers.Passenger;
//import pl.hub.flights.passengers.PassengerDTO;
//import pl.hub.flights.passengers.PassengerService;
//import pl.hub.flights.passengers.PassengerRepresentationModelAssembler;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.Arrays;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(PassengerController.class)
//public class PassengerControllerTest {
//
//    private static final String API = "http://localhost:8083/api/tourists";
//    private static final String GET_ALL = "/all";
//
//    private final PassengerRepresentationModelAssembler assembler = new PassengerRepresentationModelAssembler(new ModelMapper());
//    private final ObjectMapper mapper = new ObjectMapper()
//            .registerModule(new ParameterNamesModule())
//            .registerModule(new Jdk8Module())
//            .registerModule(new JavaTimeModule());
//
//    @Autowired
//    private MockMvc mvc;
//
//    @MockBean
//    private PassengerService passengerService;
//
//
//    @Test
//    public void getTouristsTest200() throws Exception {
//
//        Passenger passenger = prepareTourist(5L);
//        Passenger passenger2 = prepareTourist(6L);
//
//        PassengerDTO passengerDTO = prepareTouristDTO(5L);
//        PassengerDTO passengerDTO2 = prepareTouristDTO(6L);
//
//        given(passengerService.getAll()).willReturn(assembler.toCollectionModel(Arrays.asList(passenger, passenger2)));
//
//        mvc.perform(MockMvcRequestBuilders.get(API + GET_ALL)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("*.touristDTOList[0].id").value(passengerDTO.getId().intValue()))
//                .andExpect(jsonPath("*.touristDTOList[0].name").value(passengerDTO.getName()))
//                .andExpect(jsonPath("*.touristDTOList[0].surname").value(passengerDTO.getSurname()))
//                .andExpect(jsonPath("*.touristDTOList[0].gender").value(passengerDTO.getGender()))
//                .andExpect(jsonPath("*.touristDTOList[0].country").value(passengerDTO.getCountry()))
//                .andExpect(jsonPath("*.touristDTOList[0].notes").value(passengerDTO.getNotes()))
//                .andExpect(jsonPath("*.touristDTOList[0].bornDate").value(formatDate(passengerDTO.getBornDate())))
//                .andExpect(jsonPath("*.touristDTOList[0]._links.self.href").value("/api/tourists/" + passengerDTO.getId().intValue()))
//                //
//                .andExpect(jsonPath("*.touristDTOList[1].id").value(passengerDTO2.getId().intValue()))
//                .andExpect(jsonPath("*.touristDTOList[1].name").value(passengerDTO2.getName()))
//                .andExpect(jsonPath("*.touristDTOList[1].surname").value(passengerDTO2.getSurname()))
//                .andExpect(jsonPath("*.touristDTOList[1].gender").value(passengerDTO2.getGender()))
//                .andExpect(jsonPath("*.touristDTOList[1].country").value(passengerDTO2.getCountry()))
//                .andExpect(jsonPath("*.touristDTOList[1].notes").value(passengerDTO2.getNotes()))
//                .andExpect(jsonPath("*.touristDTOList[1].bornDate").value(formatDate(passengerDTO2.getBornDate())))
//                .andExpect(jsonPath("*.touristDTOList[1]._links.self.href").value("/api/tourists/" + passengerDTO2.getId().intValue()))
//                .andExpect(jsonPath("_links.self.href").value("/api/tourists/all"));
//    }
//
//    @Test
//    public void getTouristsTest404() throws Exception {
//        given(passengerService.getAll()).willReturn(null);
//
//        mvc.perform(MockMvcRequestBuilders.get(API + GET_ALL)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void notSupportedContentType415() throws Exception {
//        given(passengerService.getById(anyLong())).willReturn(assembler.toModel(prepareTourist(1L)));
//
//        mvc.perform(MockMvcRequestBuilders.get(API + "/1")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .accept(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().is(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()));
//
//    }
//
//    @Test
//    public void getTouristByIdTest200Good() throws Exception {
//        Passenger passenger = prepareTourist(15L);
//        PassengerDTO passengerDTO = prepareTouristDTO(15L);
//
//        given(passengerService.getById(anyLong())).willReturn(assembler.toModel(passenger));
//
//        mvc.perform(MockMvcRequestBuilders.get(API + "/15")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("id").value(passengerDTO.getId().intValue()))
//                .andExpect(jsonPath("name").value(passengerDTO.getName()))
//                .andExpect(jsonPath("surname").value(passengerDTO.getSurname()))
//                .andExpect(jsonPath("gender").value(passengerDTO.getGender()))
//                .andExpect(jsonPath("country").value(passengerDTO.getCountry()))
//                .andExpect(jsonPath("notes").value(passengerDTO.getNotes()))
//                .andExpect(jsonPath("bornDate").value(formatDate(passengerDTO.getBornDate())))
//                .andExpect(jsonPath("_links.self.href").value("/api/tourists/" + passengerDTO.getId().intValue()));
//    }
//
//    @Test
//    public void getTourisByIdTest404() throws Exception {
//        given(passengerService.getById(anyLong())).willReturn(null);
//
//        mvc.perform(MockMvcRequestBuilders.get(API + "/" + 15L)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void addTourist201Good() throws Exception {
//        PassengerDTO passengerDTO = prepareTouristDTO(1L);
//        given(passengerService.addPassenger(any(PassengerDTO.class))).willReturn(assembler.toModel(prepareTourist(1L)));
//        mvc.perform(MockMvcRequestBuilders.post(API)
//                .content(mapper.writeValueAsBytes(passengerDTO))
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("id").value(passengerDTO.getId().intValue()))
//                .andExpect(jsonPath("name").value(passengerDTO.getName()))
//                .andExpect(jsonPath("surname").value(passengerDTO.getSurname()))
//                .andExpect(jsonPath("gender").value(passengerDTO.getGender()))
//                .andExpect(jsonPath("country").value(passengerDTO.getCountry()))
//                .andExpect(jsonPath("notes").value(passengerDTO.getNotes()))
//                .andExpect(jsonPath("bornDate").value(formatDate(passengerDTO.getBornDate())))
//                .andExpect(jsonPath("_links.self.href").value("/api/tourists/" + passengerDTO.getId().intValue()));
//    }
//
//    @Test
//    public void addTourist400() throws Exception {
//        given(passengerService.addPassenger(null)).willReturn(null);
//
//        mvc.perform(MockMvcRequestBuilders.post(API)
//                .content("")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void putTourist204Good() throws Exception {
//        PassengerDTO passengerDTO = prepareTouristDTO(1L);
//        given(passengerService.updateById(anyLong(), any(PassengerDTO.class))).willReturn(assembler.toModel(prepareTourist(1L)));
//        mvc.perform(MockMvcRequestBuilders.put(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsBytes(passengerDTO)))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void putTourist404() throws Exception {
//        given(passengerService.updateById(anyLong(), any(PassengerDTO.class))).willReturn(null);
//        mvc.perform(MockMvcRequestBuilders.put(API + "/2")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsBytes(prepareTouristDTO(2L))))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void putTourist400() throws Exception {
//        given(passengerService.updateById(anyLong(), any(PassengerDTO.class))).willReturn(null);
//        mvc.perform(MockMvcRequestBuilders.put(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(""))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void patchTourist204Good() throws Exception {
//        Passenger passenger = prepareTourist(1L);
//        PassengerDTO passengerDTO = prepareTouristDTO(1L);
//        given(passengerService.patchById(anyLong(), any(PassengerDTO.class))).willReturn(assembler.toModel(passenger));
//        mvc.perform(MockMvcRequestBuilders.patch(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsBytes(passengerDTO)))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void patchTourist404() throws Exception {
//        PassengerDTO passengerDTO = prepareTouristDTO(1L);
//        Passenger passenger = prepareTourist(2L);
//        given(passengerService.patchById(anyLong(), any(PassengerDTO.class))).willReturn(assembler.toModel(passenger));
//        mvc.perform(MockMvcRequestBuilders.patch(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsBytes(passengerDTO)))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void patchTourist400() throws Exception {
//        given(passengerService.patchById(anyLong(), any(PassengerDTO.class))).willReturn(null);
//        mvc.perform(MockMvcRequestBuilders.patch(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(""))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void deleteTourist200Good() throws Exception {
//        Passenger passenger = prepareTourist(1L);
//        PassengerDTO passengerDTO = prepareTouristDTO(1L);
//        given(passengerService.deleteById(anyLong())).willReturn(assembler.toModel(passenger));
//        mvc.perform(MockMvcRequestBuilders.delete(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("id").value(passengerDTO.getId().intValue()))
//                .andExpect(jsonPath("name").value(passengerDTO.getName()))
//                .andExpect(jsonPath("surname").value(passengerDTO.getSurname()))
//                .andExpect(jsonPath("gender").value(passengerDTO.getGender()))
//                .andExpect(jsonPath("country").value(passengerDTO.getCountry()))
//                .andExpect(jsonPath("notes").value(passengerDTO.getNotes()))
//                .andExpect(jsonPath("bornDate").value(formatDate(passengerDTO.getBornDate())))
//                .andExpect(jsonPath("_links.self.href").value("/api/tourists/" + passengerDTO.getId().intValue()));
//
//    }
//
//    @Test
//    public void deleteTourist404() throws Exception {
//        given(passengerService.deleteById(anyLong())).willReturn(null);
//        mvc.perform(MockMvcRequestBuilders.delete(API + "/2")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    private String formatDate(LocalDate date) {
//        return date.format(DateTimeFormatter.ISO_DATE);
//    }
//
//    private Passenger prepareTourist(Long id) {
//        Passenger passenger = new Passenger();
//        passenger.setId(id);
//        passenger.setCountry("Poland");
//        passenger.setBornDate(LocalDate.of(2000, 10, 10));
//        passenger.setGender("male");
//        passenger.setName("Jan");
//        passenger.setSurname("Kowalski");
//        passenger.setReservations(null);
//        passenger.setNotes("Some notes");
//        return passenger;
//    }
//
//    private PassengerDTO prepareTouristDTO(Long id) {
//        PassengerDTO passengerDTO = new PassengerDTO();
//        passengerDTO.setId(id);
//        passengerDTO.setCountry("Poland");
//        passengerDTO.setBornDate(LocalDate.of(2000, 10, 10));
//        passengerDTO.setGender("male");
//        passengerDTO.setName("Jan");
//        passengerDTO.setSurname("Kowalski");
//        passengerDTO.setNotes("Some notes");
//        return passengerDTO;
//    }
//
//
//}