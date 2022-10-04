package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;
import org.junit.Assert;
import org.junit.Test;

public class AssertTest {
    
    @Test
    public void Test(){
        Assert.assertTrue(true);
        Assert.assertFalse(false);

        Assert.assertEquals("Erro de comparacao",1,1);
        Assert.assertEquals(0.51,0.51,0.01); // DELTA = Margem de erro
        Assert.assertEquals(Math.PI,3.14, 0.01);

        int i = 5;
        Integer i2 = 5;
        // Auto-boxing e unboxing
        Assert.assertEquals(Integer.valueOf(i),i2);
        Assert.assertEquals(i,i2.intValue());

        Assert.assertEquals("bola","bola"); // esperado e atual
        Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
        Assert.assertTrue("bola".startsWith("bo"));

        Usuario u1 = new Usuario("Usuario 1");
        Usuario u2 = new Usuario("Usuario 1");
        Usuario u3 = u2;
        Usuario u4 = null;

        Assert.assertEquals(u1,u2);

        Assert.assertSame(u3,u2);

        Assert.assertNull(u4);

        Assert.assertNotEquals(u4,u2);
        Assert.assertNotSame(u1,u2);
        Assert.assertNotNull(u1);
    }

}
