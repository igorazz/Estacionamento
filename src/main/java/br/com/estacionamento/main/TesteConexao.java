package br.com.estacionamento.main;

import br.com.estacionamento.dao.VeiculoDAO;
import br.com.estacionamento.model.Veiculo;

public class TesteConexao {
    public static void main(String[] args) {
        // 1. Criamos um objeto de veículo para testar
        // Graças ao @AllArgsConstructor do Lombok, passamos os dados no construtor
        // (ID é null pois o MySQL gera automaticamente)
        Veiculo v1 = new Veiculo(null, "ABC-1234", "Civic", "Preto", "CARRO", null);

        // 2. Instanciamos o DAO
        VeiculoDAO dao = new VeiculoDAO();

        // 3. Tentamos salvar
        try {
            System.out.println("Tentando salvar o veículo: " + v1.getModelo());
            dao.salvar(v1);
            System.out.println("Sucesso! Verifique seu banco de dados MySQL.");
        } catch (Exception e) {
            System.err.println("Ops! Algo deu errado:");
            e.printStackTrace();
        }
    }
}