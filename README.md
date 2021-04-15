# java-database-api
Interface de programação de aplicativo escrita em Java para gerenciamento do Banco de dados da aplicação.

Exemplo:

```Java
try (Query q = new Query("tabela").setLimit(2)) {
    System.out.println(q.getJSON());
}
```