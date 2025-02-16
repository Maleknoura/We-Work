package org.wora.we_work.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.entities.Role;
import org.wora.we_work.entities.User;
import org.wora.we_work.exception.ResourceNotFoundException;
import org.wora.we_work.mapper.EspaceCoworkingMapper;
import org.wora.we_work.repository.EspaceCoworkingRepository;
import org.wora.we_work.services.api.UserService;

import org.springframework.security.access.AccessDeniedException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EspaceCoworkingServiceImplTest {

    @Mock
    private EspaceCoworkingRepository espaceCoworkingRepository;
    @Mock
    private EspaceCoworkingMapper espaceCoworkingMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private EspaceCoworkingServiceImpl espaceCoworkingService;

    private User mockUser;
    private Role proprietaireRole;
    private Role adminRole;
    private EspaceCoworking mockEspace;
    private EspaceCoworkingRequestDTO requestDTO;
    private EspaceCoworkingResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        proprietaireRole = new Role();
        proprietaireRole.setName("ROLE_PROPRIETAIRE");
        adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");
        mockUser.setRoles(new HashSet<>(Collections.singletonList(proprietaireRole)));

        mockEspace = new EspaceCoworking();
        mockEspace.setId(1L);
        mockEspace.setNom("Test Space");
        mockEspace.setAdresse("Test Address");
        mockEspace.setPrixParJour(100.0);
        mockEspace.setCapacite(10);
        mockEspace.setUser(mockUser);
        mockEspace.setActive(true);

        requestDTO = new EspaceCoworkingRequestDTO();
        requestDTO.setNom("Test Space");
        requestDTO.setAdresse("Test Address");
        requestDTO.setPrixParJour(100.0);
        requestDTO.setCapacite(10);

        responseDTO = new EspaceCoworkingResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNom("Test Space");
    }

    @Nested
    class CreateTests {
        @Test
        void create_Success() {
            when(userService.getCurrentUser()).thenReturn(mockUser);
            when(espaceCoworkingMapper.toEntity(any())).thenReturn(mockEspace);
            when(espaceCoworkingRepository.save(any())).thenReturn(mockEspace);
            when(espaceCoworkingMapper.toResponseDTO(any())).thenReturn(responseDTO);

            EspaceCoworkingResponseDTO result = espaceCoworkingService.create(requestDTO);

            assertNotNull(result);
            verify(espaceCoworkingRepository).save(any());
            verify(espaceCoworkingMapper).toResponseDTO(any());
        }

        @Test
        void create_NonProprietaire_ThrowsAccessDeniedException() {
            mockUser.setRoles(new HashSet<>());
            when(userService.getCurrentUser()).thenReturn(mockUser);

            assertThrows(org.springframework.security.access.AccessDeniedException.class,
                    () -> espaceCoworkingService.create(requestDTO));
        }

        @Test
        void create_InvalidPrice_ThrowsIllegalArgumentException() {
            when(userService.getCurrentUser()).thenReturn(mockUser);
            mockEspace.setPrixParJour(-1.0);
            when(espaceCoworkingMapper.toEntity(any())).thenReturn(mockEspace);

            assertThrows(IllegalArgumentException.class,
                    () -> espaceCoworkingService.create(requestDTO));
        }
    }

    @Nested
    class UpdateTests {
        @Test
        void update_Success() {

            when(userService.getCurrentUser()).thenReturn(mockUser);
            when(espaceCoworkingRepository.findById(1L)).thenReturn(Optional.of(mockEspace));
            when(espaceCoworkingRepository.save(any())).thenReturn(mockEspace);
            when(espaceCoworkingMapper.toResponseDTO(any())).thenReturn(responseDTO);


            EspaceCoworkingResponseDTO result = espaceCoworkingService.update(1L, requestDTO);


            assertNotNull(result);
            verify(espaceCoworkingRepository).save(any());
        }

        @Test
        void update_NotFound_ThrowsResourceNotFoundException() {

            when(espaceCoworkingRepository.findById(1L)).thenReturn(Optional.empty());


            assertThrows(ResourceNotFoundException.class,
                    () -> espaceCoworkingService.update(1L, requestDTO));
        }

        @Test
        void update_UnauthorizedUser_ThrowsAccessDeniedException() {
            User differentUser = new User();
            differentUser.setId(2L);
            differentUser.setRoles(new HashSet<>());

            when(userService.getCurrentUser()).thenReturn(differentUser);
            when(espaceCoworkingRepository.findById(1L)).thenReturn(Optional.of(mockEspace));

            assertThrows(org.springframework.security.access.AccessDeniedException.class,
                    () -> espaceCoworkingService.update(1L, requestDTO));
        }

    }

    @Nested
    class DeleteTests {
        @Test
        void delete_Success() {

            when(userService.getCurrentUser()).thenReturn(mockUser);
            when(espaceCoworkingRepository.findById(1L)).thenReturn(Optional.of(mockEspace));


            espaceCoworkingService.delete(1L);


            verify(espaceCoworkingRepository).save(argThat(espace -> !espace.isActive()));
        }

        @Test
        void delete_NotFound_ThrowsResourceNotFoundException() {
            when(espaceCoworkingRepository.findById(1L)).thenReturn(Optional.empty());


            assertThrows(ResourceNotFoundException.class,
                    () -> espaceCoworkingService.delete(1L));
        }
    }

    @Nested
    class GetTests {
        @Test
        void getById_Success() {

            when(espaceCoworkingRepository.findById(1L)).thenReturn(Optional.of(mockEspace));
            when(espaceCoworkingMapper.toResponseDTO(any())).thenReturn(responseDTO);


            EspaceCoworkingResponseDTO result = espaceCoworkingService.getById(1L);


            assertNotNull(result);
            verify(espaceCoworkingMapper).toResponseDTO(mockEspace);
        }

        @Test
        void getAll_Success() {

            Page<EspaceCoworking> page = new PageImpl<>(Collections.singletonList(mockEspace));
            when(espaceCoworkingRepository.findAll(any(Pageable.class))).thenReturn(page);
            when(espaceCoworkingMapper.toResponseDTO(any())).thenReturn(responseDTO);


            Page<EspaceCoworkingResponseDTO> result = espaceCoworkingService.getAll(Pageable.unpaged());


            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        }

        @Test
        void getAllByProprietaire_Success() {

            Page<EspaceCoworking> page = new PageImpl<>(Collections.singletonList(mockEspace));
            when(espaceCoworkingRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(page);
            when(espaceCoworkingMapper.toResponseDTO(any())).thenReturn(responseDTO);


            Page<EspaceCoworkingResponseDTO> result = espaceCoworkingService.getAllByProprietaire(1L, Pageable.unpaged());


            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        }
    }
}