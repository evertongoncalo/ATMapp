package org.ProjectoATM.Utilizacao;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class formatacao {

    static NumberFormat formatarValor = new DecimalFormat("#.##€");

    public static String doubletoString(Double valor) {
        return formatarValor.format(valor);
    }
}
