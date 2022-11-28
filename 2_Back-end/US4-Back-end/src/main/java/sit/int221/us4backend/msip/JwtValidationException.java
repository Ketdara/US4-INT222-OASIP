package sit.int221.us4backend.msip;

public class JwtValidationException extends RuntimeException {

    JwtValidationException(String message, Throwable ex){
        super(message, ex);
    }

    JwtValidationException(String message){
        super(message);
    }

}
