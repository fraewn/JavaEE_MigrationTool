package core.network;

import core.services.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BatchService")
public class BatchServiceRESTService implements BatchService {

    @Autowired
    private BatchService service;

    @RequestMapping(value = "/verify", produces = { MediaType.TEXT_PLAIN_VALUE }, method = RequestMethod.GET)
    public ResponseEntity<String> verify() {
        String result = "Service started successfully";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public void createBatch() {
    }

    @Override
    public void getAllBatches() {
    }

    @Override
    public void getBatchById() {
    }
}
