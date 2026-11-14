# TravelMate

Plateforme qui permet aux utilisateurs d'organiser leurs voyages depuis une seule application.

## Équipe
| Membre | Microservice |
|---|---|
| Islem Louati | User Service |
| Souha Khenissi | Destination Service |
| Ghofrane Hraghi| Flight Service |
| Nadine Eddouch | Hebergement Service |
| Abir Saddour | Transport Service |
| Rihab Solatani | Payment Service |

## Structure du dépôt
- `Backend/` : microservices, API Gateway, Eureka Server, Config Server
- `Frontend/` : application cliente
- `Documentation/` : présentation, diagrammes, dossier technique

## Architecture
Voir `Documentation/Diagrams/`

## Technologies
Spring Boot, Spring Cloud, RabbitMQ, Angular, Docker

## Workflow Git
- `main` : version stable
- `develop` : intégration
- `feature/<service>-<fonctionnalité>` : travail de chaque membre
