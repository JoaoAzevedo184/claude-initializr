# API

Base local: `http://localhost:8080`

---

## `GET /api/metadata`

Devolve as opções que o formulário deve renderizar. O front não hardcoda nada: monta a
tela a partir daqui.

```json
{
  "bootVersions": [
    { "id": "4.1.1", "nome": "4.1.1", "padrao": true }
  ],
  "javaVersions": [
    { "id": "21", "nome": "21", "padrao": true },
    { "id": "17", "nome": "17", "padrao": false }
  ],
  "buildTools": [
    { "id": "maven", "nome": "Maven", "padrao": true }
  ],
  "dependencias": [
    {
      "id": "data-jpa",
      "nome": "Spring Data JPA",
      "grupo": "SQL",
      "descricao": "Persistencia com Hibernate"
    }
  ],
  "componentes": [
    { "id": "rules", "nome": "rules/", "padrao": true },
    { "id": "commands", "nome": "commands/", "padrao": true },
    { "id": "agents", "nome": "agents/", "padrao": false },
    { "id": "hooks", "nome": "hooks/", "padrao": false },
    { "id": "mcp", "nome": ".mcp.json", "padrao": false }
  ]
}
```

---

## `POST /api/generate`

Recebe as escolhas, devolve o zip.

**Request**

```json
{
  "group": "com.exemplo",
  "artifact": "minha-api",
  "packageName": "com.exemplo.minhaapi",
  "bootVersion": "4.1.1",
  "javaVersion": "21",
  "buildTool": "maven",
  "dependencias": ["data-jpa", "security"],
  "componentes": ["rules", "commands", "hooks"]
}
```

**Response** — `200 OK`

```
Content-Type: application/zip
Content-Disposition: attachment; filename="minha-api-claude.zip"
```

**Erros**

| Status | Quando |
|---|---|
| `400` | Campo inválido, regex de path recusado, id de dependência inexistente |
| `422` | Combinação impossível (ex: versão de Java abaixo do mínimo do Boot escolhido) |

Corpo de erro:

```json
{
  "erro": "VALIDACAO",
  "mensagem": "Campo invalido",
  "campos": { "artifact": "deve corresponder a ^[a-z][a-z0-9-]{0,49}$" }
}
```

---

## `POST /api/preview`

Mesmo request do `generate`, mesma geração — só o formato muda. Alimenta o botão
*Explore*.

**Response** — `200 OK`

```json
{
  "arquivos": [
    {
      "path": "CLAUDE.md",
      "content": "# CLAUDE.md\n\n## Visao geral\n...",
      "unixMode": 420
    },
    {
      "path": ".claude/hooks/validate-bash.sh",
      "content": "#!/usr/bin/env bash\n...",
      "unixMode": 493
    }
  ]
}
```

`unixMode` é o inteiro decimal do modo (420 = `0644`, 493 = `0755`). A lista é plana, na
ordem de geração; o front monta a árvore a partir de `path` e ordena pastas antes de arquivos.

---

## `curl` de teste

```bash
curl -X POST http://localhost:8080/api/generate \
  -H 'Content-Type: application/json' \
  -d '{"group":"com.exemplo","artifact":"minha-api","packageName":"com.exemplo.minhaapi","bootVersion":"4.1.1","javaVersion":"21","buildTool":"maven","dependencias":["data-jpa"],"componentes":["rules"]}' \
  -o saida.zip

unzip -l saida.zip
```