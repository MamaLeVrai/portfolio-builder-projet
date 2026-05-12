package alt.portfolio.builder.exceptions;

/**
 * Exception lancée quand les données envoyées par l'utilisateur ne sont pas valides.
 *
 * Exemples d'utilisation :
 * - L'email saisi est déjà utilisé par quelqu'un d'autre → ValidationException
 * - Le nom d'utilisateur est déjà pris → ValidationException
 * - Une valeur dépasse la taille maximale autorisée → ValidationException
 *
 * Comme les autres exceptions, elle est "checked" et doit être déclarée avec "throws".
 *
 * Quand elle est lancée, MainControllerAdvice l'attrape automatiquement
 * et affiche la page d'erreur 400 "Mauvaise requête".
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crée une nouvelle exception avec un message expliquant quelle validation a échoué.
     * @param message Le message d'erreur (ex : "Cet email est déjà utilisé")
     */
    public ValidationException(String message) {
        super(message);
    }
}
