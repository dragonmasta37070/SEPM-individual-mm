package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;


@Component
public class OwnerValidator extends BaseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public void validateNewOwner(OwnerCreateDto owner, Boolean alreadyExists) throws ValidationException {
    LOG.trace("validateNewHorse({})", owner);
    List<String> validationErrors = new ArrayList<>();

    validateString(validationErrors, owner.firstName(), 255, "First name od owner", false);
    validateString(validationErrors, owner.lastName(), 255, "Last name od owner", false);
    vaildateEmail(validationErrors, owner.email());

    if (alreadyExists) {
      validationErrors.add("email already in assigned");
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }
}
