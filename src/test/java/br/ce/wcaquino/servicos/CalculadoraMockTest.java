package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Locacao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class CalculadoraMockTest {

    @Mock // Caso ele não saiba o que fazer retorna 0
    private Calculadora calcMock;

    @Spy // Caso ele não saiba o que fazer roda o método
    private Calculadora calcSpy;

    @Spy
    private  EmailService email;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void devoMostrarDiferencaEntreMockSpy(){
        Mockito.when(calcMock.somar(1,2)).thenReturn(5);
//        Mockito.when(calcSpy.somar(1,2)).thenReturn(5);
        Mockito.doReturn(5).when(calcSpy).somar(1,2);
        Mockito.doNothing().when(calcSpy).imprime();

        System.out.println("Mock: " + calcMock.somar(1,2));
        System.out.println("Spy: " + calcSpy.somar(1,2));

        System.out.println("Mock");
        calcMock.imprime();
        System.out.println("Spy");
        calcSpy.imprime();
    }

    @Test
    public void teste(){
        Calculadora calc = Mockito.mock(Calculadora.class);

        ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(calc.somar(argCapt.capture(),argCapt.capture())).thenReturn(5);

        //System.out.println(calc.somar(1,2)); // não sabe o que fazer, logo retorna zero.
        Assert.assertEquals(5, calc.somar(1,100000));
//        System.out.println(argCapt.getAllValues());
    }
}
