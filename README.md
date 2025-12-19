# RAG-Multi-Chatbot

This is a Spring Boot web application that uses [Spring AI](https://docs.spring.io/spring-ai/reference/) to talk with OpenAi models.



## Setup

* Create an [OpenAI](https://openai.com/) account and [get an API key](https://platform.openai.com/api-keys).
* Set up [billing settings](https://platform.openai.com/account/billing/overview) for your account with a small spending limit.
* Create an environment variable named `SPRING_AI_OPENAI_API_KEY` that is set to your OpenAI API key, or populate the key value in ['application.yaml`](/src/main/resources/application.yaml).
* Vector database PINECONE_API_KEY: https://app.pinecone.io/organizations/-OJwxEukHa34t1GE1aJm/projects/7f07c874-fd40-4fe9-8c81-1d911b5c1b74/keys
* Install Java 17 or later.


## Populating Vector Storage
* Pinecone



## Tech Stack

* Spring Boot 3.3
* React
* Spring Boot Starter Web
* Spring AI
  * With Spring Boot Spring AI OpenAI Starter  
* Java 17
* Lombok
* Maven
