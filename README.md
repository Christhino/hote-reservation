# Gestion des réservations de chambre d'un hôtel

Stack : **Javalin** (backend Java) + **MySQL** (base de données) + **ReactJS** (frontend, Vite).

## Structure du projet

```
hotel-reservation/
├── backend/     -> API Javalin (Java 17, Maven)
│   ├── schema.sql
│   ├── pom.xml
│   └── src/main/java/com/hotel/...
└── frontend/    -> Application React (Vite)
    └── src/...
```

## 1. Base de données MySQL

Créer la base et les tables :

```bash
mysql -u root -p < backend/schema.sql
```

Cela crée la base `hotel_reservation` avec les tables `solde`, `chambre`, `reserver`,
`occuper`, `sejourner`, et insère 3 chambres de test.

## 2. Backend (Javalin)

Prérequis : Java 17+, Maven.

Variables d'environnement (optionnelles, valeurs par défaut entre parenthèses) :

| Variable       | Rôle                                   |
|----------------|-----------------------------------------|
| DB_URL         | URL JDBC (`jdbc:mysql://localhost:3306/hotel_reservation`) |
| DB_USER        | Utilisateur MySQL (`root`)             |
| DB_PASSWORD    | Mot de passe MySQL (vide)              |
| SERVER_PORT    | Port de l'API (`7000`)                 |
| SMTP_HOST      | Serveur SMTP (`smtp.gmail.com`)        |
| SMTP_PORT      | Port SMTP (`587`)                      |
| SMTP_USER      | Adresse d'envoi des emails             |
| SMTP_PASSWORD  | Mot de passe / mot de passe d'application |

> Si `SMTP_USER` n'est pas défini, l'envoi d'email est simulé (log console) — pratique en développement.

Démarrage :

```bash
cd backend
mvn clean package
java -jar target/hotel-reservation.jar
```

L'API est disponible sur `http://localhost:7000/api`.

### Endpoints principaux

- `GET/POST/PUT/DELETE /api/chambres`
- `GET /api/chambres/disponibles?dateEntree=YYYY-MM-DD&nbrJour=N` (recherche de disponibilité)
- `GET/POST/PUT/DELETE /api/reservations`
- `PUT /api/reservations/{id}/annuler` (annulation → chambre libérée)
- `GET/POST/DELETE /api/occupations` (arrivée d'un client réservé → solde mis à jour)
- `GET/POST/PUT/DELETE /api/sejours`
- `GET /api/sejours/{id}/recu` (reçu PDF)
- `GET /api/solde`

## 3. Frontend (React)

Prérequis : Node.js 18+.

```bash
cd frontend
npm install
npm run dev
```

L'application est disponible sur `http://localhost:5173`. Le proxy Vite redirige
automatiquement les appels `/api/*` vers le backend Javalin (port 7000).

## Fonctionnalités couvertes

- CRUD complet sur les 4 tables (chambre, réservation, occupation, séjour) (10 pts)
- Envoi automatique d'un email de confirmation après réservation (3 pts)
- Contrôle : une chambre réservée sur une période ne peut plus être prise par un autre client (2 pts)
- Recherche d'une chambre libre à une date donnée (2 pts)
- Génération d'un reçu PDF pour un séjour (3 pts)
