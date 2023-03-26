package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = ?"
      + "  , description = ?"
      + "  , date_of_birth = ?"
      + "  , sex = ?"
      + "  , owner_id = ?"
      + "  , father_id = ?"
      + "  , mother_id = ?"
      + " WHERE id = ?";

  private static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME
      + " (name, description, date_of_birth, sex, owner_id, father_id, mother_id)"
      + " VALUES (?, ?, ?, ?, ?, ?, ?)";

  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
  private final JdbcTemplate jdbcTemplate;

  public HorseJdbcDao(
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Horse> getAll() {
    LOG.trace("getAll()");
    return jdbcTemplate.query(SQL_SELECT_ALL, this::mapRow);
  }

  @Override
  public List<Horse> searchHorses(HorseSearchDto filter) {
    LOG.trace("search()");
    String query = "SELECT * FROM " + TABLE_NAME + " WHERE 1=1";

    if (filter.name() != null) {
      query += " AND LOWER(name) like LOWER('%" + filter.name() + "%')";
    }
    if (filter.description() != null) {
      query += " AND LOWER(description) like LOWER('%" + filter.description() + "%')";
    }
    if (filter.sex() != null) {
      query += " AND sex = '" + filter.sex() + "'";
    }
    if (filter.bornBefore() != null) {
      query += " AND date_of_birth <= '" + filter.bornBefore() + "'";
    }
    if (filter.ownerName() != null) {
      query += " AND owner_id IN (SELECT * FROM owners WHERE first_name LIKE LOWER('%" + filter.ownerName()
          + "%') OR last_name LIKE LOWER('%" + filter.ownerName() + "%')";
    }
    if (filter.limit() != null) {
      query += " LIMIT " + filter.limit();
    }

    return jdbcTemplate.query(query, this::mapRow);
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.get(0);
  }

  @Override
  public Horse create(HorseCreateDto horse) {
    LOG.trace("create({})", horse);

    KeyHolder keyHolder = new GeneratedKeyHolder();
    Long owenerId = horse.owner() == null ? null : horse.owner().id();
    Long fatherId = horse.father() == null ? null : horse.father().id();
    Long motherId = horse.mother() == null ? null : horse.mother().id();

    int updated = jdbcTemplate.update(
        connection -> {
          PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
          statement.setString(1, horse.name());
          statement.setString(2, horse.description());
          statement.setDate(3, Date.valueOf(horse.dateOfBirth()));
          statement.setString(4, horse.sex().toString());
          statement.setObject(5, owenerId);
          statement.setObject(6, fatherId);
          statement.setObject(7, motherId);
          return statement;
        }, keyHolder);

    if (updated == 0) {
      throw new FatalException("no rows affected but should be 1");
    }

    return new Horse()
        .setId((Long) keyHolder.getKey())
        .setName(horse.name())
        .setDescription(horse.description())
        .setDateOfBirth(horse.dateOfBirth())
        .setSex(horse.sex())
        .setOwnerId(owenerId)
        .setFatherId(fatherId)
        .setMotherId(motherId);
  }

  //TODO: manage gender change of parent
  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);
    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex().toString(),
        horse.ownerId(),
        horse.father() == null ? null : horse.fatherId(),
        horse.mother() == null ? null : horse.motherId(),
        horse.id());
    if (updated == 0) {
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }

    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setDescription(horse.description())
        .setDateOfBirth(horse.dateOfBirth())
        .setSex(horse.sex())
        .setOwnerId(horse.ownerId())
        .setFatherId(horse.fatherId())
        .setMotherId(horse.motherId())
        ;
  }

  @Override
  public void delete(long id) throws NotFoundException, FatalException {
    LOG.trace("delete ({})");
    int updated = jdbcTemplate.update(SQL_DELETE, id);

    if (updated == 0) {
      throw new NotFoundException("Could not update delete with ID " + id + ", because it does not exist");
    } else if (updated > 1) {
      throw new FatalException("Deleted more than one row. This should never happen");
    }
  }

  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setDescription(result.getString("description"))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setSex(Sex.valueOf(result.getString("sex")))
        .setOwnerId(result.getObject("owner_id", Long.class))
        .setFatherId(result.getObject("father_id", Long.class))
        .setMotherId(result.getObject("mother_id", Long.class))
        ;
  }
}
