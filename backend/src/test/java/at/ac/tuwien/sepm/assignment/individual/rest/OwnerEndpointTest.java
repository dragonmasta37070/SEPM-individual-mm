package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
class OwnerEndpointTest {

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
  void getAllOwners() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/owners")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<OwnerDto> ownerResult = objectMapper.readerFor(OwnerDto.class).<OwnerDto>readValues(body).readAll();
    assertThat(ownerResult).size().isEqualTo(24);
    assertThat(ownerResult)
        .extracting(OwnerDto::firstName, OwnerDto::lastName, OwnerDto::email)
        .contains(tuple("Eragon", "Schattentoeter", "erscha@gmail.com"));
  }

  @Test
  void searchExistingHorse() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/owners?name=Eragon")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<OwnerDto> ownerResult = objectMapper.readerFor(OwnerDto.class).<OwnerDto>readValues(body).readAll();
    assertThat(ownerResult).size().isEqualTo(1);
    assertThat(ownerResult)
        .extracting(OwnerDto::firstName, OwnerDto::lastName, OwnerDto::email)
        .contains(tuple("Eragon", "Schattentoeter", "erscha@gmail.com"));
  }

  @Test
  @DirtiesContext
  void createValidOwner() throws Exception {
    var newOwner = new OwnerCreateDto("Ali", "Usta", "granatapfel@ja.tr");

    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .post("/owners")
            .content(objectMapper.writeValueAsString(newOwner))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsByteArray();

    OwnerDto ownerResult = objectMapper.readValue(body, OwnerDto.class);
    assertThat(ownerResult)
        .extracting(OwnerDto::firstName, OwnerDto::lastName, OwnerDto::email)
        .contains("Ali", "Usta", "granatapfel@ja.tr");
  }

  @Test
  void createOwnerWithExistingEmailThrowsError() throws Exception {
    var newOwner = new OwnerCreateDto("Ali", "Usta", "erscha@gmail.com");

    mockMvc
        .perform(MockMvcRequestBuilders
            .post("/owners")
            .content(objectMapper.writeValueAsString(newOwner))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
  }
}