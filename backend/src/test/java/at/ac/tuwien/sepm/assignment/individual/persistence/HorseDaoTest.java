package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseDaoTest {

  @Autowired
  HorseDao horseDao;

  @Test
  public void getAllReturnsAllStoredHorses() {
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(1); // TODO adapt to exact number of elements in test data later
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName)
        .contains(tuple(-1L, "Wendy"));
  }

  @Test
  public void createNewHorseAddsDataToTheDBwithoutParents() {
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
  public void createNewHorseAddsDataToTheDBwithParents() {
    HorseCreateDto toCreate = new HorseCreateDto("Steve", "a crazy Horse",
        LocalDate.now(), Sex.MALE, null, -2L, -1L);
    horseDao.create(toCreate);
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(1);
    assertThat(horses)
        .extracting(Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex)
        .contains(tuple("Steve", "a crazy Horse", LocalDate.now(), Sex.MALE));
  }
}
