package com.example.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/OurSign/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "https://oursign.vercel.app/"})
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
   public ResponseEntity<List<UserDto>> getAllUsers(){
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId){
        Optional<UserDto> user =  userService.getUserById(userId);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto){
        UserDto updateUser = userService.updateUser(userId, userDto);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping ("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
    //자동검색 기능
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String name , @RequestParam(required =false)String email){
        List<UserDto> users = userService.searchUsersByNameAndEmail(name, email);
        return ResponseEntity.ok(users);
    }
    //이름으로 검색
    @GetMapping("/searchByName")
    public ResponseEntity<List<UserDto>> searchByName(@RequestParam String name) {
        List<UserDto> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    //이메일로 검색
    @GetMapping("/searchByEmail")
    public ResponseEntity<List<UserDto>> searchByEmail(@RequestParam String name, @RequestParam String email) {
        List<UserDto> users = userService.searchUsersByNameAndEmail(name, email);
        return ResponseEntity.ok(users);
    }

//    //
//    @PostMapping("/createOrFetch")
//    public ResponseEntity<UserDto> createOrFetchUser(@RequestBody UserDto userDto) {
//        UserDto result = userService.createOrFetchUser(userDto);
//        return ResponseEntity.ok(result);
//    }
}
