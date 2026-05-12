package alt.portfolio.builder.exceptions;

/**
 * Exception lancée quand un utilisateur essaie de faire quelque chose qu'il n'a pas le droit de faire.
 *
 * Exemples d'utilisation :
 * - Un utilisateur essaie de modifier le profil d'un autre utilisateur → UnauthorizedException
 * - Quelqu'un essaie de publier un profil qui ne lui appartient pas → UnauthorizedException
 *
 * Comme EntityNotFoundException, cette exception est "checked" : elle doit être déclarée
 * avec "throws UnauthorizedException" dans chaque méthode qui peut la lancer.
 *
 * Quand elle est lancée, MainControllerAdvice l'attrape automatiquement
 * et affiche la page d'erreur 403 "Accès interdit".
 */
public class UnauthorizedException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crée une nouvelle exception avec un message expliquant pourquoi l'accès est refusé.
     * @param message Le message d'erreur (ex : "Vous n'êtes pas autorisé à modifier ce profil")
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
