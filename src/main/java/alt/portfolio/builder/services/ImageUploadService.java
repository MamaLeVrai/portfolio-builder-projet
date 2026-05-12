package alt.portfolio.builder.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * ImageUploadService — Le service qui gère l'envoi de fichiers images.
 *
 * Quand tu choisis une photo sur ton ordinateur et que tu cliques sur
 * "Uploader", c'est ce service qui reçoit l'image, vérifie que c'est bien
 * une image (pas un virus !), lui donne un nom unique, et l'enregistre
 * dans un dossier sur le serveur.
 *
 * Créé dans Epic 5 (US-030 pour les photos de profil, US-031 pour les images de projets).
 */
@Service
public class ImageUploadService {

    /**
     * Le chemin du dossier où on va sauvegarder les images.
     * Lit la valeur "app.upload.dir" dans application.properties.
     * Ex: "src/main/resources/static/uploads"
     */
    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * (US-030) Sauvegarde une photo de profil.
     * Les photos de profil sont rangées dans le sous-dossier "profiles".
     *
     * @param file Le fichier image envoyé par l'utilisateur
     * @return L'URL publique de l'image (ex: "/uploads/profiles/abc123.jpg")
     */
    public String uploadProfilePhoto(MultipartFile file) throws IOException {
        return upload(file, "profiles");
    }

    /**
     * (US-031) Sauvegarde une image pour un projet (item).
     * Les images de projets sont rangées dans le sous-dossier "items".
     *
     * @param file Le fichier image envoyé par l'utilisateur
     * @return L'URL publique de l'image (ex: "/uploads/items/abc123.png")
     */
    public String uploadItemImage(MultipartFile file) throws IOException {
        return upload(file, "items");
    }

    /**
     * Méthode privée qui fait le vrai travail d'upload.
     * Elle est appelée par uploadProfilePhoto et uploadItemImage.
     *
     * Étapes :
     * 1. Vérifie que le fichier n'est pas vide
     * 2. Vérifie que c'est bien une image (et pas un fichier .exe ou autre)
     * 3. Crée un nom de fichier unique avec UUID (pour éviter les écrasements)
     * 4. Crée le dossier si il n'existe pas encore
     * 5. Copie le fichier dans le dossier
     * 6. Retourne l'URL à laquelle l'image sera accessible
     *
     * @param file   Le fichier à sauvegarder
     * @param subDir Le sous-dossier ("profiles" ou "items")
     * @return L'URL publique de l'image sauvegardée
     */
    private String upload(MultipartFile file, String subDir) throws IOException {
        // Étape 1 : le fichier doit exister et avoir du contenu
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier vide");
        }

        // Étape 2 : le type de fichier doit commencer par "image/"
        // Ex: "image/jpeg", "image/png" → OK | "application/pdf" → refusé
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Seules les images sont autorisées");
        }

        // Étape 3 : on récupère l'extension (.jpg, .png…) et on crée un nom unique
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        // UUID.randomUUID() génère une chaîne unique comme "f47ac10b-58cc-4372-a567-0e02b2c3d479"
        String fileName = UUID.randomUUID() + extension;

        // Étape 4 : on crée le dossier s'il n'existe pas (createDirectories = crée aussi les parents)
        Path dir = Paths.get(uploadDir, subDir);
        Files.createDirectories(dir);

        // Étape 5 : on copie le fichier dans le bon dossier
        Path dest = dir.resolve(fileName);
        file.transferTo(dest);

        // Étape 6 : on retourne l'URL que le navigateur utilisera pour afficher l'image
        return "/uploads/" + subDir + "/" + fileName;
    }
}
