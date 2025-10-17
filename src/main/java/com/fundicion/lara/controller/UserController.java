package com.fundicion.lara.controller;

import com.fundicion.lara.commons.data.ApiResponse;
import com.fundicion.lara.commons.data.Pagination;
import com.fundicion.lara.dto.UserDto;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.dto.request.UserRequest;
import com.fundicion.lara.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "USER")
@RestController
@AllArgsConstructor
@RequestMapping(value = "v1/management/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(
            operationId = "findAllUsers",
            summary = "Retrieve a paginated list of users based on specified filters",
            description = "This endpoint retrieves a list of users filtered by various parameters such as search, sorting options, and pagination. " +
                    "You can specify the page number and page size for pagination. The users can be sorted in ascending or descending order based on a specified field."
    )
    public ApiResponse<List<UserDto>> findAllUsers(
            @Parameter(name = "page", description = "The page number to retrieve, starting from 1.")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(name = "pageSize", description = "The number of users to return per page.")
            @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(name = "order", description = "Sorting order: 'asc' or 'desc'.")
            @RequestParam(defaultValue = "asc", required = false) String order,
            @Parameter(name = "orderBy", description = "The field by which to sort the users.")
            @RequestParam(defaultValue = "userId", required = false) String orderBy,
            @Parameter(name = "search", description = "Search keyword to filter users by name, lastName, motherLastName or email.")
            @RequestParam(required = false) String search
    ) {
        val pagination = Pagination.builder()
                .page(page)
                .pageSize(pageSize)
                .build();

        val requestParams = RequestParams.builder()
                .order(order)
                .orderBy(orderBy)
                .search(search)
                .pagination(pagination)
                .build();

        return ApiResponse.ok(this.userService.findAllUsers(requestParams), pagination);
    }

    @GetMapping("/{id}")
    @Operation(
            operationId = "findUserById",
            summary = "Retrieve user by its unique identifier",
            description = "This endpoint retrieves the details of a specific user identified by its unique user ID."
    )
    public ApiResponse<UserDto> findUserById(
            @Parameter(name = "id", description = "The unique identifier of the user")
            @PathVariable Long id
    ) {
        return ApiResponse.ok(this.userService.findUserById(id));
    }

    @GetMapping("/email/{email}")
    @Operation(
            operationId = "findUserByEmail",
            summary = "Retrieve user by email",
            description = "This endpoint retrieves the details of a specific user identified by its email address."
    )
    public ApiResponse<UserDto> findUserByEmail(
            @Parameter(name = "email", description = "The email of the user")
            @PathVariable String email
    ) {
        return ApiResponse.ok(this.userService.findUserByEmail(email));
    }

    @PutMapping("/{id}")
    @Operation(
            operationId = "updateUser",
            summary = "Update an existing user",
            description = "This endpoint allows for the modification of an existing user identified by its unique user ID."
    )
    public ApiResponse<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest userDto
    ) {
        return ApiResponse.ok(this.userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            operationId = "deleteUser",
            summary = "Delete a user by its unique identifier",
            description = "This endpoint allows for the deletion of a user identified by its unique user ID. Operation is irreversible."
    )
    public ApiResponse<String> deleteUser(
            @PathVariable Long id
    ) {
        this.userService.deleteUser(id);
        return ApiResponse.ok("OK");
    }
}
