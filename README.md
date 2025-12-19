# RAG-Multi-Chatbot
# Spring AI Example

This is a Spring Boot web application that uses [Spring AI](https://docs.spring.io/spring-ai/reference/) to talk with OpenAi models. It utilizes [retrieval augmented generation](https://ai.meta.com/blog/retrieval-augmented-generation-streamlining-the-creation-of-intelligent-natural-language-processing-models/) to answer questions about the [Iowa Hawkeyes football team for the 2023-24 season](https://hawkeyesports.com/sports/football/cumestats/season/2023-24/).



## Setup

* Create an [OpenAI](https://openai.com/) account and [get an API key](https://platform.openai.com/api-keys).
* Set up [billing settings](https://platform.openai.com/account/billing/overview) for your account with a small spending limit.
* Create an environment variable named `SPRING_AI_OPENAI_API_KEY` that is set to your OpenAI API key, or populate the key value in ['application.yaml`](/src/main/resources/application.yaml).
* Install Java 17 or later.


## Populating Vector Storage
* Using the Pinecone



## Tech Stack

* Spring Boot 3.3
* React
* Spring Boot Starter Web
* Spring AI
  * With Spring Boot Spring AI OpenAI Starter  
* Java 17
* Lombok
* Maven
