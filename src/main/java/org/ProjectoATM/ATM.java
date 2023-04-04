package org.ProjectoATM;

import org.ProjectoATM.Utilizacao.formatacao;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.time.LocalDateTime;


class Main {


    public static void main(String[] args) throws SQLException {
        System.out.println("UFCD: 10790 -  Projeto: ATM  - Formando: Everton Alex" );
        System.out.println("Tecnisign, 2023\n" );
        System.out.println("\n\n  -----   ---------  ---       ---\n" +
                " /     \\ /         |/  \\      /  |\n" +
                "/$$$$$$  |$$$$$$$$/ $$  \\    /$$ |\n" +
                "$$ |__$$ |   $$ |   $$$  \\  /$$$ |\n" +
                "$$    $$ |   $$ |   $$$$   /$$$$ |\n" +
                "$$$$$$$$ |   $$ |   $$ $$  $$/$$ |\n" +
                "$$ |  $$ |   $$ |   $$ |$$$/  $$ |\n" +
                "$$ |  $$ |   $$ |   $$ | $/   $$ |\n" +
                "$$/   $$/    $$/    $$/       $$/ ");
        ATM.menuATM();



    }


}

public class ATM {
    static Scanner input =  new Scanner(System.in);
    static String url = "jdbc:postgresql://localhost:5432/Cliente";
    static String user = "postgres";
    static String password = "123456";


    //FUNCOES - CONTAS


    public static void criarConta() throws SQLException {

        Connection conexao = DriverManager.getConnection(url, user, password);

        PreparedStatement pstmt = conexao.prepareStatement("INSERT INTO contas (numero_conta, nome, nif, email, morada, pin, saldo, movimentos) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        try (pstmt) {

            LocalDateTime agora = LocalDateTime.now();
            String dataHoraAtual = agora.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

            System.out.println("\n-=-= CADASTRO DE CONTA =-=-");
            System.out.println("\nNome:");
            String nome = input.next() + input.nextLine();
            System.out.println("\nNIF:");
            String nif = input.nextLine().trim();
            while (nif.length()!=9){
                System.out.println("O NIF deve 9 digitos, tente novamente: ");
                nif = input.nextLine().trim();
            }
            System.out.println("\nMorada:");
            String morada = input.nextLine().trim();
            System.out.println("\nE-mail:");
            String email = input.nextLine().trim();
            System.out.println("\nDefina um PIN (4 caracteres):");
            String pin = input.nextLine().trim();
            while (pin.length()!=4){
                System.out.println("O PIN deve ter no 4 digitos, tente novamente: ");
                pin = input.nextLine().trim();
            }

            Pessoa pessoa = new Pessoa(nome, nif, morada, email);
            int numeroContaCliente = Cliente.gerarNumeroAleatorio(1000, 9999);


            //Consultar se o numero da conta já existe para gerar outro:
            PreparedStatement pstmt1 = conexao.prepareStatement("SELECT * FROM contas WHERE numero_conta = ?"); //verifica se o numero da conta já existe
            pstmt1.setInt(1, numeroContaCliente);
            ResultSet rs1 = pstmt1.executeQuery();
            while (rs1.next()){  //Aqui vai continuar a gerar numero enquanto o numeor da conta existir
                numeroContaCliente = Cliente.gerarNumeroAleatorio(1000, 9999);
            }

            Cliente cliente = new Cliente(numeroContaCliente, pessoa, pin);

            pstmt.setInt(1, numeroContaCliente);
            pstmt.setString(2, nome);
            pstmt.setString(3, nif);
            pstmt.setString(4, email);
            pstmt.setString(5, morada);
            pstmt.setString(6, pin);
            pstmt.setDouble(7, cliente.getSaldo());
            pstmt.setString(8, "(" + dataHoraAtual + ")" + " Criacao da conta");

            pstmt.executeUpdate();

            System.out.println("\n\nA sua conta foi criada com sucesso");

            System.out.println("" +
                    "\n\n-=-=INFORMACOES DA CONTA CRIADA=-=-" +
                    "\nNumero da conta: " + numeroContaCliente +
                    "\nNome: " + nome +
                    "\nNIF: " + nif +
                    "\nE-mail: " + email +
                    "\nMorada: " + morada +
                    "\nPIN: " + pin +
                    "\nSaldo: " + formatacao.doubletoString(cliente.getSaldo()) + "\n");

            conexao.close();
            opcoesContas();

            }





        catch (SQLException e) {
            System.out.println("Erro ao criar conta: Verifique os dados inseridos e tente novamente! ");
            opcoesContas();
        }


    }

