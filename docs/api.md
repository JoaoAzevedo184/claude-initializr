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
      "caminho": "CLAUDE.md",
      "conteudo": "# CLAUDE.md\n\n## Visao geral\n...",
      "modoUnix": "644"
    },
    {
      "caminho": ".claude/hooks/validate-bash.sh",
      "conteudo": "#!/usr/bin/env bash\n...",
      "modoUnix": "755"
    }
  ]
}
```

A ordem dos arquivos é a de caminhamento da árvore, já pronta pro front renderizar sem
reordenar.

---

## `curl` de teste

```bash
curl -X POST http://localhost:8080/api/generate \
  -H 'Content-Type: application/json' \
  -d '{"group":"com.exemplo","artifact":"minha-api","packageName":"com.exemplo.minhaapi","bootVersion":"4.1.1","javaVersion":"21","buildTool":"maven","dependencias":["data-jpa"],"componentes":["rules"]}' \
  -o saida.zip

unzip -l saida.zip
```