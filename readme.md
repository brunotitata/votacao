# Execução do Projeto

    Este projeto está usando gradle como construtor e gerenciador de dependências. Na pasta raiz do projeto execute:

    ./gradlew clean build
    ./gradlew composeUp && docker container logs -f votacao-service

# Swagger

    Link: http://localhost:8080/swagger-ui.html

## Endpoint para cadastrar um associado

Request:

	POST http://localhost:8080/v1/associado

Body:

```json
{
  "nome": "Nome Teste",
  "cpf": "87508335023"
}
```

Response: 201 (Created) Com id do recurso criado no atributo Location do header da resposta:

	ex: Location → http://localhost:8080/v1/associado/784ef9e0-8f06-4a35-a7f1-30d42ee65c80 

## Endpoint para cadastrar uma nova pauta

Request:

	POST http://localhost:8080/v1/pauta

Body:

```json
{
  "titulo": "Você é a favor da taxação dos dividendos?"
}
```

Response: 201 (Created) Com id do recurso criado no atributo Location do header da resposta:

	ex: Location → http://localhost:8080/v1/pauta/a5715fc0-8be7-4f49-8a71-57b5e7c570dc

## Endpoint para cadastrar uma nova sessão para uma pauta

Request:

	POST http://localhost:8080/v1/sessao

Body:

```json
{
  "pautaId": "a5715fc0-8be7-4f49-8a71-57b5e7c570dc",
  "duracaoEmMinutos": 5
}
```

Response: 201 (Created) Com id do recurso criado no atributo Location do header da resposta:

	ex: Location → http://localhost:8080/v1/sessao/972b5af9-411a-43db-b746-6f8f14180be7

## Endpoint para realizar o voto de uma sessão aberto

Request:

	POST http://localhost:8080/v1/pauta/{pautaId}/associado/{associadoId}/votar

Body:

```json
{
  "voto": "NAO"
}
```

Response: 201 (Created) Com id do recurso criado no atributo Location do header da resposta:

	ex: Location → http://localhost:8080/v1/pauta/a5715fc0-8be7-4f49-8a71-57b5e7c570dc/associado/784ef9e0-8f06-4a35-a7f1-30d42ee65c80/votar/f072bd16-bcee-4b73-9a8a-3d6c6075ba03

## Endpoint para visualizar o resultado de uma sessão encerrado

Request:

	GET http://localhost:8080/v1/sessao/{sessaoId}/resultado

body:

```json
{
  "pauta": "Você é a favor da taxação dos dividendos?",
  "sim": 0,
  "nao": 1,
  "total": 1
}
```