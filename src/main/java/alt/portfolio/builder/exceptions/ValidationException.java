package alt.portfolio.builder.exceptions;

/**
 * Exception levée lors d'une erreur de validation (email déjà utilisé, username déjà pris, etc.)
 */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
