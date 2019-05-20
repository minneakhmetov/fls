package com.jetbrains.fls;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetbrains.app.FlsApplication;
import com.jetbrains.forms.UpdateForm;
import com.jetbrains.forms.UserForm;
import com.jetbrains.models.Auth;
import com.jetbrains.models.User;
import com.jetbrains.repositories.AuthRepository;
import com.jetbrains.repositories.UpdateRepository;
import com.jetbrains.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;
import java.util.UUID;

import static com.jetbrains.forms.UpdateForm.fromLastUpdate;
import static com.jetbrains.models.Action.LOGIN;
import static com.jetbrains.models.Action.PROFILE_CREATED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FlsApplication.class)
@AutoConfigureMockMvc
public class FlsApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UpdateRepository updateRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void contextLoads() {
    }

    @Test
    public void createRandomUserTest() throws Exception {
        String login = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String password = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        UserForm form = UserForm.builder()
                .login(login)
                .password(password)
                .build();

        mockMvc.perform(post("/createProfile")
                       .contentType(MediaType.APPLICATION_JSON)
                        .param("login", login)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(authRepository.readOne(login).get().getToken())));

        Assert.assertTrue(form.equals(encoder, userRepository.read(login)));
        UpdateForm expectedUpdateForm = UpdateForm.builder()
                .login(login)
                .action(PROFILE_CREATED)
                .build();

        Assert.assertEquals(fromLastUpdate(updateRepository.read(login)), expectedUpdateForm);
        userRepository.delete(login);
    }

    @Test
    public void loginTest() throws Exception {
        String login = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String password = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        ResultActions resultActions = mockMvc.perform(post("/createProfile")
                .contentType(MediaType.APPLICATION_JSON)
                .param("login", login)
                .param("password", password))
                .andExpect(status().isOk());

        Auth auth = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), Auth.class);
        Assert.assertNotNull(auth);

        ResultActions loginActions = mockMvc.perform(post("/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .param("login", login)
                .param("password", password))
                .andExpect(status().isOk());

        Auth loginAuth = objectMapper.readValue(loginActions.andReturn().getResponse().getContentAsString(), Auth.class);
        Assert.assertNotNull(loginAuth);
        UpdateForm expectedUpdateForm = UpdateForm.builder()
                .login(login)
                .action(LOGIN)
                .build();

        Assert.assertEquals(fromLastUpdate(updateRepository.read(login)), expectedUpdateForm);
        userRepository.delete(login);
    }

    @Test
    public void changePasswordTest() throws Exception {
        String login = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String password = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        ResultActions resultActions = mockMvc.perform(post("/createProfile")
                .contentType(MediaType.APPLICATION_JSON)
                .param("login", login)
                .param("password", password))
                .andExpect(status().isOk());

        Auth auth = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), Auth.class);
        Assert.assertNotNull(auth);

        Optional<User> userCandidate = userRepository.read(auth.getLogin());

        Assert.assertTrue(userCandidate.isPresent());

        mockMvc.perform(post("/changePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .param("login", login)
                .param("token", auth.getToken())
                .param("password", UUID.randomUUID().toString().replace("-", "").substring(0, 10)))
                .andExpect(status().isOk());

        Optional<User> userCandidateChanged = userRepository.read(auth.getLogin());

        Assert.assertNotEquals(userCandidate, userCandidateChanged);
    }





}
