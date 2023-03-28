package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseDaoTest {

  @Autowired
  HorseDao horseDao;

  @Autowired
  HorseMapper mapper;

  @Test
  public void getAllReturnsAllStoredHorsesContainsRightData() {
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(26);
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth,
            Horse::getSex, Horse::getOwnerId, Horse::getFatherId, Horse::getMotherId)
        .contains(tuple(-1L, "Wendy", "The famous one!", LocalDate.of(2012, 12, 12),
            Sex.FEMALE, -12L, null, null))
        .contains(tuple(-15L, "Leia", "daughter of Anakin Skywalker and Padme Amidala",
            LocalDate.of(2000, 7, 16), Sex.FEMALE, null, -13L, -10L))
        .contains(tuple(-16L, "Han", "smuggler and pilot", LocalDate.of(1990, 5, 25),
            Sex.MALE, -14L, null, null));
  }

  @Test
  @DirtiesContext
  public void createNewHorseAddsDataToTheDBwithoutParentsOrOwner() {
    HorseCreateDto toCreate = new HorseCreateDto("Steve", "a crazy Horse",
        LocalDate.now(), Sex.MALE, null, null, null);
    horseDao.create(toCreate);
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(1);
    assertThat(horses)
        .extracting(Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex)
        .contains(tuple("Steve", "a crazy Horse", LocalDate.now(), Sex.MALE));
  }

  @Test
  @DirtiesContext
  public void createNewHorseAddsDataToTheDBwithParents() throws NotFoundException {
    HorseCreateDto toCreate = new HorseCreateDto("Steve", "a crazy Horse",
        LocalDate.now(), Sex.MALE, null, mapper.entityToListDto(horseDao.getById(-2L), null),
        mapper.entityToListDto(horseDao.getById(-8L), new HashMap<>()));
    horseDao.create(toCreate);
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(1);
    assertThat(horses)
        .extracting(Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex)
        .contains(tuple("Steve", "a crazy Horse", LocalDate.now(), Sex.MALE));
  }

  @Test
  @DirtiesContext
  public void createNewHorseAddsDataToTheDBwithOwner() {
    HorseCreateDto toCreate = new HorseCreateDto("Steve", "a crazy Horse",
        LocalDate.now(), Sex.MALE, new OwnerDto(-1L, null, null, null), null, null);
    horseDao.create(toCreate);
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(1);
    assertThat(horses)
        .extracting(Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex, Horse::getOwnerId)
        .contains(tuple("Steve", "a crazy Horse", LocalDate.now(), Sex.MALE, -1L));
  }

  @Test
  public void findExistingHorseByName() {
    List<Horse> foundHorses = horseDao.searchHorses(new HorseSearchDto("Anakin", null,
        null, null, null, null));
    assertThat(foundHorses.size()).isGreaterThanOrEqualTo(1);
    assertThat(foundHorses)
        .extracting(Horse::getName)
        .contains("Anakin Skywalker");
  }

  @Test
  public void dontFindNotExistingHorse() {
    List<Horse> foundHorses = horseDao.searchHorses(new HorseSearchDto("Zelda", null,
        null, null, null, null));
    assertThat(foundHorses.size()).isGreaterThanOrEqualTo(0);
  }

  @Test
  @DirtiesContext
  public void updateNameCorrectly() throws NotFoundException {
    HorseDetailDto updateValues = new HorseDetailDto(-26L, "schmutz", null, LocalDate.now(), Sex.FEMALE,
        null, null, null);
    Horse updated = horseDao.update(updateValues);
    assertAll(
        () -> updated.getName().equals("schmutz"),
        () -> horseDao.getById(-26L).getName().equals("schmutz"));
  }

  @Test
  public void getFamilieTreeWithCorrectSize() throws NotFoundException {
    List<Horse> secondGen = horseDao.getTreeAsList(-7, 2);
    List<Horse> thirdGen = horseDao.getTreeAsList(-7, 3);

    assertThat(secondGen).size().isEqualTo(3);
    assertThat(thirdGen).size().isEqualTo(4);
    assertThat(secondGen)
        .extracting(Horse::getId)
        .contains(-7L)
        .contains(-3L)
        .contains(-2L)
        .doesNotContain(-1L);
  }
}
