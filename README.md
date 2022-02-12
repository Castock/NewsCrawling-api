# NewsCrawling-api

## 주식 뉴스 크롤링 API

### docker
(image build) docker build -t demo:0.0.1-SNAPSHOT .(/)

(container run) docker run -it -d -p 8880:8080 demo:0.0.1-SNAPSHOT --name ha

### mysql:8.0.17

docker start mysql-container

docker exec -it mysql-container bash

#/ mysql -u root -p
