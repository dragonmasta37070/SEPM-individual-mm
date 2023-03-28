package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class HorseEndpointTest {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  @Test
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<HorseListDto> horseResult = objectMapper.readerFor(HorseListDto.class).<HorseListDto>readValues(body).readAll();

    assertThat(horseResult).isNotNull();
    assertThat(horseResult.size()).isGreaterThanOrEqualTo(26);
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name)
        .contains(tuple(-1L, "Wendy"));
  }

  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }

  @Test
  public void searchNameReturnsCorrectEntries() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses?name=Tobisch")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<HorseDetailDto> horses = objectMapper.readerFor(HorseDetailDto.class).<HorseDetailDto>readValues(body).readAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(1);
    assertThat(horses)
        .extracting(HorseDetailDto::id)
        .contains(-2L);
  }

  @Test
  @DirtiesContext
  public void createNewHorseWithValidValues() throws Exception {
    var newValidHorse = new HorseCreateDto("Simba", null, LocalDate.now(), Sex.MALE,
        null, null, null);
    mockMvc
        .perform(MockMvcRequestBuilders
            .post("/horses")
            .content(objectMapper.writeValueAsString(newValidHorse))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
  }

  @Test
  @DirtiesContext
  public void createNewHorseWithToYoungFatherThrowsError() throws Exception {
    var newValidHorse = new HorseCreateDto("Simba", null,
        LocalDate.of(1869, 1, 1), Sex.MALE,
        null, new HorseListDto(-2L, "Tobisch", null,
        LocalDate.of(2012, 12, 12), Sex.MALE, null), null);
    mockMvc
        .perform(MockMvcRequestBuilders
            .post("/horses")
            .content(objectMapper.writeValueAsString(newValidHorse))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
  }

  @Test
  @DirtiesContext
  public void successfullyUpdateBirthday() throws Exception {
    byte[] oldBody = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses/-4")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();
    HorseDetailDto trick = objectMapper.readValue(oldBody, HorseDetailDto.class);

    HorseDetailDto newTrick = new HorseDetailDto(trick.id(), trick.name(), trick.description(),
        LocalDate.of(2023, 1, 1), trick.sex(), trick.owner(), trick.father(),
        trick.mother());

    byte[] updatedBody = mockMvc
        .perform(MockMvcRequestBuilders
            .put("/horses/-4")
            .content(objectMapper.writeValueAsString(newTrick))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();
    HorseDetailDto updatedTrick = objectMapper.readValue(updatedBody, HorseDetailDto.class);
    assertThat(updatedTrick.dateOfBirth()).isEqualTo(LocalDate.of(2023, 1, 1));
  }
}
