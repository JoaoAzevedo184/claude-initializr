# CLAUDE.md

## Visão geral

**claude-initializr** é um gerador web da estrutura `.claude/` de projetos, nos moldes do
`start.spring.io`. O usuário escolhe stack e componentes num formulário e baixa um `.zip`
com `CLAUDE.md`, `.mcp.json` e a árvore `.claude/` (rules, commands, skills, agents, hooks)
já preenchidos para aquela stack.

**v1: apenas projetos Java/Spring Boot com Maven.** Outras stacks vêm depois — não
generalize código antes de existir uma segunda stack real.

O valor do produto está no **conteúdo dos templates**, não no código que monta o zip.
Ao mexer nos templates, priorize regras específicas e acionáveis em vez de conselhos genéricos.

## Stack

- Java 21, Spring Boot 4.1.1, Maven
- Thymeleaf (UI) + htmx (interatividade) — **não** há front-end separado
- JMustache para renderizar os arquivos gerados
- Apache Commons Compress para montar o zip
- Sem banco de dados. Sem autenticação. Toda a metadata vive em `application.yml`.

## Comandos

```bash
./mvnw spring-boot:run        # sobe em http://localhost:8080
./mvnw test                   # todos os testes
./mvnw test -Dtest=NomeDoTest # um teste só
./mvnw clean package          # gera o jar
```

## Estrutura

```
src/main/java/io/github/joaoazevedo184/claudeinitializr/
├── web/          # controllers: formulário (Thymeleaf) e API REST
├── metadata/     # opções do formulário, lidas do application.yml
├── template/     # renderização dos templates Mustache
├── generator/    # montagem da árvore de arquivos e do zip
└── config/       # @ConfigurationProperties e beans

src/main/resources/
├── templates/          # views Thymeleaf (a UI do site)
├── claude-templates/   # templates Mustache dos ARQUIVOS GERADOS
└── application.yml
```

`templates/` e `claude-templates/` são coisas diferentes e é fácil confundir: a primeira é o
site, a segunda é o que o usuário baixa.

## Endpoints

- `GET /` — formulário
- `GET /api/metadata` — opções disponíveis (versões, dependências, componentes)
- `POST /api/generate` — recebe as escolhas, devolve `application/zip`
- `POST /api/preview` — mesma geração, devolve a árvore em JSON (botão "Explore")

`generate` e `preview` compartilham a mesma geração; só o formato de saída muda.

## Convenções de código

- Injeção por construtor. Nunca `@Autowired` em campo.
- DTOs de request/response são `record`, com Bean Validation nos campos.
- Exceções de domínio são checked-free e tratadas num `@RestControllerAdvice` único.
- Nomes de classe em inglês; comentários e templates em português.
- Testes: `@WebMvcTest` para controllers, teste unitário puro para `generator` e `template`.
  `@SpringBootTest` só no smoke test de contexto.

## Regras do domínio

**Zip e permissões.** `java.util.zip.ZipOutputStream` não grava modo Unix, então hooks
saem sem bit de execução e falham em silêncio. Use `ZipArchiveOutputStream` do Commons
Compress e chame `setUnixMode(0755)` em todo arquivo `.sh`, `0644` no resto.

**Geração em memória.** Nada de arquivo temporário em disco. O zip é montado num
`ByteArrayOutputStream` e devolvido no response.

**Entrada é hostil.** `group`, `artifact` e `packageName` vêm do usuário e viram caminho
de arquivo dentro do zip. Valide contra um regex estrito antes de usar; rejeite `..`,
barras e qualquer coisa fora de `[a-zA-Z0-9._-]`. Zip slip é o risco real aqui.

**Templates nunca em String Java.** Todo conteúdo gerado mora em
`resources/claude-templates/`. Se você está concatenando Markdown dentro de um `.java`,
parou no lugar errado.

**Dependência escolhida muda o output.** Marcar `Spring Data JPA` precisa acrescentar
regras sobre N+1, `@Transactional` e DTO-vs-entidade ao `CLAUDE.md` gerado e ao
`rules/persistence.md`. Essa ligação é o diferencial do produto; não trate dependências
como meros itens de lista.

## O que não fazer

- Não adicione banco de dados, Docker ou fila. O app é stateless e roda num jar só.
- Não crie um projeto React/Vue separado para o formulário.
- Não versione zips gerados nem a pasta `target/`.
- Não escreva um `CLAUDE.md` gerado com mais de ~150 linhas: ele é carregado em toda
  sessão e consome contexto do usuário final.