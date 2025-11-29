package works.jayesh.demo.address.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import works.jayesh.demo.address.model.dto.AddressRequest;
import works.jayesh.demo.address.model.dto.AddressResponse;
import works.jayesh.demo.address.model.entity.Address;
import works.jayesh.demo.address.model.entity.AddressType;
import works.jayesh.demo.address.repository.AddressRepository;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Unit Tests")
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private User testUser;
    private Address testAddress;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        testAddress = Address.builder()
                .id(1L)
                .user(testUser)
                .type(AddressType.HOME)
                .fullName("John Doe")
                .phoneNumber("+1234567890")
                .addressLine1("123 Main St")
                .city("New York")
                .state("NY")
                .country("USA")
                .postalCode("10001")
                .isDefault(true)
                .build();

        addressRequest = new AddressRequest();
        addressRequest.setType(AddressType.HOME);
        addressRequest.setFullName("John Doe");
        addressRequest.setPhoneNumber("+1234567890");
        addressRequest.setAddressLine1("123 Main St");
        addressRequest.setCity("New York");
        addressRequest.setState("NY");
        addressRequest.setCountry("USA");
        addressRequest.setPostalCode("10001");
        addressRequest.setIsDefault(true);
    }

    // ==================== CREATE ADDRESS TESTS ====================

    @Test
    @DisplayName("Should create address successfully")
    void createAddress_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When
        AddressResponse response = addressService.createAddress(1L, addressRequest);

        // Then
        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
        assertEquals("123 Main St", response.getAddressLine1());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void createAddress_UserNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.createAddress(999L, addressRequest);
        });

        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    @DisplayName("Should unset other default addresses when creating new default")
    void createAddress_UnsetOtherDefaults() {
        // Given
        Address existingDefault = Address.builder()
                .id(2L)
                .user(testUser)
                .isDefault(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.of(existingDefault));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When
        AddressResponse response = addressService.createAddress(1L, addressRequest);

        // Then
        assertNotNull(response);
        verify(addressRepository, times(2)).save(any(Address.class)); // Once for unset, once for new
    }

    // ==================== GET ADDRESS TESTS ====================

    @Test
    @DisplayName("Should get address by ID successfully")
    void getAddressById_Success() {
        // Given
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        // When
        AddressResponse response = addressService.getAddressById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when address not found")
    void getAddressById_NotFound() {
        // Given
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.getAddressById(999L);
        });
    }

    @Test
    @DisplayName("Should get user addresses successfully")
    void getUserAddresses_Success() {
        // Given
        List<Address> addresses = Arrays.asList(testAddress);
        when(addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(1L)).thenReturn(addresses);

        // When
        List<AddressResponse> response = addressService.getUserAddresses(1L);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(addressRepository, times(1)).findByUserIdOrderByIsDefaultDescCreatedAtDesc(1L);
    }

    // ==================== UPDATE ADDRESS TESTS ====================

    @Test
    @DisplayName("Should update address successfully")
    void updateAddress_Success() {
        // Given
        addressRequest.setCity("Los Angeles");
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When
        AddressResponse response = addressService.updateAddress(1L, addressRequest);

        // Then
        assertNotNull(response);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent address")
    void updateAddress_NotFound() {
        // Given
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.updateAddress(999L, addressRequest);
        });
    }

    @Test
    @DisplayName("Should unset other defaults when updating to default")
    void updateAddress_UnsetOtherDefaults() {
        // Given
        testAddress.setDefault(false);
        Address existingDefault = Address.builder()
                .id(2L)
                .user(testUser)
                .isDefault(true)
                .build();

        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(addressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.of(existingDefault));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When
        AddressResponse response = addressService.updateAddress(1L, addressRequest);

        // Then
        assertNotNull(response);
        verify(addressRepository, times(2)).save(any(Address.class));
    }

    // ==================== DELETE ADDRESS TESTS ====================

    @Test
    @DisplayName("Should delete address successfully")
    void deleteAddress_Success() {
        // Given
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        doNothing().when(addressRepository).delete(any(Address.class));

        // When
        assertDoesNotThrow(() -> addressService.deleteAddress(1L));

        // Then
        verify(addressRepository, times(1)).delete(testAddress);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent address")
    void deleteAddress_NotFound() {
        // Given
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.deleteAddress(999L);
        });

        verify(addressRepository, never()).delete(any(Address.class));
    }
}
