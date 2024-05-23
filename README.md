
<div align="center">
  <div style="display: flex; align-items: center;">
      <img src="https://github.com/ZackDiego/TeamSparks/assets/96913989/05a3b779-6afb-49fc-9ae9-38352e9cec12" alt="teamSparksIcon" width="80" height="80">
      <h1>TeamSparks</h1>
  </div>

  ![Java](https://img.shields.io/badge/Java-17-orange)
  ![Elasticsearch](https://img.shields.io/badge/Elasticsearch-7.10.1-blue)
  ![Socket.IO](https://img.shields.io/badge/Socket.IO-2.2.0-brightgreen)
</div>

## About 
TeamSparks enables seamless collaboration similar to Discord and Slack, offering effortless workspace
creation, channel management, and member connectivity through messages and video.   

## Tech Stack
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E) ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white) ![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white) ![ElasticSearch](https://img.shields.io/badge/-ElasticSearch-005571?style=for-the-badge&logo=elasticsearch) ![Socket.io](https://img.shields.io/badge/Socket.io-black?style=for-the-badge&logo=socket.io&badgeColor=010101) ![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white) ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white) ![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white) 


## Test Accounts
| User | Email               | Password |
|----------|-------------------------|--------------|
| Alice    | alice12345@gmail.com    | alice12345   |
| Bob      | bob12345@gmail.com      | bob12345     |

## Features
- **Workspace Management**: Easily create and manage workspaces with RBAC to guarantee creator authorities.
- **Channel and Private Chats**: Group members by channels or have private chats with individual members.
- **User Notifications**: Receive notifications when away from the message channel.
- **Message Search**: Search messages with multiple conditions to quickly find the matching message.
- **Video Call Meetings**: Host private or group video call meetings to express your thoughts without distance.


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
![圖片](https://github.com/ZackDiego/TeamSparks/assets/96913989/ed69cbe5-2c8e-4c81-aa7f-124f43ac7352)

## Database Schema

![image-2](https://github.com/ZackDiego/TeamSparks/assets/96913989/df15f3c8-b163-42c8-a649-08bfa75d1516)

## Applied Technologies
- Used **Java Spring Boot** framework with Maven for the back-end, and HTML, CSS, JavaScript, jQuery, and Bootstrap for the front-end.
- Implemented **WebRTC**-based video and audio streaming, establishing WebRTC peer connections in **mesh** architecture to facilitate group video calls.
- In the WebRTC framework, used **Socket.IO** for Java to construct **Signaling Server**, and implemented **Coturn** to build a **Turn Server** for optimizing video quality and ensuring smooth transmission.
- Implemented **WebSocket** and **Pub/Sub mechanism** to enable real-time communication in channels and private chats via topic subscription.
- To scale out TeamSparks service, integrated **AWS ALB** and **Redis** as a **message broker** to enable chat message exchange across different EC2 instances. 
- Ran **Elasticsearch** in **Docker**, providing robust message search functionality. 
- Implemented **RBAC** and **JWT** for managing user permissions.
- Automated deployment with **GitHub Actions**.


## Contact
**Email:** mrzackchiang@gmail.com   
**LinkedIn:** https://www.linkedin.com/in/zackchiang/
