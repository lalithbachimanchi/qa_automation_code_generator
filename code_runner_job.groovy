pipelineJob('CodeRunner') {
    definition {
        cps {
            script('''
                pipeline {
                    agent any

                    parameters {
                        choice(name: 'ProgrammingLanguage', choices: ['Python', 'Java', 'JavaScript'], description: 'Select the programming language')
                        string(name: 'CodeGeneratorBuildNumber', defaultValue: '', description: 'Enter the Build Number of Jenkins Job which generated the code')
                        password(name: 'AUTH_TOKEN', defaultValue: '', description: 'Enter your Auth token')
                        password(name: 'FSM_CONTEXT', defaultValue: '', description: 'Enter your FSM Context')
                    }

                    environment {

                     AUTH_TOKEN = "${params.AUTH_TOKEN}"
                     FSM_CONTEXT = "${params.FSM_CONTEXT}"

                    }

                    stages {
                        stage('Build') {
                            steps {
                                script {

                                    sh "env"

                                    copyArtifacts filter: '*.*', fingerprintArtifacts: true, includeBuildNumberInTargetPath: true, projectName: 'CodeGenerator', selector: specific('$CodeGeneratorBuildNumber'), target: '/opt/jenkins_code_generator_artifacts/'

                                    // Execute build script
                                    sh "/opt/gpt-integration/venv/bin/python3 -m pytest --json-report --json-report-file $WORKSPACE/$BUILD_NUMBER/result.json --junit-xml=$WORKSPACE/$BUILD_NUMBER/result.xml --html=$WORKSPACE/$BUILD_NUMBER/result.html /opt/jenkins_code_generator_artifacts/$CodeGeneratorBuildNumber -v"
                                }
                            }
                        }
                    }

                    post {
                        always {
                            // Publish JUnit test result report
                            junit "${BUILD_NUMBER}/*.xml"

                            // Publish HTML report
                            publishHTML(target: [allowMissing: false,
                                                  alwaysLinkToLastBuild: false,
                                                  keepAll: false,
                                                  reportDir: "$BUILD_NUMBER/",
                                                  reportFiles: 'result.html',
                                                  reportName: 'HTML Reports',
                                                  reportTitles: ''])

                            // Archive artifacts
                            println("Archiving ${BUILD_NUMBER} build files")
                            println("Files are available in ${WORKSPACE} folder")
                            archiveArtifacts artifacts: "**/*${BUILD_NUMBER}.*", fingerprint: true
                        }
                    }
                }
            ''')
        }
    }
}
