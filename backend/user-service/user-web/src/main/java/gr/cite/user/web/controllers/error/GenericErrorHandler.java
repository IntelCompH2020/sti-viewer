package gr.cite.user.web.controllers.error;//package gr.gr.cite.user.web.controllers.error;
//
//import gr.gr.cite.tools.exception.MyValidationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//
//@ControllerAdvice
//public class GenericErrorHandler {
//
//    @ExceptionHandler(MyValidationException.class)
//    public ResponseEntity<MyValidationException> handleValidationException(MyValidationException e, WebRequest webRequest) {
//        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
//    }
//}
