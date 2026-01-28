# Portfolio Builder - Projet de Développement Web

## 📋 Table des matières
1. [Contexte du projet](#contexte-du-projet)
2. [État d'avancement](#état-davancement)
3. [Fonctionnalités implémentées](#fonctionnalités-implémentées)
4. [TODO List](#todo-list)
5. [Installation et lancement](#installation-et-lancement)
6. [Architecture technique](#architecture-technique)
7. [Organisation SCRUM](#organisation-scrum)

---

## 1. Fonctionnalités implémentées

### EPIC 0 : Navigation et pages principales
- ✅ **US-000** : Accéder à la page d'accueil publique
- ✅ **US-002b** : Accéder à mon tableau de bord (dashboard)
- ✅ **US-039** : Naviguer entre les différentes sections depuis le menu principal
- ❌ **US-040** : Voir un aperçu de mon activité sur le dashboard (BONUS)

### EPIC 1 : Gestion des utilisateurs et authentification
- ✅ **US-001** : S'inscrire sur la plateforme
- ✅ **US-002** : Se connecter à mon compte
- ✅ **US-003** : Se déconnecter
- ✅ **US-004** : Modifier mes informations personnelles
- ✅ **US-005** : Supprimer mon compte (BONUS)

### EPIC 2 : Gestion des profils
- ✅ **US-006** : Créer un nouveau profil
- ✅ **US-007** : Consulter la liste de mes profils
- ✅ **US-008** : Modifier les informations d'un profil
- ✅ **US-009** : Dupliquer un profil existant (BONUS)
- ✅ **US-010** : Supprimer un profil
- ✅ **US-011** : Définir un profil par défaut (BONUS)

### EPIC 3 : Gestion des rubriques
- ⚠️ **US-012** : Ajouter une rubrique à mon profil (En cours)
- ❌ **US-013** : Modifier une rubrique existante
- ❌ **US-014** : Supprimer une rubrique
- ❌ **US-015** : Réorganiser l'ordre des rubriques
- ❌ **US-016** : Rendre une rubrique visible/invisible
- ❌ **US-017** : Ajouter plusieurs éléments dans une rubrique
- ❌ **US-018** : Modifier un élément d'une rubrique
- ❌ **US-019** : Supprimer un élément d'une rubrique
- ❌ **US-020** : Réorganiser les éléments au sein d'une rubrique

### EPIC 4 : Publication et partage
- ❌ **US-021** : Prévisualiser mon profil avant publication
- ❌ **US-022** : Publier mon profil en tant que CV et/ou Portfolio
- ❌ **US-023** : Consulter les statistiques de vues (BONUS)
- ❌ **US-026** : Basculer entre la prévisualisation CV et Portfolio
- ❌ **US-027** : Voir mes deux URL publiques

### EPIC 5 : Personnalisation visuelle
- ❌ **US-024** : Choisir un template pour chaque vue
- ❌ **US-025** : Personnaliser les couleurs
- ❌ **US-028** : Personnaliser les couleurs par vue (BONUS)
- ❌ **US-029** : Prévisualiser les changements en temps réel (BONUS)
- ❌ **US-030** : Uploader une photo de profil
- ❌ **US-031** : Ajouter des images aux projets (BONUS)

### EPIC 6 : Fonctionnalités avancées (BONUS)
- ❌ **US-032** : Exporter mon CV en PDF
- ❌ **US-033** : Importer des données depuis LinkedIn
- ❌ **US-034** : Partager sur les réseaux sociaux
- ❌ **US-035** : Notifications de consultation
- ❌ **US-036** : Formulaire de contact
- ❌ **US-037** : Personnaliser le slug du profil
- ❌ **US-038** : Rechercher dans mes rubriques

---

## 2. TODO List

### 🔴 URGENT - À faire MAINTENANT
- [ ] **Corriger le bug de login** (MainControllerAdvice.java)
  - Modifier la méthode `getActivatedUser()` 
  - Tester la page `/login`
  - Vérifier que les utilisateurs non connectés peuvent accéder au login
- [ ] Redémarrer le serveur et valider le fix
- [ ] Tester toutes les pages d'erreur (voir `URLS_TEST_ERREURS.md`)

### 🟠 PRIORITÉ HAUTE - Sprint actuel
#### EPIC 3 : Gestion des rubriques (MVP)
- [ ] **US-012** : Finaliser l'ajout de rubriques
  - [ ] Créer le contrôleur `RubricController` (si pas déjà fait)
  - [ ] Créer le service `RubricService`
  - [ ] Créer les DTOs pour les rubriques
  - [ ] Créer les templates Mustache
  - [ ] Lier les rubriques aux profils

- [ ] **US-013** : Modifier une rubrique
  - [ ] Endpoint GET `/profiles/{profileId}/rubrics/{rubricId}/edit`
  - [ ] Endpoint POST pour la modification
  - [ ] Validation des données

- [ ] **US-014** : Supprimer une rubrique
  - [ ] Endpoint POST `/profiles/{profileId}/rubrics/{rubricId}/delete`
  - [ ] Confirmation de suppression
  - [ ] Soft delete (archivage)

- [ ] **US-017** : Gérer les éléments multiples dans une rubrique
  - [ ] Créer l'entité `Item` ou `RubricElement`
  - [ ] CRUD complet sur les éléments
  - [ ] Lier les éléments aux rubriques

#### EPIC 4 : Publication (MVP)
- [ ] **US-021** : Prévisualisation du profil
  - [ ] Endpoint `/profiles/{id}/preview`
  - [ ] Template de prévisualisation
  - [ ] Affichage des rubriques et éléments

- [ ] **US-022** : Publication du profil
  - [ ] Système de statut (draft/published)
  - [ ] URL publique unique
  - [ ] Gestion des permissions (public vs privé)

### 🟡 PRIORITÉ MOYENNE - Prochain sprint
#### EPIC 3 : Rubriques avancées
- [ ] **US-015** : Réorganiser les rubriques (drag & drop)
  - [ ] Ajouter un champ `order` dans Rubric
  - [ ] Boutons haut/bas pour réorganiser
  - [ ] BONUS : Drag & drop avec JavaScript

- [ ] **US-016** : Visibilité des rubriques
  - [ ] Champ `visible` dans Rubric
  - [ ] Toggle visible/invisible
  - [ ] Filtrer dans l'affichage public

- [ ] **US-020** : Réorganiser les éléments
  - [ ] Champ `order` dans RubricElement
  - [ ] Interface de réorganisation

#### EPIC 5 : Personnalisation basique
- [ ] **US-024** : Templates
  - [ ] Créer 2-3 templates de CV
  - [ ] Créer 2-3 templates de Portfolio
  - [ ] Sélecteur de template

- [ ] **US-030** : Photo de profil
  - [ ] Upload d'image
  - [ ] Stockage (local ou cloud)
  - [ ] Affichage dans le profil

### 🟢 BONUS - Si le temps le permet
- [ ] **US-009** : Duplication avancée (avec rubriques)
- [ ] **US-011** : Profil par défaut (marquer comme favori)
- [ ] **US-023** : Statistiques de vues
- [ ] **US-032** : Export PDF
- [ ] **US-036** : Formulaire de contact

### 🔧 Améliorations techniques
- [ ] Ajouter des tests unitaires
  - [ ] UserService
  - [ ] ProfileService
  - [ ] RubricService
  - [ ] Contrôleurs

- [ ] Améliorer la validation
  - [ ] Annotations de validation sur les DTOs
  - [ ] Messages d'erreur personnalisés

- [ ] Optimiser les requêtes
  - [ ] Ajouter des indexes sur la BDD
  - [ ] Lazy loading pour les relations

- [ ] Améliorer l'UX
  - [ ] Messages flash plus visuels
  - [ ] Confirmations avant suppression
  - [ ] Chargement asynchrone

---


## 3. Architecture technique

### Structure du projet
```
src/main/java/alt/portfolio/builder/
├── application/
│   └── MainControllerAdvice.java      # Gestion globale des erreurs
├── configurations/
│   ├── GlobalControllerAdvice.java    # Variables globales templates
│   └── SecurityConfig.java            # Configuration Spring Security
├── controllers/
│   ├── AdminController.java           # Gestion admin
│   ├── AuthController.java            # Authentification
│   ├── UserController.java            # Gestion utilisateurs
│   ├── ProfileController.java         # Gestion profils
│   └── RubricController.java          # (À créer) Gestion rubriques
├── dtos/
│   ├── UserRegisterDto.java
│   ├── UserUpdateDto.java
│   ├── ProfileCreateDto.java
│   └── ProfileUpdateDto.java
├── entities/
│   ├── User.java
│   ├── Profile.java
│   └── Rubric.java                   
├── exceptions/
│   ├── EntityNotFoundException.java
│   ├── ValidationException.java
│   └── UnauthorizedException.java
├── repositories/
│   ├── UserRepositories.java
│   └── ProfileRepositories.java
└── services/
    ├── UserService.java
    └── ProfileService.java

---

**Dernière mise à jour** : 28 janvier 2026
**Version** : 0.5.0 (MVP en cours)
