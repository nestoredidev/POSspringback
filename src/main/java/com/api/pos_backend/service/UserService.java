package com.api.pos_backend.service;

import com.api.pos_backend.dto.UserDTO;
import com.api.pos_backend.shared.pagination.PageResponse;
import org.springframework.data.domain.Pageable;


public interface UserService {

    PageResponse<UserDTO> getAllUsers(Pageable pageable);

    PageResponse<UserDTO> getUserProfile(Long id);

    PageResponse<UserDTO> getUserByUsername(String username);

    PageResponse<UserDTO> getListByUsername(String name);

    PageResponse<UserDTO> updateUser(Long id, UserDTO userDTO);

    String deleteUser(Long id);
}
