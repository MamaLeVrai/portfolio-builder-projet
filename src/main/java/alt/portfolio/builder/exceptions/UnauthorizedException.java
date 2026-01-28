package alt.portfolio.builder.exceptions;

/**
 * Exception levée lorsqu'un utilisateur tente d'accéder à une ressource sans autorisation
 */
public class UnauthorizedException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message);
    }
}
