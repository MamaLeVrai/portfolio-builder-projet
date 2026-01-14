# Portfolio Builder - Documentation des EPICs 1 & 2

## Vue d'ensemble

Ce document décrit l'implémentation des **EPICs 1 & 2** pour l'application Portfolio Builder, couvrant la gestion des utilisateurs, l'authentification et la gestion des profils portfolio.

## Technologies utilisées

- **Framework**: Spring Boot 4.0.0
- **Java**: Version 17
- **Base de données**: MySQL
- **Template Engine**: Mustache
- **Sécurité**: Spring Security (BCrypt pour le hashage des mots de passe)
- **Validation**: Jakarta Validation (Bean Validation)
- **ORM**: Spring Data JPA avec Hibernate

## Architecture

L'application suit une architecture MVC classique :
- **Controllers**: Gèrent les requêtes HTTP et les réponses
- **Services**: Contiennent la logique métier
- **Repositories**: Gèrent l'accès aux données (JPA)
- **Entities**: Représentent les modèles de données
- **DTOs**: Objets de transfert de données pour les formulaires

## EPIC 1 : Gestion des utilisateurs et authentification

### US-001 : S'inscrire sur la plateforme ✅

**Route**: `GET/POST /register`

**Fonctionnalités implémentées**:
- Formulaire d'inscription avec validation des champs
- Vérification de l'unicité de l'email et du username
- Validation de la correspondance des mots de passe
- Hashage BCrypt des mots de passe
- Redirection vers la page de connexion après inscription réussie

**Fichiers**:
- DTO: `UserRegisterDto.java`
- Controller: `AuthController.java`
- Service: Méthode `registerUser()` dans `UserService.java`
- Template: `/users/register.html`

### US-002 : Se connecter à mon compte ✅

**Route**: `GET/POST /login`

**Fonctionnalités implémentées**:
- Formulaire de connexion (username ou email + password)
- Authentification gérée par Spring Security
- Session utilisateur automatique
- Redirection vers "Mes profils" après connexion réussie
- Lien vers la page d'inscription

**Fichiers**:
- Controller: Spring Security gère l'authentification
- Template: `/users/formLogin.html` (mis à jour)
- Configuration: `SecurityConfig.java`

### US-003 : Se déconnecter ✅

**Route**: `GET /logout`

**Fonctionnalités implémentées**:
- Invalidation de la session utilisateur
- Nettoyage du contexte de sécurité
- Redirection vers la page de connexion
- Bouton de déconnexion dans le header

**Fichiers**:
- Controller: Méthode `logout()` dans `AuthController.java`
- Template: Header mis à jour avec bouton de déconnexion

### US-004 : Modifier mes informations personnelles ✅

**Route**: `GET/POST /users/edit`

