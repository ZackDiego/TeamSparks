
<div align="center">
  <div style="display: flex; align-items: center;">
      <img src="https://github.com/ZackDiego/TeamSparks/assets/96913989/05a3b779-6afb-49fc-9ae9-38352e9cec12" alt="teamSparksIcon" width="80" height="80">
      <h1>TeamSparks</h1>
  </div>

  ![Java](https://img.shields.io/badge/Java-17-orange)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen)
  ![Socket.IO](https://img.shields.io/badge/Socket.IO-2.2.0-yellow)
</div>

## About 
TeamSparks enables seamless collaboration similar to Discord and Slack, offering effortless workspace
creation, channel management, and member connectivity through messages and video. Utilizing WebRTC, alongside WebSocket and Pub/Sub mechanisms for real-time communication, ensures smooth and efficient collaboration.

> ⚠️ Notice: 
> Due to cost consideration, the TeamSparks app is currently offline.

## Tech Stack
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E) ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white) ![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white) ![Socket.io](https://img.shields.io/badge/Socket.io-black?style=for-the-badge&logo=socket.io&badgeColor=010101) ![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white) ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white) ![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white) ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens) ![Bootstrap](https://img.shields.io/badge/bootstrap-%23563D7C.svg?style=for-the-badge&logo=bootstrap&logoColor=white) ![jQuery](https://img.shields.io/badge/jquery-%230769AD.svg?style=for-the-badge&logo=jquery&logoColor=white) ![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white) ![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)

TeamSparks has migrated from Elasticsearch to MongoDB in its latest version, due to cost considerations.

## Test Accounts
| User | Email               | Password |
|----------|-------------------------|--------------|
| Alice    | alice@gmail.com    | alice12345   |
| Zack      | zack@gmail.com      | zack12345     |

## Features
- **Workspace Management**:  
  Easily create and manage workspaces to guarantee creator authorities.
- **Channel and Private Chats**:  
  Group members by channels or have private chats with individual members.
- **User Notifications**:  
  Receive notifications when away from the message channel.
- **Message Search**:  
  Search messages with multiple conditions to quickly find the matching message.
- **Video Call Meetings**:  
  Host private or group video call meetings to express your thoughts without distance.


## Demo
### Message Notification
<p align="center">
  <img src="https://github.com/ZackDiego/TeamSparks/assets/96913989/ea34faea-957a-433b-aebb-7984e50a5d2d" alt="notification" width="100%"/>
</p>

### Message search
<p align="center">
  <img src="https://github.com/ZackDiego/TeamSparks/assets/96913989/e7709699-5b15-4471-ae79-abb11099c3bf" alt="search" width="100%"/>
</p>

### Video Call
<p align="center">
  <img src="https://github.com/ZackDiego/TeamSparks/assets/96913989/7c6776b7-862e-402b-bca1-f378bdb5f9dc" alt="videoCall" width="100%"/>
</p>

## Architecture
![image](https://github.com/user-attachments/assets/acdbc912-f575-4a25-a9f0-7efaff8955aa)

## Database Schema

![截圖 2024-05-31 上午11 05 05](https://github.com/ZackDiego/TeamSparks/assets/96913989/433350a1-1554-49ed-ac8e-64da29acc52c)


## Applied Technologies
- Used **Java Spring Boot** framework with Maven for the back-end, and HTML, CSS, JavaScript, jQuery, and Bootstrap for the front-end.
- Implemented **WebRTC**-based video and audio streaming, establishing WebRTC peer connections in **mesh** architecture to facilitate group video calls.
- In the WebRTC framework, used **Socket.IO** for Java to construct **Signaling Server**, and implemented **Coturn** to build a **Turn Server** for optimizing video quality and ensuring smooth transmission.
- Implemented **WebSocket** and **Pub/Sub mechanism** to enable real-time communication in channels and private chats via topic subscription.
- To scale out TeamSparks service, integrated **AWS ALB** and **Redis** as a **message broker** to enable chat message exchange across different EC2 instances. 
- Use MongoDB Atlas, providing robust message search functionality. 
- Implemented **RBAC** and **JWT** for managing user permissions.
- Automated deployment with **GitHub Actions**.


## Contact
**Email:** mrzackchiang@gmail.com   
**LinkedIn:** https://www.linkedin.com/in/zackchiang/
