package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import alt.portfolio.builder.exceptions.EntityNotFoundException;
import alt.portfolio.builder.exceptions.UnauthorizedException;
import alt.portfolio.builder.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.ProfileCreateDto;
import alt.portfolio.builder.dtos.ProfileRequestDto;
import alt.portfolio.builder.dtos.ProfileUpdateDto;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.entities.ProfileView;
import alt.portfolio.builder.entities.Template;
import alt.portfolio.builder.repositories.ProfileRepositories;
import alt.portfolio.builder.repositories.ProfileViewRepositories;
import alt.portfolio.builder.repositories.TemplateRepositories;
import alt.portfolio.builder.repositories.UserRepositories;

/**
 * ProfileService — Le cerveau de la gestion des profils.
 *
 * Ce service contient toute la logique métier autour des profils :
 * créer, modifier, dupliquer, supprimer, publier, personnaliser…
 * Les contrôleurs (ProfileController, CustomizationController, etc.)
 * font appel à ce service pour faire le vrai travail.
 *
 * Règle d'or : avant de modifier un profil, on vérifie TOUJOURS
 * que c'est bien le propriétaire qui agit (et pas quelqu'un d'autre).
 *
 * Enrichi dans Epic 4 (publication, URLs publiques, statistiques)
 * et Epic 5 (templates, couleurs, photo de profil).
 */
@Service
public class ProfileService {

    /** Accès à la table "profile" en base de données */
    @Autowired
    private ProfileRepositories profileRepositories;

    /** Accès à la table "user" (pour trouver le propriétaire d'un profil) */
    @Autowired
    private UserRepositories userRepositories;

    /** Accès à la table "profile_view" (pour les statistiques de vues) */
    @Autowired
    private ProfileViewRepositories profileViewRepositories;

    /** Accès à la table "template" (pour les mises en page) */
    @Autowired
    private TemplateRepositories templateRepositories;

    /** Retourne tous les profils non archivés (pour l'admin) */
    public List<Profile> getProfiles() {
        return profileRepositories.findByArchivedFalse();
    }

    /** Retourne les profils non archivés d'un utilisateur identifié par son ID */
    public List<Profile> getProfilesByUserId(UUID userId) {
        return profileRepositories.findByOwnerIdAndArchivedFalse(userId);
    }

    /**
     * Crée un profil à partir d'un ancien DTO (formulaire générique).
     * Vérifie que le nom n'est pas déjà utilisé et que le propriétaire est correct.
     */
    public Profile createProfile(ProfileRequestDto request) {
        // On vérifie qu'aucun profil n'a déjà ce nom exact
        profileRepositories.findByName(request.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Username déjà utilisé");
        });

        // On récupère l'utilisateur connecté depuis Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (authentication != null && authentication.getPrincipal() instanceof User)
                ? (User) authentication.getPrincipal()
                : null;

        // Si un ownerId est fourni dans la requête, il doit correspondre à l'utilisateur connecté
        if (request.getOwnerId() != null) {
            if (currentUser == null || !request.getOwnerId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Action non autorisée: propriétaire différent de l'utilisateur connecté");
            }
        }

        Profile profile = request.toProfile(new Profile());

        // On détermine le propriétaire : soit celui fourni dans la requête, soit l'utilisateur connecté
        User owner = null;
        if (request.getOwnerId() != null) {
            owner = userRepositories.findById(request.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur propriétaire introuvable"));
        } else {
            if (currentUser != null) {
                owner = currentUser;
            }
        }

        if (owner == null) {
            throw new IllegalArgumentException("Aucun propriétaire fourni ou connecté");
        }

        profile.setOwner(owner);
        return profileRepositories.save(profile);
    }

    /**
     * Cherche un profil par son identifiant.
     * Lève une exception si le profil n'existe pas (le programme signale l'erreur).
     */
    public Profile getProfileById(UUID id) {
        return profileRepositories.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile introuvable: " + id));
    }

    /**
     * Archive un profil (le rend invisible sans l'effacer de la base de données).
     * C'est comme mettre un document dans une boîte fermée au lieu de le déchirer.
     */
    public void archiveProfile(UUID id) {
        Profile profile = getProfileById(id);
        profile.setArchived(true);
        profileRepositories.save(profile);
    }

