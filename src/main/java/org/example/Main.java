package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);

        Statement statement = null;
        ResultSet rst = null;

        // Conexão ao Banco de Dados;
        String url = "jdbc:mysql://localhost/ambientedados";
        String user = "root";
        String password = "22050505";

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        if (connection != null) System.out.println("Open Connection!");

        // Texto no Terminal
        System.out.println("Tabelas no Banco de dados: ");
        System.out.println("");
        System.out.println("Departamento");
        System.out.println("Cargo");
        System.out.println("Funcionario");
        System.out.println("Salario");
        System.out.println("Beneficio");
        System.out.println("Historico_Emprego");

        System.out.println("Realize um SELECT no Departamento?");
        String choseOne = sc.next();

        String querySelect = "SELECT * FROM ambientedados.departamento;";

        try {
            // SELECT
            statement = connection.createStatement();
            rst = statement.executeQuery(querySelect);

            while (rst.next()) {
                int departamentoId = rst.getInt("departamento_id");
                String nome = rst.getString("nome");
                String localizacao = rst.getString("localizacao");
                String supervisor = rst.getString("supervisor");

                System.out.println("-------------------------------------");
                System.out.println("DEPARTAMENTO-ID: " + departamentoId);
                System.out.println("NOME: " + nome);
                System.out.println("LOCALIZAÇÃO: " + localizacao);
                System.out.println("SUPERVISOR: " + supervisor);
                System.out.println("-------------------------------------");
            }

            System.out.println();
            System.out.println();
            System.out.println("Faça alteração de salário do algum cargo;");

            System.out.println("Informe novo salário:");
            Integer salary = sc.nextInt();
            System.out.println("Informe o Departamento:");
            Integer department = sc.nextInt();

            String updateQuery = "UPDATE cargo SET salario_base = " + salary + " WHERE departamento_id = " + department;

            // UPDATE
            int rowsAffected = statement.executeUpdate(updateQuery);

            if (rowsAffected > 0) {
                System.out.println("Atualização bem-sucedida! Linhas afetadas: " + rowsAffected);
            } else {
                System.out.println("Nenhuma linha foi atualizada.");
            }

            System.out.println();
            System.out.println();
            System.out.println("Delete um histórico de algum funcionário");

            String querySelectFunc = "SELECT * FROM ambientedados.funcionario fn " +
                    "INNER JOIN cargo cg ON fn.cargo_id = cg.cargo_id;";
            statement = connection.createStatement();
            rst = statement.executeQuery(querySelectFunc);

            while (rst.next()) {
                int funcId = rst.getInt("funcionario_id");
                String nome = rst.getString("nome_completo");
                String cargo = rst.getString("titulo");
                System.out.println("-------------------------------------");
                System.out.println("FUNC-ID: " + funcId);
                System.out.println("NOME: " + nome);
                System.out.println("CARGO: " + cargo);
                System.out.println("-------------------------------------");
            }

            System.out.println("SELECIONE O ID: ");
            int idFunc = sc.nextInt();

            // DELETE
            String deleteQuery = "DELETE FROM historico_emprego WHERE funcionario_id = " + idFunc;

            statement = connection.createStatement();
            rowsAffected = statement.executeUpdate(deleteQuery);

            if (rowsAffected > 0) {
                System.out.println("Exclusão bem-sucedida! Linhas afetadas: " + rowsAffected);
            } else {
                System.out.println("Nenhuma linha foi excluída.");
            }

            // Criando índices
            System.out.println("Criando índices nas tabelas mais utilizadas...");

            String[] indexQueries = {
                    "CREATE INDEX idx_funcionario_departamento_id ON Funcionario(departamento_id)",
                    "CREATE INDEX idx_funcionario_cargo_id ON Funcionario(cargo_id)",
                    "CREATE INDEX idx_cargo_departamento_id ON Cargo(departamento_id)",
                    "CREATE INDEX idx_departamento_nome ON Departamento(nome)",
                    "CREATE INDEX idx_salario_funcionario_id ON Salario(funcionario_id)",
                    "CREATE INDEX idx_beneficio_funcionario_id ON Beneficio(funcionario_id)",
                    "CREATE INDEX idx_historico_emprego_funcionario_id ON Historico_Emprego(funcionario_id)"
            };

            for (String query : indexQueries) {
                statement.executeUpdate(query);
                System.out.println("Executado: " + query);
            }

            // Inserir 1000 registros sem transação
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < 1000; i++) {
                String insertQuery = "INSERT INTO salario (funcionario_id, salario_base, bonus, comissoes, beneficios, data_inicio) " +
                        "VALUES (" + (i + 1) + ", 3000.00, 500.00, 200.00, 300.00, '2024-05-30')";
                statement.executeUpdate(insertQuery);
            }

            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000;
            System.out.println("Tempo sem transação: " + duration + " segundos");

            // Inserir 1000 registros com transação
            startTime = System.currentTimeMillis();

            connection.setAutoCommit(false);
            for (int i = 0; i < 1000; i++) {
                String insertQuery = "INSERT INTO salario (funcionario_id, salario_base, bonus, comissoes, beneficios, data_inicio) " +
                        "VALUES (" + (i + 1) + ", 3000.00, 500.00, 200.00, 300.00, '2024-05-30')";
                statement.executeUpdate(insertQuery);
            }
            connection.commit();

            endTime = System.currentTimeMillis();
            duration = (endTime - startTime) / 1000;
            System.out.println("Tempo com transação: " + duration + " segundos");

            // Fechando os recursos
            rst.close();
            statement.close();
            connection.close();

        } catch (SQLException ignored) {
            throw new RuntimeException(ignored.getMessage());
        }
    }
}



/*
1.         SELECT * FROM ambientedados.departamento;
2.         UPDATE cargo SET salario_base = "+ salary +" WHERE departamento_id = "+ department;
3.         DELETE FROM historico_emprego WHERE funcionario_id = " + idFunc;
4.          for (int i = 0; i < 1000; i++) {
                String insertQuery = "INSERT INTO salario (funcionario_id, salario_base, bonus, comissoes, beneficios, data_inicio) " +
                        "VALUES (" + (i + 1) + ", 3000.00, 500.00, 200.00, 300.00, '2024-05-30')";
                statement.executeUpdate(insertQuery);
            }

 */
