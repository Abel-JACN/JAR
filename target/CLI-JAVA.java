/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sptech.looca.callmm.executavel;

import br.com.sptech.looca.callmm.conexoes.CriaConexaoAzure;
import br.com.sptech.looca.callmm.conexoes.CriaConexaoLocal;
import br.com.sptech.looca.callmm.entities.Componente;
import br.com.sptech.looca.callmm.log.Logs;
import br.com.sptech.looca.callmm.services.ComponenteServices;
import br.com.sptech.looca.callmm.services.ComputadorServices;
import br.com.sptech.looca.callmm.services.LimitesServices;
import br.com.sptech.looca.callmm.services.MonitoramentoServices;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author gabri
 */
public class ExecCli {

    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        TelaLogin telaLogin = new TelaLogin();
        Componente componente = new Componente();
        CriaConexaoLocal criaConexaoLocal = new CriaConexaoLocal();
        JdbcTemplate conLocal = new JdbcTemplate(criaConexaoLocal.conexaoLocal());
        CriaConexaoAzure criaConexaoAzure = new CriaConexaoAzure();
        JdbcTemplate conAzure = new JdbcTemplate(criaConexaoAzure.conexaoAzure());
        ComputadorServices computadorServices = new ComputadorServices();
        ComponenteServices componenteServices = new ComponenteServices();
        LimitesServices limitesService = new LimitesServices();
        MonitoramentoServices monitoramentoServices = new MonitoramentoServices();
        TelaMonitoramento enviaLoginSenha;
        Logs logs = new Logs();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o login");
        String login = scanner.nextLine();
        System.out.println("Digite a senha");
        String senha = scanner.nextLine();
        
        telaLogin.tempoExecPersistencia(monitoramentoServices,
                limitesService, telaLogin.verificaConexao());

         if (login.isBlank() || senha.isBlank()
                || telaLogin.verificaMatricula(login, senha, telaLogin.verificaConexao())) {
            String msgs = "Login ou senha invalidos";
             System.out.println(msgs);
            try {
                logs.criandoLog(msgs);
            } catch (Exception ex) {
                Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            
            System.out.println("Login realizado com sucesso");
            try {
                String s = "Monitora��o iniciada.";
                logs.criandoLog(s);
                telaLogin.persistindoDadosNoBanco();
            } catch (Exception ex) {
                System.out.println("Erro ao tentar conex�o com um dos bancos");
            }
            try {
                computadorServices.persistindoHistoricoAcesso(login, senha, conLocal);
            } catch (Exception ex) {
                Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                computadorServices.persistindoHistoricoAcesso(login, senha, conAzure);
            } catch (Exception ex) {
                Logger.getLogger(TelaLogin.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
    }
}
