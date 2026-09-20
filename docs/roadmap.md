# Roadmap

Cada fase só é considerada pronta quando o critério de saída passa. A ordem importa:
as fases iniciais entregam o caminho completo (entrada → zip) com escopo mínimo, e as
seguintes vão engrossando o conteúdo. O inverso — construir o formulário bonito antes
de existir geração — é o caminho clássico pro projeto morrer pela metade.

---

## Fase 0 — Fundação

Deixar o esqueleto de pé e provar que o Maven está limpo depois da confusão com Gradle.

- [ ] `./mvnw spring-boot:run` sobe sem erro
- [ ] Pacotes criados: `web`, `metadata`, `template`, `generator`, `config`
- [ ] `application.yml` no lugar do `application.properties`
- [ ] Smoke test de contexto passando
- [ ] `.gitignore` cobrindo `target/`, `.idea/`, `*.zip`

**Pronto quando:** o app sobe, o teste passa e o repo está no GitHub.

---

## Fase 1 — Geração mínima

O caminho inteiro funcionando com stack fixa. Nada de formulário ainda — só o endpoint.

- [x] Template `CLAUDE.md.mustache` em `resources/claude-templates/`
- [x] `TemplateRenderer` renderizando o template com um contexto simples
- [x] `ProjectZipper` montando o zip em memória com `ZipArchiveOutputStream`
- [x] `POST /api/generate` devolvendo `application/zip` com um arquivo dentro
- [x] Validação de `group`, `artifact` e `packageName` contra regex estrito
- [x] Teste garantindo que `../` no artifact é rejeitado

**Pronto quando:** um `curl` com JSON devolve um zip que abre e tem um `CLAUDE.md`
preenchido com os dados enviados.

---

## Fase 2 — Formulário

Agora sim a tela, consumindo metadata do servidor em vez de hardcode no HTML.

- [x] `GET /api/metadata` devolvendo versões de Boot, Java, build tool e componentes
- [x] `@ConfigurationProperties` lendo essa metadata do `application.yml`
- [x] Página Thymeleaf renderizando os campos a partir da metadata
- [x] Form enviando via fetch/JS e disparando o download (ver nota abaixo)
- [x] Layout minimamente apresentável (o Initializr é a referência visual)

**Pronto quando:** dá pra gerar o zip sem sair do navegador.

---

## Fase 3 — Componentes do `.claude/`

Sair de um arquivo só para a árvore completa.

- [x] Checkboxes: `rules/`, `commands/`, `skills/`, `agents/`, `hooks/`, `.mcp.json`
- [x] Um template por componente, com conteúdo real para Spring Boot
- [x] `settings.json` e `settings.local.json`
- [x] `hooks/validate-bash.sh` saindo com `setUnixMode(0755)`
- [x] Teste verificando o bit de execução no arquivo dentro do zip

**Pronto quando:** o zip gerado é um `.claude/` que você usaria de verdade num projeto seu.

---

## Fase 4 — Dependência muda o conteúdo

É aqui que o projeto deixa de ser um gerador de pasta vazia. Prioridade máxima depois da Fase 3.

- [ ] Mapeamento dependência → fragmentos de regra
- [ ] JPA: N+1, `@Transactional`, DTO em vez de entidade no controller
- [ ] Security: `agents/security-auditor.md` e regras de authz
- [ ] Redis / Mongo / Kafka: pelo menos um bloco específico cada
- [ ] Os fragmentos entram no `CLAUDE.md` gerado **e** em `rules/*.md`

**Pronto quando:** gerar com JPA e sem JPA produz `CLAUDE.md` visivelmente diferentes.

---

## Fase 5 — Explore

O botão que vende o produto em demo.

- [ ] `POST /api/preview` devolvendo a árvore + conteúdo em JSON
- [ ] Modal com árvore navegável e o arquivo selecionado ao lado
- [ ] `generate` e `preview` compartilhando a mesma geração

**Pronto quando:** dá pra inspecionar tudo antes de baixar.

---

## Fase 6 — Publicação

- [ ] `Dockerfile` (multi-stage, jre-alpine)
- [ ] Deploy em Railway, Fly.io ou Render
- [ ] GitHub Actions: build + test no push
- [ ] README com screenshot e link da demo
- [ ] Domínio ou subdomínio decente

**Pronto quando:** existe um link público que você põe no portfólio.

---

## Fora do escopo da v1

Anotado para não virar discussão no meio do caminho:

- Gradle (entra depois de Maven estar sólido)
- Node/TS e Python (o gargalo é curadoria de template, não código)
- Banco de dados, login, contas de usuário
- Front-end em React/Vue
- Salvar ou compartilhar configurações por link