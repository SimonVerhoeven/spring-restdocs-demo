# Spring rest docs

* [About](#about)
* [Requirements](#requirements)
* [How to use](#how-to-use)
* [Setup](#setup)
  + [Pom](#pom)
  + [asciidoc](#asciidoc)
  + [Tests](#tests)
  + [Custom code snippets](#custom-code-snippets)
  
***

## About

This project is a sample implementation of [Spring REST Docs](https://spring.io/projects/spring-restdocs) to showcase how we can use our test cases to generate documentation for us and bundle it as part of our build. In this case, for a controller that allows us to fetch a list of books, fetch a book by ISBN-13 and add one. (the controller is very basic, and in memory since the focus is on the documentation aspect).

***

## Requirements

* java 17
* Spring framework 6
* REST Assured 5.2 if you're using `spring-restdocs-restassured`

***

## How to use

When you run `./mvnw package` the documentation will be generated, and can be opened at `target/generated-docs/index.html`

Since we're bundling the documentation (documented below), you can also check out the documentation [here](http://localhost:8080/docs/index.html) whilst running the application.

***

## Setup

### Pom
- the `spring-restdocs-mockmvc` dependency was added 
- the `asciidoctor-maven-plugin` was added which converts the asciidoctor documentation during our build
    - the `prepare-package` plugin has been added so our documentation is included in our package
    - `spring-restdocs-asciidoctor` was added so the snippets attribute in our .adoc file(s) automatically point to our generated snippets in target/generated-snippets, and so we can use the `operation` block macro
- the `maven-resources` plugin will package our documentation. Keep in mind this needs to be defined __after__ the `asciidoctor-maven-plugin` 

### asciidoc

These three asciidoc files will point to our generated documentation, in there you can find some examples of how you can set it up.

[index.adoc](src/main/asciidoc/index.adoc)  
[library.adoc](src/main/asciidoc/library.adoc)  
[member.adoc](src/main/asciidoc/member.adoc)

Within the `library` adoc file you can also find examples of how you can further customize the layout.

### Tests

The tests responsible for generating our documentation can be found within [BookControllerTest](src/test/java/dev/simonverhoeven/restdocsdemo/library/BookControllerTest.java) and [MemberControllerTest](src/test/java/dev/simonverhoeven/restdocsdemo/member/MemberControllerTest.java)  alongside the description of what we're documenting.

The tests themselves are basic mockmvc tests, to which we add a little bit of extra functionality so the documentation is generated.

In the setup of the `BookControllerTest` we're also enriching the default configuration so our documentation is richer (for example the default URL, the encoding, removing headers by default, pretty printing, ...).

### Custom code snippets

It is possible to customize the snippets. Given this setup was done using asciidoc, we need to put these in `src\test\resources\org\springframework\restdocs\templates\asciidoctor`

***

Notes

* The `ISBN` annotation does not have a localization by (see for reference [this section](https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/#documenting-your-api-constraints-describing), hence the need to create `src\test\resources\org\springframework\restdocs\constraints\ConstraintDescriptions.properties`
* keep in mind if you document the fields, you need to document them all or the build will fail
