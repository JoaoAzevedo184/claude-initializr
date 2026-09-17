# Templates

Este é o documento mais importante do repositório. O código que monta o zip é trivial e
qualquer um escreve; o que decide se o produto presta é a qualidade do que sai dentro dele.

## Onde ficam

```
src/main/resources/
├── templates/                 # views Thymeleaf — a interface do site
└── claude-templates/          # Mustache — o conteudo que o usuario baixa
    ├── CLAUDE.md.mustache
    ├── mcp.json.mustache
    ├── settings.json.mustache
    ├── rules/
    │   ├── code-style.md.mustache
    │   ├── testing.md.mustache
    │   └── persistence.md.mustache      # so com data-jpa
    ├── commands/
    ├── agents/
    └── hooks/
        └── validate-bash.sh.mustache
```

As duas pastas `templates` são coisas diferentes e confundir custa tempo. Regra: se o
arquivo é visto pelo usuário **no navegador**, é `templates/`. Se ele aparece **dentro do
zip**, é `claude-templates/`.

## Contexto disponível

Todo template recebe o mesmo objeto:

| Variável | Exemplo |
|---|---|
| `{{artifact}}` | `minha-api` |
| `{{group}}` | `com.exemplo` |
| `{{packageName}}` | `com.exemplo.minhaapi` |
| `{{packagePath}}` | `com/exemplo/minhaapi` |
| `{{javaVersion}}` | `21` |
| `{{bootVersion}}` | `4.1.1` |
| `{{buildTool}}` | `maven` |
| `{{comandoRun}}` | `./mvnw spring-boot:run` |
| `{{comandoTest}}` | `./mvnw test` |

Flags de dependência viram seções condicionais:

```mustache
{{#temJpa}}
- Nunca retorne entidade JPA direto do controller. Mapeie para um record de resposta.
- Toda consulta que percorre coleção precisa de `join fetch` ou `@EntityGraph`.
{{/temJpa}}
```

Disponíveis: `temJpa`, `temSecurity`, `temRedis`, `temMongo`, `temKafka`, `temWeb`.

## Como escrever uma regra boa

O critério: **a regra passa se um dev sênior que conhece a stack ainda assim aprenderia
algo, ou se ela evita um erro que o modelo cometeria sozinho.** Se um LLM já faria aquilo
por padrão, a linha só está gastando contexto.

Ruim:

```
- Escreva codigo limpo e legivel.
- Siga as boas praticas do Spring Boot.
- Sempre escreva testes.
```

Bom:

```
- Injecao por construtor. `@Autowired` em campo quebra testes e esconde dependencia.
- `@Transactional` so em metodo de servico, nunca em controller nem em metodo privado
  (o proxy do Spring nao intercepta chamada interna e a anotacao vira decoracao).
- Teste de repositorio usa Testcontainers, nao H2: dialeto diferente esconde bug de SQL.
```

Três traços dos exemplos bons: são específicos da stack, dizem **por quê**, e o "por quê"
é uma consequência técnica concreta, não um valor abstrato.

## Limite de tamanho

O `CLAUDE.md` gerado é carregado no início de toda sessão do usuário final. Cada linha
inútil ali é contexto que ele perde para sempre, em todas as conversas.

- `CLAUDE.md` gerado: **até ~150 linhas**
- Cada `rules/*.md`: **até ~60 linhas**

Se estourar, o conteúdo específico demais deve migrar para um arquivo em `rules/`, que é
carregado sob demanda em vez de sempre.

## Adicionando uma dependência nova

1. Cadastre em `application.yml`, na lista `dependencias`
2. Crie a flag correspondente na montagem do contexto (`temXpto`)
3. Escreva o bloco condicional no `CLAUDE.md.mustache`
4. Se o assunto render mais de ~15 linhas, crie `rules/xpto.md.mustache` em vez de inchar
   o `CLAUDE.md`
5. Adicione um teste comparando a geração com e sem a dependência

O passo 5 não é burocracia: sem ele, dá pra cadastrar uma dependência que aparece bonita
no formulário e não muda uma vírgula do output. É exatamente a falha que transforma o
projeto em gerador de pasta vazia.

## Hooks e permissão

Todo template em `hooks/` gera arquivo com modo `0755`. O restante sai `0644`. Isso é
decidido no `ProjectGenerator` pela extensão e pela pasta, e existe um teste que abre o
zip e confere o bit — não remova.