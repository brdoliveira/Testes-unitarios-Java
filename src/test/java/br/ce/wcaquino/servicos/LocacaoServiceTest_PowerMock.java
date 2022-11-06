package br.ce.wcaquino.servicos;

import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchesProprios.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class}) // Muito pesado!
public class LocacaoServiceTest_PowerMock {
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

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28,4,2017));

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



    @Test
    //@Ignore // está ignorando o método
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        //cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29,4,2017));

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
