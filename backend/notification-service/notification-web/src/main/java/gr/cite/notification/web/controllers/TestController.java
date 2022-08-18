package gr.cite.notification.web.controllers;

import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.tools.exception.MyValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "test")
public class TestController {

    private final ErrorThesaurusProperties errorThesaurusProperties;

    @Autowired
    public TestController(ErrorThesaurusProperties errorThesaurusProperties) {
        this.errorThesaurusProperties = errorThesaurusProperties;
    }

    @GetMapping("/error/validation")
    public void testErrorHandler() {
        throw new MyValidationException(errorThesaurusProperties.getModelValidation().getCode(), errorThesaurusProperties.getModelValidation().getMessage());
    }
}
