package br.ce.wcaquino.servicos;

import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
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
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchesProprios.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class})
public class LocacaoServiceTest {
    @InjectMocks
    private LocacaoService service;

    @Mock
    private SPCService spc;

    @Mock
    private LocacaoDAO dao;

    @Mock
    private EmailService email;

    @Rule // Cria uma pilha com os erros
    public ErrorCollector error = new ErrorCollector(); // Notacao bem parecida do assert

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        service = PowerMockito.spy(service);
    }

    @Test
    public void deveAlugarFilme() throws Exception{
        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());

        //PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28,4,2017));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,29);
        calendar.set(Calendar.MONTH,Calendar.APRIL);
        calendar.set(Calendar.YEAR,2017);
        PowerMockito.mockStatic(Calendar.class);

        //ação
        Locacao locacao = service.alugarFilme(usuario,filmes);

        //validação
        error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0))); // Adicionar import estático
        // error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(),new Date()), CoreMatchers.is(true));
        // error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
        // error.checkThat(locacao.getDataRetorno(), ehHoje());
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(),DataUtils.obterData(28,4,2017)),CoreMatchers.is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(),DataUtils.obterData(29,4,2017)),CoreMatchers.is(true));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlugarFilmeSemEstoque() throws Exception{
        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());

        //ação
        service.alugarFilme(usuario,filmes);
    }

    @Test // Forma completa de tratar exceptions
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException{
        //cenario
        List<Filme> filmes = Arrays.asList(umFilme().agora());

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
        Usuario usuario = umUsuario().agora();
        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");

        // acao
        service.alugarFilme(usuario,null);
    }

    @Test
    //@Ignore // está ignorando o método
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        //PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29,4,2017));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,29);
        calendar.set(Calendar.MONTH,Calendar.APRIL);
        calendar.set(Calendar.YEAR,2017);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

        //acao
        Locacao retorno = service.alugarFilme(usuario,filmes);

        // verificacao
        // Assert.assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
        Assert.assertThat(retorno.getDataRetorno(), caiNumaSegunda());
        // PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
        // integração do powermockito com mockito é boa

        PowerMockito.verifyStatic(Mockito.times(2));
        Calendar.getInstance();
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception{
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

        // acao
        try {
            service.alugarFilme(usuario, filmes);
        // verificacao
            Assert.fail();
        }catch (LocadoraException e){
            Assert.assertThat(e.getMessage(),CoreMatchers.is("Usuário Negativado"));
        }

        verify(spc).possuiNegativacao(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas(){
        // cenario
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
        Usuario usuario3 = umUsuario().comNome("Outro Atrasado").agora();

        List<Locacao> locacoes = Arrays.asList(
                umLocacao().atrasada().comUsuario(usuario).agora(),
                umLocacao().comUsuario(usuario2).agora(),
                umLocacao().atrasada().comUsuario(usuario3).agora(),
                umLocacao().atrasada().comUsuario(usuario3).agora());
        when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        // acao
        service.notificarAtrasos();

        // verificacao
        verify(email, times(3)).notificarAtraso(Mockito.any(Usuario.class));

        // Escolhe apenas uma invocação:
         verify(email, atLeastOnce()).notificarAtraso(usuario3);

        // Escolhe o minimo de invocações:
        // verify(email, atLeast(2)).notificarAtraso(usuario3);

        // Escolhe o maximo de invocações:
        // verify(email, atMost(5)).notificarAtraso(usuario3);

        // Escolhe o numero de invocações:
        // verify(email, times(2)).notificarAtraso(usuario3);

        verify(email, never()).notificarAtraso(usuario2);
        verifyNoMoreInteractions(email);
        verifyZeroInteractions(spc);
    }

    @Test
    public void deveTratarErronoSpc() throws Exception{
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrofica"));

        // verificacao
        exception.expect(LocadoraException.class);
        exception.expectMessage("Problemas no SPC, tente novamente");

        // acao
        service.alugarFilme(usuario,filmes);
    }

    @Test
    public void deveProrrogarUmaLocacao(){
        // cenario
        Locacao locacao = LocacaoBuilder.umLocacao().agora();

        // acao
        service.prorrogarLocacao(locacao,3);

        // verificacao
        ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argCapt.capture());
        Locacao locacaoRetornada = argCapt.getValue();

        error.checkThat(locacaoRetornada.getValor(),CoreMatchers.is(12.0));
        error.checkThat(locacaoRetornada.getDataLocacao(),ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(),ehHojeComDiferencaDias(3));
    }

    @Test
    public void deveAlugarFilme_SemCalcularValor() throws Exception{
        // cenario
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao",filmes);

        // acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificacao
        Assert.assertThat(locacao.getValor(),CoreMatchers.is(1.0));
        PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao",filmes);
    }

    @Test
    public void deveCalcularValorLocacao() throws Exception{
        // cenario
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        // acao
        // (Double) = cast
        Double valor = (Double) Whitebox.invokeMethod(service,"calcularValorLocacao", filmes);

        // verificacao
        Assert.assertThat(valor,CoreMatchers.is(4.0));
    }
}
