package works.jayesh.demo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import works.jayesh.demo.common.exception.GlobalExceptionHandler;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.user.model.dto.UserRegistrationRequest;
import works.jayesh.demo.user.model.dto.UserResponse;
import works.jayesh.demo.user.model.dto.UserUpdateRequest;
import works.jayesh.demo.user.service.UserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for UserController
 * Tests REST API contracts: HTTP status codes, validation, security, response
 * structure
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserController API Tests")
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UserResponse testUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        testUser = UserResponse.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    // ==================== CREATE TESTS ====================

    @Test
    @DisplayName("POST /api/users/register - Should return 201 with user response")
    void registerUser_Success() throws Exception {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123@");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(userService.registerUser(any(UserRegistrationRequest.class)))
                .thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));

        verify(userService, times(1)).registerUser(any(UserRegistrationRequest.class));
    }

    @Test
    @DisplayName("POST /api/users/register - Should return 400 for invalid email")
    void registerUser_InvalidEmail() throws Exception {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("invalid-email");
        request.setPassword("Password123@");
        request.setFirstName("John");
        request.setLastName("Doe");

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("POST /api/users/register - Should return 400 for missing required fields")
    void registerUser_MissingFields() throws Exception {
        // Given - Empty request
        UserRegistrationRequest request = new UserRegistrationRequest();

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }

    // ==================== READ TESTS ====================

    @Test
    @DisplayName("GET /api/users/{userId} - Should return 200 with user response")
    void getUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("GET /api/users/{userId} - Should return 404 when user not found")
    void getUserById_NotFound() throws Exception {
        // Given
        when(userService.getUserById(999L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Should return 200 with user response")
    void getUserByEmail_Success() throws Exception {
        // Given
        when(userService.getUserByEmail("test@example.com")).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    @DisplayName("GET /api/users - Should return 200 with paginated users")
    void getAllUsers_Success() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<UserResponse> page = new PageImpl<>(Collections.singletonList(testUser), pageable, 1);
        when(userService.getAllUsers(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data.content[0].email").value("test@example.com"));

        verify(userService, times(1)).getAllUsers(any());
    }

    @Test
    @DisplayName("GET /api/users - Should handle pagination parameters")
    void getAllUsers_WithPagination() throws Exception {
        // Given
        Page<UserResponse> page = new PageImpl<>(Collections.singletonList(testUser), PageRequest.of(0, 10), 1);
        when(userService.getAllUsers(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.size").value(10));

        verify(userService, times(1)).getAllUsers(any());
    }

    @Test
    @DisplayName("GET /api/users/search - Should return 200 with search results")
    void searchUsers_Success() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<UserResponse> page = new PageImpl<>(Collections.singletonList(testUser), pageable, 1);
        when(userService.searchUsers(eq("John"), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/users/search")
                .param("keyword", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Search completed successfully"))
                .andExpect(jsonPath("$.data.content[0].firstName").value("John"));

        verify(userService, times(1)).searchUsers(eq("John"), any());
    }

    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("PUT /api/users/{userId} - Should return 200 with updated user")
    void updateUser_Success() throws Exception {
        // Given
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");

        UserResponse updatedUser = UserResponse.builder()
                .email("test@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class)))
                .thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                .andExpect(jsonPath("$.data.lastName").value("Smith"));

        verify(userService, times(1)).updateUser(eq(1L), any(UserUpdateRequest.class));
    }

    @Test
    @DisplayName("PUT /api/users/{userId} - Should return 404 when user not found")
    void updateUser_NotFound() throws Exception {
        // Given
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Jane");

        when(userService.updateUser(eq(999L), any()))
                .thenThrow(new ResourceNotFoundException("User not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(999L), any());
    }

    @Test
    @DisplayName("PUT /api/users/{userId} - Should return 400 for invalid data")
    void updateUser_InvalidData() throws Exception {
        // Given - Name too short
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("A"); // Less than 2 characters

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), any());
    }

    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("DELETE /api/users/{userId} - Should return 200")
    void deleteUser_Success() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /api/users/{userId} - Should return 404 when user not found")
    void deleteUser_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("User not found with id: 999"))
                .when(userService).deleteUser(999L);

        // When & Then
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(999L);
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("GET /api/users/{userId} - Should handle malformed ID")
    void getUserById_MalformedId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/invalid"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    @DisplayName("POST /api/users/register - Should handle malformed JSON")
    void registerUser_MalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid-json"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }
}
