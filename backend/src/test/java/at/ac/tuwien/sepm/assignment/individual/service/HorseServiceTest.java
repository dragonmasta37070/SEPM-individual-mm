package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    assertThat(horses.size()).isEqualTo(27);
    assertThat(horses)
        .map(HorseListDto::id, HorseListDto::sex)
        .contains(tuple(-1L, Sex.FEMALE));
  }

  @Test
  public void getHorseById() throws NotFoundException {
    HorseDetailDto horse = horseService.getById(-2L);
    assertThat(horse)
        .extracting(HorseDetailDto::id, HorseDetailDto::name, HorseDetailDto::description,
            HorseDetailDto::dateOfBirth, HorseDetailDto::sex, HorseDetailDto::ownerId,
            HorseDetailDto::fatherId, HorseDetailDto::motherId)
        .contains(-2L, "Tobisch", "The famous one!", LocalDate.of(2012, 12, 12),
            Sex.MALE, null, null, null);
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
        () -> assertNull(created.ownerId()),
        () -> assertNull(created.father()),
        () -> assertNull(created.mother())
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
    HorseListDto notExistingHorse = new HorseListDto(187L, "Max", "", LocalDate.now(), Sex.MALE, null);
    HorseCreateDto invalidHorseData = new HorseCreateDto("Invalidia", "has a birthday prediction",
        LocalDate.now(), Sex.FEMALE, null, notExistingHorse, null);
    assertThrows(NotFoundException.class,
        () -> horseService.create(invalidHorseData));
  }

  @Test
  public void findExistingHorseByName() {
    List<HorseListDto> foundHorses = horseService.searchHorses(new HorseSearchDto("Anakin", null,
        null, null, null, null)).toList();
    assertThat(foundHorses.size()).isGreaterThanOrEqualTo(1);
    assertThat(foundHorses)
        .extracting(HorseListDto::name)
        .contains("Anakin Skywalker");
  }

  @Test
  public void getFamilieTreeWithCorrectSize() throws NotFoundException, ValidationException {
    HorseTreeDto secondGen = horseService.getTree(-7L, 2L);
    HorseTreeDto thirdGen = horseService.getTree(-7L, 3L);

    assertAll(
        () -> assertEquals(secondGen.mother().id(), -3L),
        () -> assertNull(secondGen.mother().mother()),
        () -> assertEquals(thirdGen.mother().id(), -3L),
        () -> assertEquals(thirdGen.mother().mother().id(), -1L),
        () -> assertNull(thirdGen.mother().mother().mother()));
  }

  @Test
  @DirtiesContext
  public void deleteExistigHorse() throws NotFoundException {
    int oldRowCount = horseService.allHorses().toList().size();
    horseService.delete(-25L);
    assertThat(horseService.allHorses()).size().isEqualTo(oldRowCount - 1);
  }

  @Test
  @DirtiesContext
  public void updateExistingHorse() throws ValidationException, ConflictException, NotFoundException {
    HorseDetailDto updateValues = new HorseDetailDto(-26L, "schmutz", null, LocalDate.now(), Sex.FEMALE,
        null, null, null);
    HorseDetailDto updated = horseService.update(updateValues);
    assertAll(
        () -> updated.name().equals("schmutz"),
        () -> horseService.getById(-26L).name().equals("schmutz"));
  }

  @Test
  @DirtiesContext
  public void updateSexOfHorseWithChildrenThrowsError() throws ValidationException, ConflictException, NotFoundException {
    HorseDetailDto updateValues = new HorseDetailDto(-2L, "Tobisch", null,
        LocalDate.of(2012, 12, 12), Sex.MALE, null, null, null);
    HorseDetailDto updated = horseService.update(updateValues);
    assertAll(
        () -> updated.name().equals("schmutz"),
        () -> horseService.getById(-26L).name().equals("schmutz"));
  }
}
