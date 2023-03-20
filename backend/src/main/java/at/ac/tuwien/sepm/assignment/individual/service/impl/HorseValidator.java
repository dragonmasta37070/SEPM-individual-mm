package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


  public void validateForUpdate(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }
    validationErrors.addAll(validateString(horse.name(), 255, "Horse name", false));
    validationErrors.addAll(validateString(horse.description(), 4095, "Horse description", true));
    validateDateOfBirth(horse.dateOfBirth());
    if (horse.sex() == null) {
      validationErrors.add("Sex of new horse missing");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  public void validateNewHorse(HorseCreateDto horse) throws ValidationException {
    LOG.trace("validateNewHorse({})", horse);
    List<String> validationErrors = new ArrayList<>();

    validationErrors.addAll(validateString(horse.name(), 255, "New horse name", false));
    validationErrors.addAll(validateString(horse.description(), 4095, "New horse description", true));
    validationErrors.addAll(validateDateOfBirth(horse.dateOfBirth()));

    if (horse.sex() == null) {
      validationErrors.add("Sex of new horse missing");
    }

    if (horse.ownerId() != null) {
      //TODO check if owner is present
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }

  private List<String> validateString(String toVal, int maxLen, String stringName, Boolean nullValid) {
    List<String> validationErrors = new ArrayList<>();

    if (toVal == null) {
      if (!nullValid) {
        validationErrors.add(stringName + " is missing");
      }
    } else {
      if (toVal.isBlank()) {
        validationErrors.add(stringName + " is given but blank");
      }
      if (toVal.length() > maxLen) {
        validationErrors.add(stringName + " too long: longer than 4095 characters");
      }
    }
    return validationErrors;
  }

  private List<String> validateDateOfBirth(LocalDate dateOfBirth) {
    List<String> validationErrors = new ArrayList<>();

    if (dateOfBirth == null) {
      validationErrors.add("Date of birth of horse missing");
    } else if (dateOfBirth.isAfter(LocalDate.now())) {
      validationErrors.add("Date of birth of horse is after the current date");
    }
    return validationErrors;
  }

}
