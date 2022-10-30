package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Locacao;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class CalculadoraMockTest {

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
