package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
class OwnerServiceTest {

  @Autowired
  OwnerService ownerService;

  @Test
  void getOwnerByExistingId() throws NotFoundException {
    OwnerDto owner = ownerService.getById(-1L);
    assertThat(owner)
        .extracting(OwnerDto::firstName, OwnerDto::lastName, OwnerDto::email)
        .contains("Eragon", "Schattentoeter", "erscha@gmail.com");
  }

  @Test
  void searchOwnerForNameAndGetTheSearchedOne() {
    Stream<OwnerDto> owner = ownerService.search(new OwnerSearchDto("Eragon", 1));
    assertThat(owner.toList())
        .extracting(OwnerDto::firstName, OwnerDto::lastName, OwnerDto::email)
        .contains(tuple("Eragon", "Schattentoeter", "erscha@gmail.com"));
  }

  @Test
  @DirtiesContext
  void createOwnerWithValidData() throws ValidationException, NotFoundException {
    OwnerDto owner = ownerService.create(new OwnerCreateDto("VorName", "NachName", "krasse@mail.com"));
    assertThat(owner)
        .extracting(OwnerDto::firstName, OwnerDto::lastName, OwnerDto::email)
        .contains("VorName", "NachName", "krasse@mail.com");
    OwnerDto dbOwner = ownerService.getById(owner.id());
    assertThat(dbOwner)
        .extracting(OwnerDto::firstName, OwnerDto::lastName, OwnerDto::email)
        .contains("VorName", "NachName", "krasse@mail.com");
  }


  @Test
  @DirtiesContext
  void createOwnerWithDuplicateMailThrowsError() {
    assertThrows(ValidationException.class,
        () -> ownerService.create(new OwnerCreateDto("VorName", "NachName", "krasse@mail.com")));
  }
}