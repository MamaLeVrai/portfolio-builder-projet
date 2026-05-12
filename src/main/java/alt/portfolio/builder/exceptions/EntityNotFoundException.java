package alt.portfolio.builder.exceptions;

/**
 * Exception lancée quand on cherche quelque chose qui n'existe pas en base de données.
 *
 * Exemples d'utilisation :
 * - On cherche un profil avec un ID qui n'existe pas → EntityNotFoundException
 * - On cherche un utilisateur supprimé → EntityNotFoundException
 *
 * Cette exception est "checked" (elle étend Exception et pas RuntimeException),
 * ce qui veut dire que le compilateur Java nous oblige à la déclarer avec "throws"
 * dans la signature de chaque méthode qui peut la lancer.
 *
 * Quand elle est lancée, MainControllerAdvice l'attrape automatiquement
 * et affiche la page d'erreur 404.
 */
public class EntityNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crée une nouvelle exception avec un message expliquant ce qui n'a pas été trouvé.
     * @param message Le message d'erreur (ex : "Profil introuvable : 123-abc")
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
