package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseServiceTest {

  @Autowired
  HorseService horseService;

  @Test
  public void getAllReturnsAllStoredHorses() {
    List<HorseListDto> horses = horseService.allHorses()
        .toList();
    assertThat(horses.size()).isGreaterThanOrEqualTo(1); // TODO adapt to exact number of elements in test data later
    assertThat(horses)
        .map(HorseListDto::id, HorseListDto::sex)
        .contains(tuple(-1L, Sex.FEMALE));
  }

  @Test
  public void create_whenGivenValidData_shouldReturnCreatedHorse() throws ValidationException, NotFoundException {
    HorseCreateDto validHorseData = new HorseCreateDto("Steve", "a crazy Horse",
        LocalDate.now(), Sex.MALE, null, null, null);
    HorseDetailDto created = horseService.create(validHorseData);
    assertNotNull(created);
    assertAll(
        () -> assertNotNull(created.id()),
        () -> assertEquals(created.name(), validHorseData.name()),
        () -> assertEquals(created.description(), validHorseData.description()),
        () -> assertEquals(created.dateOfBirth(), validHorseData.dateOfBirth()),
        () -> assertEquals(created.sex(), validHorseData.sex()),
        () -> assertEquals(created.ownerId(), validHorseData.ownerId()),
        () -> assertEquals(created.father(), null),
        () -> assertEquals(created.mother(), null)
    );
  }

  @Test
  public void create_whenGivenInvalidData_shouldThrow() {
    HorseCreateDto invalidHorseData = new HorseCreateDto("Invalidia", "has a birthday prediction",
        LocalDate.now().plusYears(1), Sex.FEMALE, null, null, null);
    assertThrows(ValidationException.class,
        () -> horseService.create(invalidHorseData));
  }

  @Test
  public void create_whenGivenInvalidParent_shouldThrow() {
    HorseCreateDto invalidHorseData = new HorseCreateDto("Invalidia", "has a birthday prediction",
        LocalDate.now(), Sex.FEMALE, null, 187L, null);
    assertThrows(NotFoundException.class,
        () -> horseService.create(invalidHorseData));
  }
}
