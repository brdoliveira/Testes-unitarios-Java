package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchesProprios {
    public static DiaSemanaMatcher caiEm(Integer diaSemana){
        return new DiaSemanaMatcher(diaSemana);
    }

    public static DiaSemanaMatcher caiNumaSegunda(){
        return new DiaSemanaMatcher(Calendar.MONDAY);
    }

    public static DataDiferencaDiasMatcher ehHojeComDiferencaDias(Integer qtdDias){
        return new DataDiferencaDiasMatcher(qtdDias);
    }

    public static DataDiferencaDiasMatcher ehHoje(){
        return new DataDiferencaDiasMatcher(1);
    }


}
