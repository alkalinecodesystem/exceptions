# INSTALL
## Install docker
## Run mysql-db container
sudo docker run -d -p 33060:3306 --name mysql-db -e MYSQL_ROOT_PASSWORD=secret mysql
## Create database
sudo docker exec -it mysql-db mysql -p <br />
create database demo; <br />
### start stop container
sudo docker start mysql-db <br />
sudo docker stop mysql-db <br />
## Run the application
mvn spring-boot:run <br />
### Create document_type row
sudo docker exec -it mysql-db mysql -p <br />
show databases; <br />
use demo; <br />
insert into document_type(id,name) values(1,'type'); <br />
## Test the endpoints importing collection to Postman
DocumentManagement.postman_collection.json
# exceptions
