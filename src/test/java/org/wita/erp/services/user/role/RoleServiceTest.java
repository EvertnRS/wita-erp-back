package org.wita.erp.services.user.role;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wita.erp.domain.entities.audit.EntityType;
import org.wita.erp.domain.entities.user.dtos.RoleDTO;
import org.wita.erp.domain.entities.user.mappers.RoleMapper;
import org.wita.erp.domain.entities.user.role.Permission;
import org.wita.erp.domain.entities.user.role.Role;
import org.wita.erp.domain.entities.user.role.dtos.CreateRoleRequestDTO;
import org.wita.erp.domain.entities.user.role.dtos.DeleteRoleRequestDTO;
import org.wita.erp.domain.entities.user.role.dtos.UpdateRoleRequestDTO;
import org.wita.erp.domain.repositories.user.role.PermissionRepository;
import org.wita.erp.domain.repositories.user.role.RoleRepository;
import org.wita.erp.infra.exceptions.permission.PermissionException;
import org.wita.erp.infra.exceptions.role.RoleException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.user.role.observers.RoleSoftDeleteObserver;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private RoleService roleService;

    private Pageable pageable;
    private Long defaultRoleId;
    private Permission perm1;
    private Permission perm2;
    private Role defaultRole;
    private RoleDTO defaultRoleDTO;
    private Page<Role> defaultPage;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        defaultRoleId = 1L;

        perm1 = new Permission(1L, "USER_READ");
        perm2 = new Permission(2L, "USER_WRITE");

        defaultRole = new Role(defaultRoleId, "USER", true, new HashSet<>(Set.of(perm1, perm2)));
        defaultRoleDTO = new RoleDTO(defaultRoleId, "USER", Set.of("USER_READ", "USER_WRITE"));

        defaultPage = new PageImpl<>(List.of(defaultRole));
    }


    @Test
    @DisplayName("Deve retornar todas as roles quando o searchTerm for nulo")
    void shouldReturnAllRolesWhenSearchTermIsNull() {
        Mockito.when(roleRepository.findAll(pageable)).thenReturn(defaultPage);
        Mockito.when(roleMapper.toDTO(defaultRole)).thenReturn(defaultRoleDTO);

        ResponseEntity<Page<RoleDTO>> response = roleService.getAllRoles(pageable, null);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());

        Mockito.verify(roleRepository).findAll(pageable);
        Mockito.verify(roleRepository, Mockito.never()).findByRole(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar roles filtradas pelo searchTerm")
    void shouldReturnRolesFilteredBySearchTerm() {
        Mockito.when(roleRepository.findByRole("USER", pageable)).thenReturn(defaultPage);
        Mockito.when(roleMapper.toDTO(defaultRole)).thenReturn(defaultRoleDTO);

        ResponseEntity<Page<RoleDTO>> response = roleService.getAllRoles(pageable, "USER");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());

        Mockito.verify(roleRepository).findByRole("USER", pageable);
        Mockito.verify(roleRepository, Mockito.never()).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve salvar uma nova Role com sucesso quando as permissões existirem")
    void shouldSaveRoleSuccessfully() {
        CreateRoleRequestDTO fakeRequestDTO = new CreateRoleRequestDTO("USER", Set.of(1L, 2L));

        Mockito.when(permissionRepository.findById(1L)).thenReturn(Optional.of(perm1));
        Mockito.when(permissionRepository.findById(2L)).thenReturn(Optional.of(perm2));
        Mockito.when(roleMapper.toDTO(Mockito.any(Role.class))).thenReturn(defaultRoleDTO);

        ResponseEntity<RoleDTO> response = roleService.save(fakeRequestDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(defaultRoleDTO, response.getBody());

        Mockito.verify(permissionRepository).findById(1L);
        Mockito.verify(permissionRepository).findById(2L);
        Mockito.verify(roleRepository).save(Mockito.any(Role.class));
    }

    @Test
    @DisplayName("Deve lançar PermissionException e não salvar a Role se uma permissão não existir")
    void shouldThrowPermissionExceptionWhenPermissionNotFound() {
        CreateRoleRequestDTO fakeRequestDTO = new CreateRoleRequestDTO("USER", Set.of(99L));

        Mockito.when(permissionRepository.findById(99L)).thenReturn(Optional.empty());

        PermissionException exception = Assertions.assertThrows(PermissionException.class,
                () -> roleService.save(fakeRequestDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
    }

    @Test
    @DisplayName("Deve atualizar o nome e as permissões da Role com sucesso")
    void shouldUpdateBothNameAndPermissionsSuccessfully() {
        UpdateRoleRequestDTO fakeUpdateDTO = new UpdateRoleRequestDTO("MANAGER", Set.of(3L));
        Permission newPermission = new Permission(3L, "USER_DELETE");
        RoleDTO updatedRoleDTO = new RoleDTO(defaultRoleId, "MANAGER", Set.of("USER_READ", "USER_WRITE", "USER_DELETE"));

        Mockito.when(roleRepository.findById(defaultRoleId)).thenReturn(Optional.of(defaultRole));
        Mockito.when(permissionRepository.findById(3L)).thenReturn(Optional.of(newPermission));
        Mockito.when(roleMapper.toDTO(defaultRole)).thenReturn(updatedRoleDTO);

        ResponseEntity<RoleDTO> response = roleService.update(defaultRoleId, fakeUpdateDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(updatedRoleDTO, response.getBody());
        Assertions.assertEquals("MANAGER", defaultRole.getRole());
        Assertions.assertTrue(defaultRole.getPermissions().contains(newPermission));

        Mockito.verify(roleRepository).findById(defaultRoleId);
        Mockito.verify(permissionRepository).findById(3L);
        Mockito.verify(roleRepository).save(defaultRole);
    }

    @Test
    @DisplayName("Deve atualizar APENAS o nome da Role com sucesso")
    void shouldUpdateOnlyNameSuccessfully() {
        UpdateRoleRequestDTO fakeUpdateDTO = new UpdateRoleRequestDTO("NEW_NAME_ONLY", null);
        RoleDTO updatedRoleDTO = new RoleDTO(defaultRoleId, "NEW_NAME_ONLY", Set.of("USER_READ", "USER_WRITE"));

        Mockito.when(roleRepository.findById(defaultRoleId)).thenReturn(Optional.of(defaultRole));
        Mockito.when(roleMapper.toDTO(defaultRole)).thenReturn(updatedRoleDTO);

        ResponseEntity<RoleDTO> response = roleService.update(defaultRoleId, fakeUpdateDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("NEW_NAME_ONLY", defaultRole.getRole());

        Mockito.verify(permissionRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(roleRepository).save(defaultRole);
    }

    @Test
    @DisplayName("Deve atualizar APENAS as permissões da Role com sucesso")
    void shouldUpdateOnlyPermissionsSuccessfully() {
        UpdateRoleRequestDTO fakeUpdateDTO = new UpdateRoleRequestDTO(null, Set.of(3L));
        Permission newPermission = new Permission(3L, "SYSTEM_CONFIG");

        defaultRole.getPermissions().clear();

        RoleDTO updatedRoleDTO = new RoleDTO(defaultRoleId, "USER", Set.of("SYSTEM_CONFIG"));

        Mockito.when(roleRepository.findById(defaultRoleId)).thenReturn(Optional.of(defaultRole));
        Mockito.when(permissionRepository.findById(3L)).thenReturn(Optional.of(newPermission));
        Mockito.when(roleMapper.toDTO(defaultRole)).thenReturn(updatedRoleDTO);

        ResponseEntity<RoleDTO> response = roleService.update(defaultRoleId, fakeUpdateDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("USER", defaultRole.getRole());

        Mockito.verify(permissionRepository).findById(3L);
        Mockito.verify(roleRepository).save(defaultRole);
    }

    @Test
    @DisplayName("Deve lançar RoleException ao tentar atualizar uma Role inexistente")
    void shouldThrowRoleExceptionWhenUpdatingNonExistentRole() {
        Long invalidRoleId = 99L;
        UpdateRoleRequestDTO fakeUpdateDTO = new UpdateRoleRequestDTO("ANY_NAME", null);

        Mockito.when(roleRepository.findById(invalidRoleId)).thenReturn(Optional.empty());

        RoleException exception = Assertions.assertThrows(RoleException.class,
                () -> roleService.update(invalidRoleId, fakeUpdateDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
    }

    @Test
    @DisplayName("Deve lançar PermissionException e não salvar a Role ao enviar permissão inexistente")
    void shouldThrowPermissionExceptionWhenUpdatingWithInvalidPermission() {
        UpdateRoleRequestDTO fakeUpdateDTO = new UpdateRoleRequestDTO("ANY", Set.of(99L));

        Mockito.when(roleRepository.findById(defaultRoleId)).thenReturn(Optional.of(defaultRole));
        Mockito.when(permissionRepository.findById(99L)).thenReturn(Optional.empty());

        PermissionException exception = Assertions.assertThrows(PermissionException.class,
                () -> roleService.update(defaultRoleId, fakeUpdateDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
    }

    @Test
    @DisplayName("Deve inativar uma Role com sucesso")
    void shouldDeleteRoleSuccessfully() {
        RoleDTO deletedRoleDTO = new RoleDTO(defaultRoleId, "USER",  null);

        Mockito.when(roleRepository.findById(defaultRoleId)).thenReturn(Optional.of(defaultRole));
        Mockito.when(roleMapper.toDTO(defaultRole)).thenReturn(deletedRoleDTO);

        ResponseEntity<RoleDTO> response = roleService.delete(defaultRoleId, new DeleteRoleRequestDTO("Reason"));

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(deletedRoleDTO, response.getBody());
        Assertions.assertFalse(defaultRole.getActive());

        Mockito.verify(roleRepository).findById(defaultRoleId);
        Mockito.verify(roleRepository).save(defaultRole);

        Mockito.verify(publisher, Mockito.times(2)).publishEvent(Mockito.any(Object.class));
    }

    @Test
    @DisplayName("Deve lançar RoleException ao tentar deletar uma Role inexistente")
    void shouldThrowRoleExceptionWhenDeletingNonExistentRole() {
        Long invalidRoleId = 99L;
        Mockito.when(roleRepository.findById(invalidRoleId)).thenReturn(Optional.empty());

        RoleException exception = Assertions.assertThrows(RoleException.class,
                () -> roleService.delete(invalidRoleId, new DeleteRoleRequestDTO("Reason")));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
        Mockito.verify(publisher, Mockito.never()).publishEvent(Mockito.any(Object.class));
    }

    @Test
    @DisplayName("Deve publicar o evento de auditoria de soft delete da Role com os dados corretos")
    void shouldAuditRoleSoftDelete() {
        String reason = "Limpeza de base de dados";

        roleService.auditRoleSoftDelete(defaultRoleId, reason);

        ArgumentCaptor<SoftDeleteLogObserver> eventCaptor = ArgumentCaptor.forClass(SoftDeleteLogObserver.class);
        Mockito.verify(publisher).publishEvent(eventCaptor.capture());

        SoftDeleteLogObserver capturedEvent = eventCaptor.getValue();

        Assertions.assertEquals(defaultRoleId.toString(), capturedEvent.entityId());
        Assertions.assertEquals(EntityType.ROLE.getEntityType(), capturedEvent.entityType());
        Assertions.assertEquals(reason, capturedEvent.reason());
    }

    @Test
    @DisplayName("Deve publicar o evento de deleção em cascata da Role com o ID correto")
    void shouldPublishRoleCascadeDelete() {

        roleService.roleCascadeDelete(defaultRoleId);

        ArgumentCaptor<RoleSoftDeleteObserver> eventCaptor = ArgumentCaptor.forClass(RoleSoftDeleteObserver.class);
        Mockito.verify(publisher).publishEvent(eventCaptor.capture());

        RoleSoftDeleteObserver capturedEvent = eventCaptor.getValue();

        Assertions.assertEquals(defaultRoleId, capturedEvent.role());
    }
}