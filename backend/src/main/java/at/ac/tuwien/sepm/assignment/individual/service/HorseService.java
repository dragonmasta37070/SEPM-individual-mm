package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {
  /**
   * Lists all horses stored in the system.
   *
   * @return list of all stored horses
   */
  Stream<HorseListDto> allHorses();


  /**
   * Updates the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return he updated horse
   * @throws NotFoundException   if the horse with given ID does not exist in the persistent data store
   * @throws ValidationException if the update data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException   if the update data given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException;

  /**
   * get a list the of Horses which reference the horse with the given id
   *
   * @param id id of the parent of wich the children are retrieved
   * @return a list of children of the Horse
   */
  List<Horse> getChildren(long id);

  /**
   * Get the horse with given ID, with more detail information.
   * This includes the owner of the horse, and its parents.
   * The parents of the parents are not included.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  HorseDetailDto getById(long id) throws NotFoundException;

  /**
   * Create a new horse in the horse table.
   * The attributes name date_of_birth and sex are mandatory.
   *
   * @param horse the horse ho be added in the table
   * @return the added horse
   * @throws ValidationException if the validation failed
   */
  HorseDetailDto create(HorseCreateDto horse) throws ValidationException, NotFoundException;

  /**
   * Deletes a horse by its id
   *
   * @param id The id of the horse to delete
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  void delete(Long id) throws NotFoundException;

  /**
   * get a list of horses, which meet specific parameters
   *
   * @param searchParameters patterns by which are filtered
   * @return a list of horses, which meet the parameters specified in the searchPatterns
   */
  Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters);

  /**
   * get a tree representation of horses and there parents
   *
   * @param id          root horse
   * @param generations number of generations listed
   * @return a tree of horses
   */
  HorseTreeDto getTree(Long id, Long generations) throws NotFoundException, ValidationException;
}
