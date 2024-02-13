import sys
import json
import os
import logging
from openai import OpenAI


_logger = logging.getLogger(__name__)

print("Started Execution....")

api_key = os.environ.get("OPENAI_API_KEY")
org_id = os.environ.get("OPENAI_ORG_ID")
max_tokens = int(os.environ.get('OPENAI_MAX_TOKENS'))
model = os.environ.get('OPENAI_MODEL', 'gpt-3.5-turbo')


build_params = json.loads(sys.argv[1])
_logger.info("build_params json", build_params)

build_number = build_params.get('BuildNumber')
test_type = build_params.get('TestType')
language = build_params.get('ProgrammingLanguage')
swagger_url = build_params.get('SwaggerSchema')
verbose_input = build_params.get('VerboseInput')

if not swagger_url and not verbose_input:
    print("Neither Swagger URL nor Prompt is given by User. Can't Proceed further")
    sys.exit(1)


def generate_gpt_response(swagger_endpoint=None, prompt=None):

    messages = [{"role": "system",
                 "content": " Respond with only the code without explanation and heading and footing."
                            " Never Respond with explanation. Response should always have only code."
                            " This is a strict warning to consider."}]

    if swagger_endpoint:
        messages.append({"role": "user",
                         "content":
                             f"Generate API tests in Python Pytest for APIs documented in swagger here {swagger_endpoint}"})
    if prompt:
        messages.append({"role": "user", "content": f"{prompt}"})

    try:
        client = OpenAI(api_key=api_key, organization=org_id)

        response = client.chat.completions.create(
            model=model,
            messages= messages,
            max_tokens=int(max_tokens),
        )
        print(f"Tokens Used for Generating Code: {response.usage.completion_tokens}")

        return response.choices[0].message.content
    except Exception as e:
        print(f'Exception Occurred On GPT API: {e}')
        return None


def create_file(extension, build_number):
    f = open(f"test_sample_code_{build_number}.{extension}", "w")
    f.close()
    return f.name


if language == 'Python':
    created_file_name = create_file(extension='py', build_number=build_number)
    print(f"created a file named {created_file_name}")
    print("About to Generate Code from GPT")
    generated_response = generate_gpt_response(swagger_url, verbose_input)
    if not generated_response:
        print("Could not generate code from GPT. Failed")
        sys.exit(1)
    with open(created_file_name, "w") as file:
        file.write(generated_response)

else:
    print("Currently we support only Python!!!")

print("Completed Execution!")