    public static void listarClientes() throws SQLException {

        Connection conexao = DriverManager.getConnection(url, user, password);

        PreparedStatement pstmt = conexao.prepareStatement("SELECT * FROM contas");


        try (pstmt){
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int numeroContaCliente = rs.getInt("numero_conta");
                String nome = rs.getString("nome");
                String nif = rs.getString("nif");
                String email = rs.getString("email");
                String morada = rs.getString("morada");
                String pin = rs.getString("pin");
                double saldo = rs.getDouble("saldo");

                Cliente cliente = new Cliente(numeroContaCliente, new Pessoa(nome, nif, morada, email), pin);
                cliente.setSaldo(saldo);

                System.out.println(
                        "\n\n-=-=INFORMACOES DO CLIENTE=-=-" +
                        "\nNumero da conta: " + numeroContaCliente +
                        "\nNome: " + nome +
                        "\nNIF: " + nif +
                        "\nE-mail: " + email +
                        "\nMorada: " + morada +
                        "\nPIN: " + pin +
                        "\nSaldo: " + formatacao.doubletoString(cliente.getSaldo()) + "\n");
            }

            conexao.close();
            opcoesContas();



        } catch (SQLException e){
            System.out.println("Erro ao obter clientes: " + e.getMessage());
            opcoesContas();
        }

    }

    public static void excluirContas() throws SQLException {
        int numeroConta;

        System.out.println("\nInsira o numero da conta: ");
        numeroConta = input.nextInt();
        System.out.println("\nDeseja realmente excluir a conta de numero: " + numeroConta + " ?? [s/n]");
        String decisao = input.next();
        if (decisao.equalsIgnoreCase("s")){
            try (Connection conexao = DriverManager.getConnection(url, user, password);
                 PreparedStatement pstmt = conexao.prepareStatement("DELETE FROM contas WHERE numero_conta = ?")) {
                pstmt.setInt(1, numeroConta);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Conta excluida com sucesso!");
                } else {
                    System.out.println("Nenhuma conta encontrada com o numero informado.");
                    opcoesContas();
                }
            } catch (SQLException e) {
                System.out.println("Erro ao excluir conta: " + e.getMessage());
            }
            opcoesContas();
        }
        else {
            System.out.println("exclusao abortada!");
            opcoesContas();
        }
    }

    public static void encontrarClientes() throws SQLException {
        Connection conexao = DriverManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = conexao.prepareStatement("SELECT * FROM contas WHERE numero_conta = ?")) {

            System.out.println("\nInsira o número da conta: ");
            int numeroConta = input.nextInt();
            pstmt.setInt(1, numeroConta);

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()){
                System.out.println("Conta não encontrada");
                opcoesContas();
                return;


            }

            while (rs.next()) {
                int numeroContaCliente = rs.getInt("numero_conta");
                String nome = rs.getString("nome");
                String nif = rs.getString("nif");
                String email = rs.getString("email");
                String morada = rs.getString("morada");
                String pin = rs.getString("pin");
                double saldo = rs.getDouble("saldo");

                System.out.println("" +
                        "\n\n-=-=INFORMAÇÕES DO CLIENTE=-=-" +
                        "\nNumero da conta: " + numeroContaCliente +
                        "\nNome: " + nome +
                        "\nNIF: " + nif +
                        "\nE-mail: " + email +
                        "\nMorada: " + morada +
                        "\nPIN: " + pin +
                        "\nSaldo: " + formatacao.doubletoString(saldo) + "\n");
            }

            conexao.close();
            opcoesContas();


        } catch (SQLException e) {
            System.out.println("Erro ao obter cliente: " + e.getMessage());
            opcoesContas();
        }
    }



    //FUNCOES - ATM



    public static void depositar() throws SQLException {

        Connection conexao = DriverManager.getConnection(url, user, password);

        int numeroConta;


        LocalDateTime agora = LocalDateTime.now();
        String dataHoraAtual = agora.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        System.out.println("Informe o numero da conta para depositar: ");
        numeroConta = input.nextInt();

        PreparedStatement pstmt1 = conexao.prepareStatement("SELECT * FROM contas WHERE numero_conta = ?"); //verifica se o nuemro existe mesmo
        pstmt1.setInt(1, numeroConta);
        ResultSet rs1 = pstmt1.executeQuery();

        if (rs1.next()) {
            double valor;

            System.out.println("Informe o valor a ser depositado");
            valor = input.nextDouble();

            if (valor > 0 ){


                double saldoAtual = rs1.getDouble("saldo"); // Atualiza a coluna "saldo" com o valor depositado
                double novoSaldo = saldoAtual + valor;

                PreparedStatement pstmt2 = conexao.prepareStatement("UPDATE contas SET saldo = ? WHERE numero_conta = ?");
                pstmt2.setDouble(1, novoSaldo);
                pstmt2.setInt(2, numeroConta);
                pstmt2.executeUpdate();

                String movimento = "\n(" + dataHoraAtual + ")" + " Depósito no valor de "  + formatacao.doubletoString(valor);

                PreparedStatement pstmt3 = conexao.prepareStatement("UPDATE contas SET movimentos = CONCAT(movimentos, ?) WHERE numero_conta = ?");
                pstmt3.setString(1, movimento);
                pstmt3.setInt(2, numeroConta);
                pstmt3.executeUpdate();


                System.out.println("Depósito realizado com sucesso!");
                conexao.close();
                opcoesATM();

            }
            else {
                System.out.println("Valor inferior a 0, operacao nao pode ser realizada!");
                opcoesATM();
            }


        }

        if (!rs1.next()) {
            System.out.println("Conta não encontrada");
            return;
        }





    }

    public static void levantar() throws SQLException {
        Connection conexao = DriverManager.getConnection(url, user, password);

        int numeroConta;
        double valor;

        LocalDateTime agora = LocalDateTime.now();
        String dataHoraAtual = agora.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        System.out.println("Informe o numero da conta para levantar: ");
        numeroConta = input.nextInt();

        PreparedStatement pstmt1 = conexao.prepareStatement("SELECT * FROM contas WHERE numero_conta = ?"); //verifica se o nuemro da conta  existe mesmo
        pstmt1.setInt(1, numeroConta);
        ResultSet rs1 = pstmt1.executeQuery();

        if (rs1.next()){
            System.out.println("Informe o valor do levantamento: ");
            valor = input.nextDouble();

            double saldoDaConta = rs1.getDouble("saldo");

            if (valor > 0 && valor <= saldoDaConta){
                double saldoAtual = rs1.getDouble("saldo"); // Atualiza a coluna "saldo" com o valor depositado
                double novoSaldo = saldoAtual - valor;
                PreparedStatement pstmt2 = conexao.prepareStatement("UPDATE contas SET saldo = ? WHERE numero_conta = ?");
                pstmt2.setDouble(1, novoSaldo);
                pstmt2.setInt(2, numeroConta);
                pstmt2.executeUpdate();

                String movimento = "\n(" + dataHoraAtual + ")" + " Levantamento no valor de "  + formatacao.doubletoString(valor);

                PreparedStatement pstmt3 = conexao.prepareStatement("UPDATE contas SET movimentos = CONCAT(movimentos, ?) WHERE numero_conta = ?");
                pstmt3.setString(1, movimento);
                pstmt3.setInt(2, numeroConta);
                pstmt3.executeUpdate();


                System.out.println("Levantamento realizado com sucesso!");
                conexao.close();
                System.out.println("\nConta Numero: " + numeroConta +
                        "\nSaldo Atual: " + formatacao.doubletoString(novoSaldo) + "\n\n");

                opcoesATM();

            }
            else {
                System.out.println("Seu Saldo é insuficiente, tente novamente!");
                opcoesATM();
            }


        }




        else if (!rs1.next()) {
            System.out.println("Conta não encontrada");
            return;
        }





    }

    public static void transferir() throws SQLException {
        Connection conexao = DriverManager.getConnection(url, user, password);

        int numeroConta;
        double valor;

        LocalDateTime agora = LocalDateTime.now();
        String dataHoraAtual = agora.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        System.out.println("Informe o numero da conta de Origem: ");
        numeroConta = input.nextInt();

        PreparedStatement pstmt1 = conexao.prepareStatement("SELECT * FROM contas WHERE numero_conta = ?"); //verifica se o nuemro da conta  existe mesmo
        pstmt1.setInt(1, numeroConta);
        ResultSet rs1 = pstmt1.executeQuery();

        if (rs1.next()){
            System.out.println("Informe o valor a transferir: ");
            valor = input.nextDouble();

            double saldoDaConta = rs1.getDouble("saldo");

            if (valor > 0 && valor <= saldoDaConta){

                //Conta destino inicio confirmacoes

                System.out.println("Informe o numero da conta de destino: ");
                int numeroContaDestino = input.nextInt();

                PreparedStatement pstmtD = conexao.prepareStatement("SELECT * FROM contas WHERE numero_conta = ?"); //verifica se a conta destino existe
                pstmtD.setInt(1, numeroContaDestino);
                ResultSet rsD = pstmtD.executeQuery();

                if (!rsD.next()) {
                    System.out.println("\n** Conta de destino não encontrada, Operacao cancelada! **\n");
                    opcoesATM();
                    return;
                }//fim da veririficacao se existe
                double saldoAtualDestino = rsD.getDouble("saldo"); // Atualiza a coluna "saldo" com o valor depositado
                double novoSaldoDestino = saldoAtualDestino + valor;

                PreparedStatement pstmtD1 = conexao.prepareStatement("UPDATE contas SET saldo = ? WHERE numero_conta = ?");
                pstmtD1.setDouble(1, novoSaldoDestino);
                pstmtD1.setInt(2, numeroContaDestino);
                pstmtD1.executeUpdate();

                String movimentoDestino = "\n(" + dataHoraAtual + ")" + " Recebimento de transferencia no valor de "  + formatacao.doubletoString(valor) + " da conta numero: "+ numeroConta;

                PreparedStatement pstmtD2 = conexao.prepareStatement("UPDATE contas SET movimentos = CONCAT(movimentos, ?) WHERE numero_conta = ?");
                pstmtD2.setString(1, movimentoDestino);
                pstmtD2.setInt(2, numeroContaDestino);
                pstmtD2.executeUpdate();


                //fim de consulta para conta destino e concluir transferencia tirando da conta origem;

                double saldoAtual = rs1.getDouble("saldo"); // Atualiza a coluna da conta inicial
                double novoSaldo = saldoAtual - valor;
                PreparedStatement pstmt2 = conexao.prepareStatement("UPDATE contas SET saldo = ? WHERE numero_conta = ?");
                pstmt2.setDouble(1, novoSaldo);
                pstmt2.setInt(2, numeroConta);
                pstmt2.executeUpdate();

                String movimento = "\n(" + dataHoraAtual + ")" + " Transferencia no valor de "  + formatacao.doubletoString(valor) + " Para a conta numero: "+ numeroContaDestino;

                PreparedStatement pstmt3 = conexao.prepareStatement("UPDATE contas SET movimentos = CONCAT(movimentos, ?) WHERE numero_conta = ?");
                pstmt3.setString(1, movimento);
                pstmt3.setInt(2, numeroConta);
                pstmt3.executeUpdate();


                System.out.println("\n\nTransferencia realizada com sucesso!");
                conexao.close();
                System.out.println("\nConta de Origem: " + numeroConta +
                        "\nConta Destino: " + numeroContaDestino +
                        "\nSaldo Atual da conta de origem -> " + numeroConta + ": " + formatacao.doubletoString(novoSaldo) + "\n\n");

                opcoesATM();

            }
            else {
                System.out.println("Seu Saldo é insuficiente, tente novamente!");
                opcoesATM();
            }


        }


        else if (!rs1.next()) {
            System.out.println("\n** Conta de Origem não encontrada, Operacao cancelada! **\n");
            opcoesATM();
        }





    }

    public static void consultarSaldo() throws SQLException {
        Connection conexao = DriverManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = conexao.prepareStatement("SELECT * FROM contas WHERE numero_conta = ?")) {

            System.out.println("\nInsira o número da conta para consultar o saldo: ");
            int numeroConta = input.nextInt();
            pstmt.setInt(1, numeroConta);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int numeroContaCliente = rs.getInt("numero_conta");
                String nome = rs.getString("nome");
                double saldo = rs.getDouble("saldo");

                System.out.println("" +
                        "\n\n-=-=INFO SALDO DO CLIENTE=-=-" +
                        "\nNumero da conta: " + numeroContaCliente +
                        "\nNome: " + nome +
                        "\nSaldo: " + formatacao.doubletoString(saldo) + "\n");
            }

            conexao.close();
            opcoesATM();

        } catch (SQLException e) {
            System.out.println("Erro ao consultar saldo cliente: " + e.getMessage());
            opcoesATM();
        }
    }

    public static void movimentos() throws SQLException {

        Connection conexao = DriverManager.getConnection(url, user, password);

        try (PreparedStatement pstmt = conexao.prepareStatement("SELECT movimentos FROM contas WHERE numero_conta = ?;\n")) {

            System.out.println("\nInsira o número da conta: ");
            int numeroConta = input.nextInt();
            pstmt.setInt(1, numeroConta);

            PreparedStatement pstmt1 = conexao.prepareStatement("SELECT * FROM contas WHERE numero_conta = ?"); //verifica se o nuemro da conta  existe mesmo
            pstmt1.setInt(1, numeroConta);
            ResultSet rs1 = pstmt1.executeQuery();
            if (!rs1.next()) {
                System.out.println("\n** Conta não encontrada, Operacao cancelada! **\n");
                opcoesATM();
                return;
            }//fim da verificacao

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n-=-=MOVIMENTOS DA CONTA NR: " + numeroConta + " =-=-");
            while (rs.next()) {
                String movimentos = rs.getString("movimentos");
                System.out.println("\n" + movimentos + "\n");
            }

            conexao.close();
            opcoesATM();

        } catch (SQLException e) {
            System.out.println("Erro ao obter movimentos: " + e.getMessage());
            opcoesATM();
        }
    }

    public static void alterarPin() throws SQLException {

        System.out.println("\nInsira o numero da conta: ");
        int numeroConta = input.nextInt();

        Connection conexao = DriverManager.getConnection(url, user, password);


        PreparedStatement pstmt1 = conexao.prepareStatement("SELECT * FROM contas WHERE numero_conta = ?"); //verifica se o nuemro da conta  existe mesmo
        pstmt1.setInt(1, numeroConta);
        ResultSet rs1 = pstmt1.executeQuery();
        if (!rs1.next()) {
            System.out.println("\n** Conta não encontrada, Operacao cancelada! **\n");
            opcoesATM();
        }

        System.out.println("\nDefina o novo PIN: ");
        String pin = input.next();
        System.out.println("\nConfirme o novo PIN: ");
        String pinconfirm = input.next();

        if(pin.equals(pinconfirm)){
            PreparedStatement pstmt2 = conexao.prepareStatement("UPDATE contas SET pin = ? WHERE numero_conta = ?");
            pstmt2.setString(1, pinconfirm);
            pstmt2.setInt(2, numeroConta);
            pstmt2.executeUpdate();
            System.out.println("PIN alterado com sucesso!");

        }
        else {
            System.out.println("\nPIN nao confirmado tente novamente.");


        }
        conexao.close();
        opcoesATM();

    }



    //MENUS

    public static  void menuATM() throws SQLException {
        System.out.println("\n-=-=-=-=-=-=-=-=-=-=");
        System.out.println("1 - Acessar ATM");
        System.out.println("2 - Contas");
        System.out.println("3 - Sair");
        System.out.println("-=-=-=-=-=-=-=-=-=-=\n");

        int menu = input.nextInt();
        switch (menu){
            case 1:
                try {
                    validarLogin();break;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            case 2:
                try {
                    opcoesContas();break;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            case 3:
                System.out.println("Obrigado por usar a app");
                System.exit(0);
            default:
                System.out.println("dados invalidos");
                menuATM();
                break;

        }
    }

    public static void opcoesATM() throws SQLException {

        System.out.println("Bem-vindo(a) "); //Por nome;
        System.out.println("-=-=-=-=-=-=-=-=-=-=");
        System.out.println("1 - Depositar");
        System.out.println("2 - Levantar");
        System.out.println("3 - Transferir");
        System.out.println("4 - Movimentacoes");
        System.out.println("5 - Consultar SALDO");
        System.out.println("6 - Alterar PIN");
        System.out.println("7 - Voltar ao Menu");
        System.out.println("-=-=-=-=-=-=-=-=-=-=");

        int opcoesATM = input.nextInt();

        switch (opcoesATM) {
            case 1: depositar(); opcoesATM();
            case 2: levantar();opcoesATM();
            case 3: transferir();opcoesATM();
            case 4: movimentos();opcoesATM();
            case 5: consultarSaldo();opcoesATM();
            case 6: alterarPin();opcoesATM();
            case 7: menuATM();
            default:{
                System.out.println("dados invalidos");
                opcoesATM();
            }
        }

    }

    public static void opcoesContas() throws SQLException {

        System.out.println("-=-=-=-=-=-=-=-=-=-=");
        System.out.println("1 - Criar Conta");
        System.out.println("2 - Excluir Conta");
        System.out.println("3 - Listar Contas");
        System.out.println("4 - Encontrar Dados de Conta");
        System.out.println("5 - Voltar ao Menu");
        System.out.println("-=-=-=-=-=-=-=-=-=-=");

        int opcoesContas = input.nextInt();

        switch (opcoesContas){
            case 1:
                criarConta();break;
            case 2:
                excluirContas();break;
            case 3:
                listarClientes();break;
            case 4:
                encontrarClientes();break;
            case 5:
                menuATM();break;
            default:
                System.out.println("dados invalidos");
                //opcoesATM();
                break;}
    }

    public static boolean validarLogin() throws SQLException {
        Connection conexao = DriverManager.getConnection(url, user, password);

        System.out.println("\nInsira o número da conta: ");
        int numeroConta = input.nextInt();

        //verifica se o numero da conta  existe mesmo

        PreparedStatement pstmt1 = conexao.prepareStatement("SELECT * FROM contas WHERE numero_conta = ?");
        pstmt1.setInt(1, numeroConta);
        ResultSet rs1 = pstmt1.executeQuery();
        if (!rs1.next()) {
            System.out.println("\n** Conta não encontrada, Operação cancelada! **\n");
            menuATM();
        }

        //verifica se a conta tá bloqueada, caso não irá continuar
        PreparedStatement pstmt2 = conexao.prepareStatement("SELECT tentativas FROM contas WHERE numero_conta = ?");
        pstmt2.setInt(1, numeroConta);

        ResultSet rs2 = pstmt2.executeQuery();
        if (rs2.next()) {
            int numTentativas = rs2.getInt("tentativas");
            if (numTentativas >= 3) {
                System.out.println("\n** Conta bloqueada, contate um administrador para desbloqueá-la. **\n");
                conexao.close();
                menuATM();
                return false;
            }
            }


        System.out.println("\nInsira o PIN: ");
        String pin = input.next();

        PreparedStatement pstmt = conexao.prepareStatement("SELECT * FROM contas WHERE numero_conta = ? AND pin = ?");
        pstmt.setInt(1, numeroConta);
        pstmt.setString(2, pin);



        try (pstmt){

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("\nLogin realizado com sucesso!\n");

                //zera as tentativas semrpe que o login for feito para que o usuario aidna possa ter outras tentativas
                PreparedStatement pstmtZerarTentativas = conexao.prepareStatement("UPDATE contas SET tentativas = 0 WHERE numero_conta = ?");
                pstmtZerarTentativas.setInt(1, numeroConta);
                pstmtZerarTentativas.executeUpdate();

                opcoesATM();
                return true;
            } else {
                PreparedStatement pstmtTentativas = conexao.prepareStatement("SELECT tentativas FROM contas WHERE numero_conta = ?");
                pstmtTentativas.setInt(1, numeroConta);

                ResultSet rs3 = pstmtTentativas.executeQuery();

                if (rs3.next()) {
                    int numTentativas = rs3.getInt("tentativas");


                    System.out.println("\n** PIN incorreto, tente novamente.");

                    System.out.println("\n** Você tem mais " + (2 - numTentativas) + " tentativas");

                    PreparedStatement pstmtAtualTentativas = conexao.prepareStatement("UPDATE contas SET tentativas = tentativas + 1 WHERE numero_conta = ?");
                    pstmtAtualTentativas.setInt(1, numeroConta);
                    pstmtAtualTentativas.executeUpdate();

                    conexao.close();
                    menuATM();


                    }
                return false;
                }

            }
        catch (SQLException e){
            System.out.println("Erro ao validar login: " + e.getMessage());
            conexao.close();
            menuATM();
            return false;
        }
    }






}







