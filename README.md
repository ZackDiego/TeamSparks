
<div align="center">
  <div style="display: flex; align-items: center;">
      <img src="https://github.com/ZackDiego/TeamSparks/assets/96913989/05a3b779-6afb-49fc-9ae9-38352e9cec12" alt="teamSparksIcon" width="50" height="50">
      <h1 style="margin-left: 10px;">TeamSparks</h1>
  </div>
  <p align="center">
    <a href="https://github.com/ZackDiego/TeamSparks#about">About</a>
    |
    <a href="https://github.com/ZackDiego/TeamSparks#about">Features</a>
    |
    <a href="https://github.com/ZackDiego/TeamSparks#contact">Demo</a>
    |
    <a href="https://github.com/ZackDiego/TeamSparks#contact">Contact</a>
  </p>
</div>

## About
TeamSparks enables seamless collaboration similar to Discord and Slack, offering effortless workspace
creation, channel management, and member connectivity through messages and video.   

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

## Applied Technologies
- Used **Java Spring Boot** framework with Maven for the back-end, and HTML, CSS, JavaScript, jQuery, and Bootstrap for the front-end.
- Implemented **WebRTC**-based video and audio streaming, establishing WebRTC peer connections in **mesh** architecture to facilitate group video calls.
- In the WebRTC framework, used **Socket.IO** for Java to construct **Signaling Server**, and implemented **Coturn** to build a **Turn Server** for optimizing video quality and ensuring smooth transmission.
- Implemented **WebSocket** and **Pub/Sub mechanism** to enable real-time communication in channels and private chats via topic subscription.
- To scale out TeamSparks service, integrated **AWS ALB** and **Redis** as a **message broker** to enable chat message exchange across different EC2 instances. 
- Ran **Elasticsearch** in **Docker**, providing robust message search functionality. 
- Implemented **RBAC** and **JWT** for managing user permissions.
- Automated deployment with **GitHub Actions**.

## Database Schema
![圖片](https://github.com/ZackDiego/TeamSparks/assets/96913989/94d4434d-dda6-4a62-826c-d7b657777705)


## Architecture
![圖片](https://github.com/ZackDiego/TeamSparks/assets/96913989/ed69cbe5-2c8e-4c81-aa7f-124f43ac7352)



## Demo
### Workspace Page
![圖片](https://github.com/ZackDiego/TeamSparks/assets/96913989/6bf78687-fabb-4e95-abb2-6029ec729744)


## Maintenance
Unit tests and Integration tests
Implemented unit and integration tests to validate code accuracy after refactor, and use GitHub Actions for continuous deployment, reducing the potential for human errors and time consumption.


## Tools
- Cloud Service AWS: EC2, ElastiCache, RDS, S3, CloudFront, ALB
- Programming Language: Java, JavaScript, Html, Css  
- Databases: MySQL, ElasticSearch  
- Real-time Communication: Socket.IO   
- Others: GitHub Actions for Auto Deployment, Redis as Message broker, Docker


## Contact
**Email:** mrzackchiang@gmail.com   
**LinkedIn:** https://www.linkedin.com/in/zackchiang/