**Fonctionnalités implémentées**:
- Modification du prénom, nom et email
- Validation de l'unicité de l'email si modifié
- Changement de mot de passe optionnel (avec vérification de l'ancien mot de passe)
- Messages de succès/erreur

**Fichiers**:
- DTO: `UserUpdateDto.java`
- Controller: Méthodes dans `UserController.java`
- Service: Méthode `updateUser()` dans `UserService.java`
- Template: `/users/edit.html`

### US-005 : Supprimer mon compte (BONUS) ✅

**Route**: `POST /users/delete-account`

**Fonctionnalités implémentées**:
- Confirmation avant suppression
- Suppression physique de l'utilisateur et de tous ses profils (cascade)
- Invalidation de la session
- Redirection vers la page d'inscription

**Fichiers**:
- Controller: Méthode `deleteAccount()` dans `UserController.java`
- Service: Méthode `deleteAccount()` dans `UserService.java`
- Template: Confirmation dans `/users/edit.html`

## EPIC 2 : Gestion des profils

### Champs ajoutés à l'entité Profile

```java
private String imageUrl;           // URL de l'image du profil
private LocalDateTime createdAt;   // Date de création (auto)
private LocalDateTime updatedAt;   // Date de mise à jour (auto)
private String status;             // Statut: draft, published, archived
private boolean isDefault;         // Profil par défaut
```

### US-006 : Créer un nouveau profil ✅

**Route**: `GET/POST /profiles/new`

**Fonctionnalités implémentées**:
- Formulaire de création avec nom, description et URL d'image
- Association automatique à l'utilisateur connecté
- Statut "draft" par défaut
- Redirection vers la liste des profils

**Fichiers**:
- DTO: `ProfileCreateDto.java`
- Controller: Méthodes dans `ProfileController.java`
- Service: Méthode `createProfileNew()` dans `ProfileService.java`
- Template: `/profiles/create.html`

### US-007 : Consulter la liste de mes profils ✅

**Route**: `GET /profiles/my-profiles`

**Fonctionnalités implémentées**:
- Affichage de tous les profils de l'utilisateur connecté
- Tri par date de dernière modification (plus récent en premier)
- Affichage des informations: nom, description, image, statut, dates
- Badge "PAR DÉFAUT" pour le profil par défaut
- Actions rapides: Voir, Éditer, Dupliquer, Supprimer, Définir par défaut
- Message si aucun profil

**Fichiers**:
- Repository: Méthode `findByOwnerAndArchivedFalseOrderByUpdatedAtDesc()`
- Service: Méthode `getProfilesByUserSorted()`
- Controller: Méthode `myProfiles()`
- Template: `/profiles/my-profiles.html`

### US-008 : Modifier les informations d'un profil ✅

**Route**: `GET/POST /profiles/{id}/edit`

**Fonctionnalités implémentées**:
- Formulaire pré-rempli avec les données du profil
- Modification du nom, description, image et statut
- Vérification du droit de propriété
- Mise à jour automatique de `updatedAt`

**Fichiers**:
- DTO: `ProfileUpdateDto.java`
- Controller: Méthodes dans `ProfileController.java`
- Service: Méthode `updateProfile()` dans `ProfileService.java`
- Template: `/profiles/edit.html`

### US-009 : Dupliquer un profil existant (BONUS) ✅

**Route**: `POST /profiles/{id}/duplicate`

**Fonctionnalités implémentées**:
- Création d'une copie exacte du profil avec un nouveau UUID
- Ajout de " (Copie)" au nom
- Vérification du droit de propriété
- Redirection vers la liste des profils

**Fichiers**:
- Service: Méthode `duplicateProfile()` dans `ProfileService.java`
- Controller: Méthode dans `ProfileController.java`

### US-010 : Supprimer un profil ✅

**Route**: `POST /profiles/{id}/delete-confirmed`

**Fonctionnalités implémentées**:
- Confirmation JavaScript avant suppression
- Vérification du droit de propriété
- Archive du profil (soft delete)
- Redirection vers la liste des profils

**Fichiers**:
- Service: Méthode `deleteProfile()` dans `ProfileService.java`
- Controller: Méthode dans `ProfileController.java`
- Template: Confirmation dans `/profiles/my-profiles.html`

### US-011 : Définir un profil par défaut (BONUS) ✅

**Route**: `POST /profiles/{id}/set-default`

**Fonctionnalités implémentées**:
- Un seul profil par défaut par utilisateur
- Retrait automatique du statut "par défaut" des autres profils
- Indication visuelle du profil par défaut (badge jaune)
- Vérification du droit de propriété

**Fichiers**:
- Repository: Méthode `findByOwnerAndIsDefaultTrue()`
- Service: Méthode `setDefaultProfile()` dans `ProfileService.java`
- Controller: Méthode dans `ProfileController.java`
- Template: Badge dans `/profiles/my-profiles.html`

## Sécurité

### Configuration de Spring Security

- **Routes publiques**: `/`, `/register`, `/login`, `/css/**`, `/js/**`, `/styles/**`, `/img/**`
- **Routes protégées**: Toutes les autres routes nécessitent une authentification
- **Logout**: Configuré sur `/logout` avec redirection vers `/login`
- **Login**: Formulaire personnalisé sur `/login` avec redirection vers `/profiles/my-profiles`
- **CSRF**: Désactivé pour simplifier (à activer en production)

### Hashage des mots de passe

- Utilisation de BCrypt via `BCryptPasswordEncoder`
- Les mots de passe sont hashés avant stockage dans la méthode `encodePassword()` de `DbUserServices`

### Vérifications de propriété

Toutes les opérations sur les profils vérifient que l'utilisateur connecté est le propriétaire :
- Édition de profil
- Suppression de profil
- Duplication de profil
- Définition du profil par défaut

## Navigation

### Menu principal (header)

Le header contient un menu de navigation disponible pour les utilisateurs connectés :
- **Portfolio Builder**: Lien vers la page d'accueil
- **Mes profils**: Accès à la liste des profils de l'utilisateur
- **Mon compte**: Accès aux paramètres du compte
- **Déconnexion**: Déconnexion de l'application

## Messages flash

L'application utilise `RedirectAttributes.addFlashAttribute()` pour afficher des messages de succès ou d'erreur après les opérations :
- Inscription réussie
- Connexion
- Mise à jour de profil
- Création de profil
- Suppression de profil
- etc.

Les messages sont affichés dans les templates via les variables Mustache `{{#successMessage}}` et `{{#error}}`.

## Base de données

### Configuration

La configuration MySQL est définie dans `application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

### Migrations automatiques

Hibernate gère automatiquement les migrations de schéma avec `ddl-auto=update`. Les nouveaux champs ajoutés à l'entité `Profile` seront créés automatiquement au démarrage de l'application.

## Comment utiliser l'application

### 1. Inscription
1. Accéder à `/register`
2. Remplir le formulaire (prénom, nom, username, email, mot de passe)
3. Confirmer le mot de passe
4. Cliquer sur "S'inscrire"

### 2. Connexion
1. Accéder à `/login`
2. Entrer le username ou l'email
3. Entrer le mot de passe
4. Cliquer sur "Se connecter"

### 3. Gestion des profils
- **Créer un profil**: Cliquer sur "Créer un nouveau profil" depuis la page "Mes profils"
- **Voir la liste**: Accéder à "Mes profils" depuis le menu
- **Éditer un profil**: Cliquer sur "Éditer" sur un profil
- **Dupliquer un profil**: Cliquer sur "Dupliquer" sur un profil
- **Supprimer un profil**: Cliquer sur "Supprimer" avec confirmation
- **Définir par défaut**: Cliquer sur "Définir par défaut" (visible uniquement si le profil n'est pas déjà par défaut)

### 4. Gestion du compte
1. Accéder à "Mon compte" depuis le menu
2. Modifier les informations personnelles (prénom, nom, email)
3. Optionnel: Changer le mot de passe en remplissant les champs correspondants
4. Cliquer sur "Enregistrer les modifications"

### 5. Suppression du compte
1. Accéder à "Mon compte"
2. Descendre à la section "Zone de danger"
3. Cliquer sur "Supprimer mon compte"
4. Confirmer l'action (tous les profils seront également supprimés)

## Structure des fichiers

```
src/main/java/alt/portfolio/builder/
├── configurations/
│   ├── MustacheConfig.java
│   ├── SecurityConfig.java (modifié)
│   └── WebConfiguration.java
├── controllers/
│   ├── AuthController.java (nouveau)
│   ├── ProfileController.java (modifié)
│   └── UserController.java (modifié)
├── dtos/
│   ├── ProfileCreateDto.java (nouveau)
│   ├── ProfileUpdateDto.java (nouveau)
│   ├── UserLoginDto.java (nouveau)
│   ├── UserRegisterDto.java (nouveau)
│   ├── UserRequestDto.java (renommé)
│   └── UserUpdateDto.java (nouveau)
├── entities/
│   ├── Profile.java (modifié - nouveaux champs)
│   └── User.java (modifié - fix typo addProfile)
├── repositories/
│   ├── ProfileRepositories.java (modifié - nouvelles méthodes)
│   └── UserRepositories.java
└── services/
    ├── ProfileService.java (modifié - nouvelles méthodes)
    └── UserService.java (modifié - nouvelles méthodes)

src/main/resources/templates/
├── partials/
│   ├── header.html (modifié - navigation)
│   └── footer.html
├── profiles/
│   ├── create.html (nouveau)
│   ├── edit.html (nouveau)
│   ├── my-profiles.html (nouveau)
│   └── ... (autres fichiers existants)
└── users/
    ├── edit.html (nouveau)
    ├── formLogin.html (modifié)
    ├── register.html (nouveau)
    └── ... (autres fichiers existants)
```

## Corrections apportées

1. **Typo corrigée**: `addProdile()` → `addProfile()` dans `User.java`
2. **Nomenclature cohérente**: `userRequestDto` → `UserRequestDto`
3. **Champs ajoutés à Profile**: imageUrl, createdAt, updatedAt, status, isDefault
4. **Dépendance ajoutée**: spring-boot-starter-validation

## Tests

Pour tester l'application localement :

```bash
# Compiler le projet
./mvnw clean compile

# Démarrer l'application
./mvnw spring-boot:run
```

L'application sera accessible sur `http://localhost:8080`

**Note importante**: Assurez-vous que MySQL est installé et en cours d'exécution, et que la base de données `portfolio` existe.

## Améliorations futures possibles

1. Activer CSRF pour une meilleure sécurité
2. Ajouter des tests unitaires et d'intégration
3. Implémenter la pagination pour la liste des profils
4. Ajouter la recherche et le filtrage des profils
5. Implémenter l'upload d'images pour les profils
6. Ajouter la récupération de mot de passe
7. Implémenter des rôles utilisateurs (admin, user)
8. Ajouter la possibilité de partager des profils publiquement
9. Améliorer le design CSS/UI
10. Ajouter l'internationalisation (i18n)

## Support

Pour toute question ou problème, veuillez consulter la documentation Spring Boot ou contacter l'équipe de développement.
