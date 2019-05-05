pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh "chmod a+x gradlew"
                sh "./gradlew jib"
                sh "./gradlew --stop"
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
                sh "docker pull localhost:5000/service"
                sh "docker stop service"
                sh "docker rm service"
                sh "docker run --name=service --restart=always -p 8090:8090 -d localhost:5000/service
            }
        }
    }
}