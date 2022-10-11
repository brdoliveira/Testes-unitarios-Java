package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LocacaoServiceTest {
    public LocacaoService service;

    @Rule // Cria uma pilha com os erros
    public ErrorCollector error = new ErrorCollector(); // Notacao bem parecida do assert

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup(){
        service = new LocacaoService();
    }

    @Test
    public void deveAlugarFilme() throws Exception{
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1",2,5.0));

        //ação
        Locacao locacao = service.alugarFilme(usuario,filmes);

        //validação
        error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0))); // Adicionar import estático
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(),new Date()), CoreMatchers.is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),CoreMatchers.is(true));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlugarFilmeSemEstoque() throws Exception{
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1",0,5.0));

        //ação
        service.alugarFilme(usuario,filmes);
    }

    @Test // Forma mais completa de tratar exceptions
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException{
        //cenario
        List<Filme> filmes = Arrays.asList(new Filme("Filme 2",1,4.0));

        //acao
        try{
            service.alugarFilme(null,filmes);
            Assert.fail();
        }catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuário vazio"));
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException{
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");

        // acao
        service.alugarFilme(usuario,null);
    }

    @Test
    public void devePagar75pctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1",2,4.0),
                                           new Filme("Filme 2",2,4.0),
                                           new Filme("Filme 3",2,4.0)
                );

        //acao
        Locacao resultado = service.alugarFilme(usuario,filmes);

        // verificacao
        // 4+4+3 = 11
        Assert.assertThat(resultado.getValor(),CoreMatchers.is(11.0));
    }

    @Test
    public void devePagar50pctNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(
                new Filme("Filme 1",2,4.0),
                new Filme("Filme 2",2,4.0),
                new Filme("Filme 3",2,4.0),
                new Filme("Filme 4",2,4.0)
        );

        //acao
        Locacao resultado = service.alugarFilme(usuario,filmes);

        // verificacao
        // 4+4+3+2= 13
        Assert.assertThat(resultado.getValor(),CoreMatchers.is(13.0));
    }

    @Test
    public void devePagar25pctNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(
                new Filme("Filme 1",2,4.0),
                new Filme("Filme 2",2,4.0),
                new Filme("Filme 3",2,4.0),
                new Filme("Filme 4",2,4.0),
                new Filme("Filme 5",2,4.0)
        );

        //acao
        Locacao resultado = service.alugarFilme(usuario,filmes);

        // verificacao
        // 4+4+3+2+1= 14
        Assert.assertThat(resultado.getValor(),CoreMatchers.is(14.0));
    }

    @Test
    public void devePagar0pctNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(
                new Filme("Filme 1",2,4.0),
                new Filme("Filme 2",2,4.0),
                new Filme("Filme 3",2,4.0),
                new Filme("Filme 4",2,4.0),
                new Filme("Filme 5",2,4.0),
                new Filme("Filme 6",2,4.0)
        );

        //acao
        Locacao resultado = service.alugarFilme(usuario,filmes);

        // verificacao
        // 4+4+3+2+1+0= 14
        Assert.assertThat(resultado.getValor(),CoreMatchers.is(14.0));
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1",1,5.0));

        //acao
        Locacao retorno = service.alugarFilme(usuario,filmes);

        // verificacao
        boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
        Assert.assertTrue(ehSegunda);
    }
}
