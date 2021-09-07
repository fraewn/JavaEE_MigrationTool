package core.network;

import core.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ReportService")
public class ReportServiceRESTService implements ReportService {

    @Autowired
    private ReportService service;

    @RequestMapping(value = "/verify", produces = { MediaType.TEXT_PLAIN_VALUE }, method = RequestMethod.GET)
    public ResponseEntity<String> verify() {
        String result = "Service started successfully";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public void getAllCompanies() {
    }

    @Override
    public void checkUniqueCompany() {
    }

    @Override
    public void editCompany() {
    }

    @Override
    public void getAllReports() {
    }

    @Override
    public void getCompanyById() {
    }

    @Override
    public void getCompanyByName() {
    }

    @Override
    public void createCompany() {
    }

    @Override
    public void createReport() {
    }

    @Override
    public void editReport() {
    }

    @Override
    public void getReportById() {
    }

    @Override
    public void addNewLike() {
    }
}
