# Real-Time Private Messaging MVP

A full-stack web application for real-time one-to-one messaging. Users connect with a username, select another online user, and exchange private messages across separate browser windows or devices.

## Features

- Real-time private messaging using native WebSocket
- Server-controlled sender identity
- Online user session management
- Message validation and length limits
- PostgreSQL message persistence
- Conversation history retrieval through REST
- Docker Compose setup for local development

## Technology Stack

- Frontend: Vue 3 and TypeScript
- Backend: Java 21 and Spring Boot
- Real-time communication: Native WebSocket
- Database: PostgreSQL with Spring Data JPA
- Deployment: Docker and Docker Compose

## Architecture

```text
Vue frontend
  |-- WebSocket --> Spring Boot --> Session registry --> Recipient
  |-- REST ------> Spring Boot --> PostgreSQL
```

WebSocket handles new real-time messages, while REST retrieves existing conversation history. PostgreSQL stores accepted messages so they remain available after a browser refresh.

## Running the Application

### Prerequisites

- Docker Desktop
- Docker Compose

From the project root, run:

```bash
docker compose up --build
```

Then open:

- Frontend: http://localhost:5173
- Backend: http://localhost:8080

To stop the application:

```bash
docker compose down
```

To also remove the local database volume:

```bash
docker compose down -v
```

## Testing the Messaging Flow

1. Open the frontend in two browser windows.
2. Sign in as `alice` in the first window and `bob` in the second.
3. Select the other user in each window.
4. Send messages in both directions.
5. Refresh a window and reopen the conversation to confirm the persisted history is loaded.
