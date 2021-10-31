package com.hraczynski.trains;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
import org.springframework.data.repository.CrudRepository;

//@Service
@Slf4j
public abstract class AbstractService<T, S extends CrudRepository<T, Long>> {

    private final Class<T> type;
    private final Class<S> repoType;
    private S repo;

//    @Autowired
    @SuppressWarnings("unchecked")
    public AbstractService() {
        ResolvableType resolvableType = ResolvableType.forClass(getClass()).as(AbstractService.class);

        this.type = (Class<T>) resolvableType.getGeneric(0).resolve();
        this.repoType = (Class<S>) resolvableType.getGeneric(1).resolve();
    }

    public void checkInput(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Wrong JSON!");
        }
    }

    public T getEntityById(Long id) {
        insIfNull();
        return repo.findById(id).orElseThrow(() -> {
            log.error("Cannot find {} with id = {}", type.getSimpleName(), id);
            return new EntityNotFoundException(type, "id = " + id);
        });
    }

    private void insIfNull() {
        if (repo == null) {
            repo = BeanUtil.getBean(repoType);
        }
    }

}
