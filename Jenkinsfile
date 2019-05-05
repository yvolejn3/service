pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh "chmod a+x gradlew"
                sh "./gradlew --no-daemon jib"
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
                sh "docker pull 192.168.1.103:5000/service:latest"
            }
        }
    }
}