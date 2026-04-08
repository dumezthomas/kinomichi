# 🥋 Gestion de stages Kinomichi

## 📌 Description

Application console développée en Java permettant de gérer des stages de Kinomichi.

Le programme permet de :

* Créer et gérer des stages (week-end)
* Ajouter des sessions et activités
* Gérer les participants (formateurs ou non)
* Effectuer des réservations
* Calculer le coût des inscriptions
* Calculer les revenus potentiels d'un stage
* Sauvegarder et charger les données

---

## 🚀 Lancement

1. Cloner ou télécharger le projet
2. Ouvrir dans un IDE (IntelliJ recommandé)
3. Lancer la classe `Main`

---

## ⚙️ Fonctionnalités principales

### 🟢 Stages

* Création de stage
* Impossible de créer deux stages le même weekend
* Ajout de sessions (max 5 samedi, 3 dimanche)
* Ajout d'activités
* Ouverture / fermeture des réservations
* Suppression bloquée si :

    * stage contient des inscriptions

### 👤 Participants

* Création de participants
* Gestion des participants (âge, formateur)
* Suppression bloquée si :

    * participant inscrit à un stage
    * formateur assigné à une session

### 📅 Sessions

* Créneaux entre 09:00 et 17:00
* Durée entre 15 et 120 minutes (intervalle de 15 min)
* Aucun chevauchement autorisé

### 🎯 Réservations

* Inscription à un stage ouvert
* Sélection de sessions et activités
* Impossible :

    * de réserver deux fois la même activité/session
    * de s'inscrire comme participant à une session où l'on est formateur

---

## 💾 Persistance

* Sauvegarde automatique dans le dossier `data/`
* Chargement des données au démarrage
* Si aucun fichier n'existe → création automatique

---

## 📁 Structure du projet

* `app` → lancement de l'application
* `menu` → gestion de l'interface console
* `stage` → logique métier des stages, sessions, activités, prix
* `person` → gestion des participants
* `registration` → gestion des réservations
* `io` → gestion des fichiers (sauvegarde / chargement)
* `util` → utilitaires (dates, console, etc.)
* `exception` → exceptions personnalisées

---

## 🧠 Choix techniques

* Utilisation de la POO
* Utilisation de `List` pour conserver l'ordre et simplifier l'affichage
* Gestion des erreurs via exceptions personnalisées
* Formatage console pour une meilleure lisibilité

---

## ⚠️ Remarques

* Aucun fichier de données n'est fourni
* L'application fonctionne directement après lancement
* Les données sont sauvegardées automatiquement

---

## 👨‍💻 Auteur

Thomas Dumez – Projet réalisé dans le cadre de la formation Développeur Java – Technifutur
