# 🏔️ SkiProject

SkiProject est une application de gestion pour les écoles de ski, permettant aux **skieurs** et **instructeurs** de gérer facilement les cours, les réservations, et les accréditations. 
Cette application offre une interface simple et intuitive pour gérer les skieurs, les leçons, et les réservations en temps réel.

Cette application est développée avec **JAVA**, **SQL** pour la base de données Oracle, et utilise **WindowBuilder** pour l'interface graphique.

## Tech Stack

- **Backend** : Java, SQL (Base de données Oracle)
- **Interface** : WindowBuilder
- **Base de données** : Oracle (SQL)

## Fonctionnalités

### 1. **Gestion des Skieurs**
- **Ajouter un skieur** : Permet d'ajouter un nouveau skieur avec ses informations personnelles.
- **Modifier un skieur** : Offre la possibilité de mettre à jour les informations des skieurs existants.
- **Supprimer un skieur** : Permet de supprimer un skieur de la base de données.
- **Rechercher un skieur** : Recherchez un skieur en utilisant son nom.

### 2. **Gestion des Instructeurs**
- **Ajouter un instructeur** : Permet d'ajouter un instructeur avec ses accréditations.
- **Modifier un instructeur** : Modifiez les informations d'un instructeur, et gérez ses accréditations.
- **Supprimer un instructeur** : Supprimez un instructeur du système.
- **Rechercher un instructeur** : Recherchez un instructeur par son nom.

### 3. **Gestion des Leçons**
- **Ajouter une leçon** : Créez des leçons (collectives ou privées) avec des réservations minimales et maximales.
- **Modifier une leçon** : Modifiez les détails d'une leçon existante.
- **Supprimer une leçon** : Supprimez une leçon si elle n'est plus nécessaire.

### 4. **Gestion des Réservations**
- **Créer une réservation** : Ajoutez des skieurs aux leçons disponibles, avec la possibilité de choisir des créneaux horaires pour les leçons collectives.
- **Supprimer une réservation** : Annulez une réservation en cas de besoin.

### Étapes d'installation

1. Clonez le repository :
   ```bash
   git clone https://github.com/Veltri-Marie/SkiProject.git
   ```
2. Compilez le projet dans votre IDE (Eclipse avec WindowBuilder est recommandé).

3. Déployez l'application et assurez-vous que votre base de données Oracle est configurée correctement avec les tables nécessaires, que vous retrouverez dans `VeltriScriptSki.sql`.

### Lancement de l'application

1. Lancez la classe `MainPage.java` située dans le package `be.veltri.ski`.

2. Une fois démarrée, un message s’affichera dans le terminal pour confirmer la connexion à la base de données avec la mention : **"Connexion réussie !"**.

3. Si vous rencontrez un problème, assurez-vous que le serveur Oracle est opérationnel.
