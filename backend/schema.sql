-- ============================================
-- Projet 6 : Gestion des réservations d'hôtel
-- Schéma MySQL
-- ============================================

CREATE DATABASE IF NOT EXISTS hotel_reservation
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hotel_reservation;

-- ------------------------------------------------
-- Table SOLDE
-- ------------------------------------------------
CREATE TABLE IF NOT EXISTS solde (
  id INT PRIMARY KEY AUTO_INCREMENT,
  solde_actuel INT NOT NULL DEFAULT 0
);

-- On initialise une ligne unique de solde
INSERT INTO solde (solde_actuel)
SELECT 0 WHERE NOT EXISTS (SELECT 1 FROM solde);

-- ------------------------------------------------
-- Table CHAMBRE
-- ------------------------------------------------
CREATE TABLE IF NOT EXISTS chambre (
  num_chambre VARCHAR(20) PRIMARY KEY,
  design VARCHAR(100) NOT NULL,
  type VARCHAR(50) NOT NULL,
  prix_nuite INT NOT NULL
);

-- ------------------------------------------------
-- Table RESERVER
-- ------------------------------------------------
CREATE TABLE IF NOT EXISTS reserver (
  id_reserv INT PRIMARY KEY AUTO_INCREMENT,
  num_chambre VARCHAR(20) NOT NULL,
  date_reserv DATE NOT NULL,
  date_entree DATE NOT NULL,
  nbr_jour INT NOT NULL,
  nom_client VARCHAR(150) NOT NULL,
  mail VARCHAR(150) NOT NULL,
  statut VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, ANNULEE, OCCUPEE
  FOREIGN KEY (num_chambre) REFERENCES chambre(num_chambre)
);

-- ------------------------------------------------
-- Table OCCUPER (arrivée d'un client ayant réservé)
-- ------------------------------------------------
CREATE TABLE IF NOT EXISTS occuper (
  id_occup INT PRIMARY KEY AUTO_INCREMENT,
  id_reserv INT NOT NULL,
  date_occup DATE NOT NULL DEFAULT (CURRENT_DATE),
  FOREIGN KEY (id_reserv) REFERENCES reserver(id_reserv)
);

-- ------------------------------------------------
-- Table SEJOURNER (client sans réservation préalable)
-- ------------------------------------------------
CREATE TABLE IF NOT EXISTS sejourner (
  id_sejour INT PRIMARY KEY AUTO_INCREMENT,
  num_chambre VARCHAR(20) NOT NULL,
  date_entree_sejour DATE NOT NULL,
  nbr_jour INT NOT NULL,
  nom_client VARCHAR(150) NOT NULL,
  telephone VARCHAR(30) NOT NULL,
  FOREIGN KEY (num_chambre) REFERENCES chambre(num_chambre)
);

-- ------------------------------------------------
-- Quelques données de test
-- ------------------------------------------------
INSERT INTO chambre (num_chambre, design, type, prix_nuite) VALUES
 ('101', 'Chambre 101', 'Simple', 30000),
 ('102', 'Chambre 102', 'Double', 45000),
 ('103', 'Chambre 103', 'Suite', 80000)
ON DUPLICATE KEY UPDATE num_chambre = num_chambre;
