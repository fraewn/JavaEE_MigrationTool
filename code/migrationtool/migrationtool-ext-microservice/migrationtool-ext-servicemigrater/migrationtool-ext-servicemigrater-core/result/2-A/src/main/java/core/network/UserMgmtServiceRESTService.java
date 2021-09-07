package core.network;

import core.services.UserMgmtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/UserMgmtService")
public class UserMgmtServiceRESTService implements UserMgmtService {

    @Autowired
    private UserMgmtService service;

    @RequestMapping(value = "/verify", produces = { MediaType.TEXT_PLAIN_VALUE }, method = RequestMethod.GET)
    public ResponseEntity<String> verify() {
        String result = "Service started successfully";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public void checkUnique() {
    }

    @Override
    public void getUserGroupByName() {
    }

    @Override
    public void getUserByName() {
    }

    @Override
    public void createUser() {
    }

    @Override
    public void deleteUser() {
    }

    @Override
    public void editUser() {
    }

    @Override
    public void getUserById() {
    }

    @Override
    public void getUserGroupOfUser() {
    }

    @Override
    public void getAllUsers() {
    }
}
