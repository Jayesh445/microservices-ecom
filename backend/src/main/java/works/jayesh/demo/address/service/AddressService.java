package works.jayesh.demo.address.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import works.jayesh.demo.address.model.dto.AddressRequest;
import works.jayesh.demo.address.model.dto.AddressResponse;
import works.jayesh.demo.address.model.entity.Address;
import works.jayesh.demo.address.repository.AddressRepository;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressResponse createAddress(Long userId, AddressRequest request) {
        log.info("Creating new address for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // If this is set as default, unset other default addresses
        if (request.getIsDefault() != null && request.getIsDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(addr -> {
                        addr.setDefault(false);
                        addressRepository.save(addr);
                    });
        }

        Address address = Address.builder()
                .user(user)
                .type(request.getType())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .landmark(request.getLandmark())
                .isDefault(request.getIsDefault() != null && request.getIsDefault())
                .build();

        Address savedAddress = addressRepository.save(address);
        log.info("Address created successfully with ID: {}", savedAddress.getId());

        return mapToResponse(savedAddress);
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long addressId) {
        Address address = findAddressById(addressId);
        return mapToResponse(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                .stream().map(this::mapToResponse).toList();
    }

    public AddressResponse updateAddress(Long addressId, AddressRequest request) {
        log.info("Updating address with ID: {}", addressId);

        Address address = findAddressById(addressId);

        // If this is set as default, unset other default addresses
        if (request.getIsDefault() && !address.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(address.getUser().getId())
                    .ifPresent(addr -> {
                        addr.setDefault(false);
                        addressRepository.save(addr);
                    });
        }

        address.setType(request.getType());
        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        address.setLandmark(request.getLandmark());
        address.setDefault(request.getIsDefault() != null && request.getIsDefault());

        Address updatedAddress = addressRepository.save(address);
        log.info("Address updated successfully with ID: {}", addressId);

        return mapToResponse(updatedAddress);
    }

    public void deleteAddress(Long addressId) {
        log.info("Deleting address with ID: {}", addressId);
        Address address = findAddressById(addressId);
        addressRepository.delete(address);
        log.info("Address deleted successfully with ID: {}", addressId);
    }

    private Address findAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));
    }

    private AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .type(address.getType())
                .fullName(address.getFullName())
                .phoneNumber(address.getPhoneNumber())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .landmark(address.getLandmark())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .build();
    }
}
