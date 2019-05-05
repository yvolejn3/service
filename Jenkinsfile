pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                chmod a+x gradlew
                ./gradlew --no-daemon jib
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
            }
        }
    }
}