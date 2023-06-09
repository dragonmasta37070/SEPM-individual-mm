package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;

import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {
  /**
   * Get all horses stored in the persistent data store.
   *
   * @return a list of all stored horses
   */
  List<Horse> getAll();

  /**
   * get a list of horses, which meet specific parameters
   *
   * @param filter patterns by which are filtered
   * @return a list of horses, which meet the parameters specified in the searchPatterns
   */
  List<Horse> searchHorses(HorseSearchDto filter);

  /**
   * Update the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse update(HorseDetailDto horse) throws NotFoundException;

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Create a new horse
   *
   * @param horse the horse to create
   * @return a copy of the created horse
   */
  Horse create(HorseCreateDto horse);

  /**
   * get a list the of Horses which reference the horse with the given id
   *
   * @param id id of the parent of wich the children are retrieved
   * @return a list of children of the Horse
   */
  List<Horse> getChildren(long id);

  /**
   * Deletes a horse determent by its ID
   *
   * @param id id of horse to delete
   * @throws NotFoundException if the given id doeas not match an entry in the DB
   * @throws FatalException    if more than one wntry is found. (should never happen)
   */
  void delete(long id) throws NotFoundException, FatalException;

  /**
   * get tree of ancestors as lost with a limit of generations
   *
   * @param id          id of root node
   * @param generations number od generations to be fetched
   * @return the tree as a list
   * @throws NotFoundException if the given id doeas not match an entry in the DB
   */
  List<Horse> getTreeAsList(long id, long generations) throws NotFoundException;
}
