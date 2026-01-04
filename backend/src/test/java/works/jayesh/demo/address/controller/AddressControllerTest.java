package works.jayesh.demo.address.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import works.jayesh.demo.address.model.dto.AddressRequest;
import works.jayesh.demo.address.model.dto.AddressResponse;
import works.jayesh.demo.address.model.entity.AddressType;
import works.jayesh.demo.address.service.AddressService;
import works.jayesh.demo.common.exception.GlobalExceptionHandler;
import works.jayesh.demo.common.exception.ResourceNotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressController API Tests")
class AddressControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private AddressResponse addressResponse;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(addressController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();

        addressResponse = AddressResponse.builder()
                .id(1L)
                .fullName("John Doe")
                .phoneNumber("1234567890")
                .addressLine1("123 Main St")
                .city("New York")
                .state("NY")
                .country("USA")
                .postalCode("10001")
                .type(AddressType.HOME)
                .isDefault(false)
                .build();

        addressRequest = new AddressRequest();
        addressRequest.setFullName("John Doe");
        addressRequest.setPhoneNumber("1234567890");
        addressRequest.setAddressLine1("123 Main St");
        addressRequest.setCity("New York");
        addressRequest.setState("NY");
        addressRequest.setPostalCode("10001");
        addressRequest.setCountry("USA");
        addressRequest.setType(AddressType.HOME);
        addressRequest.setIsDefault(false); // Use setIsDefault for Boolean field
    }

    @Test
    @DisplayName("Should create address successfully")
    void createAddress_Success() throws Exception {
        when(addressService.createAddress(eq(1L), any(AddressRequest.class))).thenReturn(addressResponse);

        mockMvc.perform(post("/api/addresses/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("John Doe"))
                .andExpect(jsonPath("$.data.city").value("New York"));
    }

    @Test
    @DisplayName("Should return 400 when creating address with missing fullName")
    void createAddress_MissingFullName() throws Exception {
        addressRequest.setFullName(null);

        mockMvc.perform(post("/api/addresses/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when creating address with missing phoneNumber")
    void createAddress_MissingPhoneNumber() throws Exception {
        addressRequest.setPhoneNumber(null);

        mockMvc.perform(post("/api/addresses/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when creating address with missing addressLine1")
    void createAddress_MissingAddressLine1() throws Exception {
        addressRequest.setAddressLine1(null);

        mockMvc.perform(post("/api/addresses/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get address by ID successfully")
    void getAddressById_Success() throws Exception {
        when(addressService.getAddressById(1L)).thenReturn(addressResponse);

        mockMvc.perform(get("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.fullName").value("John Doe"));
    }

    @Test
    @DisplayName("Should return 404 when address not found")
    void getAddressById_NotFound() throws Exception {
        when(addressService.getAddressById(999L))
                .thenThrow(new ResourceNotFoundException("Address not found with id: 999"));

        mockMvc.perform(get("/api/addresses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get user addresses successfully")
    void getUserAddresses_Success() throws Exception {
        List<AddressResponse> addresses = Arrays.asList(addressResponse);
        when(addressService.getUserAddresses(1L)).thenReturn(addresses);

        mockMvc.perform(get("/api/addresses/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].fullName").value("John Doe"));
    }

    @Test
    @DisplayName("Should return empty list when user has no addresses")
    void getUserAddresses_EmptyList() throws Exception {
        when(addressService.getUserAddresses(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/addresses/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should update address successfully")
    void updateAddress_Success() throws Exception {
        addressResponse.setCity("Los Angeles");
        when(addressService.updateAddress(eq(1L), any(AddressRequest.class))).thenReturn(addressResponse);

        mockMvc.perform(put("/api/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent address")
    void updateAddress_NotFound() throws Exception {
        when(addressService.updateAddress(eq(999L), any(AddressRequest.class)))
                .thenThrow(new ResourceNotFoundException("Address not found with id: 999"));

        mockMvc.perform(put("/api/addresses/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when updating address with invalid data")
    void updateAddress_InvalidData() throws Exception {
        addressRequest.setPhoneNumber("invalid");

        mockMvc.perform(put("/api/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should delete address successfully")
    void deleteAddress_Success() throws Exception {
        mockMvc.perform(delete("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent address")
    void deleteAddress_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Address not found with id: 999"))
                .when(addressService).deleteAddress(999L);

        mockMvc.perform(delete("/api/addresses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void createAddress_MalformedJson() throws Exception {
        mockMvc.perform(post("/api/addresses/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed address ID")
    void getAddressById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/addresses/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed user ID")
    void getUserAddresses_InvalidUserId() throws Exception {
        mockMvc.perform(get("/api/addresses/user/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
