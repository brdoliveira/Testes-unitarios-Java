package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;

@RunWith(Parameterized.class) //Data Driven Test
public class CalculoValorLocacaoTest {
    @InjectMocks
    public LocacaoService service;

    @Mock
    private LocacaoDAO dao;

    @Mock
    public SPCService spc;

    @Parameterized.Parameter
    public List<Filme> filmes;

    @Parameterized.Parameter(1)
    public Double valorLocacao;

    @Parameterized.Parameter(2)
    public String cenario;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    private static final Filme filme1 = umFilme().agora();
    private static final Filme filme2 = umFilme().agora();
    private static final Filme filme3 = umFilme().agora();
    private static final Filme filme4 = umFilme().agora();
    private static final Filme filme5 = umFilme().agora();
    private static final Filme filme6 = umFilme().agora();
    private static final Filme filme7 = umFilme().agora();

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getParametros(){
        return Arrays.asList(new Object[][] {
                {Arrays.asList(filme1,filme2),8.0,"2 Filmes: Sem desconto"},
                {Arrays.asList(filme1,filme2,filme3),11.0,"3 Filmes: 25%"},
                {Arrays.asList(filme1,filme2,filme3,filme4),13.0,"4 Filmes: 50%"},
                {Arrays.asList(filme1,filme2,filme3,filme4,filme5),14.0,"5 Filmes: 75%"},
                {Arrays.asList(filme1,filme2,filme3,filme4,filme5,filme6,filme7),18.0,"7 Filmes: Sem desconto"}
        });
    }

    @Test
    public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");

        //acao
        Locacao resultado = service.alugarFilme(usuario,filmes);

        // verificacao
        Assert.assertThat(resultado.getValor(), CoreMatchers.is(valorLocacao));
    }
}
