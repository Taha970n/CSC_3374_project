# CSC_3374_project

## Overview
This project is a multi-client group chat application implemented using Java TCP sockets and Swing for the graphical user interface. The system allows multiple users to connect to a central server and exchange messages in real time.

The server manages all connected clients and broadcasts messages to all participants.

---

## Features

- Multi-client chat system
- Real-time message broadcasting
- Graphical user interface (GUI)
- Online users list
- Join/leave notifications
- Colored system messages
- TCP socket communication
- Threaded client handling

---

## Technologies Used

- Java
- Java Swing (GUI)
- TCP/IP Sockets
- Multithreading
- Collections Framework

---

## System Architecture

The system follows a client-server architecture.

### Components

Server:
- ChatServer
- ClientHandler

Client:
- ChatClient (GUI)

The server listens on a TCP port and handles multiple clients simultaneously using threads.

---

## How to Run

### 1. Compile the project
### 2. Start the server

The server will start listening on port **1234**.

### 3. Start clients

Open new terminals and run:

Multiple clients can connect simultaneously.

---

## How It Works

1. The server starts and waits for connections.
2. A client connects using a TCP socket.
3. The client sends its username.
4. The server creates a `ClientHandler` thread for that user.
5. Messages sent by clients are broadcast to all connected users.
6. The server also maintains and sends an updated list of active users.

---

---

## Future Improvements

- Private messaging
- Message timestamps
- User avatars
- Message history
- Improved GUI styling
- Encryption for secure communication
