package com.mgtu.akashkin.web;

import com.mgtu.akashkin.model.HashInfo;
import com.mgtu.akashkin.service.HashService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api-users")
public class UsersController {
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    @Autowired
    private HashService userService;



    @PostMapping("/create")
    public ResponseEntity createUser(@RequestBody HashInfo requestUserDetails) {
        try {


        userService.registrationUser(requestUserDetails);

                return new ResponseEntity(HttpStatus.CREATED);


        } catch (Exception e) {
            logger.error("createError", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<HashInfo>> findAllUsers() {
        try {


            List<HashInfo> users = userService.findAllUsers();

            if (users.isEmpty()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);

            } else {
                return new ResponseEntity<List<HashInfo>>(users, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("getAllError", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
