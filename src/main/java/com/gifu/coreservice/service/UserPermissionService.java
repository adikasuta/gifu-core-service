package com.gifu.coreservice.service;

import com.gifu.coreservice.entity.*;
import com.gifu.coreservice.enumeration.PermissionEnum;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.UserPermissionDto;
import com.gifu.coreservice.model.request.UpdateUserPermissionRequest;
import com.gifu.coreservice.repository.*;
import liquibase.pro.packaged.P;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserPermissionService {

    @Autowired
    private UserPermissionRepository userPermissionRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public boolean hasPermission(PermissionEnum permission, Long userId) {
        try {
            List<Permission> granted = getPermissionsByUserId(userId);
            for (Permission it : granted) {
                if (permission.name().equalsIgnoreCase(it.getCode())) {
                    return true;
                }
            }
            return false;
        } catch (InvalidRequestException e) {
            return false;
        }
    }

    public List<String> getPermissionCodesByUserId(Long userId) throws InvalidRequestException {
        List<Permission> permissions = getPermissionsByUserId(userId);
        return permissions.stream().map(Permission::getCode).collect(Collectors.toList());
    }

    public void setUserPermission(Long userId, UpdateUserPermissionRequest request, String updater){
        Optional<UserPermission> userPermissionOpt = userPermissionRepository.findByUserIdAndPermissionId(userId, request.getPermissionId());
        UserPermission userPermission;
        if(userPermissionOpt.isPresent()){
            userPermission = userPermissionOpt.get();
            userPermission.setInclude(request.isGranted());
        } else {
            userPermission = new UserPermission();
            userPermission.setUserId(userId);
            userPermission.setPermissionId(request.getPermissionId());
            userPermission.setInclude(request.isGranted());
            userPermission.setCreatedDate(ZonedDateTime.now());
        }
        userPermission.setUpdatedDate(ZonedDateTime.now());
        userPermission.setUpdatedBy(updater);
        userPermissionRepository.save(userPermission);
    }

    public List<Permission> getPermissionsByUserId(Long userId) throws InvalidRequestException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new InvalidRequestException("User is not found");
        }
        User user = userOpt.get();
        Role role = null;
        if (user.getRoleId() != null) {
            Optional<Role> roleOpt = roleRepository.findById(user.getRoleId());
            if (roleOpt.isPresent()) {
                role = roleOpt.get();
            }
        }
        List<Long> rolePermissionIds = new ArrayList<>();
        if (role != null) {
            List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(role.getId());
            rolePermissionIds = rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        }
        List<UserPermission> userPermissions = userPermissionRepository.findByUserId(user.getId());
        Map<Long, UserPermission> mapOfUserPermission = new HashMap<>();
        for (UserPermission it : userPermissions) {
            mapOfUserPermission.put(it.getPermissionId(), it);
        }
        List<Permission> permissions = permissionRepository.findAll();
        List<Permission> grantedPermissions = new ArrayList<>();
        for (Permission it : permissions) {
            UserPermission up = mapOfUserPermission.get(it.getId());
            boolean grantedByRole = rolePermissionIds.contains(it.getId());
            if (up == null) {
                if (grantedByRole) {
                    grantedPermissions.add(it);
                }
            } else if (up.isInclude()) {
                grantedPermissions.add(it);
            }
        }
        return grantedPermissions;
    }

    public List<UserPermissionDto> getUserPermissions(Long userId) throws InvalidRequestException {
        List<Permission> grantedPermissions = getPermissionsByUserId(userId);
        List<Long> grantedPermissionIds = grantedPermissions.stream().map(Permission::getId).collect(Collectors.toList());
        List<Permission> permissions = permissionRepository.findAll();
        List<UserPermissionDto> results = new ArrayList<>();
        for (Permission it : permissions) {
            UserPermissionDto res = UserPermissionDto.builder()
                    .permissionId(it.getId())
                    .permissionName(it.getName())
                    .userId(userId)
                    .granted(grantedPermissionIds.contains(it.getId()))
                    .build();
            results.add(res);
        }
        return results;
    }
}
