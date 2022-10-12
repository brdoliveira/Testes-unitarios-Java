package br.ce.wcaquino.suites;

import br.ce.wcaquino.servicos.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CalculadoraTest.class,
        CalculoValorLocacaoTest.class,
        LocacaoServiceTest.class,
        AssertTest.class,
        OrdemTest.class
})
public class SuiteExecucao {}
