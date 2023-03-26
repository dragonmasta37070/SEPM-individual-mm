package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final OwnerService ownerService;

  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator validator, OwnerService ownerService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.ownerService = ownerService;
  }

  @Override
  public Stream<HorseListDto> allHorses() {
    LOG.trace("allHorses()");
    var horses = dao.getAll();
    return getHorseListDtoStream(horses);
  }

  @Override
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.trace("searchHorses({})", searchParameters);
    var horses = dao.searchHorses(searchParameters);
    return getHorseListDtoStream(horses);
  }

  @Override
  public HorseTreeDto getTree(Long id, Long generations) throws NotFoundException {
    LOG.trace("searchHorses({},{})", id, generations);
    validator.validateForTree(id, generations);

    var tree = dao.getTreeAsList(id, generations);
    return mapper.convertListToTree(tree, tree.get(0));
  }

  private Stream<HorseListDto> getHorseListDtoStream(List<Horse> horses) {
    var ownerIds = horses.stream()
        .map(Horse::getOwnerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, ownerMap));
  }

  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);

    Horse father = horse.father() == null ? null : dao.getById(horse.father().id());
    Horse mother = horse.mother() == null ? null : dao.getById(horse.mother().id());

    validator.validateForUpdate(horse);
    var updatedHorse = dao.update(horse);
    return mapper.entityToDetailDto(
        updatedHorse,
        ownerMapForSingleId(updatedHorse.getOwnerId()),
        father,
        mother
    );
  }

  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);

    Horse father = horse.getFatherId() == null ? null : dao.getById(horse.getFatherId());
    Horse mother = horse.getMotherId() == null ? null : dao.getById(horse.getMotherId());

    return mapper.entityToDetailDto(
        horse,
        ownerMapForSingleId(horse.getOwnerId()),
        father,
        mother);
  }

  @Override
  public HorseDetailDto create(HorseCreateDto horseToCreate) throws ValidationException, NotFoundException {
    LOG.trace("create{()}", horseToCreate);

    Horse father = horseToCreate.father() == null ? null : dao.getById(horseToCreate.father().id());
    Horse mother = horseToCreate.mother() == null ? null : dao.getById(horseToCreate.mother().id());

    HorseListDto fatherListDto = father == null ? null : mapper.entityToListDto(father, ownerMapForSingleId(father.getOwnerId()));
    HorseListDto motherListDto = mother == null ? null : mapper.entityToListDto(mother, ownerMapForSingleId(mother.getOwnerId()));

    validator.validateNewHorse(horseToCreate, fatherListDto, motherListDto);
    Horse horse = dao.create(horseToCreate);

    return mapper.entityToDetailDto(
        horse,
        ownerMapForSingleId(horse.getOwnerId()),
        father,
        mother);
  }

  @Override
  public void delete(Long id) throws NotFoundException {
    LOG.trace("delete{()}", id);

    dao.delete(id);
  }

  private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) {
    try {
      return ownerId == null
          ? null
          : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
    } catch (NotFoundException e) {
      throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId));
    }
  }

}
