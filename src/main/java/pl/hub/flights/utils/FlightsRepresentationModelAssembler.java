package pl.hub.flights.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import pl.hub.flights.entities.Flight;
import pl.hub.flights.hateoasmodels.FlightModel;

public class FlightsRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Flight, FlightModel> {

    public FlightsRepresentationModelAssembler(Class<?> controllerClass, Class<FlightModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    public FlightModel toModel(Flight entity) {
        FlightModel model = instantiateModel(entity);
        mapper.create
        return null;
    }
}
