pipelineJob('CodeGenerator') {
    definition {
        cps {
            script('''
            pipeline {
    agent any

    parameters {
        choice(name: 'TestType', choices: ['API Tests', 'Performance Tests', 'UI Tests'], description: 'Select the type of test')
        choice(name: 'ProgrammingLanguage', choices: ['Python', 'Java', 'JavaScript'], description: 'Select the programming language')
        string(name: 'SwaggerSchema', defaultValue: '', description: 'Enter the Swagger schema')
        text(name: 'VerboseInput', defaultValue: '', description: 'Enter verbose input for LLM')
    }

    stages {
        stage('Build') {
            steps {
                script {
                    def parametersJson = [
                        TestType: params.TestType,
                        ProgrammingLanguage: params.ProgrammingLanguage,
                        SwaggerSchema: params.SwaggerSchema,
                        VerboseInput: params.VerboseInput,
                        BuildNumber: env.BUILD_NUMBER
                    ]

                    // Convert the map to a JSON string
                    def parametersJsonString = new groovy.json.JsonBuilder(parametersJson).toPrettyString()

                    // Print the JSON string (optional, for debugging)
                    println("Parameters JSON: ${parametersJsonString}")

                    // Execute build script
                    sh "/opt/gpt-integration/venv/bin/python3 /opt/gpt-integration/build.py '${parametersJsonString}'"
                }
            }
        }
    }

    post {
        always {

            // Archive artifacts
            println("Archiving ${BUILD_NUMBER} build files")
            println("Files are available in ${WORKSPACE} folder")
            archiveArtifacts artifacts: "**/*${BUILD_NUMBER}.*", fingerprint: true
        }
    }
}''')
        }
    }
}

