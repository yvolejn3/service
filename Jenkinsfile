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
        stage('Deploy') {
            steps {
                echo 'Deploying....'
                sh "docker pull localhost:5000/service:latest"
            }
        }
    }
}