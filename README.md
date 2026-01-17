# TicTacToe

A real-time multiplayer Tic Tac Toe game built with Spring Boot and React, featuring WebSocket communication for instant game updates.

## Tech Stack

### Backend
- **Java 21** (LTS) with virtual threads
- **Spring Boot 3.5.9**
- **WebSocket** with STOMP protocol
- **Spring Boot Actuator** for observability
- **Lombok** for boilerplate reduction
- **Guava** for caching

### Frontend
- **React 19** with React Router v7
- **Vite 6** for fast builds and HMR
- **Bootstrap 5.3** for styling
- **STOMP.js** with SockJS for WebSocket
- **Joi** for form validation

## Prerequisites

- Java 21 or higher
- Maven 3.8+
- Node.js 20+ (auto-installed by Maven)

## Quick Start

### Build and Run

```bash
# Clone the repository
git clone <repository-url>
cd TicTacToe

# Build the project (includes frontend)
mvn clean package

# Run the application
java -jar target/TicTacToe-0.0.1-SNAPSHOT.jar
```

The application will be available at `http://localhost:80`

### Development Mode

For frontend development with hot reload:

```bash
# Terminal 1: Start the backend
mvn spring-boot:run

# Terminal 2: Start the frontend dev server
cd tic-tac-toe-ui
pnpm install
pnpm dev
```

Frontend dev server runs at `http://localhost:3000` with proxy to backend.

## Project Structure

```
TicTacToe/
├── src/main/java/com/vish/game/tictactoe/
│   ├── config/          # Spring configuration
│   ├── controller/      # WebSocket controllers
│   ├── handler/         # WebSocket handlers
│   ├── metrics/         # Observability metrics
│   ├── model/           # DTOs and domain models
│   ├── service/         # Business logic
│   └── util/            # Utility classes
├── src/main/resources/
│   ├── application.yml  # Application configuration
│   └── static/          # Built frontend (generated)
├── tic-tac-toe-ui/      # React frontend
│   ├── src/
│   │   ├── components/  # React components
│   │   ├── services/    # WebSocket service
│   │   └── styles/      # CSS files
│   ├── vite.config.js   # Vite configuration
│   └── package.json
└── pom.xml              # Maven configuration
```

## How It Works

1. **User Registration**: Players enter their name and join the waiting pool
2. **Matchmaking**: When two players are in the pool, a game automatically starts
3. **Gameplay**: Players take turns clicking on the 3x3 grid
4. **Real-time Updates**: All moves are instantly broadcast to both players via WebSocket
5. **Win Detection**: The game automatically detects wins and draws

## WebSocket Endpoints

| Endpoint | Direction | Purpose |
|----------|-----------|---------|
| `/ws` | Connect | WebSocket connection endpoint |
| `/app/register` | Client -> Server | Register user for matchmaking |
| `/app/step` | Client -> Server | Send game move |
| `/user/topic/game-start` | Server -> Client | Game initialization |
| `/user/topic/game-ws` | Server -> Client | Game state updates |

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/status` | GET | Application health status |
| `/actuator/health` | GET | Spring Boot health check |
| `/actuator/metrics` | GET | Application metrics |
| `/actuator/prometheus` | GET | Prometheus metrics |

## Configuration

Key configuration in `application.yml`:

```yaml
server:
  port: 80

spring:
  threads:
    virtual:
      enabled: true  # Java 21 virtual threads

websocket:
  game-start-topic: /topic/game-start
  game-step-topic: /topic/game-ws
```

## Environment Variables

Frontend environment variables (in `.env` files):

| Variable | Description |
|----------|-------------|
| `VITE_WS_PATH` | WebSocket endpoint path |
| `VITE_API_URL` | Backend API URL (dev only) |
| `VITE_SUBSCRIPTION_TOPIC` | Game update subscription topic |
| `VITE_GAME_START_TOPIC` | Game start subscription topic |

## Available Scripts

### Maven

```bash
mvn clean package      # Build everything
mvn test               # Run tests
mvn spring-boot:run    # Run in development
mvn pmd:check          # Run static analysis
```

### Frontend (pnpm)

```bash
pnpm dev       # Start dev server with HMR
pnpm build     # Production build
pnpm preview   # Preview production build
pnpm test      # Run tests with Vitest
```

## Architecture

```
┌─────────────────┐         WebSocket         ┌─────────────────┐
│                 │<------------------------->│                 │
│  React Client   │         STOMP             │  Spring Boot    │
│                 │                           │                 │
│  - GameBoard    │    /app/register          │  - GameService  │
│  - WebSocket    │-------------------------->│  - UserService  │
│    Service      │    /app/step              │  - BroadCast    │
│                 │-------------------------->│    Service      │
│                 │                           │                 │
│                 │<--------------------------│  - GameBoard    │
│                 │    /user/topic/game-*     │  - UserCache    │
└─────────────────┘                           └─────────────────┘
```

## Documentation

- [CONTRIBUTING.md](CONTRIBUTING.md) - Contribution guidelines
- [CLAUDE.md](CLAUDE.md) - AI assistant context
- [BACKEND_ANALYSIS.md](BACKEND_ANALYSIS.md) - Backend code review
- [FRONTEND_ANALYSIS.md](FRONTEND_ANALYSIS.md) - Frontend code review
- [UPGRADE_COMPARISON.md](UPGRADE_COMPARISON.md) - Java/Spring upgrade notes

## License

This project is for educational purposes.