    /**
     * (US-006) Crée un nouveau profil avec le formulaire simplifié.
     * Associe automatiquement le profil à l'utilisateur connecté.
     */
    public Profile createProfileNew(ProfileCreateDto createDto, User currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Utilisateur non connecté");
        }
        Profile profile = createDto.toProfile(new Profile());
        profile.setOwner(currentUser);
        return profileRepositories.save(profile);
    }

    /**
     * (US-007) Retourne les profils d'un utilisateur, du plus récemment modifié au plus ancien.
     * Utilisé pour afficher la liste "Mes profils".
     */
    public List<Profile> getProfilesByUserSorted(User user) {
        return profileRepositories.findByOwnerAndArchivedFalseOrderByUpdatedAtDesc(user);
    }

    /**
     * (US-008) Met à jour les informations d'un profil.
     * Vérifie d'abord que c'est bien le propriétaire qui modifie.
     */
    public Profile updateProfile(UUID profileId, ProfileUpdateDto updateDto, User currentUser) {
        Profile profile = getProfileById(profileId);

        // Vérification du propriétaire : seul lui peut modifier son profil
        if (!profile.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce profil");
        }

        profile = updateDto.updateProfile(profile);
        return profileRepositories.save(profile);
    }

    /**
     * (US-009) Crée une copie d'un profil existant.
     * La copie est en mode "brouillon" et n'est pas le profil par défaut.
     */
    public Profile duplicateProfile(UUID profileId, User currentUser) {
        Profile original = getProfileById(profileId);

        if (!original.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à dupliquer ce profil");
        }

        // On crée un nouveau profil avec les mêmes informations + "(Copie)" dans le nom
        Profile duplicate = new Profile();
        duplicate.setName(original.getName() + " (Copie)");
        duplicate.setDescription(original.getDescription());
        duplicate.setImageUrl(original.getImageUrl());
        duplicate.setStatus("draft");    // brouillon par défaut
        duplicate.setDefault(false);     // pas le profil principal
        duplicate.setOwner(original.getOwner());

        return profileRepositories.save(duplicate);
    }

    /**
     * (US-010) "Supprime" un profil en l'archivant.
     * On ne le supprime pas vraiment pour garder l'historique.
     */
    public void deleteProfile(UUID profileId, User currentUser) {
        Profile profile = getProfileById(profileId);

        if (!profile.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer ce profil");
        }

        // Archivage = "suppression douce" (soft delete)
        profile.setArchived(true);
        profileRepositories.save(profile);
    }

    /**
     * (US-011) Définit un profil comme profil "par défaut".
     * D'abord, on retire le statut par défaut de l'ancien profil par défaut,
     * puis on le donne au nouveau.
     */
    public Profile setDefaultProfile(UUID profileId, User currentUser) throws EntityNotFoundException, UnauthorizedException {
        Profile profile = getProfileById(profileId);

        if (!profile.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce profil");
        }

        // On enlève le statut "par défaut" de l'ancien profil principal
        profileRepositories.findByOwnerAndIsDefaultTrue(currentUser).ifPresent(defaultProfile -> {
            defaultProfile.setDefault(false);
            profileRepositories.save(defaultProfile);
        });

        // On donne le statut "par défaut" au profil choisi
        profile.setDefault(true);
        return profileRepositories.save(profile);
    }

    /**
     * (Epic 4 - US-022) Publie ou dépublie un profil en tant que CV et/ou Portfolio.
     * - Si on publie en CV ou Portfolio → statut = "published" (visible publiquement)
     * - Si on dépublie les deux → statut = "draft" (retour en brouillon)
     *
     * @param asCv        true = publier comme CV
     * @param asPortfolio true = publier comme Portfolio
     */
    public Profile publishProfile(UUID profileId, boolean asCv, boolean asPortfolio, User currentUser) throws UnauthorizedException {
        Profile profile = getProfileById(profileId);

        if (!profile.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à publier ce profil");
        }

        profile.setPublishedAsCv(asCv);
        profile.setPublishedAsPortfolio(asPortfolio);

        // Si au moins une vue est publiée, le statut global passe à "published"
        if (asCv || asPortfolio) {
            profile.setStatus("published");
        } else {
            profile.setStatus("draft"); // plus rien de publié → retour en brouillon
        }

        return profileRepositories.save(profile);
    }

    /**
     * (Epic 4 - US-022) Raccourci pour dépublier complètement un profil.
     * Équivalent à publishProfile(..., false, false, ...).
     */
    public Profile unpublishProfile(UUID profileId, User currentUser) throws UnauthorizedException {
        return publishProfile(profileId, false, false, currentUser);
    }

    /**
     * (Epic 4 - US-027) Récupère le profil public CV d'un utilisateur identifié par son pseudo.
     * Cherche le profil "par défaut" de cet utilisateur qui est publié en tant que CV.
     * Utilisé par PublicController pour afficher /public/cv/{username}.
     */
    public Profile getPublicCvProfile(String username) throws EntityNotFoundException {
        // D'abord on cherche l'utilisateur par son pseudo
        User owner = userRepositories.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + username));
        // Puis on cherche son profil par défaut publié en CV
        return profileRepositories.findByOwnerAndIsDefaultTrueAndPublishedAsCvTrue(owner)
                .orElseThrow(() -> new EntityNotFoundException("Aucun CV publié pour cet utilisateur"));
    }

    /**
     * (Epic 4 - US-027) Récupère le profil public Portfolio d'un utilisateur.
     * Même logique que getPublicCvProfile, mais pour le Portfolio.
     */
    public Profile getPublicPortfolioProfile(String username) throws EntityNotFoundException {
        User owner = userRepositories.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + username));
        return profileRepositories.findByOwnerAndIsDefaultTrueAndPublishedAsPortfolioTrue(owner)
                .orElseThrow(() -> new EntityNotFoundException("Aucun portfolio publié pour cet utilisateur"));
    }

    /**
     * (Epic 4 - US-023) Enregistre une visite sur un profil.
     * Crée un enregistrement dans la table ProfileView ET incrémente le compteur viewCount.
     *
     * @param viewType  "cv" ou "portfolio"
     * @param visitorIp L'adresse IP du visiteur
     */
    public void recordView(Profile profile, String viewType, String visitorIp) {
        // On crée la fiche de visite et on la sauvegarde
        ProfileView view = new ProfileView();
        view.setProfile(profile);
        view.setViewType(viewType);
        view.setVisitorIp(visitorIp);
        profileViewRepositories.save(view);

        // On incrémente aussi le compteur sur le profil lui-même (accès rapide)
        profile.setViewCount(profile.getViewCount() + 1);
        profileRepositories.save(profile);
    }

    /** (Epic 4 - US-023) Retourne le nombre total de vues d'un profil (CV + Portfolio) */
    public long getTotalViews(Profile profile) {
        return profileViewRepositories.countByProfile(profile);
    }

    /** (Epic 4 - US-023) Retourne le nombre de vues en mode CV uniquement */
    public long getCvViews(Profile profile) {
        return profileViewRepositories.countByProfileAndViewType(profile, "cv");
    }

    /** (Epic 4 - US-023) Retourne le nombre de vues en mode Portfolio uniquement */
    public long getPortfolioViews(Profile profile) {
        return profileViewRepositories.countByProfileAndViewType(profile, "portfolio");
    }

    /** (Epic 4 - US-023) Retourne la liste des visites récentes d'un profil (de la plus récente à la plus ancienne) */
    public List<ProfileView> getRecentViews(Profile profile) {
        return profileViewRepositories.findByProfileOrderByViewedAtDesc(profile);
    }

    /**
     * (Epic 5 - US-024) Associe un template (mise en page) à la vue CV ou Portfolio d'un profil.
     * "cv" → modifie le champ "template" | autre → modifie "template1".
     */
    public Profile setTemplate(UUID profileId, UUID templateId, String viewType, User currentUser) throws UnauthorizedException {
        Profile profile = getProfileById(profileId);
        if (!profile.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Non autorisé");
        }
        Template template = templateRepositories.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template introuvable"));

        if ("cv".equals(viewType)) {
            profile.setTemplate(template);   // template pour la vue CV
        } else {
            profile.setTemplate1(template);  // template pour la vue Portfolio
        }
        return profileRepositories.save(profile);
    }

    /**
     * (Epic 5 - US-025 / US-028) Sauvegarde la couleur principale choisie pour une vue.
     * "cv" → modifie cvColor | autre → modifie portfolioColor.
     * La couleur est au format hexadécimal : ex "#2c3e50".
     */
    public Profile setColor(UUID profileId, String color, String viewType, User currentUser) throws UnauthorizedException {
        Profile profile = getProfileById(profileId);
        if (!profile.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Non autorisé");
        }
        if ("cv".equals(viewType)) {
            profile.setCvColor(color);
        } else {
            profile.setPortfolioColor(color);
        }
        return profileRepositories.save(profile);
    }

    /**
     * (Epic 5 - US-030) Sauvegarde l'URL de la photo de profil après upload.
     * L'URL pointe vers le fichier sauvegardé par ImageUploadService.
     */
    public Profile setProfilePhoto(UUID profileId, String photoUrl, User currentUser) throws UnauthorizedException {
        Profile profile = getProfileById(profileId);
        if (!profile.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Non autorisé");
        }
        profile.setImageUrl(photoUrl);
        return profileRepositories.save(profile);
    }

    /** Retourne tous les templates disponibles (pour remplir le formulaire de personnalisation) */
    public List<Template> getAllTemplates() {
        return templateRepositories.findAll();
    }
}
