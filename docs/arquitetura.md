# Arquitetura

## Fluxo de uma geração

```
POST /api/generate
      │
      ▼
GenerationController ──► valida o DTO (Bean Validation + regex de path)
      │
      ▼
ProjectGenerator ──────► decide QUAIS arquivos existem, dado o request
      │
      ├──► TemplateRenderer ──► JMustache renderiza cada arquivo
      │                         (resources/claude-templates/*.mustache)
      │
      ▼
ProjectZipper ─────────► ZipArchiveOutputStream em memória
      │
      ▼
ResponseEntity<byte[]>  application/zip
```

`ProjectGenerator` é o cérebro: ele traduz "marcou JPA e hooks" numa lista de
`GeneratedFile(caminho, conteúdo, modoUnix)`. `ProjectZipper` não sabe nada de domínio —
recebe a lista e empacota. Essa separação é o que permite o `/api/preview` reusar tudo
até o penúltimo passo e devolver JSON em vez de zip.

## Camadas

| Pacote | Responsabilidade |
|---|---|
| `web` | Controllers, DTOs de request/response, `@RestControllerAdvice` |
| `metadata` | Opções do formulário, carregadas do `application.yml` |
| `template` | Compilação e renderização Mustache, montagem do contexto |
| `generator` | Regras de quais arquivos existem; empacotamento em zip |
| `config` | `@ConfigurationProperties` e beans |

Dependências apontam para dentro: `web` → `generator` → `template`. Nada em `template`
importa Spring MVC.

## Decisões

**Sem banco de dados.** Nada é persistido — a geração é uma função pura do request.
Toda a metadata (versões, dependências, componentes) vive no `application.yml`, o que
significa que adicionar uma opção nova é editar YAML e um template, sem migration nem
deploy de schema. Isso mantém o app num jar único e stateless.

**Thymeleaf + htmx, não React.** O front é um formulário que envia JSON e recebe um
arquivo. Um SPA aqui adicionaria build step, gerenciador de pacote e um segundo deploy
pra resolver um problema que `hx-post` resolve em um atributo.

**Commons Compress, não `java.util.zip`.** O `ZipOutputStream` do JDK não grava modo
Unix. Hooks gerados sairiam sem bit de execução e falhariam em silêncio na máquina do
usuário — o pior tipo de bug, porque o zip abre normalmente e o problema só aparece
depois. `ZipArchiveOutputStream` + `setUnixMode(0755)` resolve.

**JMustache cru, não o starter.** `spring-boot-starter-mustache` registra um
`ViewResolver` que passa a competir com o Thymeleaf pela resolução de views. Aqui o
Mustache é biblioteca de template para conteúdo gerado, não camada de apresentação.

**Geração em memória.** Nada de arquivo temporário. Um `.claude/` completo tem alguns
KB; escrever em disco só adicionaria limpeza, corrida de concorrência e permissão de
diretório sem ganho nenhum.

## Superfície de risco

Só existe uma entrada não confiável: os campos de texto do formulário. `group`,
`artifact` e `packageName` viram caminho de arquivo dentro do zip, então um `../../` ali
é zip slip clássico — a extração escreve fora da pasta de destino.

A defesa é validar na borda, com allowlist, antes de qualquer uso:

- `artifact`: `^[a-z][a-z0-9-]{0,49}$`
- `group` e `packageName`: `^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)*$`

Rejeite em vez de sanitizar. Sanitização silenciosa gera nome estranho e esconde o
ataque; rejeitar devolve 400 e deixa rastro.