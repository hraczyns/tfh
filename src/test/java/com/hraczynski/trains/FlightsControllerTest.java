//package pl.hub.flights;
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
//import pl.hub.flights.train.TrainController;
//import pl.hub.flights.train.Train;
//import pl.hub.flights.train.TrainDTO;
//import pl.hub.flights.train.TrainService;
//import pl.hub.flights.train.TrainRepresentationModelAssembler;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Arrays;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@WebMvcTest(TrainController.class)
//public class FlightsControllerTest {
//
//    private static final String API = "http://localhost:8083/api/flights";
//    private static final String GET_ALL = "/all";
//
//    private final TrainRepresentationModelAssembler assembler = new TrainRepresentationModelAssembler(new ModelMapper());
//    private final ObjectMapper mapper = new ObjectMapper()
//            .registerModule(new ParameterNamesModule())
//            .registerModule(new Jdk8Module())
//            .registerModule(new JavaTimeModule());
//
//    @Autowired
//    private MockMvc mvc;
//
//    @MockBean
//    private TrainService trainService;
//
//
//    @Test
//    public void getFlightsTest200Good() throws Exception {
//        Train train = prepareFlight(5L);
//        Train train2 = prepareFlight(6L);
//
//        TrainDTO trainDTO = prepareFlightDTO(5L);
//        TrainDTO trainDTO2 = prepareFlightDTO(6L);
//
//
//        given(trainService.getAll()).willReturn(assembler.toCollectionModel(Arrays.asList(train, train2)));
//
//        mvc.perform(MockMvcRequestBuilders.get(API + GET_ALL)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("*.flightDTOList[0].id").value(trainDTO.getId().intValue()))
//                .andExpect(jsonPath("*.flightDTOList[0].departureTime").value(formatDate(trainDTO.getDepartureTime())))
//                .andExpect(jsonPath("*.flightDTOList[0].arrivalTime").value(formatDate(trainDTO.getArrivalTime())))
//                .andExpect(jsonPath("*.flightDTOList[0].numberOfSeats").value(trainDTO.getNumberOfSeats()))
//                .andExpect(jsonPath("*.flightDTOList[0].price").value(trainDTO.getPrice()))
//                .andExpect(jsonPath("*.flightDTOList[0]._links.self.href").value("/api/flights/" + trainDTO.getId().intValue()))
//                //
//                .andExpect(jsonPath("*.flightDTOList[1].id").value(trainDTO2.getId().intValue()))
//                .andExpect(jsonPath("*.flightDTOList[1].departureTime").value(formatDate(trainDTO2.getDepartureTime())))
//                .andExpect(jsonPath("*.flightDTOList[1].arrivalTime").value(formatDate(trainDTO2.getArrivalTime())))
//                .andExpect(jsonPath("*.flightDTOList[1].numberOfSeats").value(trainDTO2.getNumberOfSeats()))
//                .andExpect(jsonPath("*.flightDTOList[1].price").value(trainDTO2.getPrice()))
//                .andExpect(jsonPath("*.flightDTOList[1]._links.self.href").value("/api/flights/" + trainDTO2.getId().intValue()))
//                .andExpect(jsonPath("_links.self.href").value("/api/flights/all"));
//    }
//
//    @Test
//    public void getFlightsTest404() throws Exception {
//        given(trainService.getAll()).willReturn(null);
//
//        mvc.perform(MockMvcRequestBuilders.get(API + GET_ALL)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void notSupportedContentType415() throws Exception {
//        given(trainService.getById(anyLong())).willReturn(assembler.toModel(prepareFlight(1L)));
//
//        mvc.perform(MockMvcRequestBuilders.get(API + "/1")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .accept(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().is(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()));
//
//    }
//
//    @Test
//    public void getFlightByIdTest200Good() throws Exception {
//        Train train = prepareFlight(15L);
//        TrainDTO trainDTO = prepareFlightDTO(15L);
//
//        given(trainService.getById(anyLong())).willReturn(assembler.toModel(train));
//
//        mvc.perform(MockMvcRequestBuilders.get(API + "/15")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("id").value(trainDTO.getId().intValue()))
//                .andExpect(jsonPath("departureTime").value(formatDate(trainDTO.getDepartureTime())))
//                .andExpect(jsonPath("arrivalTime").value(formatDate(trainDTO.getArrivalTime())))
//                .andExpect(jsonPath("numberOfSeats").value(trainDTO.getNumberOfSeats()))
//                .andExpect(jsonPath("price").value(trainDTO.getPrice()))
//                .andExpect(jsonPath("_links.self.href").value("/api/flights/" + trainDTO.getId().intValue()));
//    }
//
//    @Test
//    public void getFlightByIdTest404() throws Exception {
//        given(trainService.getById(anyLong())).willReturn(null);
//
//        mvc.perform(MockMvcRequestBuilders.get(API + "/" + 15L)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void addFlight201Good() throws Exception {
//        TrainDTO trainDTO = prepareFlightDTO(1L);
//        given(trainService.save(any(TrainDTO.class))).willReturn(assembler.toModel(prepareFlight(1L)));
//        mvc.perform(MockMvcRequestBuilders.post(API)
//                .content(mapper.writeValueAsBytes(trainDTO))
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("id").value(trainDTO.getId().intValue()))
//                .andExpect(jsonPath("departureTime").value(formatDate(trainDTO.getDepartureTime())))
//                .andExpect(jsonPath("arrivalTime").value(formatDate(trainDTO.getArrivalTime())))
//                .andExpect(jsonPath("numberOfSeats").value(trainDTO.getNumberOfSeats()))
//                .andExpect(jsonPath("price").value(trainDTO.getPrice()))
//                .andExpect(jsonPath("_links.self.href").value("/api/flights/" + trainDTO.getId().intValue()));
//    }
//
//    @Test
//    public void addFlight400() throws Exception {
//        given(trainService.save(null)).willReturn(null);
//
//        mvc.perform(MockMvcRequestBuilders.post(API)
//                .content("")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void putFlight204Good() throws Exception {
//        TrainDTO trainDTO = prepareFlightDTO(1L);
//        given(trainService.updateById(anyLong(), any(TrainDTO.class))).willReturn(assembler.toModel(prepareFlight(1L)));
//        mvc.perform(MockMvcRequestBuilders.put(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsBytes(trainDTO)))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void putFlight404() throws Exception {
//        given(trainService.updateById(anyLong(), any(TrainDTO.class))).willReturn(null);
//        mvc.perform(MockMvcRequestBuilders.put(API + "/2")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsBytes(prepareFlightDTO(2L))))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void putFlight400() throws Exception {
//        given(trainService.updateById(anyLong(), any(TrainDTO.class))).willReturn(null);
//        mvc.perform(MockMvcRequestBuilders.put(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(""))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void patchFlight204Good() throws Exception {
//        Train train = prepareFlight(1L);
//        TrainDTO trainDTO = prepareFlightDTO(1L);
//        given(trainService.patchById(anyLong(), any(TrainDTO.class))).willReturn(assembler.toModel(train));
//        mvc.perform(MockMvcRequestBuilders.patch(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsBytes(trainDTO)))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void patchFlight404() throws Exception {
//        TrainDTO trainDTO = prepareFlightDTO(1L);
//        Train train = prepareFlight(2L);
//        given(trainService.patchById(anyLong(), any(TrainDTO.class))).willReturn(assembler.toModel(train));
//        mvc.perform(MockMvcRequestBuilders.patch(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsBytes(trainDTO)))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void patchFlight400() throws Exception {
//        given(trainService.patchById(anyLong(), any(TrainDTO.class))).willReturn(null);
//        mvc.perform(MockMvcRequestBuilders.patch(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(""))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void deleteFlight200Good() throws Exception {
//        Train train = prepareFlight(1L);
//        TrainDTO trainDTO = prepareFlightDTO(1L);
//        given(trainService.deleteById(anyLong())).willReturn(assembler.toModel(train));
//        mvc.perform(MockMvcRequestBuilders.delete(API + "/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("id").value(trainDTO.getId().intValue()))
//                .andExpect(jsonPath("departureTime").value(formatDate(trainDTO.getDepartureTime())))
//                .andExpect(jsonPath("arrivalTime").value(formatDate(trainDTO.getArrivalTime())))
//                .andExpect(jsonPath("price").value(trainDTO.getPrice()))
//                .andExpect(jsonPath("numberOfSeats").value(trainDTO.getNumberOfSeats()))
//                .andExpect(jsonPath("_links.self.href").value("/api/flights/" + trainDTO.getId().intValue()));
//
//    }
//
//    @Test
//    public void deleteFlight404() throws Exception {
//        given(trainService.deleteById(anyLong())).willReturn(null);
//        mvc.perform(MockMvcRequestBuilders.delete(API + "/2")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    private String formatDate(LocalDateTime data) {
//        return data.format(DateTimeFormatter.ISO_DATE_TIME);
//    }
//
//    private Train prepareFlight(Long id) {
//        Train train = new Train();
//        train.setId(id);
////        train.setArrivalTime(LocalDateTime.of(2021, 5, 12, 12, 32));
////        train.setDepartureTime(LocalDateTime.of(2020, 5, 12, 12, 32));
//        train.setNumberOfSeats(30);
//        train.setPrice(100);
//        train.setReservations(null);
//        return train;
//    }
//
//    private TrainDTO prepareFlightDTO(Long id) {
//        TrainDTO trainDTO = new TrainDTO();
//        trainDTO.setId(id);
//        trainDTO.setArrivalTime(LocalDateTime.of(2021, 5, 12, 12, 32));
//        trainDTO.setDepartureTime(LocalDateTime.of(2020, 5, 12, 12, 32));
//        trainDTO.setNumberOfSeats(30);
//        trainDTO.setPrice(100);
//        return trainDTO;
//    }
//
//
//}
//
