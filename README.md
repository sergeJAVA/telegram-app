# 🛍️ Telegram Shop Backend
 
> Open-source backend for a Telegram Mini App clothing store — free to use, fork, and build upon.
 
## About
 
This is a monolithic Spring Boot backend powering a clothing store Telegram Mini App. It handles everything from user auth to order management, built to be straightforward to deploy and easy to extend.
 
Anyone can use this project — personal, commercial, whatever. Just take it and build something cool.
 
## Features
 
- 🔐 **Auth** — user registration and login with JWT
- 🔍 **Product Search** — search and browse clothing items
- 🛒 **Cart** — create a cart, add and remove items
- 📦 **Orders** — place orders, cancel them, update order status
- ➕ **Product Management** — create and manage products (admin)
 
## Tech Stack
 
- **Java 25** + **Spring Boot 4**
- **PostgreSQL** — main database
- **Redis** — caching
- **Flyway** — database migrations
- **JWT** — authentication
- **Docker** + **Docker Compose**
 
## Getting Started
 
### Prerequisites
 
- Docker & Docker Compose
- Telegram Bot Token ([get one via @BotFather](https://t.me/BotFather))
 
### Setup
 
1. Clone the repo
 
```bash
git clone https://github.com/sergeJAVA/telegram-app.git
cd telegram-app
```
 
2. Create a `.env` file in the root directory
 
```env
DB_USERNAME=postgres
DB_PASSWORD=your_db_password
REDIS_PASSWORD=your_redis_password
REDIS_PORT=6379
BOT_TOKEN=your_telegram_bot_token
ADMIN_ID=your_telegram_user_id
```
 
3. Build and run
 
```bash
docker compose up -d --build
```
 
The app will be available at `http://localhost:8080`.
 
## Environment Variables
 
| Variable         | Description                              | Default |
|-----------------|------------------------------------------|---------|
| `DB_USERNAME`   | PostgreSQL username                      | —       |
| `DB_PASSWORD`   | PostgreSQL password                      | —       |
| `REDIS_PASSWORD`| Redis password                           | —       |
| `REDIS_PORT`    | Redis port                               | `6379`  |
| `BOT_TOKEN`     | Telegram bot token                       | —       |
| `ADMIN_ID`      | Telegram user ID with admin privileges   | `-1`    |
 
## License
 
MIT — do whatever you want with it.
