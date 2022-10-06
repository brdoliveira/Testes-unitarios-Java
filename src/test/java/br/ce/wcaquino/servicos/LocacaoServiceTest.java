package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.Date;

public class LocacaoServiceTest {
    @Rule // Cria uma pilha com os erros
    public ErrorCollector error = new ErrorCollector(); // Notacao bem parecida do assert

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testeLocacao() throws Exception{
        //cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1",2,5.0);

        //ação
        Locacao locacao = service.alugarFilme(usuario,filme);

        //validação
        error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0))); // Adicionar import estático
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(),new Date()), CoreMatchers.is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),CoreMatchers.is(true));
    }

    @Test(expected = Exception.class)
    public void testeLocacao_filmeSemEstoque() throws Exception{
        //cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1",0,5.0);

        //ação
        service.alugarFilme(usuario,filme);
    }

    @Test
    public void testeLocacao_filmeSemEstoque_2(){
        //cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1",0,5.0);

        //ação
        try {
            service.alugarFilme(usuario, filme);
            Assert.fail("Deveria ter lançado uma excecao");
        }catch (Exception e){
            Assert.assertThat(e.getMessage(),CoreMatchers.is("Filme sem estoque!"));
        }
    }

    @Test
    public void testeLocacao_filmeSemEstoque_3() throws Exception{
        //cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1",0,5.0);

        exception.expect(Exception.class);
        exception.expectMessage("Filme sem estoque!");

        //ação
        service.alugarFilme(usuario,filme);
    }



}
