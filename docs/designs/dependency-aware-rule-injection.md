# Design: Injeção de regras por dependência

Status: em execução
Escopo: JPA + Security, conteúdo primeiro

## Problema

O `CLAUDE.md` deste projeto define injeção de regras por dependência como o diferencial:
marcar "Spring Data JPA" precisa acrescentar regras específicas e acionáveis (N+1,
`@Transactional`, DTO-vs-entidade) ao `CLAUDE.md` gerado e a `rules/persistence.md`.

Nada disso existe ainda. O repositório tem `pom.xml` configurado, a classe de aplicação e
os documentos — nenhum pacote `metadata/`, `template/` ou `generator/`, nenhum
`claude-templates/`.

## A pergunta que decide o projeto

**O conteúdo das regras vale a leitura?**

Não é "o Mustache renderiza?" nem "o zip abre?". Se as regras geradas não mudarem o
comportamento do Claude Code numa sessão real, nenhuma quantidade de encanamento salva o
produto — e todo encanamento construído antes dessa resposta é risco de retrabalho.

Por isso a ordem abaixo começa fora do código.

## Etapa 1 — Conteúdo à mão, validado em projeto real

Escrever, como Markdown puro, sem Mustache e sem Java:

- `rules/persistence.md` — N+1, `@Transactional` e o proxy do Spring, DTO em vez de
  entidade no controller, Testcontainers em vez de H2
- `rules/security.md` — authz em serviço e não em controller, o que nunca vai para log,
  validação na borda
- as seções correspondentes do `CLAUDE.md`

Colar no **HotelHub** — é o projeto mais pesado em JPA da base e tem o problema de
concorrência/overbooking, que é justamente onde regra genérica não ajuda.

**Critério de saída:** rodar duas ou três sessões de Claude Code no HotelHub e identificar
pelo menos uma decisão concreta que mudou por causa de uma regra. Se nada mudar, a
hipótese do produto está errada e o custo total da descoberta foi um fim de semana
escrevendo Markdown.

## Etapa 2 — Duas dependências, não uma

JPA e Security desde o começo, de propósito.

Com uma dependência só, o design parece funcionar e quebra na segunda. O que só aparece
com duas:

- ordem dos fragmentos no `CLAUDE.md` gerado
- o que cortar quando o orçamento de ~150 linhas estoura
- sobreposição de assunto (as duas falam sobre o que não expor em resposta de API)

Adiar isso significa escrever resolver, template e teste em cima de uma premissa que a
segunda dependência derruba.

## Etapa 3 — Mecanismo

Converter o conteúdo já validado em template e código:

- `resources/claude-templates/rules/persistence.md.mustache`
- `resources/claude-templates/rules/security.md.mustache`
- `resources/claude-templates/claude-md/jpa-section.md.mustache`
- `resources/claude-templates/claude-md/security-section.md.mustache`
- `template/DependencyRuleResolver.java` — `Set<String>` de dependências →
  `RenderedRules(String claudeMdSections, Map<String, String> ruleFiles)`

`Map` em vez de campos fixos porque a etapa 2 já provou que são várias regras, não uma.

**Testes:**

- `{"spring-data-jpa"}` → contém "N+1" e `@Transactional`
- `{"spring-data-jpa", "security"}` → contém as duas e **o `CLAUDE.md` montado fica abaixo
  de 150 linhas** — esse é o teste que importa, e ele é impossível com uma dependência só
- conjunto vazio ou chave desconhecida → `RenderedRules` vazio, sem erro

Sem controller, sem zip, sem I/O de disco.

## Etapa 4 — CLI reverso (`pom.xml` → `CLAUDE.md`)

Ler `<dependencies>` de um `pom.xml` existente, casar `artifactId` contra o mesmo mapa,
passar pelo mesmo resolver, imprimir no stdout.

Esta etapa vem **antes** da web, e a razão é a resposta de "para quem é isso": para mim,
depois. Formulário não serve a esse uso — nos meus próprios repositórios eu já tenho um
`pom.xml` na mão e nunca vou preencher um form. O modo reverso é o que economiza tempo em
HotelHub, HelpDesk AI e Smart Support API já na semana seguinte.

Isso não descarta o produto web: `pom.xml` e formulário são duas entradas alimentando o
mesmo resolver. O CLI é o uso; a web é a vitrine.

## Etapa 5 — Web e zip

Formulário Thymeleaf + htmx, `generator/`, `POST /api/generate` com
`ZipArchiveOutputStream` e `setUnixMode`. É a Fase 2–3 do roadmap, retomada aqui só
quando as etapas anteriores estiverem pagas.

## Fora de escopo

- Preview ao vivo com diff — funcionalidade separada, depois da etapa 5
- Resolução de conflito entre dependências além de ordenação simples
- Outras stacks
- Templates escritos pelo usuário

## Riscos

**O conteúdo ser genérico.** É o risco real e o mais fácil de não perceber, porque um
`CLAUDE.md` genérico parece perfeitamente bom quando lido isolado. A defesa é a etapa 1:
uso em projeto real antes de qualquer abstração.

**Orçamento de linha estourar com dependências empilhadas.** Endereçado na etapa 2, em vez
de adiado.

**Divergir do `/init` nativo apenas no formato.** Se as regras geradas descreverem o que já
está no código, o `/init` faz melhor. O valor está em injetar o que *não* está em lugar
nenhum do repositório — o proxy que não intercepta chamada interna, o dialeto do H2 que
esconde bug de SQL.